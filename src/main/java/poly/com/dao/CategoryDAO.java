package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import poly.com.entity.Category;
import poly.com.util.JDBCHelper;

/**
 * DAO quản lý thao tác database cho entity Category (Loại tin)
 * Cung cấp các phương thức: insert, update, delete, find, findAll, existsById
 */
public class CategoryDAO {

    /**
     * Thêm một loại tin mới vào CSDL
     * @param entity Đối tượng Category chứa thông tin loại tin
     */
    public void insert(Category entity) {
        String sql = "INSERT INTO Categories (Id, Name) VALUES (?, ?)";
        JDBCHelper.executeUpdate(sql, entity.getId(), entity.getName());
    }

    /**
     * Cập nhật thông tin loại tin trong CSDL
     * @param entity Đối tượng Category chứa thông tin cần cập nhật
     */
    public void update(Category entity) {
        String sql = "UPDATE Categories SET Name = ? WHERE Id = ?";
        JDBCHelper.executeUpdate(sql, entity.getName(), entity.getId());
    }

    /**
     * Xóa một loại tin khỏi CSDL
     * @param id Mã loại tin cần xóa
     */
    public void delete(String id) {
        String sql = "DELETE FROM Categories WHERE Id = ?";
        JDBCHelper.executeUpdate(sql, id);
    }

    /**
     * Lấy tất cả các loại tin từ CSDL
     * @return Danh sách (List) các đối tượng Category
     */
    public List<Category> findAll() {
        String sql = "SELECT * FROM Categories";
        return selectBySql(sql);
    }

    /**
     * Tìm một loại tin theo mã
     * @param id Mã loại tin cần tìm
     * @return Đối tượng Category tìm thấy, hoặc null nếu không tìm thấy
     */
    public Category findById(String id) {
        String sql = "SELECT * FROM Categories WHERE Id = ?";
        List<Category> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * Kiểm tra xem mã loại tin (Id) đã tồn tại chưa
     * @param id Mã loại tin cần kiểm tra
     * @return true nếu ID đã tồn tại, false nếu chưa tồn tại
     */
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM Categories WHERE Id = ?";
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
            throw new RuntimeException("Lỗi kiểm tra mã loại tin", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return false;
    }

    /**
     * Đếm tổng số lượng loại tin
     * @return Tổng số lượng loại tin
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Categories";
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
            throw new RuntimeException("Lỗi đếm tổng số lượng loại tin", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }

    /**
     * Phương thức nội bộ để thực thi câu lệnh SELECT
     * @param sql Câu lệnh SQL
     * @param args Các tham số cho câu lệnh (nếu có)
     * @return Danh sách các đối tượng Category
     */
    private List<Category> selectBySql(String sql, Object... args) {
        List<Category> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Tạo PreparedStatement
            pstmt = JDBCHelper.getPreparedStatement(sql, args);
            // Lấy Connection từ PreparedStatement
            conn = pstmt.getConnection(); 
            // Thực thi truy vấn
            rs = pstmt.executeQuery();
            
            // Xử lý kết quả
            while (rs.next()) {
                Category entity = new Category();
                entity.setId(rs.getString("Id"));
                entity.setName(rs.getString("Name"));
                list.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            // Đóng tất cả tài nguyên
            JDBCHelper.close(rs, pstmt, conn);
        }
        return list;
    }
}