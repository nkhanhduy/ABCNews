package poly.com.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.entity.User;

/**
 * Filter kiểm tra đăng nhập trước khi cho phép truy cập các trang /admin/*
 * Nếu chưa đăng nhập, chuyển hướng về trang login
 */
@WebFilter("/admin/*")
public class AuthFilter extends HttpFilter {
    
    private static final long serialVersionUID = 1L;

    /**
     * Kiểm tra session user, nếu không có thì chuyển về trang login
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
        } else {
            // Kiểm tra tài khoản có bị khóa không
            if (!user.isEnabled()) {
                // Xóa session và redirect về login
                if (session != null) {
                    session.invalidate();
                }
                // Lưu thông báo lỗi vào session để hiển thị sau khi redirect
                req.getSession().setAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            chain.doFilter(request, response);
        }
    }
}