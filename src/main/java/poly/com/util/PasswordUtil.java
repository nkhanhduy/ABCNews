package poly.com.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class để hash và verify password sử dụng BCrypt
 * 
 * BCrypt tự động thêm salt và sử dụng adaptive hashing (slow by design)
 * để chống brute-force attacks.
 * 
 * @author ABCNews Development Team
 */
public class PasswordUtil {
    
    /**
     * Cost factor cho BCrypt (10 = 2^10 rounds = 1024 rounds)
     * Càng cao càng bảo mật nhưng càng chậm
     * Recommended: 10-12
     */
    private static final int BCRYPT_COST = 10;
    
    /**
     * Hash password sử dụng BCrypt
     * 
     * @param plainPassword Mật khẩu plain text
     * @return Hashed password (60 ký tự)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
        
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));
    }
    
    /**
     * Verify password với hash
     * 
     * @param plainPassword Mật khẩu plain text cần verify
     * @param hashedPassword Hashed password từ database
     * @return true nếu password đúng, false nếu sai
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            // Nếu hashedPassword không phải BCrypt hash hợp lệ
            return false;
        }
    }
    
    /**
     * Kiểm tra password có đủ mạnh không
     * 
     * Yêu cầu:
     * - Ít nhất 8 ký tự
     * - Chứa ít nhất 1 chữ và 1 số (optional, có thể bỏ comment nếu cần strict hơn)
     * 
     * @param password Password cần kiểm tra
     * @return true nếu hợp lệ
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Optional: Yêu cầu có cả chữ và số
        // boolean hasLetter = password.matches(".*[a-zA-Z].*");
        // boolean hasDigit = password.matches(".*\\d.*");
        // return hasLetter && hasDigit;
        
        return true;
    }

    /**
     * Kiểm tra xem một chuỗi đã là BCrypt hash hợp lệ hay chưa
     * Định dạng chuẩn BCrypt: bắt đầu bằng $2a$, $2b$, hoặc $2y$, độ dài chuẩn 60 ký tự
     * 
     * @param password Chuỗi mật khẩu cần kiểm tra
     * @return true nếu đã là BCrypt hash, false nếu là plain text
     */
    public static boolean isBCryptHash(String password) {
        if (password == null || password.length() != 60) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

    /**
     * Đảm bảo mật khẩu luôn được băm BCrypt:
     * - Nếu chuỗi đã là BCrypt hash thì giữ nguyên để tránh double-hash
     * - Nếu chuỗi là plain text thì tự động băm bằng BCrypt
     * 
     * @param plainOrHashedPassword Chuỗi mật khẩu đầu vào
     * @return Mật khẩu chuẩn BCrypt hash (60 ký tự)
     */
    public static String ensureHashed(String plainOrHashedPassword) {
        if (plainOrHashedPassword == null || plainOrHashedPassword.trim().isEmpty()) {
            return plainOrHashedPassword;
        }
        if (isBCryptHash(plainOrHashedPassword)) {
            return plainOrHashedPassword;
        }
        return hashPassword(plainOrHashedPassword);
    }
}

