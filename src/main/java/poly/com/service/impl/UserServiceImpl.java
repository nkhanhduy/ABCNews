package poly.com.service.impl;

import java.util.List;

import poly.com.dao.NewsDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.User;
import poly.com.service.ActivityLogService;
import poly.com.service.RememberMeService;
import poly.com.service.UserService;
import poly.com.util.PasswordUtil;

/**
 * Service Implementation quản lý người dùng (UserServiceImpl)
 * 
 * Thực thi các quy tắc nghiệp vụ, kiểm tra ràng buộc phân quyền và gọi DAO.
 * 
 * @author Nguyen Duy Khanh
 */
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final NewsDAO newsDAO;
    private final ActivityLogService activityLogService;
    private final RememberMeService rememberMeService;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
        this.newsDAO = new NewsDAO();
        this.activityLogService = new ActivityLogService();
        this.rememberMeService = new RememberMeServiceImpl();
    }

    public UserServiceImpl(UserDAO userDAO, NewsDAO newsDAO, ActivityLogService activityLogService) {
        this(userDAO, newsDAO, activityLogService, new RememberMeServiceImpl());
    }

    public UserServiceImpl(UserDAO userDAO, NewsDAO newsDAO, ActivityLogService activityLogService, RememberMeService rememberMeService) {
        this.userDAO = userDAO;
        this.newsDAO = newsDAO;
        this.activityLogService = activityLogService;
        this.rememberMeService = rememberMeService;
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

        // Quy tắc bảo vệ: Phóng viên không có quyền sửa tài khoản khác
        if (currentUser != null && !currentUser.isRole() && !currentUser.getId().equalsIgnoreCase(existingUser.getId())) {
            throw new SecurityException("Phóng viên không có quyền chỉnh sửa tài khoản người dùng khác.");
        }

        // Quy tắc bảo vệ: Admin thường không được sửa Super Admin
        if (currentUser != null && !currentUser.isSuperAdmin() && existingUser.isSuperAdmin()) {
            throw new SecurityException("Admin thường không có quyền chỉnh sửa tài khoản Super Admin.");
        }

        // Nếu mật khẩu để trống thì giữ nguyên mật khẩu cũ
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            // Nếu nhập mật khẩu mới, validate độ dài tối thiểu
            if (!PasswordUtil.isPasswordValid(user.getPassword())) {
                throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
            }
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            if (rememberMeService != null) {
                rememberMeService.revokeAllTokens(user.getId());
            }
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
        boolean updated = userDAO.updatePassword(userId, hashed);
        if (updated && rememberMeService != null) {
            rememberMeService.revokeAllTokens(userId);
        }
        return updated;
    }

    @Override
    public boolean changePassword(String userId, String currentPassword, String newPassword, String confirmPassword) {
        // 1. Kiểm tra dữ liệu đầu vào không được để trống
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã người dùng không hợp lệ.");
        }
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu hiện tại.");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu mới.");
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng xác nhận mật khẩu mới.");
        }

        // 2. Tìm người dùng trong DB
        User user = userDAO.findById(userId.trim());
        if (user == null || !user.isEnabled()) {
            throw new IllegalArgumentException("Tài khoản không tồn tại hoặc đã bị khóa.");
        }

        // 3. Xác thực mật khẩu hiện tại bằng BCrypt (hỗ trợ graceful migration cho dữ liệu cũ)
        boolean isMatch = false;
        String dbPassword = user.getPassword();
        if (dbPassword != null) {
            if (PasswordUtil.isBCryptHash(dbPassword)) {
                isMatch = PasswordUtil.verifyPassword(currentPassword, dbPassword);
            } else if (dbPassword.equals(currentPassword)) {
                isMatch = true;
            }
        }
        if (!isMatch) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }

        // 4. Mật khẩu mới không được trùng mật khẩu hiện tại
        if (newPassword.equals(currentPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu hiện tại.");
        }

        // 5. Xác nhận mật khẩu mới phải khớp
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }

        // 6. Kiểm tra độ mạnh mật khẩu (ít nhất 8 ký tự theo quy định dự án)
        if (!PasswordUtil.isPasswordValid(newPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        }

        // 7. Hash BCrypt và cập nhật xuống DB
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(user.getId(), hashedPassword);
        if (updated) {
            user.setPassword(hashedPassword);
            // 8. Thu hồi toàn bộ persistent remember-me tokens của user
            if (rememberMeService != null) {
                rememberMeService.revokeAllTokens(user.getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean resetPasswordByAdmin(String targetUserId, String newRawPassword, User currentUser) {
        if (targetUserId == null || targetUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã người dùng không được để trống.");
        }
        if (currentUser == null || !currentUser.isEnabled()) {
            throw new SecurityException("Yêu cầu đăng nhập để thực hiện chức năng này.");
        }
        // Chỉ Admin hoặc Super Admin mới được reset mật khẩu
        if (!currentUser.isRole()) {
            throw new SecurityException("Phóng viên không có quyền đặt lại mật khẩu cho người dùng khác.");
        }

        User targetUser = userDAO.findById(targetUserId.trim());
        if (targetUser == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng cần đặt lại mật khẩu.");
        }

        // Admin thường không được reset mật khẩu Super Admin
        if (!currentUser.isSuperAdmin() && targetUser.isSuperAdmin()) {
            throw new SecurityException("Admin thường không có quyền đặt lại mật khẩu của Super Admin.");
        }

        // Nếu mật khẩu để trống -> giữ nguyên mật khẩu cũ
        if (newRawPassword == null || newRawPassword.trim().isEmpty()) {
            return true;
        }

        // Validate độ dài mật khẩu mới
        if (!PasswordUtil.isPasswordValid(newRawPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        }

        String hashedPassword = PasswordUtil.hashPassword(newRawPassword);
        boolean updated = userDAO.updatePassword(targetUser.getId(), hashedPassword);
        if (updated) {
            targetUser.setPassword(hashedPassword);
            if (rememberMeService != null) {
                rememberMeService.revokeAllTokens(targetUser.getId());
            }
            return true;
        }
        return false;
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
