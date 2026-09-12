package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import poly.com.entity.OtpToken;
import poly.com.util.JDBCHelper;

/**
 * DAO class để thao tác với bảng OtpTokens qua JDBC
 * 
 * @author ABCNews Development Team
 */
public class OtpTokenDAO {
    
    /**
     * Tạo OTP mới trong database
     * 
     * @param otpToken OTP token cần lưu
     * @return true nếu thành công
     */
    public boolean createOtp(OtpToken otpToken) {
        String sql = "INSERT INTO OtpTokens (user_id, otp_code, expiry_time, created_at, is_used, attempts) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            Timestamp expiry = otpToken.getExpiryTime() != null ? new Timestamp(otpToken.getExpiryTime().getTime()) : null;
            Timestamp created = otpToken.getCreatedAt() != null ? new Timestamp(otpToken.getCreatedAt().getTime()) : new Timestamp(System.currentTimeMillis());
            
            JDBCHelper.executeUpdate(sql, 
                otpToken.getUserId(),
                otpToken.getOtpCode(),
                expiry,
                created,
                otpToken.isUsed() ? 1 : 0,
                otpToken.getAttempts()
            );
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.createOtp: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm bản ghi OTP mới nhất (latest active) của user dựa theo userId
     * Không tìm theo OTP code để đảm bảo có thể kiểm tra attempts và lock khi nhập sai
     */
    public OtpToken findLatestByUserId(String userId) {
        String sql = "SELECT TOP 1 id, user_id, otp_code, expiry_time, created_at, is_used, attempts " +
                     "FROM OtpTokens " +
                     "WHERE user_id = ? " +
                     "ORDER BY created_at DESC, id DESC";
        try {
            List<OtpToken> list = selectBySql(sql, userId);
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.findLatestByUserId: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Tìm OTP hợp lệ (chưa dùng, chưa hết hạn) của user
     */
    public OtpToken findValidOtp(String userId, String otpCode) {
        String sql = "SELECT id, user_id, otp_code, expiry_time, created_at, is_used, attempts FROM OtpTokens " +
                     "WHERE user_id = ? AND otp_code = ? " +
                     "AND is_used = 0 AND expiry_time > GETDATE() " +
                     "ORDER BY created_at DESC";
        
        try {
            List<OtpToken> list = selectBySql(sql, userId, otpCode);
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.findValidOtp: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Đánh dấu OTP đã được sử dụng
     */
    public boolean markOtpAsUsed(int id) {
        String sql = "UPDATE OtpTokens SET is_used = 1 WHERE id = ?";
        try {
            return JDBCHelper.executeUpdate(sql, id) > 0;
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.markOtpAsUsed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Consume OTP nguyên tử (atomic): Chỉ cập nhật is_used=1 nếu OTP chưa được dùng và attempts < 3.
     * Ngăn chặn race condition khi có 2 request đồng thời.
     * 
     * @param id ID của OTP token
     * @return true nếu consume thành công (1 dòng bị ảnh hưởng), false nếu bị xung đột hoặc đã bị dùng/khóa
     */
    public boolean consumeOtp(int id) {
        String sql = "UPDATE OtpTokens SET is_used = 1 WHERE id = ? AND is_used = 0 AND attempts < 3";
        try {
            return JDBCHelper.executeUpdate(sql, id) > 0;
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.consumeOtp: " + e.getMessage());
            return false;
        }
    }

    /**
     * Khóa/vô hiệu hóa OTP khi nhập sai quá số lần quy định
     */
    public boolean lockOtp(int id) {
        String sql = "UPDATE OtpTokens SET is_used = 1, attempts = 3 WHERE id = ?";
        try {
            return JDBCHelper.executeUpdate(sql, id) > 0;
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.lockOtp: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tăng số lần thử sai
     */
    public boolean incrementAttempts(int id) {
        String sql = "UPDATE OtpTokens SET attempts = attempts + 1 WHERE id = ?";
        try {
            return JDBCHelper.executeUpdate(sql, id) > 0;
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.incrementAttempts: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa tất cả OTP cũ của user (khi tạo OTP mới)
     */
    public boolean deleteUserOtps(String userId) {
        String sql = "DELETE FROM OtpTokens WHERE user_id = ?";
        try {
            JDBCHelper.executeUpdate(sql, userId);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.deleteUserOtps: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa các OTP đã hết hạn (cleanup job)
     */
    public int deleteExpiredOtps() {
        String sql = "DELETE FROM OtpTokens WHERE expiry_time < GETDATE()";
        try {
            return JDBCHelper.executeUpdate(sql);
        } catch (Exception e) {
            System.err.println("[ERROR] OtpTokenDAO.deleteExpiredOtps: " + e.getMessage());
            return 0;
        }
    }
    
    private List<OtpToken> selectBySql(String sql, Object... args) {
        List<OtpToken> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = JDBCHelper.getPreparedStatement(sql, args);
            conn = pstmt.getConnection();
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OtpToken token = new OtpToken();
                token.setId(rs.getInt("id"));
                token.setUserId(rs.getString("user_id"));
                token.setOtpCode(rs.getString("otp_code"));
                token.setExpiryTime(rs.getTimestamp("expiry_time"));
                token.setCreatedAt(rs.getTimestamp("created_at"));
                token.setUsed(rs.getBoolean("is_used"));
                token.setAttempts(rs.getInt("attempts"));
                list.add(token);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] OtpTokenDAO.selectBySql: " + e.getMessage());
            throw new RuntimeException("Lỗi khi query OtpTokens", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        
        return list;
    }
}
