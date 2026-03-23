package poly.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.entity.User;
import poly.com.service.ActivityLogService;

/**
 * Servlet implementation class LogoutController
 * Xử lý đăng xuất
 */
@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private ActivityLogService activityLogService;
    
    @Override
    public void init() throws ServletException {
        activityLogService = new ActivityLogService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Lấy session hiện tại (không tạo mới nếu không có)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Lấy user TRƯỚC KHI invalidate session
            User user = (User) session.getAttribute("user");
            
            // Log đăng xuất (nếu có user)
            if (user != null) {
                activityLogService.logLogout(user, request);
            }
            
            // 2. Xóa session (xóa "user")
            session.invalidate();
        }
        
        // 3. Xóa cookie "remember" nếu có
        Cookie rememberCookie = new Cookie("remember", "");
        rememberCookie.setMaxAge(0);
        rememberCookie.setPath("/");
        response.addCookie(rememberCookie);
        
        // 4. Chuyển hướng về trang chủ
        response.sendRedirect(request.getContextPath() + "/home");
    }
}