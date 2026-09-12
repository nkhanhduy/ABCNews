package poly.com.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.CategoryDAO;
import poly.com.dao.NewsDAO;
import poly.com.dao.NewsletterDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.Category;
import poly.com.entity.News;
import poly.com.entity.Newsletter;
import poly.com.entity.User;
import poly.com.service.ActivityLogService;
import poly.com.util.export.ExportFormat;
import poly.com.util.export.ExportService;

/**
 * Controller xử lý export dữ liệu ra CSV, Excel, PDF
 * Chỉ Admin mới có quyền export (được bảo vệ bởi AuthFilter)
 * 
 * URL format: /admin/export?type=news&format=excel
 * 
 * Parameters:
 * - type: news | users | categories | newsletters
 * - format: csv | excel | pdf
 */
@WebServlet("/admin/export")
public class ExportController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private NewsDAO newsDAO = new NewsDAO();
    private UserDAO userDAO = new UserDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private NewsletterDAO newsletterDAO = new NewsletterDAO();
    private ActivityLogService activityLogService = new ActivityLogService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        User currentUser = checkLogin(request, response);
        if (currentUser == null) {
            return;
        }
        
        // Lấy danh sách types (có thể chọn nhiều)
        String[] types = request.getParameterValues("type");
        String formatParam = request.getParameter("format");
        String timeRange = request.getParameter("timeRange"); // all, 3days, 7days, 30days
        
        // Validate parameters
        if (types == null || types.length == 0 || isNullOrEmpty(formatParam)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: type and format");
            return;
        }
        
        try {
            ExportFormat format = ExportFormat.fromString(formatParam);
            boolean isAdmin = currentUser.isRole();
            
            // Tính toán date range từ timeRange parameter
            Date startDate = calculateStartDate(timeRange);
            
            // Nếu chọn nhiều module → gộp vào 1 file
            if (types.length > 1) {
                exportMultiple(types, format, response, currentUser, isAdmin, startDate);
            } else {
                // Chỉ chọn 1 module → export đơn
                String type = types[0];
                switch (type.toLowerCase()) {
                    case "news":
                        exportNews(format, response, currentUser, startDate, request);
                        break;
                    case "users":
                    case "categories":
                    case "newsletters":
                        if (!isAdmin) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                                "Bạn không có quyền xuất dữ liệu này. Chỉ Quản trị viên mới có quyền này.");
                            return;
                        }
                        if (type.equals("users")) {
                            exportUsers(format, response, startDate, currentUser, request);
                        } else if (type.equals("categories")) {
                            exportCategories(format, response);
                        } else {
                            exportNewsletters(format, response, startDate);
                        }
                        break;
                    default:
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid type: " + type);
                }
            }
            
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] ExportController: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Xuất dữ liệu thất bại. Vui lòng thử lại sau.");
        }
    }
    
    /**
     * Tính toán start date dựa trên time range
     */
    private Date calculateStartDate(String timeRange) {
        if (timeRange == null || timeRange.equals("all")) {
            return null; // Null = lấy tất cả
        }
        
        Calendar cal = Calendar.getInstance();
        
        switch (timeRange) {
            case "3days":
                cal.add(Calendar.DAY_OF_YEAR, -3);
                break;
            case "7days":
                cal.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case "30days":
                cal.add(Calendar.DAY_OF_YEAR, -30);
                break;
            default:
                return null; // Invalid value = lấy tất cả
        }
        
        // Set về đầu ngày (00:00:00)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTime();
    }
    
    /**
     * Filter danh sách theo date (chỉ áp dụng cho News, User, Newsletter có date field)
     */
    private List<News> filterNewsByDate(List<News> newsList, Date startDate) {
        if (startDate == null) return newsList;
        
        return newsList.stream()
            .filter(news -> news.getPostedDate() != null && news.getPostedDate().after(startDate))
            .collect(Collectors.toList());
    }
    
    private List<User> filterUsersByDate(List<User> usersList, Date startDate) {
        if (startDate == null) return usersList;
        
        // User không có created date, nên không filter
        return usersList;
    }
    
    private List<Newsletter> filterNewslettersByDate(List<Newsletter> newslettersList, Date startDate) {
        if (startDate == null) return newslettersList;
        
        return newslettersList.stream()
            .filter(nl -> nl.getSubscribedDate() != null && nl.getSubscribedDate().after(startDate))
            .collect(Collectors.toList());
    }
    
    /**
     * Export nhiều module gộp vào 1 file
     */
    private void exportMultiple(String[] types, ExportFormat format, HttpServletResponse response, 
                                User currentUser, boolean isAdmin, Date startDate) throws IOException {
        
        // Collect data từ các module đã chọn
        List<News> newsList = null;
        List<User> usersList = null;
        List<Category> categoriesList = null;
        List<Newsletter> newslettersList = null;
        
        for (String type : types) {
            switch (type.toLowerCase()) {
                case "news":
                    if (currentUser.isRole()) {
                        newsList = newsDAO.findAll();
                    } else {
                        newsList = newsDAO.findByAuthor(currentUser.getId());
                    }
                    newsList = filterNewsByDate(newsList, startDate);
                    break;
                case "users":
                    if (isAdmin) {
                        usersList = userDAO.findAll();
                        usersList = filterUsersByDate(usersList, startDate);
                    }
                    break;
                case "categories":
                    if (isAdmin) {
                        categoriesList = categoryDAO.findAll();
                    }
                    break;
                case "newsletters":
                    if (isAdmin) {
                        newslettersList = newsletterDAO.findAll();
                        newslettersList = filterNewslettersByDate(newslettersList, startDate);
                    }
                    break;
            }
        }
        
        // Gọi ExportService để gộp và export
        ExportService.exportMultiple(newsList, usersList, categoriesList, newslettersList, format, response);
    }
    
    /**
     * Export danh sách News
     * - Admin: Export TẤT CẢ tin tức
     * - Reporter: CHỈ export tin tức của chính họ
     */
    private void exportNews(ExportFormat format, HttpServletResponse response, User currentUser, Date startDate, HttpServletRequest request) throws IOException {
        List<News> newsList;
        
        if (currentUser.isRole()) {
            // Admin: Export tất cả
            newsList = newsDAO.findAll();
        } else {
            // Reporter: Chỉ export tin của mình
            newsList = newsDAO.findByAuthor(currentUser.getId());
        }
        
        // Filter theo date nếu có
        newsList = filterNewsByDate(newsList, startDate);
        
        ExportService.exportNews(newsList, format, response);
        
        // Log export
        activityLogService.logExport(currentUser, "NEWS", format.name(), request);
    }
    
    /**
     * Export danh sách Users
     */
    private void exportUsers(ExportFormat format, HttpServletResponse response, Date startDate, User currentUser, HttpServletRequest request) throws IOException {
        List<User> users = userDAO.findAll();
        users = filterUsersByDate(users, startDate);
        ExportService.exportUsers(users, format, response);
        
        // Log export
        activityLogService.logExport(currentUser, "USER", format.name(), request);
    }
    
    /**
     * Export danh sách Categories
     */
    private void exportCategories(ExportFormat format, HttpServletResponse response) throws IOException {
        List<Category> categories = categoryDAO.findAll();
        ExportService.exportCategories(categories, format, response);
    }
    
    /**
     * Export danh sách Newsletters
     */
    private void exportNewsletters(ExportFormat format, HttpServletResponse response, Date startDate) throws IOException {
        List<Newsletter> newsletters = newsletterDAO.findAll();
        newsletters = filterNewslettersByDate(newsletters, startDate);
        ExportService.exportNewsletters(newsletters, format, response);
    }
}

