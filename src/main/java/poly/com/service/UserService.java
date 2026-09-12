package poly.com.service;

import java.util.List;
import poly.com.entity.User;

/**
 * Service Interface quản lý người dùng (UserService)
 * 
 * Đóng gói toàn bộ nghiệp vụ tài khoản, xác thực, phân quyền và kiểm toán
 * theo mô hình 3-Tier Clean Architecture.
 * 
 * @author ABCNews Development Team
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
     * Sinh mã định danh người dùng tiếp theo dựa trên vai trò
     */
    String getNextUserId(String roleType);

    /**
     * Đếm tổng số người dùng
     */
    int countAll();
}
