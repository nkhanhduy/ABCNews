package poly.com.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.NewsletterDAO;
import poly.com.entity.Newsletter;
import poly.com.entity.User;
import poly.com.service.ActivityLogService;

/**
 * Controller quản lý newsletter (Email đăng ký) - chỉ dành cho Admin
 * Xử lý: Xem danh sách, xóa email, kích hoạt/hủy đăng ký, tìm kiếm và lọc
 */
@WebServlet("/admin/newsletters")
public class NewsletterAdminController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private NewsletterDAO newsletterDAO;
    private ActivityLogService activityLogService;

    @Override
    public void init() throws ServletException {
        newsletterDAO = new NewsletterDAO();
        activityLogService = new ActivityLogService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkAdminRole(request, response)) return;

        String action = request.getParameter("action");
        try {
            if (action != null) {
                switch (action) {
                    case "delete":
                        String email = request.getParameter("email");
                        if (!isNullOrEmpty(email)) {
                            newsletterDAO.delete(email);
                            
                            // Log xóa newsletter
                            User currentUser = (User) request.getSession().getAttribute("user");
                            if (currentUser != null) {
                                activityLogService.logNewsletterDelete(currentUser, email, request);
                            }
                        }
                        break;
                    case "toggle":
                        String toggleEmail = request.getParameter("email");
                        if (!isNullOrEmpty(toggleEmail)) {
                            Newsletter n = newsletterDAO.findById(toggleEmail);
                            if (n != null) {
                                boolean newStatus = !n.isEnabled();
                                n.setEnabled(newStatus);
                                newsletterDAO.update(n);
                                
                                // Log toggle newsletter
                                User currentUser = (User) request.getSession().getAttribute("user");
                                if (currentUser != null) {
                                    if (newStatus) {
                                        activityLogService.logNewsletterEnable(currentUser, toggleEmail, request);
                                    } else {
                                        activityLogService.logNewsletterDisable(currentUser, toggleEmail, request);
                                    }
                                }
                            }
                        }
                        break;
                    case "searchAjax":
                        searchNewslettersAjax(request, response);
                        return;
                }
            }
            showList(request, response);
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy tham số tìm kiếm và filter
        String searchKeyword = request.getParameter("searchKeyword");
        String filterEnabledParam = request.getParameter("filterEnabled");
        
        // Parse filterEnabled
        Boolean filterEnabled = null;
        if (filterEnabledParam != null && !filterEnabledParam.trim().isEmpty()) {
            if ("true".equals(filterEnabledParam)) {
                filterEnabled = true;
            } else if ("false".equals(filterEnabledParam)) {
                filterEnabled = false;
            }
        }
        
        // Thống kê
        int totalNewsletters = newsletterDAO.countAll();
        int enabledCount = newsletterDAO.countByEnabled(true);
        int disabledCount = newsletterDAO.countByEnabled(false);
        
        // Xử lý tìm kiếm và lọc
        List<Newsletter> list;
        if ((searchKeyword != null && !searchKeyword.trim().isEmpty()) || filterEnabled != null) {
            list = newsletterDAO.searchAndFilter(searchKeyword, filterEnabled);
        } else {
            list = newsletterDAO.findAll();
        }
        
        // Set attributes
        request.setAttribute("newsletterList", list);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("filterEnabled", filterEnabledParam);
        request.setAttribute("totalNewsletters", totalNewsletters);
        request.setAttribute("enabledCount", enabledCount);
        request.setAttribute("disabledCount", disabledCount);
        
        forwardToAdminView(request, response, "Quản lý Newsletter", "/views/admin/newsletter_crud.jsp");
    }
    
    /**
     * Xử lý AJAX search request - trả về HTML fragment của danh sách newsletter
     */
    private void searchNewslettersAjax(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy tham số tìm kiếm và filter
        String searchKeyword = request.getParameter("searchKeyword");
        String filterEnabledParam = request.getParameter("filterEnabled");
        
        // Parse filterEnabled
        Boolean filterEnabled = null;
        if (filterEnabledParam != null && !filterEnabledParam.trim().isEmpty()) {
            if ("true".equals(filterEnabledParam)) {
                filterEnabled = true;
            } else if ("false".equals(filterEnabledParam)) {
                filterEnabled = false;
            }
        }
        
        // Xử lý tìm kiếm và lọc
        List<Newsletter> list;
        if ((searchKeyword != null && !searchKeyword.trim().isEmpty()) || filterEnabled != null) {
            list = newsletterDAO.searchAndFilter(searchKeyword, filterEnabled);
        } else {
            list = newsletterDAO.findAll();
        }
        
        // Set attributes để JSP fragment có thể render
        request.setAttribute("newsletterList", list);
        
        // Trả về HTML fragment
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        // Forward đến JSP fragment để render phần table
        request.getRequestDispatcher("/views/admin/newsletter_list_fragment.jsp").forward(request, response);
    }
}