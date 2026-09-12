package poly.com.service;

import java.util.List;
import poly.com.entity.Newsletter;

/**
 * Service Interface quản lý đăng ký bản tin (NewsletterService)
 * 
 * @author ABCNews Development Team
 */
public interface NewsletterService {

    boolean subscribe(String email);

    List<Newsletter> getAllSubscribers();

    boolean toggleSubscriber(String email);

    boolean deleteSubscriber(String email);

    int countAll();
}
