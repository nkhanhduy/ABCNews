package poly.com.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.CommentDAO;
import poly.com.entity.Comment;
import poly.com.entity.User;
import poly.com.service.ActivityLogService;

/**
 * Controller quản lý và kiểm duyệt bình luận (Moderated Comments) trong Admin
 * Xử lý: Danh sách bình luận theo trạng thái, Phê duyệt, Từ chối, Xóa, Tìm kiếm
 * 
 * @author ABCNews Development Team
 */
@WebServlet("/admin/comments")
public class CommentAdminController extends BaseController {
    private static final long serialVersionUID = 1L;

    private CommentDAO commentDAO;
    private ActivityLogService activityLogService;

    @Override
    public void init() throws ServletException {
        commentDAO = new CommentDAO();
        activityLogService = new ActivityLogService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User currentUser = checkLogin(request, response);
        if (currentUser == null) return;

        HttpSession session = request.getSession();
        String contextPath = getContextPath(request);
        String action = request.getParameter("action");

        // 1. Xử lý các thao tác hành động (Approve, Reject, Delete)
        if (action != null) {
            String idStr = request.getParameter("id");
            String returnStatus = request.getParameter("status");
            String returnUrl = contextPath + "/admin/comments" + (returnStatus != null ? "?status=" + URLEncoder.encode(returnStatus, StandardCharsets.UTF_8) : "");

            try {
                int id = Integer.parseInt(idStr);
                Comment comment = commentDAO.findById(id);

                if (comment != null) {
                    switch (action) {
                        case "approve" -> {
                            commentDAO.updateStatus(id, Comment.STATUS_APPROVED);
                            activityLogService.log(currentUser, "APPROVE", "COMMENT", String.valueOf(id), 
                                    "Phê duyệt bình luận của: " + comment.getAuthorName(), request);
                            session.setAttribute("message", "Đã phê duyệt bình luận của '" + comment.getAuthorName() + "' thành công!");
                        }
                        case "reject" -> {
                            commentDAO.updateStatus(id, Comment.STATUS_REJECTED);
                            activityLogService.log(currentUser, "REJECT", "COMMENT", String.valueOf(id), 
                                    "Từ chối bình luận của: " + comment.getAuthorName(), request);
                            session.setAttribute("message", "Đã chuyển bình luận sang trạng thái Từ chối!");
                        }
                        case "delete" -> {
                            commentDAO.delete(id);
                            activityLogService.log(currentUser, "DELETE", "COMMENT", String.valueOf(id), 
                                    "Xóa bình luận của: " + comment.getAuthorName(), request);
                            session.setAttribute("message", "Đã xóa bình luận vĩnh viễn!");
                        }
                    }
                } else {
                    session.setAttribute("error", "Không tìm thấy bình luận tương ứng.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("error", "Lỗi xử lý yêu cầu: " + e.getMessage());
            }

            response.sendRedirect(returnUrl);
            return;
        }

        // 2. Hiển thị danh sách bình luận kèm phân trang và bộ lọc
        String statusParam = request.getParameter("status");
        Integer statusFilter = null;
        if (statusParam == null || statusParam.trim().isEmpty() || "0".equals(statusParam) || "pending".equalsIgnoreCase(statusParam)) {
            statusFilter = Comment.STATUS_PENDING; // Mặc định hiển thị danh sách Chờ duyệt
        } else if ("1".equals(statusParam) || "approved".equalsIgnoreCase(statusParam)) {
            statusFilter = Comment.STATUS_APPROVED;
        } else if ("2".equals(statusParam) || "rejected".equalsIgnoreCase(statusParam)) {
            statusFilter = Comment.STATUS_REJECTED;
        } else if ("-1".equals(statusParam) || "all".equalsIgnoreCase(statusParam)) {
            statusFilter = null; // Tất cả
        }

        String keyword = request.getParameter("keyword");
        if (keyword != null) {
            keyword = keyword.trim();
        }

        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr));
            } catch (NumberFormatException ignored) {}
        }
        int pageSize = 10;

        // Đếm số lượng theo trạng thái để hiển thị Tabs và Badge
        int countPending = commentDAO.countByStatus(Comment.STATUS_PENDING);
        int countApproved = commentDAO.countByStatus(Comment.STATUS_APPROVED);
        int countRejected = commentDAO.countByStatus(Comment.STATUS_REJECTED);
        int countAll = commentDAO.countByStatus(-1);

        // Cập nhật số bình luận chờ duyệt vào Session để Sidebar badge luôn chuẩn xác
        session.setAttribute("pendingCommentCount", countPending);

        int totalItems = commentDAO.countWithFilter(statusFilter, keyword);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        List<Comment> comments = commentDAO.findWithFilter(statusFilter, keyword, page, pageSize);

        request.setAttribute("comments", comments);
        request.setAttribute("currentStatus", statusFilter != null ? statusFilter : -1);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);

        request.setAttribute("countPending", countPending);
        request.setAttribute("countApproved", countApproved);
        request.setAttribute("countRejected", countRejected);
        request.setAttribute("countAll", countAll);

        forwardToAdminView(request, response, "Kiểm Duyệt Bình Luận", "/views/admin/comment_crud.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
