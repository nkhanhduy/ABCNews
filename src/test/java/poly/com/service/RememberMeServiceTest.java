package poly.com.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.RememberTokenDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.RememberToken;
import poly.com.entity.User;
import poly.com.service.impl.RememberMeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RememberMeServiceTest {

    @Mock
    private RememberTokenDAO rememberTokenDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private RememberMeServiceImpl rememberMeService;
    private User testUser;

    @BeforeEach
    void setUp() {
        rememberMeService = new RememberMeServiceImpl(rememberTokenDAO, userDAO);

        testUser = new User();
        testUser.setId("usr001");
        testUser.setEmail("user@abcnews.vn");
        testUser.setEnabled(true);
        testUser.setRole(false);
    }

    @Test
    @DisplayName("Cấp phát Remember Me token an toàn: Sinh token, lưu hash trong DB, gửi HttpOnly cookie")
    void testIssueRememberMe() {
        when(rememberTokenDAO.insert(any(RememberToken.class))).thenReturn(true);

        rememberMeService.issueRememberMe("usr001", request, response);

        ArgumentCaptor<RememberToken> tokenCaptor = ArgumentCaptor.forClass(RememberToken.class);
        verify(rememberTokenDAO).insert(tokenCaptor.capture());

        RememberToken savedToken = tokenCaptor.getValue();
        assertEquals("usr001", savedToken.getUserId());
        assertNotNull(savedToken.getTokenHash());
        assertEquals(64, savedToken.getTokenHash().length()); // 256-bit SHA-256 hex string

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        Cookie cookie = cookieCaptor.getValue();
        assertEquals(RememberMeServiceImpl.COOKIE_NAME, cookie.getName());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertEquals(RememberMeServiceImpl.TOKEN_VALIDITY_DAYS * 24 * 60 * 60, cookie.getMaxAge());
    }

    @Test
    @DisplayName("Tự động đăng nhập thành công với token hợp lệ và thực hiện Token Rotation")
    void testProcessAutoLogin_SuccessAndRotate() {
        String rawToken = "valid_random_secure_token_abc123";
        String tokenHash = rememberMeService.hashToken(rawToken);

        Cookie authCookie = new Cookie(RememberMeServiceImpl.COOKIE_NAME, rawToken);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 10);
        RememberToken dbToken = new RememberToken("usr001", tokenHash, cal.getTime());

        when(rememberTokenDAO.findValidToken(tokenHash)).thenReturn(dbToken);
        when(userDAO.findById("usr001")).thenReturn(testUser);
        when(rememberTokenDAO.insert(any(RememberToken.class))).thenReturn(true);

        User loggedInUser = rememberMeService.processAutoLogin(request, response);

        assertNotNull(loggedInUser);
        assertEquals("usr001", loggedInUser.getId());

        // Phải thu hồi token cũ (Token Rotation)
        verify(rememberTokenDAO).revokeByTokenHash(tokenHash);
        // Phải phát hành token mới
        verify(rememberTokenDAO).insert(any(RememberToken.class));
    }

    @Test
    @DisplayName("Tự động đăng nhập thất bại khi không có cookie")
    void testProcessAutoLogin_NoCookie() {
        when(request.getCookies()).thenReturn(null);

        User loggedInUser = rememberMeService.processAutoLogin(request, response);
        assertNull(loggedInUser);
    }

    @Test
    @DisplayName("Tự động đăng nhập thất bại khi token không tồn tại hoặc đã hết hạn trong DB")
    void testProcessAutoLogin_InvalidOrExpiredToken() {
        String rawToken = "expired_token_123";
        String tokenHash = rememberMeService.hashToken(rawToken);

        Cookie authCookie = new Cookie(RememberMeServiceImpl.COOKIE_NAME, rawToken);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});
        when(rememberTokenDAO.findValidToken(tokenHash)).thenReturn(null);

        User loggedInUser = rememberMeService.processAutoLogin(request, response);

        assertNull(loggedInUser);
        // Xóa cookie khỏi client
        verify(response, atLeastOnce()).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("Tự động đăng nhập thất bại và thu hồi token khi tài khoản bị khóa (disabled)")
    void testProcessAutoLogin_AccountDisabled() {
        String rawToken = "valid_token_disabled_user";
        String tokenHash = rememberMeService.hashToken(rawToken);

        Cookie authCookie = new Cookie(RememberMeServiceImpl.COOKIE_NAME, rawToken);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});

        RememberToken dbToken = new RememberToken("usr001", tokenHash, new Date());
        testUser.setEnabled(false); // Tài khoản bị khóa

        when(rememberTokenDAO.findValidToken(tokenHash)).thenReturn(dbToken);
        when(userDAO.findById("usr001")).thenReturn(testUser);

        User loggedInUser = rememberMeService.processAutoLogin(request, response);

        assertNull(loggedInUser);
        // Token phải bị thu hồi ngay lập tức
        verify(rememberTokenDAO).revokeByTokenHash(tokenHash);
    }

    @Test
    @DisplayName("Đăng xuất: Thu hồi token trong database và xóa cookie")
    void testCancelRememberMe() {
        String rawToken = "logout_token_abc";
        String tokenHash = rememberMeService.hashToken(rawToken);

        Cookie authCookie = new Cookie(RememberMeServiceImpl.COOKIE_NAME, rawToken);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});

        rememberMeService.cancelRememberMe(request, response);

        verify(rememberTokenDAO).revokeByTokenHash(tokenHash);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, atLeastOnce()).addCookie(cookieCaptor.capture());

        boolean foundClearedCookie = cookieCaptor.getAllValues().stream()
            .anyMatch(c -> RememberMeServiceImpl.COOKIE_NAME.equals(c.getName()) && c.getMaxAge() == 0);
        assertTrue(foundClearedCookie, "Cookie phải được xóa với MaxAge = 0");
    }
}
