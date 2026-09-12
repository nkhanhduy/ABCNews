package poly.com.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Tiện ích quản lý và kiểm tra CSRF (Cross-Site Request Forgery) Token
 */
public final class CsrfUtil {

    public static final String CSRF_SESSION_ATTR = "CSRF_TOKEN";
    public static final String CSRF_PARAM_NAME = "_csrf";
    public static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CsrfUtil() {
    }

    /**
     * Lấy token hiện tại từ session, nếu chưa có thì sinh mới (256-bit SecureRandom)
     */
    public static String getOrCreateToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        String token = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (token == null || token.trim().isEmpty()) {
            token = (String) session.getAttribute("csrfToken");
        }
        if (token == null || token.trim().isEmpty()) {
            byte[] bytes = new byte[32]; // 256 bits
            SECURE_RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF_SESSION_ATTR, token);
        }
        // Đồng bộ cả 2 tên attribute trong session để tương thích mọi cú pháp JSP (${sessionScope.CSRF_TOKEN} và ${sessionScope.csrfToken})
        session.setAttribute("csrfToken", token);
        return token;
    }

    /**
     * Xác thực CSRF token từ request so với token lưu trong session
     * Sử dụng thuật toán so sánh constant-time MessageDigest.isEqual để chống timing attack
     * Hỗ trợ header X-CSRF-TOKEN, parameter _csrf (kể cả URL query), và multipart Part _csrf
     */
    public static boolean isValidToken(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            sessionToken = (String) session.getAttribute("csrfToken");
        }
        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            return false;
        }

        // 1. Kiểm tra header (dành cho request AJAX / fetch)
        String requestToken = request.getHeader(CSRF_HEADER_NAME);

        // 2. Nếu header không có, kiểm tra parameter từ form / query string
        if (requestToken == null || requestToken.trim().isEmpty()) {
            requestToken = request.getParameter(CSRF_PARAM_NAME);
        }

        // 3. Nếu vẫn chưa có và request là multipart/form-data, đọc từ Part của request nếu có cấu hình multipart
        if ((requestToken == null || requestToken.trim().isEmpty())
                && request.getContentType() != null
                && request.getContentType().toLowerCase().startsWith("multipart/form-data")) {
            try {
                jakarta.servlet.http.Part part = request.getPart(CSRF_PARAM_NAME);
                if (part != null) {
                    try (java.io.InputStream is = part.getInputStream()) {
                        requestToken = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                    }
                }
            } catch (Exception ignored) {
                // An toàn: Bỏ qua nếu servlet chưa khởi tạo multipart config hoặc không có part
            }
        }

        if (requestToken == null || requestToken.trim().isEmpty()) {
            return false;
        }

        return MessageDigest.isEqual(
            sessionToken.getBytes(StandardCharsets.UTF_8),
            requestToken.trim().getBytes(StandardCharsets.UTF_8)
        );
    }
}
