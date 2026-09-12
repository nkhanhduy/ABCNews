package poly.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.NewsDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.User;
import poly.com.util.ImagePathHelper;
import poly.com.util.SafeImageStorage;

/**
 * Controller xử lý xem profile và cập nhật ảnh đại diện thông minh của user
 * Có thể xem profile của chính mình hoặc của user khác (nếu là admin)
 */
@WebServlet("/admin/profile")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 5,       // 5MB
    maxRequestSize = 1024 * 1024 * 10    // 10MB
)
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UserDAO userDAO;
    private NewsDAO newsDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        newsDAO = new NewsDAO();
    }

    /**
     * Hiển thị trang profile của user
     * Nếu không có tham số id, hiển thị profile của user đang đăng nhập
     * Nếu có tham số id, chỉ admin mới được xem profile của user khác
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Lấy thông báo Flash Toast từ session nếu có
            String toastSuccess = (String) session.getAttribute("toastSuccess");
            if (toastSuccess != null) {
                request.setAttribute("toastSuccess", toastSuccess);
                session.removeAttribute("toastSuccess");
            }
            String toastError = (String) session.getAttribute("toastError");
            if (toastError != null) {
                request.setAttribute("toastError", toastError);
                session.removeAttribute("toastError");
            }

            String userId = request.getParameter("id");
            User profileUser;
            
            // Nếu không có id, hiển thị profile của chính user đang đăng nhập
            if (userId == null || userId.isEmpty()) {
                profileUser = currentUser;
            } else {
                // Nếu có id, kiểm tra quyền
                // Chỉ admin mới được xem profile của user khác
                if (!currentUser.isRole()) {
                    // Nếu không phải admin, chỉ được xem profile của chính mình
                    profileUser = currentUser;
                } else {
                    // Admin có thể xem profile của bất kỳ user nào
                    profileUser = userDAO.findById(userId);
                    if (profileUser == null) {
                        profileUser = currentUser; // Nếu không tìm thấy, hiển thị profile của chính mình
                    }
                }
            }
            
            // Chuẩn hóa đường dẫn ảnh cho user
            String contextPath = request.getContextPath();
            if (profileUser != null) {
                ImagePathHelper.normalizeUserImagePath(profileUser, contextPath);
            }
            
            // Đếm số tin tức của user (nếu là phóng viên)
            int newsCount = 0;
            if (profileUser != null && !profileUser.isRole()) {
                newsCount = newsDAO.findByAuthor(profileUser.getId()).size();
            }
            
            // Kiểm tra quyền để hiển thị nút chỉnh sửa và đổi avatar
            boolean canEdit = false;
            if (currentUser != null && profileUser != null) {
                boolean isCurrentUser = currentUser.getId().equals(profileUser.getId());
                boolean isCurrentUserSuperAdmin = currentUser.isSuperAdmin();
                boolean isProfileUserSuperAdmin = profileUser.isSuperAdmin();
                
                // Có thể sửa nếu:
                // 1. Là chính mình, hoặc
                // 2. Là Super Admin (có thể sửa tất cả), hoặc
                // 3. Là Admin thường và profileUser không phải Super Admin
                if (isCurrentUser || isCurrentUserSuperAdmin || 
                    (currentUser.isRole() && !isProfileUserSuperAdmin)) {
                    canEdit = true;
                }
            }
            
            request.setAttribute("profileUser", profileUser);
            request.setAttribute("newsCount", newsCount);
            request.setAttribute("canEdit", canEdit);
            request.setAttribute("pageTitle", "Thông tin " + profileUser.getFullname());
            request.setAttribute("view", "/views/admin/profile.jsp");
            
            request.getRequestDispatcher("/views/admin/admin_layout.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    /**
     * Xử lý upload ảnh đại diện thông minh tại trang Profile
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String targetUserId = request.getParameter("targetUserId");
        String redirectUrl = request.getContextPath() + "/admin/profile";

        try {
            User targetUser;
            boolean isSelf = (targetUserId == null || targetUserId.trim().isEmpty() || targetUserId.equals(currentUser.getId()));
            
            if (isSelf) {
                targetUser = userDAO.findById(currentUser.getId());
            } else {
                redirectUrl = request.getContextPath() + "/admin/profile?id=" + targetUserId;
                // Chỉ admin mới được đổi avatar của user khác
                if (!currentUser.isRole()) {
                    session.setAttribute("toastError", "Bạn không có quyền thay đổi ảnh của người dùng khác.");
                    response.sendRedirect(request.getContextPath() + "/admin/profile");
                    return;
                }
                targetUser = userDAO.findById(targetUserId);
                if (targetUser == null) {
                    session.setAttribute("toastError", "Không tìm thấy thông tin người dùng cần cập nhật.");
                    response.sendRedirect(request.getContextPath() + "/admin/profile");
                    return;
                }
                // Admin thường không được sửa Super Admin
                if (!currentUser.isSuperAdmin() && targetUser.isSuperAdmin()) {
                    session.setAttribute("toastError", "Bạn không có quyền sửa tài khoản Quản trị cấp cao (Super Admin).");
                    response.sendRedirect(redirectUrl);
                    return;
                }
            }

            // Xử lý upload ảnh bằng SafeImageStorage
            String newAvatarPath = SafeImageStorage.storeAvatar(request, "avatarFile", targetUser.getImagePath());
            
            if (newAvatarPath != null && !newAvatarPath.isEmpty()) {
                targetUser.setImagePath(newAvatarPath);
                userDAO.update(targetUser);

                // Cập nhật lại session nếu là chính mình
                if (isSelf) {
                    User refreshedUser = userDAO.findById(currentUser.getId());
                    if (refreshedUser != null) {
                        ImagePathHelper.normalizeUserImagePath(refreshedUser, request.getContextPath());
                        session.setAttribute("user", refreshedUser);
                    }
                }

                session.setAttribute("toastSuccess", "Cập nhật ảnh đại diện thành công!");
            } else {
                session.setAttribute("toastError", "Vui lòng chọn một tập tin ảnh để tải lên.");
            }

        } catch (IllegalArgumentException | SecurityException e) {
            session.setAttribute("toastError", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("toastError", "Đã xảy ra lỗi khi lưu ảnh đại diện. Vui lòng thử lại sau.");
        }

        // Điều hướng thông minh về đúng trang profile tương ứng
        response.sendRedirect(redirectUrl);
    }
}
