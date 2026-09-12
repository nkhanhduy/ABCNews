package poly.com.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho OtpUtil
 */
public class OtpUtilTest {

    @Test
    @DisplayName("Kiểm tra sinh OTP: Luôn tạo chuỗi 6 chữ số")
    void testGenerateOtp() {
        for (int i = 0; i < 50; i++) {
            String otp = OtpUtil.generateOtp();
            assertNotNull(otp);
            assertEquals(6, otp.length(), "Mã OTP phải có đúng 6 ký tự");
            assertTrue(otp.matches("^\\d{6}$"), "Mã OTP phải hoàn toàn là các chữ số");
        }
    }

    @Test
    @DisplayName("Kiểm tra tính toán thời gian hết hạn OTP (5 phút)")
    void testCalculateExpiryTime() {
        Date now = new Date();
        Date expiry = OtpUtil.calculateExpiryTime(5);

        assertNotNull(expiry);
        assertTrue(expiry.after(now), "Thời gian hết hạn phải nằm trong tương lai");

        long diffMinutes = (expiry.getTime() - now.getTime()) / (60 * 1000);
        assertTrue(diffMinutes >= 4 && diffMinutes <= 5, "Khoảng cách thời gian phải xấp xỉ 5 phút");
    }
}
