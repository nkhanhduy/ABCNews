package poly.com.service.impl;

import java.util.List;

import poly.com.dao.NewsDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.User;
import poly.com.service.ActivityLogService;
import poly.com.service.UserService;
import poly.com.util.PasswordUtil;

/**
 * Service Implementation quản lý người dùng (UserServiceImpl)
 * 
 * Thực thi các quy tắc nghiệp vụ, kiểm tra ràng buộc phân quyền và gọi DAO.
 * 
 * @author ABCNews Development Team
 */
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final NewsDAO newsDAO;
    private final ActivityLogService activityLogService;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
        this.newsDAO = new NewsDAO();
        this.activityLogService = new ActivityLogService();
    }

    public UserServiceImpl(UserDAO userDAO, NewsDAO newsDAO, ActivityLogService activityLogService) {
        this.userDAO = userDAO;
        this.newsDAO = newsDAO;
        this.activityLogService = activityLogService;
    }

    @Override
    public User login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.findByEmailAndPassword(email.trim(), password);
    }

    @Override
    public User findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return userDAO.findById(id.trim());
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userDAO.findByEmail(email.trim());
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public List<User> searchAndFilter(String keyword, String role, Boolean enabled, String sortBy, String sortOrder) {
        return userDAO.searchAndFilter(keyword, role, enabled, sortBy, sortOrder);
    }

    @Override
    public boolean createUser(User user, User currentUser) {
        if (user == null) {
            return false;
        }

        // Kiểm tra phân quyền: Chỉ Super Admin mới được tạo Super Admin
        if (user.isSuperAdmin() && (currentUser == null || !currentUser.isSuperAdmin())) {
            throw new SecurityException("Chỉ Super Admin mới có quyền tạo tài khoản Super Admin.");
        }

        // Băm mật khẩu BCrypt an toàn
        user.setPassword(PasswordUtil.ensureHashed(user.getPassword()));
        
        userDAO.insert(user);
        return true;
    }

    @Override
    public boolean updateUser(User user, User currentUser) {
        if (user == null || user.getId() == null) {
            return false;
        }

        User existingUser = userDAO.findById(user.getId());
        if (existingUser == null) {
            return false;
        }

        // Quy tắc bảo vệ: Admin thường không được sửa Super Admin
        if (currentUser != null && !currentUser.isSuperAdmin() && existingUser.isSuperAdmin()) {
            throw new SecurityException("Admin thường không có quyền chỉnh sửa tài khoản Super Admin.");
        }

        // Nếu mật khẩu để trống thì giữ nguyên mật khẩu cũ
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(PasswordUtil.ensureHashed(user.getPassword()));
        }

        userDAO.update(user);
        return true;
    }

    @Override
    public boolean deleteUser(String targetUserId, User currentUser) {
        if (targetUserId == null || currentUser == null) {
            return false;
        }

        // Không được phép tự xóa tài khoản của chính mình
        if (targetUserId.equalsIgnoreCase(currentUser.getId())) {
            throw new IllegalArgumentException("Không thể tự xóa tài khoản đang đăng nhập.");
        }

        User targetUser = userDAO.findById(targetUserId);
        if (targetUser == null) {
            return false;
        }

        // Quy tắc phân quyền:
        // - Super Admin: có thể xóa mọi user (trừ chính mình)
        // - Admin thường: chỉ xóa được phóng viên (role = false), không xóa được admin
        if (!currentUser.isSuperAdmin() && targetUser.isRole()) {
            throw new SecurityException("Admin thường không thể xóa tài khoản Quản trị viên khác.");
        }

        // Cập nhật các bài viết của user thành tác giả NULL để tránh lỗi Foreign Key
        int newsCount = newsDAO.countByAuthor(targetUserId);
        if (newsCount > 0) {
            newsDAO.setAuthorToNull(targetUserId);
        }

        userDAO.delete(targetUserId);
        return true;
    }

    @Override
    public boolean toggleStatus(String targetUserId, User currentUser) {
        if (targetUserId == null || currentUser == null) {
            return false;
        }

        // Không thể tự khóa chính mình
        if (targetUserId.equalsIgnoreCase(currentUser.getId())) {
            throw new IllegalArgumentException("Không thể tự khóa tài khoản của chính mình.");
        }

        User targetUser = userDAO.findById(targetUserId);
        if (targetUser == null) {
            return false;
        }

        // Admin thường không thể khóa Admin khác
        if (!currentUser.isSuperAdmin() && targetUser.isRole()) {
            throw new SecurityException("Admin thường không thể thay đổi trạng thái của Quản trị viên khác.");
        }

        targetUser.setEnabled(!targetUser.isEnabled());
        userDAO.update(targetUser);
        return true;
    }

    @Override
    public boolean updatePassword(String userId, String newPassword) {
        if (userId == null || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        String hashed = PasswordUtil.ensureHashed(newPassword);
        return userDAO.updatePassword(userId, hashed);
    }

    @Override
    public String getNextUserId(String roleType) {
        return userDAO.getNextUserId(roleType);
    }

    @Override
    public int countAll() {
        return userDAO.countAll();
    }
}
