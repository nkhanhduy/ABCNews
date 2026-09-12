package poly.com.util;

import java.security.SecureRandom;
import java.util.Date;

/**
 * Utility class để generate và validate OTP (One-Time Password)
 * 
 * @author Nguyen Duy Khanh
 */
public class OtpUtil {
    
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate OTP code 6 số
     * 
     * Sử dụng SecureRandom (không dùng Math.random()) để security tốt hơn
     * 
     * @return OTP string 6 số (VD: "123456")
     */
    public static String generateOtp() {
        // Generate số từ 0 đến 999999
        int otp = random.nextInt(1000000);
        
        // Format thành 6 số (thêm leading zeros nếu cần)
        return String.format("%06d", otp);
    }
    
    /**
     * Kiểm tra OTP còn hạn hay không
     * 
     * @param expiryTime Thời gian hết hạn
     * @return true nếu chưa hết hạn, false nếu đã hết hạn
     */
    public static boolean isOtpValid(Date expiryTime) {
        if (expiryTime == null) {
            return false;
        }
        
        Date now = new Date();
        return now.before(expiryTime);
    }
    
    /**
     * Tính expiry time (hiện tại + số phút)
     * 
     * @param minutes Số phút từ bây giờ
     * @return Date object đại diện cho thời gian hết hạn
     */
    public static Date calculateExpiryTime(int minutes) {
        long currentTimeMillis = System.currentTimeMillis();
        long expiryTimeMillis = currentTimeMillis + (minutes * 60 * 1000L);
        return new Date(expiryTimeMillis);
    }
}

