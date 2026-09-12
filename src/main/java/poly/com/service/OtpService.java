package poly.com.service;

import poly.com.entity.OtpToken;

/**
 * Service quản lý quy trình cấp phát, giới hạn tần suất và xác thực mã OTP
 */
public interface OtpService {

    int MAX_ATTEMPTS = 3;
    int OTP_EXPIRY_MINUTES = 5;
    long OTP_COOLDOWN_MS = 60 * 1000L; // 60 giây

    /**
     * Tạo mã OTP mới 6 số, đặt thời hạn 5 phút và lưu vào database
     *
     * @param userId ID người dùng
     * @return OtpToken được tạo
     */
    OtpToken generateAndSaveOtp(String userId);

    /**
     * Xác thực OTP theo đúng quy chuẩn bảo mật:
     * 1. Tìm bản ghi OTP mới nhất theo userId (không filter theo otpCode).
     * 2. Kiểm tra trạng thái: đã dùng, đã hết hạn, số lần thử >= 3.
     * 3. So sánh mã OTP bằng thuật toán constant-time MessageDigest.isEqual.
     * 4. Nếu sai: tăng attempts; nếu attempts >= 3 -> khóa mã.
     * 5. Nếu đúng: consume nguyên tử (atomic), chống race condition và chống tái sử dụng.
     *
     * @param userId ID người dùng
     * @param inputOtpCode mã OTP người dùng nhập
     * @return kết quả xác thực chi tiết
     */
    OtpVerificationResult verifyOtp(String userId, String inputOtpCode);

    /**
     * Kiểm tra xem email có đang trong thời gian cooldown chống spam hay không
     */
    boolean isCooldownActive(String email);

    /**
     * Lấy số giây còn lại của đợt cooldown
     */
    long getRemainingCooldownSeconds(String email);

    /**
     * Ghi nhận thời điểm gửi OTP cho email
     */
    void recordOtpSent(String email);
}
