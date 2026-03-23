package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import poly.com.entity.ActivityLog;
import poly.com.util.JDBCHelper;

/**
 * DAO để quản lý ActivityLogs
 * 
 * @author ABCNews Development Team
 */
public class ActivityLogDAO {
    
    /**
     * Tạo một log mới
     * 
     * @param log ActivityLog cần lưu
     * @return true nếu thành công
     */
    public boolean createLog(ActivityLog log) {
        String sql = "INSERT INTO ActivityLogs (user_id, username, action_type, entity_type, " +
                     "entity_id, description, old_data, new_data, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            JDBCHelper.executeUpdate(sql,
                log.getUserId(),
                log.getUsername(),
                log.getActionType(),
                log.getEntityType(),
                log.getEntityId(),
                log.getDescription(),
                log.getOldData(),
                log.getNewData(),
                log.getCreatedAt()
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy tất cả logs (phân trang)
     * 
     * @param page Trang hiện tại (bắt đầu từ 1)
     * @param pageSize Số records mỗi trang
     * @return List ActivityLog
     */
    public List<ActivityLog> findAll(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT al.*, u.Email as user_email " +
                     "FROM ActivityLogs al " +
                     "LEFT JOIN Users u ON al.user_id = u.Id " +
                     "ORDER BY al.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        return selectBySql(sql, offset, pageSize);
    }
    
    /**
     * Lấy logs với filter
     * 
     * @param userId User ID (null = all)
     * @param actionType Action type (null = all)
     * @param entityType Entity type (null = all)
     * @param fromDate Từ ngày (null = không giới hạn)
     * @param toDate Đến ngày (null = không giới hạn)
     * @param page Trang hiện tại
     * @param pageSize Số records mỗi trang
     * @return List ActivityLog
     */
    public List<ActivityLog> findWithFilters(String userId, String actionType, String entityType,
                                             Date fromDate, Date toDate, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT al.*, u.Email as user_email ");
        sql.append("FROM ActivityLogs al ");
        sql.append("LEFT JOIN Users u ON al.user_id = u.Id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (userId != null && !userId.isEmpty()) {
            sql.append("AND al.user_id = ? ");
            params.add(userId);
        }
        
        if (actionType != null && !actionType.isEmpty()) {
            sql.append("AND al.action_type = ? ");
            params.add(actionType);
        }
        
        if (entityType != null && !entityType.isEmpty()) {
            sql.append("AND al.entity_type = ? ");
            params.add(entityType);
        }
        
        if (fromDate != null) {
            sql.append("AND al.created_at >= ? ");
            params.add(fromDate);
        }
        
        if (toDate != null) {
            sql.append("AND al.created_at <= ? ");
            params.add(toDate);
        }
        
        sql.append("ORDER BY al.created_at DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        params.add(offset);
        params.add(pageSize);
        
        return selectBySql(sql.toString(), params.toArray());
    }
    
    /**
     * Đếm tổng số logs
     * 
     * @return Số lượng logs
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM ActivityLogs";
        
        try (ResultSet rs = JDBCHelper.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Đếm số logs với filter
     * 
     * @param userId User ID (null = all)
     * @param actionType Action type (null = all)
     * @param entityType Entity type (null = all)
     * @param fromDate Từ ngày (null = không giới hạn)
     * @param toDate Đến ngày (null = không giới hạn)
     * @return Số lượng logs
     */
    public int countWithFilters(String userId, String actionType, String entityType,
                                Date fromDate, Date toDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ActivityLogs WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (userId != null && !userId.isEmpty()) {
            sql.append("AND user_id = ? ");
            params.add(userId);
        }
        
        if (actionType != null && !actionType.isEmpty()) {
            sql.append("AND action_type = ? ");
            params.add(actionType);
        }
        
        if (entityType != null && !entityType.isEmpty()) {
            sql.append("AND entity_type = ? ");
            params.add(entityType);
        }
        
        if (fromDate != null) {
            sql.append("AND created_at >= ? ");
            params.add(fromDate);
        }
        
        if (toDate != null) {
            sql.append("AND created_at <= ? ");
            params.add(toDate);
        }
        
        try (ResultSet rs = JDBCHelper.executeQuery(sql.toString(), params.toArray())) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Lấy logs gần đây (100 records)
     * 
     * @return List ActivityLog
     */
    public List<ActivityLog> findRecent() {
        String sql = "SELECT TOP 100 al.*, u.Email as user_email " +
                     "FROM ActivityLogs al " +
                     "LEFT JOIN Users u ON al.user_id = u.Id " +
                     "ORDER BY al.created_at DESC";
        
        return selectBySql(sql);
    }
    
    /**
     * Lấy logs của một user
     * 
     * @param userId User ID
     * @param limit Số lượng records
     * @return List ActivityLog
     */
    public List<ActivityLog> findByUser(String userId, int limit) {
        String sql = "SELECT TOP (?) al.*, u.Email as user_email " +
                     "FROM ActivityLogs al " +
                     "LEFT JOIN Users u ON al.user_id = u.Id " +
                     "WHERE al.user_id = ? " +
                     "ORDER BY al.created_at DESC";
        
        return selectBySql(sql, limit, userId);
    }
    
    /**
     * Xóa logs cũ (cleanup)
     * 
     * @param daysToKeep Số ngày giữ lại
     * @return Số lượng logs đã xóa
     */
    public int deleteOldLogs(int daysToKeep) {
        String sql = "DELETE FROM ActivityLogs WHERE created_at < DATEADD(DAY, -?, GETDATE())";
        
        try {
            return JDBCHelper.executeUpdate(sql, daysToKeep);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Helper method để map ResultSet sang ActivityLog
     */
    private List<ActivityLog> selectBySql(String sql, Object... args) {
        List<ActivityLog> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, args);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getString("user_id"));
                log.setUsername(rs.getString("username"));
                
                // userEmail có thể null nếu user đã bị xóa
                try {
                    log.setUserEmail(rs.getString("user_email"));
                } catch (SQLException e) {
                    log.setUserEmail(null);
                }
                
                log.setActionType(rs.getString("action_type"));
                log.setEntityType(rs.getString("entity_type"));
                log.setEntityId(rs.getString("entity_id"));
                log.setDescription(rs.getString("description"));
                log.setOldData(rs.getString("old_data"));
                log.setNewData(rs.getString("new_data"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                
                // Tính time ago
                log.setTimeAgo(calculateTimeAgo(log.getCreatedAt()));
                
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi query ActivityLogs", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        
        return list;
    }
    
    /**
     * Tính "time ago" từ Date
     * 
     * @param date Date cần tính
     * @return String "5 phút trước", "2 giờ trước"...
     */
    private String calculateTimeAgo(Date date) {
        if (date == null) {
            return "";
        }
        
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

