package poly.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.RememberTokenDAO;
import poly.com.dao.UserDAO;
import poly.com.service.OtpService;
import poly.com.service.OtpVerificationResult;
import poly.com.service.impl.OtpServiceImpl;
import poly.com.util.PasswordUtil;

/**
 * Controller xử lý xác thực OTP và reset password
 * Tuân thủ chuẩn bảo mật:
 * - Tra cứu OTP theo userId, kiểm tra attempts/expiry/used trước khi so sánh mã.
 * - Khóa OTP sau 3 lần sai.
 * - Consume OTP nguyên tử để chống race condition và replay.
 * - Thu hồi mọi remember token khi đổi mật khẩu thành công.
 */
@WebServlet("/verify-otp")
public class VerifyOtpController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private final OtpService otpService = new OtpServiceImpl();
    private final UserDAO userDAO = new UserDAO();
    private final RememberTokenDAO rememberTokenDAO = new RememberTokenDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String userId = session != null ? (String) session.getAttribute("resetUserId") : null;
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String userId = session != null ? (String) session.getAttribute("resetUserId") : null;
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        String otpCode = request.getParameter("otpCode");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // 1. Validate input form
        if (otpCode == null || otpCode.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã OTP");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu mới");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        if (!PasswordUtil.isPasswordValid(newPassword)) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 8 ký tự");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        // 2. Xác thực OTP qua Service
        OtpVerificationResult result = otpService.verifyOtp(userId, otpCode);
        if (!result.isSuccess()) {
            request.setAttribute("error", result.getMessage());
            if (result.getStatus() == OtpVerificationResult.Status.EXPIRED ||
                result.getStatus() == OtpVerificationResult.Status.MAX_ATTEMPTS_EXCEEDED ||
                result.getStatus() == OtpVerificationResult.Status.ALREADY_USED) {
                request.setAttribute("expired", true);
            }
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        // 3. OTP hợp lệ & đã được consume -> Cập nhật mật khẩu mới
        try {
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            boolean updated = userDAO.updatePassword(userId, hashedPassword);
            
            if (!updated) {
                request.setAttribute("error", "Không thể cập nhật mật khẩu, vui lòng thử lại");
                request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
                return;
            }
            
            // 4. Thu hồi tất cả remember token cũ của user
            rememberTokenDAO.revokeAllByUserId(userId);
            
            // 5. Xóa session reset
            session.removeAttribute("resetUserId");
            session.removeAttribute("resetUserEmail");
            
            // 6. Chuyển hướng đến đăng nhập kèm thông báo thành công
            response.sendRedirect(request.getContextPath() + "/login?resetSuccess=true");
            
        } catch (Exception e) {
            System.err.println("[ERROR] VerifyOtpController.doPost: " + e.getMessage());
            request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
        }
    }
}
