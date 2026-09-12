package poly.com.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Lớp Entity đại diện cho thông tin người dùng (User) trong hệ thống
 */
@Entity
@Table(name = "Users")
public class User {
    /** Mã định danh duy nhất của người dùng (Username/ID) */
    @Id
    @Column(name = "Id", length = 50, nullable = false)
    private String id;
    
    /** Mật khẩu đăng nhập (Đã được mã hóa bảo mật bằng chuẩn BCrypt hash) */
    @Column(name = "Password", length = 255, nullable = false)
    private String password;
    
    /** Họ và tên đầy đủ của người dùng */
    @Column(name = "Fullname", length = 200)
    private String fullname;
    
    /** Ngày sinh của người dùng */
    @Column(name = "Birthday")
    @Temporal(TemporalType.DATE)
    private Date birthday;
    
    /** Giới tính: true = Nam, false = Nữ */
    @Column(name = "Gender")
    private boolean gender;
    
    /** Số điện thoại liên hệ */
    @Column(name = "Mobile", length = 20)
    private String mobile;
    
    /** Địa chỉ email (dùng để đăng nhập) */
    @Column(name = "Email", length = 255)
    private String email;
    
    /** Vai trò trong hệ thống: true = Quản trị viên (Admin), false = Phóng viên (Reporter) */
    @Column(name = "Role", nullable = false)
    private boolean role;
    
    /** Đường dẫn đến file ảnh đại diện của người dùng */
    @Column(name = "ImagePath", length = 255)
    private String imagePath;
    
    /** ID từ Google OAuth (nếu đăng nhập bằng Google) */
    @Column(name = "GoogleId", length = 255)
    private String googleId;
    
    /** Phương thức xác thực: 'local' (đăng nhập thường), 'google' (OAuth), hoặc 'both' (cả hai) */
    @Column(name = "AuthProvider", length = 20)
    private String authProvider;
    
    /** Trạng thái tài khoản: true = Hoạt động, false = Bị khóa */
    @Column(name = "Enabled")
    private boolean enabled;

    /**
     * Constructor mặc định - tạo đối tượng User rỗng
     * Dùng khi cần tạo object trước rồi set giá trị sau (ví dụ: từ form hoặc database)
     */
    public User() {
    }

    /**
     * Constructor với tất cả các trường cơ bản (không có imagePath)
     * 
     * @param id Mã định danh người dùng
     * @param password Mật khẩu
     * @param fullname Họ và tên
     * @param birthday Ngày sinh
     * @param gender Giới tính (true = Nam, false = Nữ)
     * @param mobile Số điện thoại
     * @param email Email
     * @param role Vai trò (true = Admin, false = Reporter)
     */
    public User(String id, String password, String fullname, Date birthday, boolean gender, String mobile, String email, boolean role) {
        this.id = id;
        this.password = password;
        this.fullname = fullname;
        this.birthday = birthday;
        this.gender = gender;
        this.mobile = mobile;
        this.email = email;
        this.role = role;
    }
    
    /**
     * Constructor với tất cả các trường bao gồm cả imagePath
     * 
     * @param id Mã định danh người dùng
     * @param password Mật khẩu
     * @param fullname Họ và tên
     * @param birthday Ngày sinh
     * @param gender Giới tính (true = Nam, false = Nữ)
     * @param mobile Số điện thoại
     * @param email Email
     * @param role Vai trò (true = Admin, false = Reporter)
     * @param imagePath Đường dẫn đến ảnh đại diện
     */
    public User(String id, String password, String fullname, Date birthday, boolean gender, String mobile, String email, boolean role, String imagePath) {
        this.id = id;
        this.password = password;
        this.fullname = fullname;
        this.birthday = birthday;
        this.gender = gender;
        this.mobile = mobile;
        this.email = email;
        this.role = role;
        this.imagePath = imagePath;
    }

    // ========== GETTERS VÀ SETTERS ==========
    
    /**
     * Lấy mã định danh (ID) của người dùng
     * @return Mã định danh người dùng
     */
    public String getId() {
        return id;
    }

    /**
     * Thiết lập mã định danh (ID) cho người dùng
     * @param id Mã định danh người dùng
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Lấy mật khẩu của người dùng (dạng BCrypt hash)
     * @return Mật khẩu đã được băm
     */
    public String getPassword() {
        return password;
    }

    /**
     * Thiết lập mật khẩu cho người dùng
     * @param password Mật khẩu (chuỗi trần hoặc đã hash, DAO sẽ tự đảm bảo băm BCrypt)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Lấy họ và tên đầy đủ của người dùng
     * @return Họ và tên
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Thiết lập họ và tên cho người dùng
     * @param fullname Họ và tên đầy đủ
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * Lấy ngày sinh của người dùng
     * @return Ngày sinh (Date object)
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * Thiết lập ngày sinh cho người dùng
     * @param birthday Ngày sinh
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * Kiểm tra giới tính của người dùng
     * @return true nếu là Nam, false nếu là Nữ
     */
    public boolean isGender() {
        return gender;
    }

    /**
     * Thiết lập giới tính cho người dùng
     * @param gender true = Nam, false = Nữ
     */
    public void setGender(boolean gender) {
        this.gender = gender;
    }

    /**
     * Lấy số điện thoại của người dùng
     * @return Số điện thoại (có thể null)
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Thiết lập số điện thoại cho người dùng
     * @param mobile Số điện thoại
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * Lấy địa chỉ email của người dùng
     * @return Địa chỉ email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Thiết lập địa chỉ email cho người dùng
     * @param email Địa chỉ email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Kiểm tra vai trò của người dùng
     * @return true nếu là Quản trị viên (Admin), false nếu là Phóng viên (Reporter)
     */
    public boolean isRole() {
        return role;
    }

    /**
     * Thiết lập vai trò cho người dùng
     * @param role true = Quản trị viên (Admin), false = Phóng viên (Reporter)
     */
    public void setRole(boolean role) {
        this.role = role;
    }

    /**
     * Lấy đường dẫn đến ảnh đại diện của người dùng
     * @return Đường dẫn ảnh đại diện (có thể null nếu chưa có ảnh)
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Thiết lập đường dẫn đến ảnh đại diện cho người dùng
     * @param imagePath Đường dẫn ảnh đại diện
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Lấy Google ID của người dùng (nếu đăng nhập bằng Google OAuth)
     * @return Google ID (có thể null nếu chưa đăng nhập bằng Google)
     */
    public String getGoogleId() {
        return googleId;
    }

    /**
     * Thiết lập Google ID cho người dùng
     * @param googleId Google ID từ Google OAuth
     */
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    /**
     * Lấy phương thức xác thực của người dùng
     * @return 'local' (đăng nhập thường), 'google' (OAuth), hoặc 'both' (cả hai)
     */
    public String getAuthProvider() {
        return authProvider;
    }

    /**
     * Thiết lập phương thức xác thực cho người dùng
     * @param authProvider 'local', 'google', hoặc 'both'
     */
    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    /**
     * Kiểm tra trạng thái hoạt động của tài khoản
     * @return true nếu tài khoản đang hoạt động, false nếu bị khóa
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Thiết lập trạng thái hoạt động cho tài khoản
     * @param enabled true = Hoạt động, false = Bị khóa
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Kiểm tra xem người dùng có phải là Super Admin không
     * 
     * Super Admin được xác định dựa trên ID:
     * - ID bắt đầu bằng "super" (không phân biệt hoa thường)
     * - Hoặc ID chính xác là "superadmin" (không phân biệt hoa thường)
     * 
     * Super Admin có quyền cao nhất trong hệ thống, có thể:
     * - Quản lý tất cả Admin và Reporter
     * - Xóa Admin khác
     * - Tạo Super Admin mới
     * 
     * @return true nếu là Super Admin, false nếu không
     */
    public boolean isSuperAdmin() {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        String lowerId = id.trim().toLowerCase();
        return lowerId.startsWith("super") || lowerId.equals("superadmin");
    }
}