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
 * Hỗ trợ định tuyến SEO friendly: /category/{slug}
 * Tương thích ngược với link cũ: /category?id={id} (HTTP 302 redirect sang slug tương ứng)
 */
@WebServlet(urlPatterns = {"/category", "/category/*"})
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
     * Hiển thị danh sách tin tức theo loại tin được chọn qua slug URL
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String pathInfo = request.getPathInfo();
            String contextPath = getContextPath(request);

            // 1. Tương thích ngược: Nếu truy cập dạng cũ /category?id=...
            String legacyId = request.getParameter("id");
            if ((pathInfo == null || "/".equals(pathInfo)) && !isNullOrEmpty(legacyId)) {
                Category cat = categoryService.findById(legacyId.trim());
                if (cat != null && !isNullOrEmpty(cat.getSlug())) {
                    // Chuyển hướng 302 sang URL chuẩn SEO dạng /category/{slug}
                    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                    response.setHeader("Location", contextPath + "/category/" + cat.getSlug());
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy danh mục yêu cầu");
                    return;
                }
            }

            // 2. Nếu truy cập /category mà không có slug hoặc id, chuyển hướng về trang chủ
            if (pathInfo == null || "/".equals(pathInfo)) {
                response.sendRedirect(contextPath + "/home");
                return;
            }

            // 3. Tách slug từ pathInfo: ví dụ "/cong-nghe" -> "cong-nghe"
            String slug = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
            if (slug.endsWith("/")) {
                slug = slug.substring(0, slug.length() - 1);
            }

            // 4. Bảo mật: Validate cấu trúc slug qua regex chuẩn, ngăn chặn path traversal hoặc ký tự lạ
            if (!poly.com.util.SlugUtil.isValidSlug(slug)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Đường dẫn chuyên mục không hợp lệ");
                return;
            }

            // 5. Truy vấn chuyên mục theo slug
            Category category = categoryService.findBySlug(slug);
            if (category == null) {
                // Yêu cầu: Nếu slug không tồn tại, trả về HTTP 404, KHÔNG redirect trang chủ im lặng
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Chuyên mục tin tức không tồn tại");
                return;
            }

            // 6. Tải dữ liệu hiển thị (tin tức theo categoryId, tin nóng, tin mới, v.v.)
            List<Category> listCategories = categoryService.getAllCategories();
            List<News> listTop5HotNews = newsService.getTop5HotNews();
            List<News> listTop5NewestNews = newsService.getTop5Newest();
            
            // Chuẩn hóa đường dẫn ảnh
            ImagePathHelper.normalizeImagePaths(listTop5HotNews, contextPath);
            ImagePathHelper.normalizeImagePaths(listTop5NewestNews, contextPath);
            
            List<News> listViewedNews = NewsHelper.getViewedNews(request.getSession(), newsService, contextPath);
            
            // Truy vấn danh sách bài viết thuộc chuyên mục qua CategoryId
            List<News> listNewsByCategory = newsService.findByCategoryId(category.getId());
            ImagePathHelper.normalizeImagePaths(listNewsByCategory, contextPath);
            
            request.setAttribute("categories", listCategories);
            request.setAttribute("currentCategory", category);
            request.setAttribute("category", category);
            request.setAttribute("categoryName", category.getName());
            request.setAttribute("categoryNews", listNewsByCategory);
            request.setAttribute("top5HotNews", listTop5HotNews);
            request.setAttribute("top5NewestNews", listTop5NewestNews);
            request.setAttribute("viewedNews", listViewedNews);
            
            String pageTitle = category.getName();
            forwardToPublicView(request, response, pageTitle, "/views/public/category-content.jsp");

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }
}