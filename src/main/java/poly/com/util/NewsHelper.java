package poly.com.util;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import poly.com.dao.NewsDAO;
import poly.com.entity.News;

/**
 * Lớp tiện ích (Helper Class) để xử lý các logic liên quan đến News (Tin tức)
 * 
 * Lớp này cung cấp các phương thức static để:
 * - Lấy danh sách tin đã xem từ session
 * - Xử lý và chuẩn hóa dữ liệu News
 * 
 * Giúp giảm code trùng lặp trong các Controller liên quan đến News.
 * 
 * @author ABCNews Development Team
 * @version 1.0
 */
public class NewsHelper {
    
    /**
     * Lấy danh sách tin tức đã xem từ session và trả về đầy đủ thông tin
     * 
     * Phương thức này:
     * 1. Lấy danh sách ID của các tin đã xem từ session (viewedNewsIds)
     * 2. Query database để lấy thông tin đầy đủ của từng tin
     * 3. Chuẩn hóa đường dẫn ảnh cho mỗi tin
     * 4. Giới hạn tối đa 5 tin để hiển thị trong sidebar
     * 
     * Danh sách ID trong session đã được sắp xếp theo thứ tự mới nhất trước,
     * nên chỉ cần lấy từ đầu danh sách.
     * 
     * @param session HttpSession chứa attribute "viewedNewsIds" (List<String>)
     *                Danh sách này được cập nhật mỗi khi user xem một tin mới
     * @param newsDAO NewsDAO để query database lấy thông tin tin tức
     * @param contextPath Context path của ứng dụng (ví dụ: "/ABCNews" hoặc "")
     *                   Dùng để chuẩn hóa đường dẫn ảnh
     * @return Danh sách News đã xem (theo thứ tự mới nhất trước, tối đa 5 tin)
     *         Mỗi News đã được chuẩn hóa đường dẫn ảnh
     */
    @SuppressWarnings("unchecked")
    public static List<News> getViewedNews(HttpSession session, NewsDAO newsDAO, String contextPath) {
        List<News> listViewedNews = new ArrayList<>();
        
        // Lấy danh sách ID các tin đã xem từ session
        List<String> viewedNewsIds = (List<String>) session.getAttribute("viewedNewsIds");
        
        if (viewedNewsIds != null && !viewedNewsIds.isEmpty()) {
            // Danh sách đã có thứ tự mới nhất ở đầu, chỉ cần lấy từ đầu
            for (String newsId : viewedNewsIds) {
                // Query database để lấy thông tin đầy đủ của tin
                News news = newsDAO.findById(newsId);
                if (news != null) {
                    // Chuẩn hóa đường dẫn ảnh để đảm bảo hiển thị đúng
                    ImagePathHelper.normalizeImagePath(news, contextPath);
                    listViewedNews.add(news);
                }
            }
            // Giới hạn tối đa 5 tin để hiển thị trong sidebar (không làm quá dài)
            // Tạo ArrayList mới từ subList để tránh UnsupportedOperationException
            if (listViewedNews.size() > 5) {
                listViewedNews = new ArrayList<>(listViewedNews.subList(0, 5));
            }
        }
        return listViewedNews;
    }
}
