package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import poly.com.entity.Comment;
import poly.com.util.JDBCHelper;

/**
 * DAO quản lý thao tác cơ sở dữ liệu cho bình luận độc giả (Comments)
 * Hỗ trợ các chức năng hiển thị bài viết và kiểm duyệt trong trang Admin
 * 
 * @author Nguyen Duy Khanh
 */
public class CommentDAO {

    private static final Logger LOGGER = Logger.getLogger(CommentDAO.class.getName());

    /**
     * Thêm bình luận mới (mặc định trạng thái chờ duyệt - status = 0)
     */
    public boolean insert(Comment comment) {
        String sql = "INSERT INTO Comments (NewsId, AuthorName, AuthorEmail, Content, CreatedDate, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            JDBCHelper.executeUpdate(sql,
                comment.getNewsId(),
                comment.getAuthorName(),
                comment.getAuthorEmail(),
                comment.getContent(),
                comment.getCreatedDate() != null ? comment.getCreatedDate() : new Date(),
                comment.getStatus()
            );
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi thêm bình luận: {0}", e.getMessage());
            return false;
        }
    }

    /**
     * Tìm bình luận theo ID
     */
    public Comment findById(int id) {
        String sql = "SELECT c.*, n.Title as news_title " +
                     "FROM Comments c " +
                     "LEFT JOIN News n ON c.NewsId = n.Id " +
                     "WHERE c.Id = ?";
        List<Comment> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Lấy danh sách bình luận đã duyệt (Status = 1) của một bài viết cụ thể
     */
    public List<Comment> findByNewsIdApproved(String newsId) {
        String sql = "SELECT c.*, n.Title as news_title " +
                     "FROM Comments c " +
                     "LEFT JOIN News n ON c.NewsId = n.Id " +
                     "WHERE c.NewsId = ? AND c.Status = 1 " +
                     "ORDER BY c.CreatedDate DESC";
        return selectBySql(sql, newsId);
    }

    /**
     * Đếm số bình luận đã duyệt của một bài viết
     */
    public int countApprovedByNewsId(String newsId) {
        String sql = "SELECT COUNT(*) FROM Comments WHERE NewsId = ? AND Status = 1";
        return queryScalarInt(sql, newsId);
    }

    /**
     * Đếm số lượng bình luận theo trạng thái (-1: đếm tất cả)
     */
    public int countByStatus(int status) {
        if (status < 0) {
            String sql = "SELECT COUNT(*) FROM Comments";
            return queryScalarInt(sql);
        } else {
            String sql = "SELECT COUNT(*) FROM Comments WHERE Status = ?";
            return queryScalarInt(sql, status);
        }
    }

    /**
     * Tìm kiếm và lọc danh sách bình luận (có phân trang) cho màn hình kiểm duyệt Admin
     */
    public List<Comment> findWithFilter(Integer status, String keyword, int page, int pageSize) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.*, n.Title as news_title FROM Comments c ");
        sql.append("LEFT JOIN News n ON c.NewsId = n.Id WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (status != null && status >= 0) {
            sql.append("AND c.Status = ? ");
            params.add(status);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (c.AuthorName LIKE ? OR c.AuthorEmail LIKE ? OR c.Content LIKE ? OR n.Title LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        sql.append("ORDER BY c.CreatedDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        int offset = Math.max(0, (page - 1) * pageSize);
        params.add(offset);
        params.add(pageSize);

        return selectBySql(sql.toString(), params.toArray());
    }

    /**
     * Đếm tổng số bản ghi lọc để phân trang
     */
    public int countWithFilter(Integer status, String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM Comments c ");
        sql.append("LEFT JOIN News n ON c.NewsId = n.Id WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (status != null && status >= 0) {
            sql.append("AND c.Status = ? ");
            params.add(status);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (c.AuthorName LIKE ? OR c.AuthorEmail LIKE ? OR c.Content LIKE ? OR n.Title LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        return queryScalarInt(sql.toString(), params.toArray());
    }

    /**
     * Thực thi truy vấn đếm scalar an toàn, đảm bảo đóng Connection và PreparedStatement về Pool
     */
    private int queryScalarInt(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = JDBCHelper.getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pstmt.setObject(i + 1, args[i]);
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi đếm số lượng bình luận theo trạng thái: {0}", e.getMessage());
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        return 0;
    }

    /**
     * Cập nhật trạng thái kiểm duyệt cho bình luận
     */
    public boolean updateStatus(int id, int status) {
        String sql = "UPDATE Comments SET Status = ? WHERE Id = ?";
        try {
            JDBCHelper.executeUpdate(sql, status, id);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật trạng thái bình luận ID " + id, e);
            return false;
        }
    }

    /**
     * Xóa một bình luận
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Comments WHERE Id = ?";
        try {
            JDBCHelper.executeUpdate(sql, id);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi xóa bình luận ID " + id, e);
            return false;
        }
    }

    /**
     * Query và map dữ liệu sang List<Comment>
     */
    private List<Comment> selectBySql(String sql, Object... args) {
        List<Comment> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = JDBCHelper.getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pstmt.setObject(i + 1, args[i]);
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("Id"));
                comment.setNewsId(rs.getString("NewsId"));
                comment.setAuthorName(rs.getString("AuthorName"));
                comment.setAuthorEmail(rs.getString("AuthorEmail"));
                comment.setContent(rs.getString("Content"));
                comment.setCreatedDate(rs.getTimestamp("CreatedDate"));
                comment.setStatus(rs.getInt("Status"));

                try {
                    comment.setNewsTitle(rs.getString("news_title"));
                } catch (SQLException ignored) {
                }

                comment.setTimeAgo(calculateTimeAgo(comment.getCreatedDate()));
                list.add(comment);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn Comments: {0}", e.getMessage());
            throw new RuntimeException("Lỗi truy vấn Comments", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }

        return list;
    }

    private String calculateTimeAgo(Date date) {
        if (date == null) return "";
        long diff = System.currentTimeMillis() - date.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "Vừa xong";
        } else if (minutes < 60) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days < 7) {
            return days + " ngày trước";
        } else {
            return (days / 7) + " tuần trước";
        }
    }
}
