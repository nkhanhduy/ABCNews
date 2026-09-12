package poly.com.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.CommentDAO;
import poly.com.entity.Comment;
import poly.com.util.XssSanitizer;

/**
 * Controller xử lý gửi bình luận từ độc giả ở trang chi tiết bài viết
 * Tích hợp XSS Sanitization và Cooldown chống spam
 * 
 * @author Nguyen Duy Khanh
 */
@WebServlet("/comment")
public class CommentController extends BaseController {
    private static final long serialVersionUID = 1L;

    private CommentDAO commentDAO;

    @Override
    public void init() throws ServletException {
        commentDAO = new CommentDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String newsId = request.getParameter("newsId");
        String authorName = request.getParameter("authorName");
        String authorEmail = request.getParameter("authorEmail");
        String content = request.getParameter("content");
        
        String contextPath = getContextPath(request);

        if (isNullOrEmpty(newsId)) {
            response.sendRedirect(contextPath + "/home");
            return;
        }

        HttpSession session = request.getSession();

        // 1. Rate Limiting: Kiểm tra khoảng cách giữa 2 lần gửi bình luận (Tối thiểu 15s)
        Long lastCommentTime = (Long) session.getAttribute("LAST_COMMENT_SUBMIT_TIME");
        long currentTime = System.currentTimeMillis();
        if (lastCommentTime != null && (currentTime - lastCommentTime) < 15000) {
            long waitSeconds = (15000 - (currentTime - lastCommentTime)) / 1000 + 1;
            session.setAttribute("commentError", "Bạn đang gửi bình luận quá nhanh! Vui lòng chờ " + waitSeconds + " giây trước khi thử lại.");
            response.sendRedirect(contextPath + "/detail?id=" + URLEncoder.encode(newsId, StandardCharsets.UTF_8) + "#comments-section");
            return;
        }

        // 2. Validate dữ liệu đầu vào
        if (isNullOrEmpty(authorName) || authorName.trim().length() < 2) {
            session.setAttribute("commentError", "Vui lòng nhập họ tên của bạn (tối thiểu 2 ký tự).");
            response.sendRedirect(contextPath + "/detail?id=" + URLEncoder.encode(newsId, StandardCharsets.UTF_8) + "#comments-section");
            return;
        }

        if (isNullOrEmpty(content) || content.trim().length() < 5) {
            session.setAttribute("commentError", "Nội dung bình luận quá ngắn (tối thiểu 5 ký tự).");
            response.sendRedirect(contextPath + "/detail?id=" + URLEncoder.encode(newsId, StandardCharsets.UTF_8) + "#comments-section");
            return;
        }

        if (content.trim().length() > 1000) {
            session.setAttribute("commentError", "Nội dung bình luận không được vượt quá 1.000 ký tự.");
            response.sendRedirect(contextPath + "/detail?id=" + URLEncoder.encode(newsId, StandardCharsets.UTF_8) + "#comments-section");
            return;
        }

        // 3. Defense in Depth: XSS Sanitization sạch sẽ trước khi lưu trữ
        String sanitizedName = XssSanitizer.stripHtml(authorName.trim());
        String sanitizedEmail = isNullOrEmpty(authorEmail) ? null : XssSanitizer.stripHtml(authorEmail.trim());
        String sanitizedContent = XssSanitizer.stripHtml(content.trim());

        // 4. Khởi tạo đối tượng Comment và lưu vào Database ở trạng thái Chờ duyệt (Status = 0)
        Comment comment = new Comment(newsId, sanitizedName, sanitizedEmail, sanitizedContent);
        comment.setStatus(Comment.STATUS_PENDING);

        boolean success = commentDAO.insert(comment);

        if (success) {
            session.setAttribute("LAST_COMMENT_SUBMIT_TIME", currentTime);
            session.setAttribute("commentSuccess", "Cảm ơn ý kiến của bạn! Bình luận đã được tiếp nhận và đang chờ ban biên tập kiểm duyệt trước khi xuất bản.");
        } else {
            session.setAttribute("commentError", "Đã có lỗi xảy ra trong quá trình gửi bình luận. Vui lòng thử lại sau.");
        }

        response.sendRedirect(contextPath + "/detail?id=" + URLEncoder.encode(newsId, StandardCharsets.UTF_8) + "#comments-section");
    }
}
