package poly.com.controller;

import java.io.IOException;
import java.util.Base64;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.entity.User;
import poly.com.service.ActivityLogService;
import poly.com.service.UserService;
import poly.com.service.impl.UserServiceImpl;
import poly.com.util.ConfigHelper;

/**
 * Controller xử lý đăng nhập - hiển thị form và xác thực thông tin đăng nhập
 * Sử dụng tầng Service cho xử lý nghiệp vụ cốt lõi theo mô hình Layered MVC
 */
@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserService userService;
    private ActivityLogService activityLogService;
    private poly.com.service.RememberMeService rememberMeService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
        activityLogService = new ActivityLogService();
        rememberMeService = new poly.com.service.impl.RememberMeServiceImpl();
        getServletContext().setAttribute("googleClientId", ConfigHelper.get("google.client.id", ""));
    }

    /**
     * Hiển thị trang đăng nhập
     * Kiểm tra cookie remember-me an toàn để tự động đăng nhập
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra remember-me token an toàn
        User autoUser = rememberMeService.processAutoLogin(request, response);
        if (autoUser != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", autoUser);
            activityLogService.logLogin(autoUser, request);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        
        request.setAttribute("googleClientId", ConfigHelper.get("google.client.id", ""));
        request.getRequestDispatcher("/views/public/login.jsp").forward(request, response);
    }

    /**
     * Xử lý thông tin đăng nhập từ form
     * Nếu đăng nhập thành công, lưu user vào session và chuyển đến dashboard
     * Nếu thất bại, hiển thị thông báo lỗi
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            User user = userService.login(email, password);
            
            if (user != null) {
                // Kiểm tra tài khoản có bị khóa không
                if (!user.isEnabled()) {
                    rememberMeService.cancelRememberMe(request, response);
                    request.setAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
                    request.getRequestDispatcher("/views/public/login.jsp").forward(request, response);
                    return;
                }
                
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // Log đăng nhập thành công
                activityLogService.logLogin(user, request);
                
                // Xử lý "Remember me" bảo mật bằng 256-bit SecureRandom token và SHA-256 hash
                String remember = request.getParameter("remember");
                if ("on".equals(remember) || "true".equalsIgnoreCase(remember)) {
                    rememberMeService.issueRememberMe(user.getId(), request, response);
                } else {
                    rememberMeService.cancelRememberMe(request, response);
                }
                
                // Redirect đến dashboard
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                // Log đăng nhập thất bại
                activityLogService.logLoginFailed(email, request);
                
                request.setAttribute("error", "Email hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("/views/public/login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] LoginController.doPost: " + e.getMessage());
            request.setAttribute("error", "Đã có lỗi hệ thống xảy ra!");
            request.getRequestDispatcher("/views/public/login.jsp").forward(request, response);
        }
    }
}