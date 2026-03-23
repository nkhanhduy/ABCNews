package poly.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.OtpTokenDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.OtpToken;
import poly.com.util.OtpUtil;
import poly.com.util.PasswordUtil;

/**
 * Controller xử lý xác thực OTP và reset password
 * 
 * Flow:
 * 1. User nhập OTP code + password mới
 * 2. Validate OTP (chưa dùng, chưa hết hạn, attempts < 3)
 * 3. Nếu OTP đúng:
 *    - Hash password mới
 *    - Update password trong Users table
 *    - Mark OTP as used
 *    - Clear session
 *    - Redirect đến login với success message
 * 4. Nếu OTP sai:
 *    - Increment attempts
 *    - Show error với số lần thử còn lại
 * 
 * @author ABCNews Development Team
 */
@WebServlet("/verify-otp")
public class VerifyOtpController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private OtpTokenDAO otpTokenDAO = new OtpTokenDAO();
    private UserDAO userDAO = new UserDAO();
    
    private static final int MAX_ATTEMPTS = 3;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("resetUserId");
        
        // Kiểm tra session có userId không (phải đi từ forgot-password)
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        // Hiển thị form nhập OTP (standalone, không qua layout)
        request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("resetUserId");
        
        // Kiểm tra session
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        String otpCode = request.getParameter("otpCode");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
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
        
        // Validate password strength
        if (!PasswordUtil.isPasswordValid(newPassword)) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 8 ký tự");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        // Validate password match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        otpCode = otpCode.trim();
        
        // Tìm OTP token
        OtpToken otpToken = otpTokenDAO.findOtpByCode(userId, otpCode);
        
        if (otpToken == null) {
            request.setAttribute("error", "Mã OTP không đúng");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra OTP đã được sử dụng chưa
        if (otpToken.isUsed()) {
            request.setAttribute("error", "Mã OTP đã được sử dụng");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra OTP hết hạn chưa
        if (!OtpUtil.isOtpValid(otpToken.getExpiryTime())) {
            request.setAttribute("error", "Mã OTP đã hết hạn, vui lòng gửi lại");
            request.setAttribute("expired", true);
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra số lần thử
        if (otpToken.getAttempts() >= MAX_ATTEMPTS) {
            request.setAttribute("error", "Đã nhập sai quá " + MAX_ATTEMPTS + " lần, vui lòng gửi lại OTP");
            request.setAttribute("expired", true);
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra OTP code có đúng không
        if (!otpToken.getOtpCode().equals(otpCode)) {
            // Increment attempts
            otpTokenDAO.incrementAttempts(otpToken.getId());
            
            int remainingAttempts = MAX_ATTEMPTS - (otpToken.getAttempts() + 1);
            
            if (remainingAttempts > 0) {
                request.setAttribute("error", "Mã OTP không đúng (còn " + remainingAttempts + " lần thử)");
            } else {
                request.setAttribute("error", "Đã nhập sai quá " + MAX_ATTEMPTS + " lần, vui lòng gửi lại OTP");
                request.setAttribute("expired", true);
            }
            
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            return;
        }
        
        // OTP hợp lệ! Reset password
        try {
            // Hash password mới
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            
            // Update password trong database
            boolean updated = userDAO.updatePassword(userId, hashedPassword);
            
            if (!updated) {
                request.setAttribute("error", "Không thể cập nhật mật khẩu, vui lòng thử lại");
                request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
                return;
            }
            
            // Mark OTP as used
            otpTokenDAO.markOtpAsUsed(otpToken.getId());
            
            // Clear session
            session.removeAttribute("resetUserId");
            session.removeAttribute("resetUserEmail");
            
            // Redirect đến login với success message
            response.sendRedirect(request.getContextPath() + "/login?resetSuccess=true");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại");
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
        }
    }
}

