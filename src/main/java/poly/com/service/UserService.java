package poly.com.service;

import java.util.List;
import poly.com.entity.User;

/**
 * Service Interface quản lý người dùng (UserService)
 * 
 * Đóng gói toàn bộ nghiệp vụ tài khoản, xác thực, phân quyền và kiểm toán
 * theo mô hình phân tầng Layered MVC.
 * 
 * @author Nguyen Duy Khanh
 */
public interface UserService {

    /**
     * Xác thực đăng nhập bằng email và mật khẩu
     */
    User login(String email, String password);

    /**
     * Tìm người dùng theo ID
     */
    User findById(String id);

    /**
     * Tìm người dùng theo Email
     */
    User findByEmail(String email);

    /**
     * Lấy tất cả người dùng
     */
    List<User> getAllUsers();

    /**
     * Tìm kiếm và lọc người dùng theo nhiều tiêu chí
     */
    List<User> searchAndFilter(String keyword, String role, Boolean enabled, String sortBy, String sortOrder);

    /**
     * Tạo mới người dùng (đảm bảo mật khẩu được băm BCrypt)
     */
    boolean createUser(User user, User currentUser);

    /**
     * Cập nhật thông tin người dùng
     */
    boolean updateUser(User user, User currentUser);

    /**
     * Xóa người dùng theo quy tắc phân quyền (Super Admin / Admin)
     */
    boolean deleteUser(String targetUserId, User currentUser);

    /**
     * Khóa hoặc mở khóa trạng thái tài khoản
     */
    boolean toggleStatus(String targetUserId, User currentUser);

    /**
     * Cập nhật mật khẩu mới (hash BCrypt)
     */
    boolean updatePassword(String userId, String newPassword);

    /**
     * Đổi mật khẩu cho người dùng đang đăng nhập
     *
     * @param userId ID người dùng đang đăng nhập
     * @param currentPassword Mật khẩu hiện tại (plain text)
     * @param newPassword Mật khẩu mới (plain text)
     * @param confirmPassword Xác nhận mật khẩu mới (plain text)
     * @return true nếu đổi mật khẩu thành công
     * @throws IllegalArgumentException nếu thông tin không hợp lệ hoặc mật khẩu sai
     */
    boolean changePassword(String userId, String currentPassword, String newPassword, String confirmPassword);

    /**
     * Đặt lại mật khẩu người dùng bởi Quản trị viên (Admin Reset Password)
     *
     * @param targetUserId ID người dùng cần đặt lại mật khẩu
     * @param newRawPassword Mật khẩu mới (plain text). Nếu null/rỗng -> giữ nguyên mật khẩu cũ
     * @param currentUser Quản trị viên đang thực hiện thao tác (kiểm tra quyền)
     * @return true nếu thành công
     * @throws SecurityException nếu vi phạm quyền quản trị
     * @throws IllegalArgumentException nếu mật khẩu mới không hợp lệ
     */
    boolean resetPasswordByAdmin(String targetUserId, String newRawPassword, User currentUser);

    /**
     * Sinh mã định danh người dùng tiếp theo dựa trên vai trò
     */
    String getNextUserId(String roleType);

    /**
     * Đếm tổng số người dùng
     */
    int countAll();
}
