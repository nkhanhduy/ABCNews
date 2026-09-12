package poly.com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.entity.Category;
import poly.com.entity.News;
import poly.com.service.CategoryService;
import poly.com.service.NewsService;
import poly.com.service.impl.CategoryServiceImpl;
import poly.com.service.impl.NewsServiceImpl;
import poly.com.util.ImagePathHelper;
import poly.com.util.NewsHelper;

/**
 * Controller xử lý trang chi tiết tin tức - hiển thị nội dung đầy đủ và các tin liên quan
 */
@WebServlet("/detail")
public class DetailController extends BaseController {
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
     * Hiển thị chi tiết tin tức, tăng lượt xem và lưu vào danh sách đã xem
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String newsId = request.getParameter("id");
            String contextPath = getContextPath(request);
            
            if (isNullOrEmpty(newsId)) {
                response.sendRedirect(contextPath + "/home");
                return;
            }

            News newsDetail = newsService.findById(newsId);
            
            if (newsDetail == null) {
                response.sendRedirect(contextPath + "/home");
                return;
            }
            
            // Chuẩn hóa đường dẫn ảnh cho tin chi tiết
            ImagePathHelper.normalizeImagePath(newsDetail, contextPath);
            
            newsService.incrementViewCount(newsId);
            
            // Cập nhật danh sách đã xem trong session
            HttpSession session = request.getSession();
            @SuppressWarnings("unchecked")
            List<String> viewedNewsIds = (List<String>) session.getAttribute("viewedNewsIds");
            if (viewedNewsIds == null) {
                viewedNewsIds = new ArrayList<>();
            }
            viewedNewsIds.remove(newsId);
            viewedNewsIds.add(0, newsId);
            if (viewedNewsIds.size() > 10) {
                // Tạo ArrayList mới từ subList để tránh lỗi UnsupportedOperationException
                viewedNewsIds = new ArrayList<>(viewedNewsIds.subList(0, 10));
            }
            session.setAttribute("viewedNewsIds", viewedNewsIds);
            
            List<Category> listCategories = categoryService.getAllCategories();
            List<News> listTop5HotNews = newsService.getTop5HotNews();
            List<News> listTop5NewestNews = newsService.getTop5Newest();
            List<News> listViewedNews = NewsHelper.getViewedNews(session, newsService, contextPath);
            List<News> listRelatedNews = newsService.getRelatedNews(newsDetail.getCategoryId(), newsId);
            
            // Chuẩn hóa đường dẫn ảnh cho tất cả danh sách
            ImagePathHelper.normalizeImagePaths(listTop5HotNews, contextPath);
            ImagePathHelper.normalizeImagePaths(listTop5NewestNews, contextPath);
            ImagePathHelper.normalizeImagePaths(listRelatedNews, contextPath);

            request.setAttribute("categories", listCategories);
            request.setAttribute("top5HotNews", listTop5HotNews);
            request.setAttribute("top5NewestNews", listTop5NewestNews);
            request.setAttribute("viewedNews", listViewedNews);
            request.setAttribute("news", newsDetail);
            request.setAttribute("relatedNews", listRelatedNews);
            
            forwardToPublicView(request, response, newsDetail.getTitle(), "/views/public/detail-content.jsp");

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }
}