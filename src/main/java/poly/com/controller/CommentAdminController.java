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

import poly.com.entity.Comment;
import poly.com.entity.User;
import poly.com.service.CommentService;
import poly.com.service.impl.CommentServiceImpl;
import poly.com.util.SecurityHelper;

/**
 * Controller quản lý và kiểm duyệt bình luận (Moderated Comments) trong Admin
 * Quy chuẩn bảo mật:
 * - Chỉ Quản trị viên (Admin) mới có quyền truy cập và kiểm duyệt bình luận.
 * - GET: Chỉ hiển thị danh sách, phân trang và tìm kiếm.
 * - POST: Xử lý các hành động thay đổi trạng thái (Phê duyệt, Từ chối, Xóa) kèm xác thực CSRF.
 */
@WebServlet("/admin/comments")
public class CommentAdminController extends BaseController {
    private static final long serialVersionUID = 1L;

    private CommentService commentService;

    @Override
    public void init() throws ServletException {
        commentService = new CommentServiceImpl();
    }

    /**
     * Xử lý request GET: Xem danh sách bình luận, lọc và phân trang (Không thay đổi dữ liệu)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền: Chỉ Admin / Super Admin mới được vào trang kiểm duyệt bình luận
        if (!checkAdminRole(request, response)) {
            return;
        }

        HttpSession session = request.getSession();

        // Hiển thị danh sách bình luận kèm phân trang và bộ lọc
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
        int countPending = commentService.countByStatus(Comment.STATUS_PENDING);
        int countApproved = commentService.countByStatus(Comment.STATUS_APPROVED);
        int countRejected = commentService.countByStatus(Comment.STATUS_REJECTED);
        int countAll = countPending + countApproved + countRejected;

        // Cập nhật số bình luận chờ duyệt vào Session để Sidebar badge luôn chuẩn xác
        session.setAttribute("pendingCommentCount", countPending);

        int totalItems = commentService.countWithFilter(statusFilter, keyword);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        List<Comment> comments = commentService.findWithFilter(statusFilter, keyword, page, pageSize);

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

    /**
     * Xử lý request POST: Các hành động thay đổi trạng thái (Approve, Reject, Delete)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkAdminRole(request, response)) {
            return;
        }

        User currentUser = SecurityHelper.getCurrentUser(request);
        HttpSession session = request.getSession();
        String contextPath = getContextPath(request);
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String returnStatus = request.getParameter("status");
        String returnUrl = contextPath + "/admin/comments" + 
                (returnStatus != null && !returnStatus.isEmpty() ? "?status=" + URLEncoder.encode(returnStatus, StandardCharsets.UTF_8) : "");

        if (action == null || idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(returnUrl);
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());
            Comment comment = commentService.findById(id);

            if (comment != null) {
                switch (action) {
                    case "approve" -> {
                        boolean ok = commentService.approveComment(id, currentUser, request);
                        if (ok) {
                            session.setAttribute("message", "Đã phê duyệt bình luận của '" + comment.getAuthorName() + "' thành công!");
                        } else {
                            session.setAttribute("error", "Không thể phê duyệt bình luận.");
                        }
                    }
                    case "reject" -> {
                        boolean ok = commentService.rejectComment(id, currentUser, request);
                        if (ok) {
                            session.setAttribute("message", "Đã chuyển bình luận sang trạng thái Từ chối!");
                        } else {
                            session.setAttribute("error", "Không thể từ chối bình luận.");
                        }
                    }
                    case "delete" -> {
                        boolean ok = commentService.deleteComment(id, currentUser, request);
                        if (ok) {
                            session.setAttribute("message", "Đã xóa bình luận vĩnh viễn!");
                        } else {
                            session.setAttribute("error", "Không thể xóa bình luận.");
                        }
                    }
                    default -> session.setAttribute("error", "Hành động không hợp lệ.");
                }
            } else {
                session.setAttribute("error", "Không tìm thấy bình luận tương ứng.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Mã bình luận không hợp lệ.");
        } catch (SecurityException e) {
            session.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] CommentAdminController.doPost: " + e.getMessage());
            session.setAttribute("error", "Đã xảy ra lỗi khi xử lý bình luận. Vui lòng thử lại.");
        }

        response.sendRedirect(returnUrl);
    }
}
