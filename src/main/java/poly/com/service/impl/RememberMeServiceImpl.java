package poly.com.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HexFormat;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.RememberTokenDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.RememberToken;
import poly.com.entity.User;
import poly.com.service.RememberMeService;

/**
 * Triển khai RememberMeService bảo mật:
 * - 256-bit SecureRandom token.
 * - Client chỉ giữ plaintext token qua Cookie (HttpOnly, SameSite=Lax, Secure khi HTTPS).
 * - Server chỉ lưu SHA-256 hash của token trong DB.
 * - Hỗ trợ Token Rotation khi auto-login thành công.
 * - Thu hồi token server-side khi đăng xuất hoặc tài khoản bị khóa.
 */
public class RememberMeServiceImpl implements RememberMeService {

    public static final String COOKIE_NAME = "REMEMBER_ME_TOKEN";
    public static final int TOKEN_VALIDITY_DAYS = 30;
    public static final int TOKEN_BYTE_LENGTH = 32; // 256 bits

    private final RememberTokenDAO rememberTokenDAO;
    private final UserDAO userDAO;
    private final SecureRandom secureRandom;

    public RememberMeServiceImpl() {
        this(new RememberTokenDAO(), new UserDAO());
    }

    public RememberMeServiceImpl(RememberTokenDAO rememberTokenDAO, UserDAO userDAO) {
        this.rememberTokenDAO = rememberTokenDAO;
        this.userDAO = userDAO;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public void issueRememberMe(String userId, HttpServletRequest request, HttpServletResponse response) {
        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String tokenHash = hashToken(rawToken);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, TOKEN_VALIDITY_DAYS);
        Date expiresAt = cal.getTime();

        RememberToken tokenEntity = new RememberToken(userId, tokenHash, expiresAt);
        boolean saved = rememberTokenDAO.insert(tokenEntity);
        if (!saved) {
            return;
        }

        Cookie cookie = new Cookie(COOKIE_NAME, rawToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(TOKEN_VALIDITY_DAYS * 24 * 60 * 60);

        if (request != null && request.isSecure()) {
            cookie.setSecure(true);
        }

        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    @Override
    public User processAutoLogin(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        String rawToken = null;
        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                rawToken = c.getValue();
                break;
            }
        }

        if (rawToken == null || rawToken.trim().isEmpty()) {
            return null;
        }

        String tokenHash = hashToken(rawToken);
        RememberToken token = rememberTokenDAO.findValidToken(tokenHash);
        if (token == null) {
            cancelRememberMe(request, response);
            return null;
        }

        User user = userDAO.findById(token.getUserId());
        if (user == null || !user.isEnabled()) {
            cancelRememberMe(request, response);
            return null;
        }

        // Token rotation: Thu hồi token cũ, cấp phát token mới
        rememberTokenDAO.revokeByTokenHash(tokenHash);
        issueRememberMe(user.getId(), request, response);

        return user;
    }

    @Override
    public void cancelRememberMe(HttpServletRequest request, HttpServletResponse response) {
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (COOKIE_NAME.equals(c.getName())) {
                        String rawToken = c.getValue();
                        if (rawToken != null && !rawToken.trim().isEmpty()) {
                            String tokenHash = hashToken(rawToken);
                            rememberTokenDAO.revokeByTokenHash(tokenHash);
                        }
                        break;
                    }
                }
            }
        }

        // Xóa cookie hiện tại
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);

        // Xóa thêm cookie cũ "remember" nếu còn sót lại ở client
        Cookie legacyCookie = new Cookie("remember", "");
        legacyCookie.setPath("/");
        legacyCookie.setMaxAge(0);
        response.addCookie(legacyCookie);
    }

    @Override
    public void revokeAllTokens(String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            rememberTokenDAO.revokeAllByUserId(userId.trim());
        }
    }

    @Override
    public String hashToken(String rawToken) {
        if (rawToken == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
