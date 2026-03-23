package poly.com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import poly.com.entity.OtpToken;
import poly.com.util.JDBCHelper;

/**
 * DAO class để thao tác với OtpTokens table
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
            JDBCHelper.executeUpdate(sql, 
                otpToken.getUserId(),
                otpToken.getOtpCode(),
                otpToken.getExpiryTime(),
                otpToken.getCreatedAt(),
                otpToken.isUsed(),
                otpToken.getAttempts()
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tìm OTP hợp lệ (chưa dùng, chưa hết hạn) của user
     * 
     * @param userId User ID
     * @param otpCode Mã OTP cần tìm
     * @return OtpToken nếu tìm thấy, null nếu không
     */
    public OtpToken findValidOtp(String userId, String otpCode) {
        String sql = "SELECT * FROM OtpTokens " +
                     "WHERE user_id = ? AND otp_code = ? " +
                     "AND is_used = 0 AND expiry_time > GETDATE() " +
                     "ORDER BY created_at DESC";
        
        try {
            List<OtpToken> list = selectBySql(sql, userId, otpCode);
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tìm OTP của user (bất kể đã dùng hay hết hạn)
     * 
     * @param userId User ID
     * @param otpCode Mã OTP
     * @return OtpToken nếu tìm thấy, null nếu không
     */
    public OtpToken findOtpByCode(String userId, String otpCode) {
        String sql = "SELECT * FROM OtpTokens WHERE user_id = ? AND otp_code = ? " +
                     "ORDER BY created_at DESC";
        
        try {
            List<OtpToken> list = selectBySql(sql, userId, otpCode);
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Đánh dấu OTP đã được sử dụng
     * 
     * @param id ID của OTP token
     * @return true nếu thành công
     */
    public boolean markOtpAsUsed(int id) {
        String sql = "UPDATE OtpTokens SET is_used = 1 WHERE id = ?";
        
        try {
            JDBCHelper.executeUpdate(sql, id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tăng số lần thử sai
     * 
     * @param id ID của OTP token
     * @return true nếu thành công
     */
    public boolean incrementAttempts(int id) {
        String sql = "UPDATE OtpTokens SET attempts = attempts + 1 WHERE id = ?";
        
        try {
            JDBCHelper.executeUpdate(sql, id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa tất cả OTP cũ của user (khi tạo OTP mới)
     * 
     * @param userId User ID
     * @return true nếu thành công
     */
    public boolean deleteUserOtps(String userId) {
        String sql = "DELETE FROM OtpTokens WHERE user_id = ?";
        
        try {
            JDBCHelper.executeUpdate(sql, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa các OTP đã hết hạn (cleanup job)
     * 
     * @return Số lượng OTP đã xóa
     */
    public int deleteExpiredOtps() {
        String sql = "DELETE FROM OtpTokens WHERE expiry_time < GETDATE()";
        
        try {
            return JDBCHelper.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Helper method để select và map ResultSet thành List<OtpToken>
     */
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
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi query OtpTokens", e);
        } finally {
            JDBCHelper.close(rs, pstmt, conn);
        }
        
        return list;
    }
}

