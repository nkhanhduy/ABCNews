package poly.com.controller;

import java.io.IOException;
import java.util.Date;

// BẮT BUỘC dùng jakarta
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.NewsletterDAO;
import poly.com.entity.Newsletter;

/**
 * Servlet implementation class NewsletterController
 * Xử lý form đăng ký nhận bản tin từ sidebar
 */
@WebServlet("/newsletter")
public class NewsletterController extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    private NewsletterDAO newsletterDAO;

    @Override
    public void init() throws ServletException {
        newsletterDAO = new NewsletterDAO();
    }

    /**
     * Xử lý khi người dùng gửi form (method="post")
     * Kiểm tra email đã tồn tại:
     * - Nếu có: cập nhật enabled = true và SubscribedDate mới
     * - Nếu chưa: insert mới
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Lấy email từ form
            request.setCharacterEncoding("UTF-8");
            String email = request.getParameter("email");

            if (email != null && !email.trim().isEmpty()) {
                email = email.trim();
                
                // Kiểm tra email đã tồn tại chưa
                Newsletter existing = newsletterDAO.findById(email);
                
                if (existing != null) {
                    // Email đã tồn tại → cập nhật enabled = true và SubscribedDate mới
                    existing.setEnabled(true);
                    existing.setSubscribedDate(new Date());
                    newsletterDAO.update(existing);
                    
                    // Thông báo: Email đã được kích hoạt lại
                    request.getSession().setAttribute("newsletterMessage", "Email của bạn đã được kích hoạt lại nhận bản tin!");
                } else {
                    // Email mới → insert
                    Newsletter entity = new Newsletter(email, true, new Date());
                    newsletterDAO.insert(entity);
                    
                    // Thông báo: Đăng ký thành công
                    request.getSession().setAttribute("newsletterMessage", "Đăng ký nhận bản tin thành công! Cảm ơn bạn đã quan tâm.");
                }
            }
            
            // Quay trở lại trang chủ (hoặc trang trước đó)
            // Lấy trang mà người dùng vừa gửi form
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer); // Quay lại trang vừa đứng
            } else {
                response.sendRedirect(request.getContextPath() + "/home"); // Mặc định về trang chủ
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, chuyển sang trang lỗi
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    /**
     * Nếu ai đó truy cập /newsletter bằng GET, cứ đưa họ về trang chủ
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}