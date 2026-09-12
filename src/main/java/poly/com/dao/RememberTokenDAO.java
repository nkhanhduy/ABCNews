package poly.com.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import poly.com.entity.RememberToken;
import poly.com.util.JDBCHelper;

/**
 * DAO quản lý persistence cho RememberToken.
 * Thao tác trên bảng RememberTokens với kết nối JDBC / HikariCP.
 */
public class RememberTokenDAO {

    /**
     * Lưu token hash mới vào DB
     */
    public boolean insert(RememberToken token) {
        String sql = "INSERT INTO RememberTokens (user_id, token_hash, created_at, expires_at, revoked) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try {
            Timestamp createdAt = token.getCreatedAt() != null ? new Timestamp(token.getCreatedAt().getTime()) : new Timestamp(System.currentTimeMillis());
            Timestamp expiresAt = token.getExpiresAt() != null ? new Timestamp(token.getExpiresAt().getTime()) : null;
            
            JDBCHelper.executeUpdate(sql,
                token.getUserId(),
                token.getTokenHash(),
                createdAt,
                expiresAt,
                token.isRevoked() ? 1 : 0
            );
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] RememberTokenDAO.insert: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm token hợp lệ (chưa bị revoke và chưa hết hạn) theo tokenHash
     */
    public RememberToken findValidToken(String tokenHash) {
        String sql = "SELECT id, user_id, token_hash, created_at, expires_at, revoked "
                   + "FROM RememberTokens "
                   + "WHERE token_hash = ? AND revoked = 0 AND expires_at > GETDATE()";
        try {
            List<RememberToken> list = selectBySql(sql, tokenHash);
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            System.err.println("[ERROR] RememberTokenDAO.findValidToken: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tìm token bất kỳ theo tokenHash (dù đã hết hạn hay revoke)
     */
    public RememberToken findByTokenHash(String tokenHash) {
        String sql = "SELECT id, user_id, token_hash, created_at, expires_at, revoked "
                   + "FROM RememberTokens "
                   + "WHERE token_hash = ?";
        try {
            List<RememberToken> list = selectBySql(sql, tokenHash);
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            System.err.println("[ERROR] RememberTokenDAO.findByTokenHash: " + e.getMessage());
            return null;
        }
    }

    /**
     * Thu hồi (revoke) một token cụ thể bằng hash
     */
    public boolean revokeByTokenHash(String tokenHash) {
        String sql = "UPDATE RememberTokens SET revoked = 1 WHERE token_hash = ?";
        try {
            JDBCHelper.executeUpdate(sql, tokenHash);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] RememberTokenDAO.revokeByTokenHash: " + e.getMessage());
            return false;
        }
    }

    /**
     * Thu hồi tất cả token của một user (ví dụ khi đổi mật khẩu hoặc đăng xuất khỏi mọi thiết bị)
     */
    public boolean revokeAllByUserId(String userId) {
        String sql = "UPDATE RememberTokens SET revoked = 1 WHERE user_id = ?";
        try {
            JDBCHelper.executeUpdate(sql, userId);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] RememberTokenDAO.revokeAllByUserId: " + e.getMessage());
            return false;
        }
    }

    /**
     * Dọn dẹp token đã hết hạn hoặc bị revoke để giải phóng dung lượng DB
     */
    public int cleanupExpiredTokens() {
        String sql = "DELETE FROM RememberTokens WHERE expires_at < GETDATE() OR revoked = 1";
        try {
            return JDBCHelper.executeUpdate(sql);
        } catch (Exception e) {
            System.err.println("[ERROR] RememberTokenDAO.cleanupExpiredTokens: " + e.getMessage());
            return 0;
        }
    }

    private List<RememberToken> selectBySql(String sql, Object... args) throws SQLException {
        List<RememberToken> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = JDBCHelper.executeQuery(sql, args);
            while (rs.next()) {
                RememberToken entity = new RememberToken();
                entity.setId(rs.getInt("id"));
                entity.setUserId(rs.getString("user_id"));
                entity.setTokenHash(rs.getString("token_hash"));
                entity.setCreatedAt(rs.getTimestamp("created_at"));
                entity.setExpiresAt(rs.getTimestamp("expires_at"));
                entity.setRevoked(rs.getBoolean("revoked"));
                list.add(entity);
            }
        } finally {
            if (rs != null && rs.getStatement() != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (Exception ignored) {
                }
            }
        }
        return list;
    }
}
