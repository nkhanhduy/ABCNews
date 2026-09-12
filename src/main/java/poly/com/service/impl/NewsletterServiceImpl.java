package poly.com.service.impl;

import java.util.Date;
import java.util.List;

import poly.com.dao.NewsletterDAO;
import poly.com.entity.Newsletter;
import poly.com.service.NewsletterService;

/**
 * Service Implementation quản lý Newsletter (NewsletterServiceImpl)
 * 
 * @author Nguyen Duy Khanh
 */
public class NewsletterServiceImpl implements NewsletterService {

    private final NewsletterDAO newsletterDAO;

    public NewsletterServiceImpl() {
        this.newsletterDAO = new NewsletterDAO();
    }

    public NewsletterServiceImpl(NewsletterDAO newsletterDAO) {
        this.newsletterDAO = newsletterDAO;
    }

    @Override
    public boolean subscribe(String email) {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return false;
        }
        String cleanEmail = email.trim();
        Newsletter existing = newsletterDAO.findById(cleanEmail);
        if (existing != null) {
            // Nếu đã từng đăng ký nhưng bị tắt thì kích hoạt lại
            if (!existing.isEnabled()) {
                existing.setEnabled(true);
                newsletterDAO.update(existing);
            }
            return true;
        }

        Newsletter sub = new Newsletter(cleanEmail, true, new Date());
        newsletterDAO.insert(sub);
        return true;
    }

    @Override
    public List<Newsletter> getAllSubscribers() {
        return newsletterDAO.findAll();
    }

    @Override
    public boolean toggleSubscriber(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Newsletter sub = newsletterDAO.findById(email.trim());
        if (sub == null) {
            return false;
        }
        sub.setEnabled(!sub.isEnabled());
        newsletterDAO.update(sub);
        return true;
    }

    @Override
    public boolean deleteSubscriber(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        newsletterDAO.delete(email.trim());
        return true;
    }

    @Override
    public int countAll() {
        return newsletterDAO.countAll();
    }
}
