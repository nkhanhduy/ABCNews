package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import poly.com.entity.Newsletter;
import poly.com.util.JDBCHelper;

/**
 * DAO quản lý thao tác database cho entity Newsletter (Email đăng ký)
 * Cung cấp các phương thức: insert, update, delete, find, findAll, searchAndFilter, count
 */
public class NewsletterDAO {

    /**
     * Thêm một email đăng ký nhận tin
     * @param entity Đối tượng Newsletter
     */
    public void insert(Newsletter entity) {
        String sql = "INSERT INTO Newsletters (Email, Enabled, SubscribedDate) VALUES (?, ?, ?)";
        JDBCHelper.executeUpdate(sql, entity.getEmail(), entity.isEnabled(), entity.getSubscribedDate());
    }

    /**
     * Cập nhật trạng thái email (Enabled) và SubscribedDate
     * @param entity Đối tượng Newsletter
     */
    public void update(Newsletter entity) {
        String sql = "UPDATE Newsletters SET Enabled = ?, SubscribedDate = ? WHERE Email = ?";
        JDBCHelper.executeUpdate(sql, entity.isEnabled(), entity.getSubscribedDate(), entity.getEmail());
    }

    /**
     * Xóa một email
     * @param email Email cần xóa
     */
    public void delete(String email) {
        String sql = "DELETE FROM Newsletters WHERE Email = ?";
        JDBCHelper.executeUpdate(sql, email);
    }

    /**
     * Lấy tất cả các email đã đăng ký
     * @return Danh sách Newsletter
     */
    public List<Newsletter> findAll() {
        String sql = "SELECT * FROM Newsletters";
        return selectBySql(sql);
    }

    /**
     * Tìm email
     * @param email Email cần tìm
     * @return Đối tượng Newsletter hoặc null
     */
    public Newsletter findById(String email) {
        String sql = "SELECT * FROM Newsletters WHERE Email = ?";
        List<Newsletter> list = selectBySql(sql, email);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Tìm kiếm email đăng ký theo từ khóa
     * 
     * Phương thức này cho phép tìm kiếm email theo từ khóa (LIKE search).
     * Tìm kiếm trong cột Email với pattern LIKE '%keyword%'.
     * 
     * @param searchKeyword Từ khóa tìm kiếm trong Email (null hoặc empty để lấy tất cả)
     * @return Danh sách Newsletter đã được lọc, sắp xếp theo ngày đăng ký mới nhất
     */
    public List<Newsletter> searchByKeyword(String searchKeyword) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return findAll();
        }
        String sql = "SELECT * FROM Newsletters WHERE Email LIKE ? ORDER BY SubscribedDate DESC";
        String keyword = "%" + searchKeyword.trim() + "%";
        return selectBySql(sql, keyword);
    }
    
    /**
     * Đếm tổng số lượng email đăng ký
     * @return Tổng số lượng email đăng ký
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Newsletters";
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
            throw new RuntimeException("Lỗi đếm tổng số lượng email đăng ký", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }
    
    /**
     * Đếm số lượng email đăng ký theo trạng thái (enabled)
     * 
     * Phương thức này thực hiện SELECT COUNT(*) với điều kiện WHERE Enabled = ?.
     * Dùng để hiển thị thống kê trong dashboard và admin page.
     * 
     * @param enabled true nếu đếm email đang hoạt động, false nếu đếm email đã hủy
     * @return Số lượng email thuộc trạng thái đó
     * @throws RuntimeException nếu có lỗi SQL khi thực thi query
     */
    public int countByEnabled(boolean enabled) {
        String sql = "SELECT COUNT(*) FROM Newsletters WHERE Enabled = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, enabled);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi đếm số lượng email theo trạng thái", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }
    
    /**
     * Lấy danh sách email đăng ký theo trạng thái enabled
     * 
     * Phương thức này lấy tất cả email có trạng thái enabled = true hoặc false.
     * Dùng để lấy danh sách subscribers đang hoạt động để gửi email newsletter.
     * 
     * @param enabled true để lấy email đang hoạt động, false để lấy email đã hủy
     * @return Danh sách Newsletter theo trạng thái enabled
     */
    public List<Newsletter> findByEnabled(boolean enabled) {
        String sql = "SELECT * FROM Newsletters WHERE Enabled = ? ORDER BY SubscribedDate DESC";
        return selectBySql(sql, enabled);
    }
    
    /**
     * Tìm kiếm và lọc email đăng ký với nhiều điều kiện
     * 
     * Phương thức này cho phép tìm kiếm và lọc email theo:
     * - Từ khóa tìm kiếm (trong Email)
     * - Trạng thái enabled (hoạt động/hủy)
     * 
     * @param searchKeyword Từ khóa tìm kiếm trong Email (null hoặc empty để bỏ qua)
     * @param filterEnabled Trạng thái enabled để lọc (null để lấy tất cả, true = hoạt động, false = hủy)
     * @return Danh sách Newsletter đã được lọc, sắp xếp theo ngày đăng ký mới nhất
     */
    public List<Newsletter> searchAndFilter(String searchKeyword, Boolean filterEnabled) {
        StringBuilder sql = new StringBuilder("SELECT * FROM Newsletters WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Tìm kiếm theo từ khóa
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND Email LIKE ?");
            params.add("%" + searchKeyword.trim() + "%");
        }
        
        // Lọc theo trạng thái enabled
        if (filterEnabled != null) {
            sql.append(" AND Enabled = ?");
            params.add(filterEnabled);
        }
        
        // Sắp xếp theo ngày đăng ký mới nhất
        sql.append(" ORDER BY SubscribedDate DESC");
        
        return selectBySql(sql.toString(), params.toArray());
    }

    /**
     * Phương thức nội bộ để ánh xạ dữ liệu
     */
    private List<Newsletter> selectBySql(String sql, Object... args) {
        List<Newsletter> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, args);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Newsletter entity = new Newsletter();
                entity.setEmail(rs.getString("Email"));
                entity.setEnabled(rs.getBoolean("Enabled"));
                entity.setSubscribedDate(rs.getTimestamp("SubscribedDate"));
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