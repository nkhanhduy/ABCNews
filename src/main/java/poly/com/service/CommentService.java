package poly.com.service;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import poly.com.entity.Comment;
import poly.com.entity.User;

/**
 * Service quản lý và kiểm duyệt bình luận (Comment Moderation)
 */
public interface CommentService {

    Comment findById(int id);

    boolean approveComment(int id, User operator, HttpServletRequest request);

    boolean rejectComment(int id, User operator, HttpServletRequest request);

    boolean deleteComment(int id, User operator, HttpServletRequest request);

    int countByStatus(int status);

    int countWithFilter(Integer status, String keyword);

    List<Comment> findWithFilter(Integer status, String keyword, int page, int pageSize);
}
