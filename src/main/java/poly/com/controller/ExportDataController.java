package poly.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.CategoryDAO;
import poly.com.dao.NewsDAO;
import poly.com.dao.NewsletterDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.User;

/**
 * Controller hiển thị trang Export Data với form chọn module và format
 * 
 * URL: /admin/export-data
 * Access: Chỉ Admin (protected by AuthFilter)
 */
@WebServlet("/admin/export-data")
public class ExportDataController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private NewsDAO newsDAO = new NewsDAO();
    private UserDAO userDAO = new UserDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private NewsletterDAO newsletterDAO = new NewsletterDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập (cả Admin và Reporter đều truy cập được)
        User currentUser = checkLogin(request, response);
        if (currentUser == null) {
            return;
        }
        
        boolean isAdmin = currentUser.isRole();
        
        // Lấy thống kê số lượng
        int totalNews;
        if (isAdmin) {
            // Admin: Thống kê tất cả
            totalNews = newsDAO.countAll();
        } else {
            // Reporter: Chỉ tin của mình
            totalNews = newsDAO.findByAuthor(currentUser.getId()).size();
        }
        
        int totalUsers = userDAO.countAll();
        int totalCategories = categoryDAO.countAll();
        int totalNewsletters = newsletterDAO.countAll();
        
        // Set attributes
        request.setAttribute("isAdmin", isAdmin);
        request.setAttribute("totalNews", totalNews);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalCategories", totalCategories);
        request.setAttribute("totalNewsletters", totalNewsletters);
        
        // Forward to view
        forwardToAdminView(request, response, "Xuất Dữ Liệu", "/views/admin/export-data.jsp");
    }
}

