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
import poly.com.service.RememberMeService;
import poly.com.service.impl.RememberMeServiceImpl;

/**
 * Servlet implementation class LogoutController
 * Xử lý đăng xuất: vô hiệu hóa session, thu hồi remember token server-side và xóa cookie
 */
@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private ActivityLogService activityLogService;
    private RememberMeService rememberMeService;
    
    @Override
    public void init() throws ServletException {
        activityLogService = new ActivityLogService();
        rememberMeService = new RememberMeServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processLogout(request, response);
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // 1. Lấy session hiện tại (không tạo mới nếu không có)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Lấy user TRƯỚC KHI invalidate session
            User user = (User) session.getAttribute("user");
            
            // Log đăng xuất (nếu có user)
            if (user != null) {
                activityLogService.logLogout(user, request);
            }
            
            // 2. Hủy session
            session.invalidate();
        }
        
        // 3. Thu hồi token server-side và xóa cookie Remember Me an toàn
        rememberMeService.cancelRememberMe(request, response);
        
        // 4. Chuyển hướng về trang chủ
        response.sendRedirect(request.getContextPath() + "/home");
    }
}