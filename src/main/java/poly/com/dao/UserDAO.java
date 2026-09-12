package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import poly.com.entity.User;
import poly.com.util.JDBCHelper;
import poly.com.util.PasswordUtil;

/**
 * Lớp DAO (Data Access Object) để thao tác với bảng Users trong database
 * 
 * Lớp này cung cấp các phương thức để:
 * - Thêm, sửa, xóa người dùng (CRUD operations)
 * - Tìm kiếm người dùng theo các tiêu chí khác nhau (ID, email, Google ID)
 * - Kiểm tra sự tồn tại của ID hoặc email
 * - Tạo mã người dùng tự động theo vai trò
 * - Đếm số lượng người dùng theo vai trò
 * 
 * Tất cả các phương thức sử dụng PreparedStatement để tránh SQL Injection.
 * 
 * @author ABCNews Development Team
 * @version 1.0
 */
public class UserDAO {

    /**
     * Thêm một người dùng mới vào cơ sở dữ liệu
     * 
     * Phương thức này thực hiện INSERT vào bảng Users với tất cả các trường:
     * - Id, Password, Fullname, Birthday, Gender, Mobile, Email
     * - Role (vai trò: Admin/Reporter)
     * - ImagePath (đường dẫn ảnh đại diện)
     * - GoogleId, AuthProvider (cho OAuth)
     * - Enabled (trạng thái hoạt động)
     * 
     * @param entity Đối tượng User chứa đầy đủ thông tin người dùng cần thêm
     *               Phải có ID hợp lệ và không trùng với ID đã tồn tại
     * @throws RuntimeException nếu có lỗi SQL (ví dụ: duplicate key)
     */
    public void insert(User entity) {
        String hashedPassword = PasswordUtil.ensureHashed(entity.getPassword());
        String sql = "INSERT INTO Users (Id, Password, Fullname, Birthday, Gender, Mobile, Email, Role, ImagePath, GoogleId, AuthProvider, Enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JDBCHelper.executeUpdate(sql, 
                entity.getId(), 
                hashedPassword,
                entity.getFullname(), 
                entity.getBirthday(), 
                entity.isGender(), 
                entity.getMobile(), 
                entity.getEmail(), 
                entity.isRole(),
                entity.getImagePath(),
                entity.getGoogleId(),
                entity.getAuthProvider(),
                entity.isEnabled());
    }

    /**
     * Cập nhật thông tin người dùng đã tồn tại trong cơ sở dữ liệu
     * 
     * Phương thức này thực hiện UPDATE tất cả các trường của User (trừ ID).
     * ID được dùng làm điều kiện WHERE để xác định user cần cập nhật.
     * Mật khẩu sẽ được tự động kiểm tra và băm BCrypt nếu chưa được băm.
     * 
     * @param entity Đối tượng User chứa thông tin cần cập nhật
     *               Phải có ID hợp lệ và tồn tại trong database
     * @throws RuntimeException nếu có lỗi SQL hoặc user không tồn tại
     */
    public void update(User entity) {
        String hashedPassword = PasswordUtil.ensureHashed(entity.getPassword());
        String sql = "UPDATE Users SET Password = ?, Fullname = ?, Birthday = ?, Gender = ?, Mobile = ?, Email = ?, Role = ?, ImagePath = ?, GoogleId = ?, AuthProvider = ?, Enabled = ? WHERE Id = ?";
        JDBCHelper.executeUpdate(sql, 
                hashedPassword, 
                entity.getFullname(), 
                entity.getBirthday(), 
                entity.isGender(), 
                entity.getMobile(), 
                entity.getEmail(), 
                entity.isRole(), 
                entity.getImagePath(),
                entity.getGoogleId(),
                entity.getAuthProvider(),
                entity.isEnabled(),
                entity.getId());
    }

    /**
     * Cập nhật mật khẩu của user (dùng cho Forgot Password feature)
     * 
     * @param userId ID của user cần update password
     * @param hashedPassword Password đã được hash (BCrypt)
     * @return true nếu update thành công, false nếu thất bại
     */
    public boolean updatePassword(String userId, String hashedPassword) {
        String sql = "UPDATE Users SET Password = ? WHERE Id = ?";
        
        try {
            int rowsAffected = JDBCHelper.executeUpdate(sql, hashedPassword, userId);
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa một người dùng khỏi cơ sở dữ liệu
     * 
     * Phương thức này thực hiện DELETE từ bảng Users theo ID.
     * 
     * [LUU Y]: Nếu user này là tác giả của các bài viết (News),
     * cần xử lý foreign key constraint trước (ví dụ: set Author = NULL).
     * 
     * @param id Mã người dùng (ID) cần xóa
     * @throws RuntimeException nếu có lỗi SQL (ví dụ: foreign key constraint violation)
     */
    public void delete(String id) {
        String sql = "DELETE FROM Users WHERE Id = ?";
        JDBCHelper.executeUpdate(sql, id);
    }

    /**
     * Lấy tất cả người dùng từ cơ sở dữ liệu
     * 
     * Phương thức này thực hiện SELECT * FROM Users để lấy toàn bộ danh sách người dùng.
     * 
     * Lưu ý: Với database lớn, nên sử dụng phân trang (pagination) thay vì lấy tất cả.
     * 
     * @return Danh sách (List) tất cả các đối tượng User trong database
     *         Danh sách rỗng nếu không có user nào
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM Users";
        return selectBySql(sql);
    }

    /**
     * Tìm một người dùng theo mã định danh (ID)
     * 
     * Phương thức này thực hiện SELECT với điều kiện WHERE Id = ?.
     * ID là PRIMARY KEY nên chỉ trả về tối đa 1 kết quả.
     * 
     * @param id Mã người dùng (ID) cần tìm (không phân biệt hoa thường trong SQL)
     * @return Đối tượng User tìm thấy, hoặc null nếu không tìm thấy user với ID đó
     */
    public User findById(String id) {
        String sql = "SELECT * FROM Users WHERE Id = ?";
        List<User> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * Kiểm tra xem mã người dùng (ID) đã tồn tại trong database chưa
     * 
     * Phương thức này dùng để validate trước khi tạo user mới,
     * tránh lỗi duplicate key exception.
     * 
     * @param id Mã người dùng cần kiểm tra
     * @return true nếu ID đã tồn tại trong database, false nếu chưa tồn tại
     * @throws RuntimeException nếu có lỗi SQL khi thực thi query
     */
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, id);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi kiểm tra mã người dùng", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return false;
    }
    
    /**
     * Tạo mã người dùng tiếp theo tự động theo vai trò (role)
     * 
     * Format mã: {rolePrefix}{số thứ tự 3 chữ số với leading zeros}
     * - Super Admin: super001, super002, super003, ...
     * - Admin: admin001, admin002, admin003, ...
     * - Reporter: rep001, rep002, rep003, ...
     * 
     * Logic tạo mã:
     * 1. Lấy tất cả các ID hiện có của role này từ database
     * 2. Parse số từ các ID (ví dụ: "admin001" → 1, "admin002" → 2)
     * 3. Tìm số nhỏ nhất chưa được sử dụng (tái sử dụng mã đã xóa)
     * 4. Nếu tất cả số từ 1 đến max đều đã dùng, tăng lên max + 1
     * 5. Format số thành 3 chữ số với leading zeros (001, 002, ...)
     * 
     * Ví dụ: Nếu có admin001, admin003 → sẽ tạo admin002 (tái sử dụng số 2)
     * 
     * @param roleType Loại vai trò:
     *                 - "super" hoặc "superadmin" → Super Admin (prefix: "super")
     *                 - "true" hoặc boolean true → Admin (prefix: "admin")
     *                 - "false" hoặc boolean false → Reporter (prefix: "rep")
     * @return Mã người dùng tiếp theo (ví dụ: "super001", "admin001", "rep002")
     * @throws RuntimeException nếu có lỗi SQL khi query database
     */
    public String getNextUserId(String roleType) {
        String prefix;
        boolean isAdmin;
        
        if ("super".equalsIgnoreCase(roleType)) {
            prefix = "super";
            isAdmin = true; // Super Admin cũng có role = true trong DB
        } else {
            boolean isAdminBool = "true".equalsIgnoreCase(roleType) || Boolean.parseBoolean(roleType);
            prefix = isAdminBool ? "admin" : "rep";
            isAdmin = isAdminBool;
        }
        
        String pattern = prefix + "%";
        
        // Lấy tất cả các mã hiện có của role này
        // Super Admin: chỉ lấy ID bắt đầu bằng "super"
        // Admin: lấy ID bắt đầu bằng "admin" và Role = true (nhưng không phải Super Admin)
        // Reporter: lấy ID bắt đầu bằng "rep" và Role = false
        String sql;
        if ("super".equalsIgnoreCase(roleType)) {
            // Super Admin: chỉ kiểm tra ID pattern, không cần check Role
            sql = "SELECT Id FROM Users WHERE Id LIKE ? ORDER BY Id";
        } else {
            // Admin hoặc Reporter: check cả Role và ID pattern
            sql = "SELECT Id FROM Users WHERE Role = ? AND Id LIKE ? ORDER BY Id";
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if ("super".equalsIgnoreCase(roleType)) {
                pstmt = JDBCHelper.getPreparedStatement(sql, pattern);
            } else {
                pstmt = JDBCHelper.getPreparedStatement(sql, isAdmin, pattern);
            }
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            // Tập hợp các số đã được sử dụng
            Set<Integer> usedNumbers = new HashSet<>();
            int maxNumber = 0;
            
            while (rs.next()) {
                String id = rs.getString("Id");
                if (id != null && id.length() > prefix.length()) {
                    try {
                        String numberPart = id.substring(prefix.length());
                        int number = Integer.parseInt(numberPart);
                        usedNumbers.add(number);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua nếu không parse được
                    }
                }
            }
            
            // Tìm số nhỏ nhất chưa được sử dụng (từ 1 đến maxNumber)
            int nextNumber = 1;
            
            // Nếu không có mã nào, bắt đầu từ 1
            if (maxNumber == 0) {
                nextNumber = 1;
            } else {
                // Tìm số nhỏ nhất chưa được sử dụng
                boolean found = false;
                for (int i = 1; i <= maxNumber; i++) {
                    if (!usedNumbers.contains(i)) {
                        nextNumber = i;
                        found = true;
                        break;
                    }
                }
                // Nếu tất cả số từ 1 đến maxNumber đều đã dùng, tăng lên maxNumber + 1
                if (!found) {
                    nextNumber = maxNumber + 1;
                }
            }
            
            // Format: {prefix}{số 3 chữ số với leading zeros}
            return String.format("%s%03d", prefix, nextNumber);
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi tạo mã người dùng", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
    }
    
    /**
     * Tìm một người dùng theo email và mật khẩu (dùng cho đăng nhập thường)
     * 
     * Phương thức này thực hiện SELECT với điều kiện WHERE Email = ? AND Password = ?.
     * 
     * [LUU Y BAO MAT]: 
     * - Mật khẩu hiện tại được so sánh plain text (không an toàn)
     * - Nên hash mật khẩu và so sánh hash trong production
     * 
     * @param email Email đăng nhập (phải khớp chính xác, không phân biệt hoa thường trong SQL)
     * @param password Mật khẩu đăng nhập (phải khớp chính xác)
     * @return Đối tượng User tìm thấy nếu email và password đúng,
     *         hoặc null nếu không tìm thấy hoặc sai mật khẩu
     */
    public User findByEmailAndPassword(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        User user = findByEmail(email.trim());
        if (user == null) {
            return null;
        }

        String dbPassword = user.getPassword();
        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            return null;
        }

        boolean isMatch = false;
        boolean needsRehash = false;

        // 1. Kiểm tra nếu mật khẩu trong DB đã là chuẩn BCrypt hash
        if (PasswordUtil.isBCryptHash(dbPassword)) {
            isMatch = PasswordUtil.verifyPassword(password, dbPassword);
        } 
        // 2. Cơ chế Graceful Auto-Migration: Hỗ trợ dữ liệu mật khẩu cũ (plain text)
        else if (dbPassword.equals(password)) {
            isMatch = true;
            needsRehash = true; // Đánh dấu để tự động nâng cấp sang BCrypt
        }

        if (isMatch) {
            // Tự động nâng cấp mật khẩu cũ sang BCrypt hash ngay trong lần đăng nhập đầu tiên
            if (needsRehash) {
                try {
                    String newHashedPassword = PasswordUtil.hashPassword(password);
                    updatePassword(user.getId(), newHashedPassword);
                    user.setPassword(newHashedPassword);
                } catch (Exception e) {
                    System.err.println("[CẢNH BÁO] Không thể tự động rehash mật khẩu cho user " + user.getId() + ": " + e.getMessage());
                }
            }
            return user;
        }

        return null;
    }
    
    /**
     * Tìm một người dùng theo email (không phân biệt phương thức xác thực)
     * 
     * Phương thức này dùng để tìm user khi đăng nhập bằng Google OAuth.
     * Email là unique trong hệ thống, nên chỉ trả về tối đa 1 kết quả.
     * 
     * @param email Email cần tìm (phải khớp chính xác)
     * @return Đối tượng User tìm thấy, hoặc null nếu không tìm thấy user với email đó
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        List<User> list = selectBySql(sql, email);
        return list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * Kiểm tra xem email đã tồn tại trong database chưa
     * 
     * Phương thức này dùng để validate trước khi tạo user mới hoặc cập nhật email,
     * tránh lỗi duplicate key exception (email phải unique).
     * 
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại trong database, false nếu chưa tồn tại
     * @throws RuntimeException nếu có lỗi SQL khi thực thi query
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, email);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi kiểm tra email", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return false;
    }
    
    /**
     * Tìm một người dùng theo Google ID (dùng cho Google OAuth)
     * 
     * Phương thức này dùng để tìm user đã đăng nhập bằng Google trước đó.
     * GoogleId có thể null nếu user chưa từng đăng nhập bằng Google.
     * 
     * @param googleId Google ID từ Google OAuth token (Subject ID)
     * @return Đối tượng User tìm thấy, hoặc null nếu không tìm thấy user với GoogleId đó
     */
    public User findByGoogleId(String googleId) {
        String sql = "SELECT * FROM Users WHERE GoogleId = ?";
        List<User> list = selectBySql(sql, googleId);
        return list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * Đếm tổng số lượng người dùng trong hệ thống
     * 
     * Phương thức này thực hiện SELECT COUNT(*) FROM Users.
     * Dùng để hiển thị thống kê trong dashboard.
     * 
     * @return Tổng số lượng người dùng (bao gồm cả Admin và Reporter)
     * @throws RuntimeException nếu có lỗi SQL khi thực thi query
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Users";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi đếm tổng số lượng người dùng", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }
    
    /**
     * Đếm số lượng người dùng theo vai trò (Admin hoặc Reporter)
     * 
     * Phương thức này thực hiện SELECT COUNT(*) với điều kiện WHERE Role = ?.
     * Dùng để hiển thị thống kê trong dashboard.
     * 
     * Lưu ý: Super Admin cũng có Role = true, nên sẽ được đếm vào Admin.
     * 
     * @param isAdmin true nếu đếm Admin (bao gồm cả Super Admin), 
     *                false nếu đếm Reporter
     * @return Số lượng người dùng thuộc vai trò đó
     * @throws RuntimeException nếu có lỗi SQL khi thực thi query
     */
    public int countByRole(boolean isAdmin) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Role = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, isAdmin);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi đếm số lượng người dùng theo vai trò", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }
    
    /**
     * Đếm số lượng người dùng theo vai trò và trạng thái (enabled)
     * 
     * Phương thức này thực hiện SELECT COUNT(*) với điều kiện WHERE Role = ? AND Enabled = ?.
     * Dùng để hiển thị thống kê chi tiết trong dashboard (ví dụ: Admin hoạt động, Admin bị khóa).
     * 
     * @param isAdmin true nếu đếm Admin (bao gồm cả Super Admin), false nếu đếm Reporter
     * @param enabled true nếu đếm người dùng đang hoạt động, false nếu đếm người dùng bị khóa
     * @return Số lượng người dùng thuộc vai trò và trạng thái đó
     * @throws RuntimeException nếu có lỗi SQL khi thực thi query
     */
    public int countByRoleAndEnabled(boolean isAdmin, boolean enabled) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Role = ? AND Enabled = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, isAdmin, enabled);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi đếm số lượng người dùng theo vai trò và trạng thái", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }

    /**
     * Tìm kiếm và lọc người dùng với nhiều điều kiện
     * 
     * Phương thức này cho phép tìm kiếm và lọc người dùng theo:
     * - Từ khóa tìm kiếm (trong ID, Fullname, Email)
     * - Vai trò (Admin/Reporter/Super Admin)
     * - Trạng thái (Hoạt động/Bị khóa)
     * - Sắp xếp theo các trường khác nhau
     * 
     * @param searchKeyword Từ khóa tìm kiếm trong ID, Fullname, hoặc Email (null hoặc empty để bỏ qua)
     * @param filterRole Vai trò để lọc: "true" (Admin), "false" (Reporter), "super" (Super Admin), hoặc null (tất cả)
     * @param filterEnabled Trạng thái để lọc: true (Hoạt động), false (Bị khóa), hoặc null (tất cả)
     * @param sortBy Trường để sắp xếp: "id", "fullname", "email", "newsCount" (mặc định: "id")
     * @param sortOrder Thứ tự sắp xếp: "ASC" hoặc "DESC" (mặc định: "ASC")
     * @return Danh sách người dùng đã được lọc và sắp xếp
     */
    public List<User> searchAndFilter(String searchKeyword, String filterRole, Boolean filterEnabled, String sortBy, String sortOrder) {
        StringBuilder sql = new StringBuilder("SELECT * FROM Users WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Tìm kiếm theo từ khóa (ID, Fullname, Email)
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (Id LIKE ? OR Fullname LIKE ? OR Email LIKE ?)");
            String keyword = "%" + searchKeyword.trim() + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }
        
        // Lọc theo vai trò
        if (filterRole != null && !filterRole.trim().isEmpty()) {
            String roleStr = filterRole.trim().toLowerCase();
            if ("super".equals(roleStr)) {
                // Super Admin: ID bắt đầu bằng "super"
                sql.append(" AND Id LIKE ?");
                params.add("super%");
            } else if ("true".equals(roleStr)) {
                // Admin (bao gồm cả Super Admin vì Super Admin cũng có Role = true)
                sql.append(" AND Role = ?");
                params.add(true);
            } else if ("false".equals(roleStr)) {
                // Reporter
                sql.append(" AND Role = ?");
                params.add(false);
            }
        }
        
        // Lọc theo trạng thái (enabled)
        if (filterEnabled != null) {
            sql.append(" AND Enabled = ?");
            params.add(filterEnabled);
        }
        
        // Sắp xếp
        String orderBy = "Id";
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String sortField = sortBy.trim().toLowerCase();
            if (sortField.equals("fullname") || sortField.equals("full_name")) {
                orderBy = "Fullname";
            } else if (sortField.equals("email")) {
                orderBy = "Email";
            } else if (sortField.equals("id")) {
                orderBy = "Id";
            } else {
                // Mặc định là Id
                orderBy = "Id";
            }
        }
        
        String order = "ASC";
        if (sortOrder != null && !sortOrder.trim().isEmpty()) {
            String orderStr = sortOrder.trim().toUpperCase();
            if (orderStr.equals("DESC") || orderStr.equals("ASC")) {
                order = orderStr;
            }
        }
        
        sql.append(" ORDER BY ").append(orderBy).append(" ").append(order);
        
        return selectBySql(sql.toString(), params.toArray());
    }
    
    /**
     * Phương thức nội bộ (private) để thực thi câu lệnh SELECT và ánh xạ kết quả sang đối tượng User
     * 
     * Phương thức này:
     * 1. Thực thi câu lệnh SQL SELECT với các tham số
     * 2. Duyệt qua ResultSet và tạo đối tượng User cho mỗi hàng
     * 3. Ánh xạ các cột từ ResultSet sang các field của User
     * 4. Xử lý trường hợp cột Enabled có thể không tồn tại (backward compatibility)
     * 5. Đóng tất cả tài nguyên (ResultSet, PreparedStatement, Connection)
     * 
     * Được dùng bởi các phương thức public như findAll(), findById(), findByEmail(), v.v.
     * 
     * @param sql Câu lệnh SQL SELECT (có thể có placeholder ?)
     * @param args Các tham số để thay thế vào placeholder trong SQL (nếu có)
     * @return Danh sách các đối tượng User được tạo từ kết quả query
     * @throws RuntimeException nếu có lỗi SQL khi thực thi query
     */
    private List<User> selectBySql(String sql, Object... args) {
        List<User> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, args);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User entity = new User();
                entity.setId(rs.getString("Id"));
                entity.setPassword(rs.getString("Password"));
                entity.setFullname(rs.getString("Fullname"));
                entity.setBirthday(rs.getDate("Birthday"));
                entity.setGender(rs.getBoolean("Gender"));
                entity.setMobile(rs.getString("Mobile"));
                entity.setEmail(rs.getString("Email"));
                entity.setRole(rs.getBoolean("Role"));
                entity.setImagePath(rs.getString("ImagePath"));
                entity.setGoogleId(rs.getString("GoogleId"));
                entity.setAuthProvider(rs.getString("AuthProvider"));
                // Enabled: mặc định true nếu null (cho tương thích với dữ liệu cũ)
                try {
                    entity.setEnabled(rs.getBoolean("Enabled"));
                } catch (SQLException e) {
                    entity.setEnabled(true); // Mặc định enabled nếu cột chưa tồn tại
                }
                list.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return list;
    }
}