package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import poly.com.util.JDBCHelper;

/**
 * DAO để lấy dữ liệu thống kê và báo cáo cho Admin Dashboard
 * Cung cấp các phương thức để export báo cáo thống kê tổng quan
 */
public class ReportDAO {
    
    /**
     * Lấy thống kê tổng quan của hệ thống
     * @return Map chứa các thống kê: totalUsers, totalNews, totalCategories, totalNewsletters, etc.
     */
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        Connection conn = null;
        
        try {
            conn = JDBCHelper.getConnection();
            
            // Count Users
            stats.put("totalUsers", getCount(conn, "SELECT COUNT(*) FROM Users"));
            stats.put("totalAdmins", getCount(conn, "SELECT COUNT(*) FROM Users WHERE Role = 1"));
            stats.put("totalReporters", getCount(conn, "SELECT COUNT(*) FROM Users WHERE Role = 0"));
            stats.put("activeUsers", getCount(conn, "SELECT COUNT(*) FROM Users WHERE Enabled = 1"));
            stats.put("disabledUsers", getCount(conn, "SELECT COUNT(*) FROM Users WHERE Enabled = 0"));
            
            // Count News
            stats.put("totalNews", getCount(conn, "SELECT COUNT(*) FROM News"));
            stats.put("homeNews", getCount(conn, "SELECT COUNT(*) FROM News WHERE Home = 1"));
            stats.put("totalViews", getCount(conn, "SELECT ISNULL(SUM(ViewCount), 0) FROM News"));
            
            // Count Categories
            stats.put("totalCategories", getCount(conn, "SELECT COUNT(*) FROM Categories"));
            
            // Count Newsletters
            stats.put("totalNewsletters", getCount(conn, "SELECT COUNT(*) FROM Newsletters"));
            stats.put("activeNewsletters", getCount(conn, "SELECT COUNT(*) FROM Newsletters WHERE Enabled = 1"));
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCHelper.close(null, conn);
        }
        
        return stats;
    }
    
    /**
     * Lấy top 10 bài viết có lượt xem cao nhất
     * @return Map với key là News ID, value là view count
     */
    public Map<String, Integer> getTopViewedNews() {
        Map<String, Integer> topNews = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT TOP 10 Id, Title, ViewCount FROM News ORDER BY ViewCount DESC";
            pstmt = JDBCHelper.getPreparedStatement(sql);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                topNews.put(rs.getString("Title"), rs.getInt("ViewCount"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        
        return topNews;
    }
    
    /**
     * Lấy thống kê số lượng tin theo từng category
     * @return Map với key là Category Name, value là số lượng tin
     */
    public Map<String, Integer> getNewsByCategory() {
        Map<String, Integer> categoryStats = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT c.Name, COUNT(n.Id) as NewsCount " +
                        "FROM Categories c " +
                        "LEFT JOIN News n ON c.Id = n.CategoryId " +
                        "GROUP BY c.Name " +
                        "ORDER BY NewsCount DESC";
            pstmt = JDBCHelper.getPreparedStatement(sql);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                categoryStats.put(rs.getString("Name"), rs.getInt("NewsCount"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        
        return categoryStats;
    }
    
    /**
     * Lấy thống kê số lượng bài viết theo tác giả (reporter)
     * @return Map với key là User Fullname, value là số lượng bài viết
     */
    public Map<String, Integer> getNewsByAuthor() {
        Map<String, Integer> authorStats = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT u.Fullname, COUNT(n.Id) as NewsCount " +
                        "FROM Users u " +
                        "LEFT JOIN News n ON u.Id = n.Author " +
                        "WHERE u.Role = 0 " + // Only reporters
                        "GROUP BY u.Fullname " +
                        "ORDER BY NewsCount DESC";
            pstmt = JDBCHelper.getPreparedStatement(sql);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                authorStats.put(rs.getString("Fullname"), rs.getInt("NewsCount"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        
        return authorStats;
    }
    
    /**
     * Helper method để đếm số lượng records
     */
    private int getCount(Connection conn, String sql) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
}

