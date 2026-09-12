package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import poly.com.entity.Category;
import poly.com.exception.DuplicateSlugException;
import poly.com.util.JDBCHelper;
import poly.com.util.ValidationHelper;

/**
 * DAO quản lý thao tác database cho entity Category (Loại tin)
 * Cung cấp các phương thức: insert, update, delete, find, findAll, existsById
 */
public class CategoryDAO {

    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    /**
     * Thêm một loại tin mới vào CSDL
     * @param entity Đối tượng Category chứa thông tin loại tin
     */
    public void insert(Category entity) {
        String sql = "INSERT INTO Categories (Id, Name, Slug) VALUES (?, ?, ?)";
        try {
            JDBCHelper.executeUpdate(sql, entity.getId(), entity.getName(), entity.getSlug());
        } catch (RuntimeException e) {
            if (ValidationHelper.isDuplicateSlugException(e)) {
                throw new DuplicateSlugException("Đường dẫn thân thiện (slug) \"" + entity.getSlug() + "\" đã tồn tại trong hệ thống.", e, entity.getSlug());
            }
            throw e;
        }
    }

    /**
     * Cập nhật thông tin loại tin trong CSDL
     * @param entity Đối tượng Category chứa thông tin cần cập nhật
     */
    public void update(Category entity) {
        String sql = "UPDATE Categories SET Name = ?, Slug = ? WHERE Id = ?";
        try {
            JDBCHelper.executeUpdate(sql, entity.getName(), entity.getSlug(), entity.getId());
        } catch (RuntimeException e) {
            if (ValidationHelper.isDuplicateSlugException(e)) {
                throw new DuplicateSlugException("Đường dẫn thân thiện (slug) \"" + entity.getSlug() + "\" đã tồn tại trong hệ thống.", e, entity.getSlug());
            }
            throw e;
        }
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
            LOGGER.log(Level.SEVERE, "Lỗi kiểm tra mã loại tin: {0}", e.getMessage());
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
            LOGGER.log(Level.SEVERE, "Lỗi đếm tổng số lượng loại tin: {0}", e.getMessage());
            throw new RuntimeException("Lỗi đếm tổng số lượng loại tin", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }

    /**
     * Tìm một loại tin theo slug
     * @param slug Chuỗi slug cần tìm
     * @return Đối tượng Category tìm thấy, hoặc null nếu không tìm thấy
     */
    public Category findBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM Categories WHERE Slug = ?";
        List<Category> list = selectBySql(sql, slug.trim());
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Kiểm tra xem slug loại tin đã tồn tại chưa
     * @param slug Chuỗi slug cần kiểm tra
     * @return true nếu slug đã tồn tại, false nếu chưa
     */
    public boolean existsBySlug(String slug) {
        return existsBySlugExcludingId(slug, null);
    }

    /**
     * Kiểm tra xem slug loại tin đã tồn tại chưa (loại trừ một ID cụ thể, dùng khi cập nhật)
     * @param slug Chuỗi slug cần kiểm tra
     * @param excludeId ID danh mục cần loại trừ khỏi việc kiểm tra (có thể null)
     * @return true nếu slug đã bị danh mục khác chiếm dụng, false nếu chưa
     */
    public boolean existsBySlugExcludingId(String slug, String excludeId) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }
        String sql;
        Object[] params;
        if (excludeId == null || excludeId.trim().isEmpty()) {
            sql = "SELECT COUNT(*) FROM Categories WHERE Slug = ?";
            params = new Object[]{slug.trim()};
        } else {
            sql = "SELECT COUNT(*) FROM Categories WHERE Slug = ? AND Id <> ?";
            params = new Object[]{slug.trim(), excludeId.trim()};
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, params);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi kiểm tra trùng lặp slug loại tin: {0}", e.getMessage());
            throw new RuntimeException("Lỗi kiểm tra trùng lặp slug loại tin", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return false;
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
                try {
                    entity.setSlug(rs.getString("Slug"));
                } catch (SQLException ignored) {
                    // Fallback nếu câu query chưa có cột Slug
                }
                list.add(entity);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn dữ liệu Category: {0}", e.getMessage());
            throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            // Đóng tất cả tài nguyên
            JDBCHelper.close(rs, pstmt, conn);
        }
        return list;
    }
}