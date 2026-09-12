package poly.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.UserDAO;
import poly.com.entity.OtpToken;
import poly.com.entity.User;
import poly.com.service.OtpService;
import poly.com.service.impl.OtpServiceImpl;
import poly.com.util.EmailService;

/**
 * Controller xử lý chức năng "Quên mật khẩu"
 * Sử dụng OtpService để quản lý sinh mã, hạn giờ và chống spam
 */
@WebServlet("/forgot-password")
public class ForgotPasswordController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private final UserDAO userDAO = new UserDAO();
    private final OtpService otpService = new OtpServiceImpl();
    private final EmailService emailService = new EmailService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        // 1. Validate email
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email");
            request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
            return;
        }
        
        email = email.trim().toLowerCase();
        
        // 2. Kiểm tra Rate Limiting chống spam gửi email
        if (otpService.isCooldownActive(email)) {
            long remainingSeconds = otpService.getRemainingCooldownSeconds(email);
            request.setAttribute("error", "Yêu cầu gửi mã quá nhanh. Vui lòng đợi " + remainingSeconds + " giây trước khi thử lại.");
            request.setAttribute("cooldownSeconds", remainingSeconds);
            request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
            return;
        }
        
        // 3. Tìm user theo email
        User user = userDAO.findByEmail(email);
        
        if (user == null) {
            request.setAttribute("error", "Email chưa được đăng ký trong hệ thống");
            request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
            return;
        }
        
        if (!user.isEnabled()) {
            request.setAttribute("error", "Tài khoản của bạn đang bị khóa. Vui lòng liên hệ quản trị viên.");
            request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
            return;
        }
        
        try {
            // 4. Tạo OTP mới và lưu database qua OtpService
            OtpToken otpToken = otpService.generateAndSaveOtp(user.getId());
            
            // 5. Gửi email OTP
            emailService.sendOtpEmail(email, otpToken.getOtpCode(), user.getFullname());
            
            // 6. Ghi nhận thời gian gửi OTP để chống spam
            otpService.recordOtpSent(email);
            
            // 7. Lưu thông tin vào session
            HttpSession session = request.getSession();
            session.setAttribute("resetUserId", user.getId());
            session.setAttribute("resetUserEmail", email);
            session.setAttribute("lastOtpSentTime", System.currentTimeMillis());
            session.setMaxInactiveInterval(10 * 60); // 10 phút timeout
            
            // 8. Chuyển hướng đến trang verify OTP
            response.sendRedirect(request.getContextPath() + "/verify-otp?success=sent");
            
        } catch (Exception e) {
            System.err.println("[ERROR] ForgotPasswordController.doPost: " + e.getMessage());
            request.setAttribute("error", "Không thể gửi email OTP, vui lòng thử lại sau");
            request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
        }
    }
}
