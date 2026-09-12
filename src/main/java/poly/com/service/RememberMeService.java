package poly.com.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import poly.com.entity.User;

/**
 * Service xử lý tính năng Remember Me an toàn:
 * - Sinh token ngẫu nhiên bằng SecureRandom (256-bit).
 * - Gửi raw token cho browser qua HttpOnly cookie với SameSite=Lax.
 * - Chỉ lưu SHA-256 hash của token trong database.
 * - Rotate token khi auto-login thành công để ngăn replay attacks.
 * - Thu hồi token server-side khi logout hoặc khi tài khoản bị khóa.
 */
public interface RememberMeService {

    /**
     * Tạo token mới và ghi Cookie bảo mật vào response
     *
     * @param userId ID của người dùng
     * @param request HTTP request
     * @param response HTTP response
     */
    void issueRememberMe(String userId, HttpServletRequest request, HttpServletResponse response);

    /**
     * Kiểm tra cookie remember-me, xác thực token hash trong database,
     * thực hiện token rotation và trả về User nếu hợp lệ
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return User hợp lệ hoặc null nếu không hợp lệ / hết hạn / bị khóa
     */
    User processAutoLogin(HttpServletRequest request, HttpServletResponse response);

    /**
     * Thu hồi token trong database và xóa cookie ở trình duyệt khi người dùng đăng xuất
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    void cancelRememberMe(HttpServletRequest request, HttpServletResponse response);

    /**
     * Băm raw token thành SHA-256 hex string
     *
     * @param rawToken chuỗi token ngẫu nhiên plaintext
     * @return chuỗi SHA-256 hash
     */
    String hashToken(String rawToken);
}
