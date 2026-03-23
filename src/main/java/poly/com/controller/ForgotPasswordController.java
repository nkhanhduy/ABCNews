package poly.com.controller;

import java.io.IOException;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.OtpTokenDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.OtpToken;
import poly.com.entity.User;
import poly.com.util.EmailService;
import poly.com.util.OtpUtil;

/**
 * Controller xử lý chức năng "Quên mật khẩu"
 * 
 * Flow:
 * 1. User nhập email
 * 2. Kiểm tra email tồn tại
 * 3. Generate OTP 6 số
 * 4. Lưu OTP vào database (expiry = 5 phút)
 * 5. Gửi OTP qua email
 * 6. Redirect đến trang verify OTP
 * 
 * @author ABCNews Development Team
 */
@WebServlet("/forgot-password")
public class ForgotPasswordController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private UserDAO userDAO = new UserDAO();
    private OtpTokenDAO otpTokenDAO = new OtpTokenDAO();
    private EmailService emailService = new EmailService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Hiển thị form nhập email (standalone, không qua layout)
        request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email");
            request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
            return;
        }
        
        email = email.trim();
        
        // Tìm user theo email
        User user = userDAO.findByEmail(email);
        
        if (user == null) {
            request.setAttribute("error", "Email chưa được đăng ký trong hệ thống");
            request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
            return;
        }
        
        try {
            // Xóa các OTP cũ của user (nếu có)
            otpTokenDAO.deleteUserOtps(user.getId());
            
            // Generate OTP 6 số
            String otpCode = OtpUtil.generateOtp();
            
            // Tính expiry time (5 phút từ bây giờ)
            Date expiryTime = OtpUtil.calculateExpiryTime(5);
            
            // Tạo OTP token
            OtpToken otpToken = new OtpToken(user.getId(), otpCode, expiryTime);
            
            // Lưu vào database
            boolean saved = otpTokenDAO.createOtp(otpToken);
            
            if (!saved) {
                request.setAttribute("error", "Không thể tạo OTP, vui lòng thử lại");
                request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
                return;
            }
            
            // Gửi email OTP
            emailService.sendOtpEmail(email, otpCode, user.getFullname());
            
            // Lưu thông tin vào session để dùng ở bước verify
            HttpSession session = request.getSession();
            session.setAttribute("resetUserId", user.getId());
            session.setAttribute("resetUserEmail", email);
            session.setMaxInactiveInterval(10 * 60); // 10 phút timeout
            
            // Redirect đến trang verify OTP
            response.sendRedirect(request.getContextPath() + "/verify-otp?success=sent");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể gửi email, vui lòng thử lại sau");
            request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response);
        }
    }
}

