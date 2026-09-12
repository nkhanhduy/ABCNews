package poly.com.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import poly.com.entity.News;
import poly.com.entity.User;

/**
 * Lớp tiện ích tập trung kiểm tra phân quyền (Authorization)
 * Đảm bảo nguyên tắc bảo mật chặt chẽ ở tầng Backend:
 * - Phân cấp rõ ràng: Khách < Phóng viên (Reporter) < Quản trị viên (Admin) < Quản trị tối cao (Super Admin).
 * - Phóng viên chỉ được thao tác trên bài viết của chính mình.
 * - Quản trị viên thường không được can thiệp vào tài khoản Admin khác hoặc Super Admin.
 * - Kiểm duyệt bình luận chỉ dành cho Admin / Super Admin.
 */
public final class SecurityHelper {

    private SecurityHelper() {
        // Utility class
    }

    /**
     * Lấy đối tượng người dùng hiện tại đang đăng nhập từ Session
     */
    public static User getCurrentUser(HttpServletRequest request) {
        if (request == null) return null;
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute("user");
    }

    /**
     * Kiểm tra người dùng đã xác thực và tài khoản còn hiệu lực hay không
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && user.isEnabled();
    }

    /**
     * Kiểm tra có phải Quản trị viên (Admin thường hoặc Super Admin) hay không
     */
    public static boolean isAdmin(User user) {
        return user != null && user.isEnabled() && user.isRole();
    }

    /**
     * Kiểm tra có phải Quản trị viên tối cao (Super Admin) hay không
     * Dựa trên cờ IsSuperAdmin = 1 trong Database
     */
    public static boolean isSuperAdmin(User user) {
        return user != null && user.isEnabled() && user.isSuperAdmin();
    }

    /**
     * Kiểm tra có phải Phóng viên (Reporter) hay không
     */
    public static boolean isReporter(User user) {
        return user != null && user.isEnabled() && !user.isRole();
    }

    /**
     * Kiểm tra quyền kiểm duyệt bình luận (Duyệt, Từ chối, Xóa)
     * Chỉ Admin hoặc Super Admin mới có quyền kiểm duyệt
     */
    public static boolean canModerateComments(User user) {
        return isAdmin(user);
    }

    /**
     * Kiểm tra quyền quản lý người dùng
     */
    public static boolean canManageUsers(User user) {
        return isAdmin(user);
    }

    /**
     * Kiểm tra xem currentUser có quyền tạo tài khoản với quyền Super Admin hay không
     * Chỉ Super Admin mới có quyền tạo Super Admin
     */
    public static boolean canCreateSuperAdmin(User currentUser) {
        return isSuperAdmin(currentUser);
    }

    /**
     * Kiểm tra xem currentUser có quyền cập nhật targetUser hay không
     */
    public static boolean canUpdateUser(User currentUser, User targetUser) {
        if (currentUser == null || targetUser == null || !currentUser.isEnabled()) {
            return false;
        }

        // Người dùng được cập nhật thông tin cá nhân của chính mình (qua Profile)
        if (currentUser.getId().equals(targetUser.getId())) {
            return true;
        }

        // Super Admin có quyền cập nhật bất kỳ ai
        if (isSuperAdmin(currentUser)) {
            return true;
        }

        // Admin thường không thể sửa Super Admin hoặc Admin khác
        if (isAdmin(currentUser)) {
            return !targetUser.isRole() && !targetUser.isSuperAdmin();
        }

        return false;
    }

    /**
     * Kiểm tra xem currentUser có quyền xóa targetUser hay không
     */
    public static boolean canDeleteUser(User currentUser, User targetUser) {
        if (currentUser == null || targetUser == null || !currentUser.isEnabled()) {
            return false;
        }

        // Tuyệt đối không cho phép tự xóa chính mình
        if (currentUser.getId().equals(targetUser.getId())) {
            return false;
        }

        // Super Admin có thể xóa bất kỳ Admin hoặc Reporter nào
        if (isSuperAdmin(currentUser)) {
            return true;
        }

        // Admin thường chỉ được phép xóa Phóng viên
        if (isAdmin(currentUser)) {
            return !targetUser.isRole() && !targetUser.isSuperAdmin();
        }

        return false;
    }

    /**
     * Kiểm tra xem currentUser có quyền khóa/mở khóa targetUser hay không
     */
    public static boolean canToggleUserStatus(User currentUser, User targetUser) {
        if (currentUser == null || targetUser == null || !currentUser.isEnabled()) {
            return false;
        }

        // Không cho phép tự khóa chính mình
        if (currentUser.getId().equals(targetUser.getId())) {
            return false;
        }

        // Super Admin có thể khóa bất kỳ ai
        if (isSuperAdmin(currentUser)) {
            return true;
        }

        // Admin thường chỉ được khóa Phóng viên
        if (isAdmin(currentUser)) {
            return !targetUser.isRole() && !targetUser.isSuperAdmin();
        }

        return false;
    }

    /**
     * Kiểm tra xem currentUser có quyền chỉnh sửa bài viết News hay không
     * Admin: sửa mọi bài viết
     * Reporter: CHỈ sửa bài viết do chính mình làm tác giả
     */
    public static boolean canEditNews(User currentUser, News news) {
        if (currentUser == null || news == null || !currentUser.isEnabled()) {
            return false;
        }
        if (isAdmin(currentUser)) {
            return true;
        }
        return news.getAuthor() != null && news.getAuthor().equals(currentUser.getId());
    }

    /**
     * Kiểm tra xem currentUser có quyền xóa bài viết News hay không
     */
    public static boolean canDeleteNews(User currentUser, News news) {
        return canEditNews(currentUser, news);
    }
}
