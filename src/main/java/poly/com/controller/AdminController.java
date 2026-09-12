package poly.com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;

import poly.com.dao.CategoryDAO;
import poly.com.dao.NewsDAO;
import poly.com.dao.NewsletterDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.Category;
import poly.com.entity.News;
import poly.com.entity.User;
import poly.com.util.ImagePathHelper;
import com.google.gson.Gson;

/**
 * Servlet implementation class AdminController
 * Hiển thị trang dashboard - khác nhau cho Admin và Reporter
 */
@WebServlet("/admin/dashboard")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private NewsDAO newsDAO;
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;
    private NewsletterDAO newsletterDAO;

    @Override
    public void init() throws ServletException {
        newsDAO = new NewsDAO();
        userDAO = new UserDAO();
        categoryDAO = new CategoryDAO();
        newsletterDAO = new NewsletterDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String contextPath = request.getContextPath();
        
        // Phân biệt Admin và Reporter
        if (user.isRole()) {
            // ADMIN DASHBOARD
            showAdminDashboard(request, response, contextPath);
        } else {
            // REPORTER DASHBOARD
            showReporterDashboard(request, response, contextPath, user);
        }
    }
    
    /**
     * Hiển thị dashboard cho Admin
     */
    private void showAdminDashboard(HttpServletRequest request, HttpServletResponse response, String contextPath) 
            throws ServletException, IOException {
        
        // 1. Thống kê tổng quan
        int totalNews = newsDAO.countAll();
        int totalUsers = userDAO.countAll();
        int totalCategories = categoryDAO.countAll();
        int totalNewsletters = newsletterDAO.countAll();
        
        // 2. Thống kê chi tiết
        int totalAdmins = userDAO.countByRole(true);
        int totalReporters = userDAO.countByRole(false);
        
        // Thống kê Admin theo trạng thái
        int adminsEnabled = userDAO.countByRoleAndEnabled(true, true);
        int adminsDisabled = userDAO.countByRoleAndEnabled(true, false);
        
        // Thống kê Reporter theo trạng thái
        int reportersEnabled = userDAO.countByRoleAndEnabled(false, true);
        int reportersDisabled = userDAO.countByRoleAndEnabled(false, false);
        
        List<News> top5HotNews = newsDAO.findTop5HotNews();
        List<News> recentNews = newsDAO.findRecentNews(7);
        List<Category> categories = categoryDAO.findAll();
        
        // Tạo Map số lượng tin theo category
        Map<String, Integer> newsCountByCategory = new HashMap<>();
        for (Category cat : categories) {
            int count = newsDAO.countByCategoryId(cat.getId());
            newsCountByCategory.put(cat.getId(), count);
        }
        
        // Chuẩn hóa đường dẫn ảnh
        ImagePathHelper.normalizeImagePaths(top5HotNews, contextPath);
        ImagePathHelper.normalizeImagePaths(recentNews, contextPath);
        
        // Chuẩn bị dữ liệu cho biểu đồ Chart.js
        List<String> catLabels = new ArrayList<>();
        List<Integer> catData = new ArrayList<>();
        for (Category cat : categories) {
            catLabels.add(cat.getName());
            catData.add(newsCountByCategory.getOrDefault(cat.getId(), 0));
        }

        List<String> newsLabels = new ArrayList<>();
        List<Integer> newsViews = new ArrayList<>();
        for (News n : top5HotNews) {
            String shortTitle = n.getTitle();
            if (shortTitle != null && shortTitle.length() > 25) {
                shortTitle = shortTitle.substring(0, 22) + "...";
            }
            newsLabels.add(shortTitle);
            newsViews.add(n.getViewCount());
        }

        Gson gson = new Gson();
        request.setAttribute("chartCategoryLabels", gson.toJson(catLabels));
        request.setAttribute("chartCategoryData", gson.toJson(catData));
        request.setAttribute("chartNewsLabels", gson.toJson(newsLabels));
        request.setAttribute("chartNewsViews", gson.toJson(newsViews));

        // 3. Set attributes
        request.setAttribute("totalNews", totalNews);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalCategories", totalCategories);
        request.setAttribute("totalNewsletters", totalNewsletters);
        request.setAttribute("totalAdmins", totalAdmins);
        request.setAttribute("totalReporters", totalReporters);
        request.setAttribute("adminsEnabled", adminsEnabled);
        request.setAttribute("adminsDisabled", adminsDisabled);
        request.setAttribute("reportersEnabled", reportersEnabled);
        request.setAttribute("reportersDisabled", reportersDisabled);
        request.setAttribute("top5HotNews", top5HotNews);
        request.setAttribute("recentNews", recentNews);
        request.setAttribute("categories", categories);
        request.setAttribute("newsCountByCategory", newsCountByCategory);
        
        // Đặt tiêu đề
        request.setAttribute("pageTitle", "Tổng quan");
        // Chỉ định file nội dung
        request.setAttribute("view", "/views/admin/dashboard.jsp");
        
        // Forward sang admin_layout.jsp
        request.getRequestDispatcher("/views/admin/admin_layout.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị dashboard cho Reporter
     */
    private void showReporterDashboard(HttpServletRequest request, HttpServletResponse response, String contextPath, User user) 
            throws ServletException, IOException {
        
        String authorId = user.getId();
        
        // 1. Thống kê tổng quan - chỉ tin của phóng viên này
        List<News> myNewsList = newsDAO.findByAuthor(authorId);
        int myTotalNews = myNewsList.size();
        
        // Tính tổng lượt xem
        int totalViews = 0;
        int newsIn7Days = 0;
        int newsOnHome = 0;
        for (News news : myNewsList) {
            totalViews += news.getViewCount();
            // Đếm tin trong 7 ngày qua
            long daysDiff = (System.currentTimeMillis() - news.getPostedDate().getTime()) / (1000 * 60 * 60 * 24);
            if (daysDiff <= 7) {
                newsIn7Days++;
            }
            // Đếm tin trên trang nhất
            if (news.isHome()) {
                newsOnHome++;
            }
        }
        
        // 2. Top 5 tin xem nhiều nhất của tôi
        List<News> myTop5HotNews = new ArrayList<>();
        myNewsList.sort((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()));
        for (int i = 0; i < Math.min(5, myNewsList.size()); i++) {
            myTop5HotNews.add(myNewsList.get(i));
        }
        
        // 3. Tin mới nhất của tôi (7 ngày qua)
        List<News> myRecentNews = new ArrayList<>();
        for (News news : myNewsList) {
            long daysDiff = (System.currentTimeMillis() - news.getPostedDate().getTime()) / (1000 * 60 * 60 * 24);
            if (daysDiff <= 7) {
                myRecentNews.add(news);
            }
        }
        myRecentNews.sort((a, b) -> b.getPostedDate().compareTo(a.getPostedDate()));
        if (myRecentNews.size() > 10) {
            myRecentNews = myRecentNews.subList(0, 10);
        }
        
        // 4. Phân bổ tin theo loại của tôi
        List<Category> categories = categoryDAO.findAll();
        Map<String, Integer> myNewsCountByCategory = new HashMap<>();
        for (Category cat : categories) {
            int count = 0;
            for (News news : myNewsList) {
                if (cat.getId().equals(news.getCategoryId())) {
                    count++;
                }
            }
            myNewsCountByCategory.put(cat.getId(), count);
        }
        
        // Chuẩn hóa đường dẫn ảnh
        ImagePathHelper.normalizeImagePaths(myTop5HotNews, contextPath);
        ImagePathHelper.normalizeImagePaths(myRecentNews, contextPath);
        
        // 5. Tính tỷ lệ tin trên trang nhất
        double homeNewsPercentage = 0.0;
        if (myTotalNews > 0) {
            homeNewsPercentage = (double) newsOnHome / myTotalNews * 100;
        }
        
        // Chuẩn bị dữ liệu biểu đồ cho Reporter
        List<String> repCatLabels = new ArrayList<>();
        List<Integer> repCatData = new ArrayList<>();
        for (Category cat : categories) {
            repCatLabels.add(cat.getName());
            repCatData.add(myNewsCountByCategory.getOrDefault(cat.getId(), 0));
        }

        List<String> repNewsLabels = new ArrayList<>();
        List<Integer> repNewsViews = new ArrayList<>();
        for (News n : myTop5HotNews) {
            String shortTitle = n.getTitle();
            if (shortTitle != null && shortTitle.length() > 25) {
                shortTitle = shortTitle.substring(0, 22) + "...";
            }
            repNewsLabels.add(shortTitle);
            repNewsViews.add(n.getViewCount());
        }

        Gson repGson = new Gson();
        request.setAttribute("chartCategoryLabels", repGson.toJson(repCatLabels));
        request.setAttribute("chartCategoryData", repGson.toJson(repCatData));
        request.setAttribute("chartNewsLabels", repGson.toJson(repNewsLabels));
        request.setAttribute("chartNewsViews", repGson.toJson(repNewsViews));

        // 6. Set attributes
        request.setAttribute("myTotalNews", myTotalNews);
        request.setAttribute("totalViews", totalViews);
        request.setAttribute("newsIn7Days", newsIn7Days);
        request.setAttribute("newsOnHome", newsOnHome);
        request.setAttribute("homeNewsPercentage", homeNewsPercentage);
        request.setAttribute("myTop5HotNews", myTop5HotNews);
        request.setAttribute("myRecentNews", myRecentNews);
        request.setAttribute("categories", categories);
        request.setAttribute("myNewsCountByCategory", myNewsCountByCategory);
        
        // Đặt tiêu đề
        request.setAttribute("pageTitle", "Tổng quan");
        // Chỉ định file nội dung cho Reporter
        request.setAttribute("view", "/views/admin/dashboard_reporter.jsp");
        
        // Forward sang admin_layout.jsp
        request.getRequestDispatcher("/views/admin/admin_layout.jsp").forward(request, response);
    }
}