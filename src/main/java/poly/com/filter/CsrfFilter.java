package poly.com.filter;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.util.CsrfUtil;

/**
 * Filter bảo vệ ứng dụng trước các cuộc tấn công CSRF (Cross-Site Request Forgery)
 * - Tự động cấp phát CSRF token cho các request GET.
 * - Kiểm tra token nghiêm ngặt với các request thay đổi trạng thái (POST, PUT, DELETE, PATCH) trong khu vực quản trị /admin/*.
 * - Miễn trừ các public callback như Google OAuth verification.
 */
@WebFilter("/*")
public class CsrfFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    // Các endpoint công khai hoặc callback bên thứ 3 không áp dụng CSRF session token
    private static final Set<String> EXEMPT_PATHS = Set.of(
        "/auth/google/verify",
        "/login",
        "/forgot-password",
        "/verify-otp",
        "/newsletter",
        "/comment"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String method = req.getMethod();
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Đồng bộ CSRF token vào session và request attributes nếu có session
        HttpSession session = req.getSession(false);
        if (session != null) {
            String token = CsrfUtil.getOrCreateToken(session);
            req.setAttribute("csrfToken", token);
            req.setAttribute("CSRF_TOKEN", token);
        }

        // 1. Đối với request an toàn (GET, HEAD...)
        if (SAFE_METHODS.contains(method)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Đối với request thay đổi state (POST, PUT, DELETE...)
        // Bỏ qua nếu thuộc danh sách miễn trừ công khai
        for (String exempt : EXEMPT_PATHS) {
            if (path.equals(exempt) || path.startsWith(exempt + "/")) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 3. Chỉ bắt buộc xác thực CSRF đối với các thao tác thay đổi dữ liệu trong admin hoặc có session
        if (path.startsWith("/admin/") || req.getSession(false) != null) {
            if (!CsrfUtil.isValidToken(req)) {
                System.err.println("[SECURITY WARN] Chặn CSRF không hợp lệ tại path: " + path + " - Method: " + method);
                res.setContentType("text/plain;charset=UTF-8");
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Yêu cầu bị từ chối: CSRF token không hợp lệ hoặc phiên làm việc đã hết hạn. Vui lòng tải lại trang.");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
