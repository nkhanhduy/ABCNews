package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import poly.com.entity.News;
import poly.com.util.JDBCHelper;

/**
 * DAO quản lý thao tác database cho entity News (Tin tức)
 * Cung cấp các phương thức: insert, update, delete, find, search, count, filter theo category/author
 */
public class NewsDAO {

    private static final Logger LOGGER = Logger.getLogger(NewsDAO.class.getName());

    /**
     * Thêm bản tin mới
     */
    public void insert(News entity) {
        String sql = "INSERT INTO News (Id, Title, Summary, Content, Image, PostedDate, Author, ViewCount, CategoryId, Home) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JDBCHelper.executeUpdate(sql,
                entity.getId(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getContent(),
                entity.getImage(),
                entity.getPostedDate(),
                entity.getAuthor(),
                entity.getViewCount(),
                entity.getCategoryId(),
                entity.isHome());
    }

    /**
     * Cập nhật bản tin
     */
    public void update(News entity) {
        String sql = "UPDATE News SET Title = ?, Summary = ?, Content = ?, Image = ?, PostedDate = ?, Author = ?, ViewCount = ?, CategoryId = ?, Home = ? WHERE Id = ?";
        JDBCHelper.executeUpdate(sql,
                entity.getTitle(),
                entity.getSummary(),
                entity.getContent(),
                entity.getImage(),
                entity.getPostedDate(),
                entity.getAuthor(),
                entity.getViewCount(),
                entity.getCategoryId(),
                entity.isHome(),
                entity.getId());
    }

    /**
     * Xóa bản tin
     */
    public void delete(String id) {
        String sql = "DELETE FROM News WHERE Id = ?";
        JDBCHelper.executeUpdate(sql, id);
    }
    
    /**
     * Đếm số bài viết của một tác giả
     * @param authorId Mã tác giả (User ID)
     * @return Số lượng bài viết
     */
    public int countByAuthor(String authorId) {
        String sql = "SELECT COUNT(*) FROM News WHERE Author = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, authorId);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi đếm số bài viết của tác giả: {0}", e.getMessage());
            throw new RuntimeException("Lỗi đếm số bài viết của tác giả", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }
    
    /**
     * Set author = NULL cho tất cả bài viết của một tác giả
     * (Dùng khi xóa user để tránh lỗi foreign key constraint)
     * @param authorId Mã tác giả (User ID)
     */
    public void setAuthorToNull(String authorId) {
        String sql = "UPDATE News SET Author = NULL WHERE Author = ?";
        JDBCHelper.executeUpdate(sql, authorId);
    }

    /**
     * Tăng lượt xem cho bản tin (Yêu cầu khi xem chi tiết)
     */
    public void incrementViewCount(String id) {
        String sql = "UPDATE News SET ViewCount = ViewCount + 1 WHERE Id = ?";
        JDBCHelper.executeUpdate(sql, id);
    }

    /**
     * Lấy tất cả bản tin
     */
    public List<News> findAll() {
        String sql = "SELECT * FROM News ORDER BY PostedDate DESC";
        return selectBySql(sql);
    }

    /**
     * Tìm bản tin theo ID
     */
    public News findById(String id) {
        String sql = "SELECT * FROM News WHERE Id = ?";
        List<News> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * Tự động tạo mã bản tin theo chuẩn UUID v4 (RFC 4122)
     * Đảm bảo tính duy nhất toàn cầu và ngăn chặn rà quét ID thực trên URL công khai
     * @param categoryId Mã loại tin (tùy chọn)
     * @return Chuỗi UUID v4 chuẩn (ví dụ: "c4b3a1d2-7e8f-4a5b-9c0d-1e2f3a4b5c6d")
     */
    public String getNextNewsId(String categoryId) {
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * Kiểm tra xem mã bản tin (Id) đã tồn tại chưa
     * @param id Mã bản tin cần kiểm tra
     * @return true nếu ID đã tồn tại, false nếu chưa tồn tại
     */
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM News WHERE Id = ?";
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
            LOGGER.log(Level.SEVERE, "Lỗi kiểm tra mã bản tin: {0}", e.getMessage());
            throw new RuntimeException("Lỗi kiểm tra mã bản tin", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return false;
    }
    
    /**
     * Lấy các bản tin theo Mã loại tin (CategoryId)
     */
    public List<News> findByCategoryId(String categoryId) {
        String sql = "SELECT * FROM News WHERE CategoryId = ? ORDER BY PostedDate DESC";
        return selectBySql(sql, categoryId);
    }
    
    /**
     * Lấy các bản tin được đánh dấu "Trang nhất" (Home = true)
     */
    public List<News> findHomeNews() {
        String sql = "SELECT * FROM News WHERE Home = 1 ORDER BY PostedDate DESC";
        return selectBySql(sql);
    }
    
    /**
     * Lấy 5 bản tin mới nhất (Yêu cầu Sidebar)
     */
    public List<News> findTop5Newest() {
        String sql = "SELECT TOP 5 * FROM News ORDER BY PostedDate DESC";
        return selectBySql(sql);
    }
    
    /**
     * Lấy 5 bản tin xem nhiều nhất (Yêu cầu Sidebar)
     */
    public List<News> findTop5HotNews() {
        String sql = "SELECT TOP 5 * FROM News ORDER BY ViewCount DESC";
        return selectBySql(sql);
    }

    /**
     * Lấy các bản tin cùng loại (trừ bản tin đang xem)
     */
    public List<News> findRelatedNews(String categoryId, String currentNewsId) {
        String sql = "SELECT TOP 5 * FROM News WHERE CategoryId = ? AND Id != ? ORDER BY PostedDate DESC";
        return selectBySql(sql, categoryId, currentNewsId);
    }
    
    /**
     * Lấy tất cả bản tin của một tác giả (Phóng viên)
     * @param authorId Mã tác giả
     * @return Danh sách tin tức của tác giả đó
     */
    public List<News> findByAuthor(String authorId) {
        String sql = "SELECT * FROM News WHERE Author = ? ORDER BY PostedDate DESC";
        return selectBySql(sql, authorId);
    }
    
    /**
     * Đếm số lượng bản tin theo CategoryId
     * @param categoryId Mã loại tin
     * @return Số lượng bản tin thuộc loại tin đó
     */
    public int countByCategoryId(String categoryId) {
        String sql = "SELECT COUNT(*) FROM News WHERE CategoryId = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, categoryId);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi đếm số lượng tin tức theo loại tin: {0}", e.getMessage());
            throw new RuntimeException("Lỗi đếm số lượng tin tức", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }
    
    /**
     * Đếm tổng số lượng bản tin
     * @return Tổng số lượng bản tin
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM News";
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
            LOGGER.log(Level.SEVERE, "Lỗi đếm tổng số lượng tin tức: {0}", e.getMessage());
            throw new RuntimeException("Lỗi đếm tổng số lượng tin tức", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }
    
    /**
     * Lấy các bản tin mới nhất (7 ngày qua)
     * @return Danh sách tin tức mới nhất
     */
    public List<News> findRecentNews(int days) {
        String sql = "SELECT TOP 10 * FROM News WHERE PostedDate >= DATEADD(day, -?, GETDATE()) ORDER BY PostedDate DESC";
        return selectBySql(sql, days);
    }
    
    /**
     * Tìm kiếm và lọc bản tin với nhiều điều kiện
     * @param title Từ khóa tìm kiếm trong tiêu đề (null hoặc empty để bỏ qua)
     * @param categoryId Mã loại tin để lọc (null hoặc empty để bỏ qua)
     * @param authorId Mã tác giả để lọc (null hoặc empty để bỏ qua)
     * @param sortBy Trường để sắp xếp: "postedDate" hoặc "viewCount" (mặc định: "postedDate")
     * @param sortOrder Thứ tự sắp xếp: "ASC" hoặc "DESC" (mặc định: "DESC")
     * @return Danh sách bản tin đã được lọc và sắp xếp
     */
    public List<News> searchAndFilter(String title, String categoryId, String authorId, String sortBy, String sortOrder) {
        StringBuilder sql = new StringBuilder("SELECT * FROM News WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Tìm kiếm theo tiêu đề
        if (title != null && !title.trim().isEmpty()) {
            sql.append(" AND Title LIKE ?");
            params.add("%" + title.trim() + "%");
        }
        
        // Lọc theo loại tin
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            sql.append(" AND CategoryId = ?");
            params.add(categoryId.trim());
        }
        
        // Lọc theo tác giả
        if (authorId != null && !authorId.trim().isEmpty()) {
            sql.append(" AND Author = ?");
            params.add(authorId.trim());
        }
        
        // Sắp xếp
        String orderBy = "PostedDate";
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String sortField = sortBy.trim().toLowerCase();
            if (sortField.equals("viewcount") || sortField.equals("view_count")) {
                orderBy = "ViewCount";
            } else if (sortField.equals("posteddate") || sortField.equals("posted_date")) {
                orderBy = "PostedDate";
            }
        }
        
        String order = "DESC";
        if (sortOrder != null && !sortOrder.trim().isEmpty()) {
            String orderStr = sortOrder.trim().toUpperCase();
            if (orderStr.equals("ASC")) {
                order = "ASC";
            }
        }
        
        sql.append(" ORDER BY ").append(orderBy).append(" ").append(order);
        
        return selectBySql(sql.toString(), params.toArray());
    }
    
    /**
     * Phương thức nội bộ để ánh xạ ResultSet sang đối tượng News
     */
    /**
     * Thực thi câu lệnh SELECT và ánh xạ kết quả sang đối tượng News
     * @param sql Câu lệnh SQL
     * @param args Các tham số cho câu lệnh
     * @return Danh sách các đối tượng News
     */
    private List<News> selectBySql(String sql, Object... args) {
        List<News> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, args);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                News entity = new News();
                entity.setId(rs.getString("Id"));
                entity.setTitle(rs.getString("Title"));
                entity.setSummary(rs.getString("Summary"));
                entity.setContent(rs.getString("Content"));
                entity.setImage(rs.getString("Image"));
                entity.setPostedDate(rs.getTimestamp("PostedDate"));
                entity.setAuthor(rs.getString("Author"));
                entity.setViewCount(rs.getInt("ViewCount"));
                entity.setCategoryId(rs.getString("CategoryId"));
                entity.setHome(rs.getBoolean("Home"));
                list.add(entity);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn dữ liệu News: {0}", e.getMessage());
            throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return list;
    }
}