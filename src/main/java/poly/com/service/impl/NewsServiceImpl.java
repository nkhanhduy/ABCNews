package poly.com.service.impl;

import java.util.Date;
import java.util.List;

import poly.com.dao.NewsDAO;
import poly.com.dao.NewsletterDAO;
import poly.com.entity.News;
import poly.com.entity.Newsletter;
import poly.com.entity.User;
import poly.com.service.NewsService;
import poly.com.util.EmailService;

/**
 * Service Implementation quản lý tin tức (NewsServiceImpl)
 * 
 * @author ABCNews Development Team
 */
public class NewsServiceImpl implements NewsService {

    private final NewsDAO newsDAO;
    private final NewsletterDAO newsletterDAO;
    private final EmailService emailService;

    public NewsServiceImpl() {
        this.newsDAO = new NewsDAO();
        this.newsletterDAO = new NewsletterDAO();
        this.emailService = new EmailService();
    }

    public NewsServiceImpl(NewsDAO newsDAO, NewsletterDAO newsletterDAO, EmailService emailService) {
        this.newsDAO = newsDAO;
        this.newsletterDAO = newsletterDAO;
        this.emailService = emailService;
    }

    @Override
    public News findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return newsDAO.findById(id.trim());
    }

    @Override
    public List<News> getHomeNews() {
        return newsDAO.findHomeNews();
    }

    @Override
    public List<News> getTop5HotNews() {
        return newsDAO.findTop5HotNews();
    }

    @Override
    public List<News> getTop5Newest() {
        return newsDAO.findTop5Newest();
    }

    @Override
    public List<News> findByCategoryId(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return newsDAO.findByCategoryId(categoryId.trim());
    }

    @Override
    public List<News> getRelatedNews(String categoryId, String currentNewsId) {
        return newsDAO.findRelatedNews(categoryId, currentNewsId);
    }

    @Override
    public List<News> searchAndFilter(String keyword, String categoryId, String authorId, String sortBy, String sortOrder) {
        return newsDAO.searchAndFilter(keyword, categoryId, authorId, sortBy, sortOrder);
    }

    @Override
    public List<News> findByAuthor(String authorId) {
        return newsDAO.findByAuthor(authorId);
    }

    @Override
    public boolean createNews(News news, boolean sendNewsletter, String baseUrl, String servletContextPath, User author) {
        if (news == null || author == null) {
            return false;
        }

        // Gán tác giả và ngày đăng mặc định
        news.setAuthor(author.getId());
        if (news.getPostedDate() == null) {
            news.setPostedDate(new Date());
        }

        newsDAO.insert(news);

        // Gửi newsletter nếu có tùy chọn
        if (sendNewsletter) {
            try {
                List<Newsletter> subscribers = newsletterDAO.findByEnabled(true);
                if (subscribers != null && !subscribers.isEmpty()) {
                    List<String> emails = subscribers.stream().map(Newsletter::getEmail).toList();
                    new Thread(() -> {
                        try {
                            emailService.sendBulkNewsletter(emails, news, baseUrl, servletContextPath);
                        } catch (Exception e) {
                            System.err.println("[LỖI] Không thể gửi bulk newsletter: " + e.getMessage());
                        }
                    }).start();
                }
            } catch (Exception e) {
                System.err.println("[CẢNH BÁO] Lỗi lấy danh sách subscribers: " + e.getMessage());
            }
        }

        return true;
    }

    @Override
    public boolean updateNews(News news, User currentUser) {
        if (news == null || currentUser == null) {
            return false;
        }

        News existing = newsDAO.findById(news.getId());
        if (existing == null) {
            return false;
        }

        // Phóng viên chỉ được sửa tin của chính mình
        if (!currentUser.isRole() && !existing.getAuthor().equalsIgnoreCase(currentUser.getId())) {
            throw new SecurityException("Phóng viên không có quyền chỉnh sửa bài viết của người khác.");
        }

        newsDAO.update(news);
        return true;
    }

    @Override
    public boolean deleteNews(String id, User currentUser) {
        if (id == null || currentUser == null) {
            return false;
        }

        News existing = newsDAO.findById(id);
        if (existing == null) {
            return false;
        }

        // Phóng viên chỉ được xóa bài viết của chính mình
        if (!currentUser.isRole() && !existing.getAuthor().equalsIgnoreCase(currentUser.getId())) {
            throw new SecurityException("Phóng viên không có quyền xóa bài viết của người khác.");
        }

        newsDAO.delete(id);
        return true;
    }

    @Override
    public void incrementViewCount(String id) {
        if (id != null && !id.trim().isEmpty()) {
            newsDAO.incrementViewCount(id.trim());
        }
    }

    @Override
    public int countAll() {
        return newsDAO.countAll();
    }

    @Override
    public int countByAuthor(String authorId) {
        return newsDAO.countByAuthor(authorId);
    }
}
