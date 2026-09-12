package poly.com.service.impl;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

import poly.com.dao.CommentDAO;
import poly.com.entity.Comment;
import poly.com.entity.User;
import poly.com.service.ActivityLogService;
import poly.com.service.CommentService;
import poly.com.util.SecurityHelper;

/**
 * Triển khai CommentService:
 * - Kiểm tra authorization chặt chẽ ở tầng service bằng SecurityHelper.
 * - Ghi activity log đầy đủ cho các hành động phê duyệt, từ chối, xóa bình luận.
 */
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;
    private final ActivityLogService activityLogService;

    public CommentServiceImpl() {
        this(new CommentDAO(), new ActivityLogService());
    }

    public CommentServiceImpl(CommentDAO commentDAO, ActivityLogService activityLogService) {
        this.commentDAO = commentDAO;
        this.activityLogService = activityLogService;
    }

    @Override
    public Comment findById(int id) {
        return commentDAO.findById(id);
    }

    @Override
    public boolean approveComment(int id, User operator, HttpServletRequest request) {
        if (!SecurityHelper.canModerateComments(operator)) {
            throw new SecurityException("Bạn không có quyền phê duyệt bình luận");
        }

        Comment comment = commentDAO.findById(id);
        if (comment == null) {
            return false;
        }

        boolean updated = commentDAO.updateStatus(id, Comment.STATUS_APPROVED);
        if (updated && request != null) {
            activityLogService.log(operator, "APPROVE", "COMMENT", String.valueOf(id),
                    "Phê duyệt bình luận của: " + comment.getAuthorName(), request);
        }
        return updated;
    }

    @Override
    public boolean rejectComment(int id, User operator, HttpServletRequest request) {
        if (!SecurityHelper.canModerateComments(operator)) {
            throw new SecurityException("Bạn không có quyền từ chối bình luận");
        }

        Comment comment = commentDAO.findById(id);
        if (comment == null) {
            return false;
        }

        boolean updated = commentDAO.updateStatus(id, Comment.STATUS_REJECTED);
        if (updated && request != null) {
            activityLogService.log(operator, "REJECT", "COMMENT", String.valueOf(id),
                    "Từ chối bình luận của: " + comment.getAuthorName(), request);
        }
        return updated;
    }

    @Override
    public boolean deleteComment(int id, User operator, HttpServletRequest request) {
        if (!SecurityHelper.canModerateComments(operator)) {
            throw new SecurityException("Bạn không có quyền xóa bình luận");
        }

        Comment comment = commentDAO.findById(id);
        if (comment == null) {
            return false;
        }

        commentDAO.delete(id);
        if (request != null) {
            activityLogService.log(operator, "DELETE", "COMMENT", String.valueOf(id),
                    "Xóa bình luận của: " + comment.getAuthorName(), request);
        }
        return true;
    }

    @Override
    public int countByStatus(int status) {
        return commentDAO.countByStatus(status);
    }

    @Override
    public int countWithFilter(Integer status, String keyword) {
        return commentDAO.countWithFilter(status, keyword);
    }

    @Override
    public List<Comment> findWithFilter(Integer status, String keyword, int page, int pageSize) {
        return commentDAO.findWithFilter(status, keyword, page, pageSize);
    }
}
