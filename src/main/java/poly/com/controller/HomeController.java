package poly.com.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.CategoryDAO;
import poly.com.dao.NewsDAO;
import poly.com.entity.Category;
import poly.com.entity.News;
import poly.com.util.ImagePathHelper;
import poly.com.util.NewsHelper;

/**
 * Controller xử lý trang chủ - hiển thị tin tức trang nhất và các widget sidebar
 */
@WebServlet("/home")
public class HomeController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private CategoryDAO categoryDAO;
    private NewsDAO newsDAO;

    /**
     * Khởi tạo các DAO khi servlet được load
     */
    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
        newsDAO = new NewsDAO();
    }

    /**
     * Hiển thị trang chủ với danh sách tin trang nhất và các widget sidebar
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String contextPath = getContextPath(request);
            
            List<Category> listCategories = categoryDAO.findAll();
            List<News> listTop5HotNews = newsDAO.findTop5HotNews();
            List<News> listTop5NewestNews = newsDAO.findTop5Newest();
            List<News> listHomeNews = newsDAO.findHomeNews();
            
            // Chuẩn hóa đường dẫn ảnh cho tất cả danh sách
            ImagePathHelper.normalizeImagePaths(listTop5HotNews, contextPath);
            ImagePathHelper.normalizeImagePaths(listTop5NewestNews, contextPath);
            ImagePathHelper.normalizeImagePaths(listHomeNews, contextPath);
            
            List<News> listViewedNews = NewsHelper.getViewedNews(request.getSession(), newsDAO, contextPath);

            request.setAttribute("categories", listCategories);
            request.setAttribute("top5HotNews", listTop5HotNews);
            request.setAttribute("top5NewestNews", listTop5NewestNews);
            request.setAttribute("viewedNews", listViewedNews);
            request.setAttribute("homeNews", listHomeNews);
            
            forwardToPublicView(request, response, "Trang chủ", "/views/public/home-content.jsp");

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    /**
     * Xử lý POST request - chuyển về doGet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}