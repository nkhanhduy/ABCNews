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

/**
 * Controller xử lý đăng nhập - hiển thị form và xác thực thông tin đăng nhập
 * Sử dụng tầng Service theo chuẩn 3-Tier Clean Architecture
 */
@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    private UserService userService;
    private ActivityLogService activityLogService;

    /**
     * Khởi tạo UserService và ActivityLogService khi servlet được load
     */
    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
        activityLogService = new ActivityLogService();
    }

    /**
     * Hiển thị trang đăng nhập
     * Kiểm tra cookie "remember" để tự động đăng nhập
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra cookie "remember" để auto-login
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember".equals(cookie.getName())) {
                    String rememberValue = cookie.getValue();
                    if (rememberValue != null && !rememberValue.isEmpty()) {
                        try {
                            // Giải mã: chỉ có userId
                            String userId = new String(Base64.getDecoder().decode(rememberValue));
                            User user = userService.findById(userId);
                            if (user != null) {
                                // Kiểm tra tài khoản có bị khóa không
                                if (!user.isEnabled()) {
                                    // Xóa cookie nếu tài khoản bị khóa
                                    Cookie invalidCookie = new Cookie("remember", "");
                                    invalidCookie.setMaxAge(0);
                                    invalidCookie.setPath("/");
                                    response.addCookie(invalidCookie);
                                    request.setAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
                                    request.getRequestDispatcher("/views/public/login.jsp").forward(request, response);
                                    return;
                                }
                                // Auto-login
                                HttpSession session = request.getSession();
                                session.setAttribute("user", user);
                                
                                // Log đăng nhập thành công (auto-login từ cookie)
                                activityLogService.logLogin(user, request);
                                
                                // Redirect đến dashboard (AdminController sẽ phân biệt Admin/Reporter)
                                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                                return;
                            }
                        } catch (Exception e) {
                            // Cookie không hợp lệ, xóa nó
                            Cookie invalidCookie = new Cookie("remember", "");
                            invalidCookie.setMaxAge(0);
                            invalidCookie.setPath("/");
                            response.addCookie(invalidCookie);
                        }
                    }
                    break;
                }
            }
        }
        
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
                    request.setAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
                    request.getRequestDispatcher("/views/public/login.jsp").forward(request, response);
                    return;
                }
                
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // Log đăng nhập thành công
                activityLogService.logLogin(user, request);
                
                // Xử lý "Remember me"
                String remember = request.getParameter("remember");
                if (remember != null && remember.equals("on")) {
                    // Tạo remember token: chỉ lưu userId (đơn giản và an toàn)
                    try {
                        String encoded = Base64.getEncoder().encodeToString(user.getId().getBytes());
                        
                        Cookie rememberCookie = new Cookie("remember", encoded);
                        rememberCookie.setMaxAge(24 * 60 * 60); // 1 ngày
                        rememberCookie.setPath("/");
                        rememberCookie.setHttpOnly(true); // Bảo mật hơn
                        response.addCookie(rememberCookie);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Xóa cookie nếu không chọn "Remember me"
                    Cookie rememberCookie = new Cookie("remember", "");
                    rememberCookie.setMaxAge(0);
                    rememberCookie.setPath("/");
                    response.addCookie(rememberCookie);
                }
                
                // Redirect đến dashboard (AdminController sẽ phân biệt Admin/Reporter)
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                // Log đăng nhập thất bại
                activityLogService.logLoginFailed(email, request);
                
                request.setAttribute("error", "Email hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("/views/public/login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã có lỗi hệ thống xảy ra!");
            request.getRequestDispatcher("/views/public/login.jsp").forward(request, response);
        }
    }
}