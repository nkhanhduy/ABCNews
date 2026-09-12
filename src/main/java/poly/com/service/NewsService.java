package poly.com.service;

import java.util.List;
import poly.com.entity.News;
import poly.com.entity.User;

/**
 * Service Interface quản lý tin tức (NewsService)
 * 
 * @author ABCNews Development Team
 */
public interface NewsService {

    News findById(String id);

    List<News> getHomeNews();

    List<News> getTop5HotNews();

    List<News> getTop5Newest();

    List<News> findByCategoryId(String categoryId);

    List<News> getRelatedNews(String categoryId, String currentNewsId);

    List<News> searchAndFilter(String keyword, String categoryId, String authorId, String sortBy, String sortOrder);

    List<News> findByAuthor(String authorId);

    boolean createNews(News news, boolean sendNewsletter, String baseUrl, String servletContextPath, User author);

    boolean updateNews(News news, User currentUser);

    boolean deleteNews(String id, User currentUser);

    void incrementViewCount(String id);

    int countAll();

    int countByAuthor(String authorId);
}
