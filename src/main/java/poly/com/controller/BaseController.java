package poly.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.entity.User;

/**
 * Base Controller cung cấp các method chung cho tất cả controllers
 * Giúp giảm code trùng lặp và tăng tính nhất quán
 */
public abstract class BaseController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Kiểm tra quyền Admin - chỉ Admin mới được truy cập
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return true nếu là Admin, false nếu không phải
     * @throws IOException nếu có lỗi khi redirect
     */
    protected boolean checkAdminRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if (user.isRole()) {
                return true;
            }
            // Người dùng đã đăng nhập nhưng không có quyền Quản trị (ví dụ: Phóng viên)
            session.setAttribute("toastError", "Bạn không có quyền truy cập vào chức năng Quản lý này.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return false;
        }
        // Chưa đăng nhập -> redirect về login
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    /**
     * Kiểm tra user đã đăng nhập chưa
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return User object nếu đã đăng nhập, null nếu chưa
     * @throws IOException nếu có lỗi khi redirect
     */
    protected User checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        // Kiểm tra tài khoản có bị khóa không
        if (!user.isEnabled()) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return user;
    }

    /**
     * Forward request với các attributes chung
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param pageTitle Tiêu đề trang
     * @param viewPath Đường dẫn đến view JSP
     * @param layoutPath Đường dẫn đến layout JSP (mặc định: admin_layout.jsp)
     * @throws ServletException
     * @throws IOException
     */
    protected void forwardToView(HttpServletRequest request, HttpServletResponse response, 
            String pageTitle, String viewPath, String layoutPath) throws ServletException, IOException {
        request.setAttribute("pageTitle", pageTitle);
        request.setAttribute("view", viewPath);
        request.getRequestDispatcher(layoutPath).forward(request, response);
    }

    /**
     * Forward request với layout mặc định (admin_layout.jsp)
     */
    protected void forwardToAdminView(HttpServletRequest request, HttpServletResponse response, 
            String pageTitle, String viewPath) throws ServletException, IOException {
        forwardToView(request, response, pageTitle, viewPath, "/views/admin/admin_layout.jsp");
    }

    /**
     * Forward request với layout public (layout.jsp)
     */
    protected void forwardToPublicView(HttpServletRequest request, HttpServletResponse response, 
            String pageTitle, String viewPath) throws ServletException, IOException {
        forwardToView(request, response, pageTitle, viewPath, "/views/public/layout.jsp");
    }

    /**
     * Xử lý exception và redirect đến error page
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param e Exception
     * @throws IOException
     */
    protected void handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        System.err.println("[ERROR] Exception caught in " + getClass().getSimpleName() + ": " + e.getClass().getName() + ": " + e.getMessage());
        response.sendRedirect(request.getContextPath() + "/error.jsp");
    }

    /**
     * Kiểm tra parameter có null hoặc empty không
     * @param param Giá trị cần kiểm tra
     * @return true nếu null hoặc empty
     */
    protected boolean isNullOrEmpty(String param) {
        return param == null || param.trim().isEmpty();
    }

    /**
     * Lấy context path từ request (tái sử dụng)
     * @param request HttpServletRequest
     * @return Context path
     */
    protected String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
    }
}
