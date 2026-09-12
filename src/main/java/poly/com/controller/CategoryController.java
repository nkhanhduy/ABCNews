package poly.com.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.entity.Category;
import poly.com.entity.News;
import poly.com.service.CategoryService;
import poly.com.service.NewsService;
import poly.com.service.impl.CategoryServiceImpl;
import poly.com.service.impl.NewsServiceImpl;
import poly.com.util.ImagePathHelper;
import poly.com.util.NewsHelper;

/**
 * Controller xử lý trang danh sách tin theo loại tin (category)
 */
@WebServlet("/category")
public class CategoryController extends BaseController {
    private static final long serialVersionUID = 1L;
       
    private CategoryService categoryService;
    private NewsService newsService;

    /**
     * Khởi tạo các Service khi servlet được load
     */
    @Override
    public void init() throws ServletException {
        categoryService = new CategoryServiceImpl();
        newsService = new NewsServiceImpl();
    }

    /**
     * Hiển thị danh sách tin tức theo loại tin được chọn
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String categoryId = request.getParameter("id");
            String contextPath = getContextPath(request);
            
            if (isNullOrEmpty(categoryId)) {
                response.sendRedirect(contextPath + "/home");
                return;
            }

            List<Category> listCategories = categoryService.getAllCategories();
            List<News> listTop5HotNews = newsService.getTop5HotNews();
            List<News> listTop5NewestNews = newsService.getTop5Newest();
            
            // Chuẩn hóa đường dẫn ảnh
            ImagePathHelper.normalizeImagePaths(listTop5HotNews, contextPath);
            ImagePathHelper.normalizeImagePaths(listTop5NewestNews, contextPath);
            
            List<News> listViewedNews = NewsHelper.getViewedNews(request.getSession(), newsService, contextPath);
            
            Category category = categoryService.findById(categoryId);
            List<News> listNewsByCategory = newsService.findByCategoryId(categoryId);
            
            // Chuẩn hóa đường dẫn ảnh cho danh sách tin theo category
            ImagePathHelper.normalizeImagePaths(listNewsByCategory, contextPath);
            
            request.setAttribute("categories", listCategories);
            request.setAttribute("top5HotNews", listTop5HotNews);
            request.setAttribute("top5NewestNews", listTop5NewestNews);
            request.setAttribute("viewedNews", listViewedNews);
            request.setAttribute("categoryNews", listNewsByCategory);
            request.setAttribute("categoryName", (category != null) ? category.getName() : "Không tìm thấy");
            
            String pageTitle = (category != null) ? category.getName() : "Danh sách tin";
            forwardToPublicView(request, response, pageTitle, "/views/public/category-content.jsp");

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }
}