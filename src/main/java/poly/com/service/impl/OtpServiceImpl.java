package poly.com.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import poly.com.dao.OtpTokenDAO;
import poly.com.entity.OtpToken;
import poly.com.service.OtpService;
import poly.com.service.OtpVerificationResult;
import poly.com.util.OtpUtil;

/**
 * Triển khai nghiệp vụ xác thực OTP bảo mật:
 * - Tra cứu theo userId (không theo otpCode trước khi đếm attempts).
 * - Kiểm tra attempts, expiry, used trước khi so khớp mã.
 * - Tăng attempts khi sai; tự động khóa OTP sau 3 lần sai.
 * - Consume OTP nguyên tử để chống race condition và ngăn chặn tái sử dụng.
 */
public class OtpServiceImpl implements OtpService {

    private final OtpTokenDAO otpTokenDAO;
    private static final ConcurrentHashMap<String, Long> LAST_OTP_SENT_MAP = new ConcurrentHashMap<>();

    public OtpServiceImpl() {
        this(new OtpTokenDAO());
    }

    public OtpServiceImpl(OtpTokenDAO otpTokenDAO) {
        this.otpTokenDAO = otpTokenDAO;
    }

    @Override
    public OtpToken generateAndSaveOtp(String userId) {
        otpTokenDAO.deleteUserOtps(userId);

        String otpCode = OtpUtil.generateOtp();
        Date expiryTime = OtpUtil.calculateExpiryTime(OTP_EXPIRY_MINUTES);

        OtpToken otpToken = new OtpToken(userId, otpCode, expiryTime);
        boolean saved = otpTokenDAO.createOtp(otpToken);
        if (!saved) {
            throw new RuntimeException("Không thể tạo mã OTP trong cơ sở dữ liệu");
        }
        return otpToken;
    }

    @Override
    public OtpVerificationResult verifyOtp(String userId, String inputOtpCode) {
        if (userId == null || userId.trim().isEmpty()) {
            return OtpVerificationResult.error(OtpVerificationResult.Status.NOT_FOUND, "Thông tin người dùng không hợp lệ", 0, null);
        }

        if (inputOtpCode == null || inputOtpCode.trim().isEmpty()) {
            return OtpVerificationResult.error(OtpVerificationResult.Status.INVALID_CODE, "Vui lòng nhập mã OTP", 0, null);
        }

        String trimmedCode = inputOtpCode.trim();

        // 1. Tìm bản ghi OTP mới nhất của user theo userId
        OtpToken latest = otpTokenDAO.findLatestByUserId(userId);
        if (latest == null) {
            return OtpVerificationResult.error(OtpVerificationResult.Status.NOT_FOUND, "Không tìm thấy mã OTP cho tài khoản này", 0, null);
        }

        // 2. Kiểm tra đã sử dụng chưa
        if (latest.isUsed()) {
            return OtpVerificationResult.error(OtpVerificationResult.Status.ALREADY_USED, "Mã OTP đã được sử dụng, vui lòng yêu cầu mã mới", 0, latest);
        }

        // 3. Kiểm tra đã hết hạn chưa
        if (!OtpUtil.isOtpValid(latest.getExpiryTime())) {
            return OtpVerificationResult.error(OtpVerificationResult.Status.EXPIRED, "Mã OTP đã hết hạn, vui lòng gửi lại mã mới", 0, latest);
        }

        // 4. Kiểm tra số lần thử đã vượt giới hạn chưa
        if (latest.getAttempts() >= MAX_ATTEMPTS) {
            return OtpVerificationResult.error(OtpVerificationResult.Status.MAX_ATTEMPTS_EXCEEDED, "Mã OTP đã bị khóa do nhập sai quá " + MAX_ATTEMPTS + " lần. Vui lòng yêu cầu mã mới.", 0, latest);
        }

        // 5. So sánh mã OTP (constant-time)
        boolean isMatch = MessageDigest.isEqual(
            latest.getOtpCode().getBytes(StandardCharsets.UTF_8),
            trimmedCode.getBytes(StandardCharsets.UTF_8)
        );

        if (!isMatch) {
            // Tăng số lần thử sai
            otpTokenDAO.incrementAttempts(latest.getId());
            int newAttempts = latest.getAttempts() + 1;
            latest.setAttempts(newAttempts);

            if (newAttempts >= MAX_ATTEMPTS) {
                // Khóa OTP ngay lập tức
                otpTokenDAO.lockOtp(latest.getId());
                return OtpVerificationResult.error(OtpVerificationResult.Status.MAX_ATTEMPTS_EXCEEDED, "Bạn đã nhập sai quá " + MAX_ATTEMPTS + " lần. Mã OTP đã bị khóa, vui lòng gửi lại.", 0, latest);
            }

            int remaining = MAX_ATTEMPTS - newAttempts;
            return OtpVerificationResult.error(OtpVerificationResult.Status.INVALID_CODE, "Mã OTP không chính xác. Bạn còn " + remaining + " lần thử.", remaining, latest);
        }

        // 6. Nếu đúng: consume nguyên tử để chống race condition và chống tái sử dụng
        boolean consumed = otpTokenDAO.consumeOtp(latest.getId());
        if (!consumed) {
            return OtpVerificationResult.error(OtpVerificationResult.Status.ALREADY_USED, "Mã OTP đã được xử lý hoặc đã bị khóa", 0, latest);
        }

        latest.setUsed(true);
        return OtpVerificationResult.success("Xác thực OTP thành công", latest);
    }

    @Override
    public boolean isCooldownActive(String email) {
        if (email == null) return false;
        Long lastSent = LAST_OTP_SENT_MAP.get(email.toLowerCase());
        if (lastSent == null) return false;
        return (System.currentTimeMillis() - lastSent) < OTP_COOLDOWN_MS;
    }

    @Override
    public long getRemainingCooldownSeconds(String email) {
        if (email == null) return 0;
        Long lastSent = LAST_OTP_SENT_MAP.get(email.toLowerCase());
        if (lastSent == null) return 0;
        long diff = System.currentTimeMillis() - lastSent;
        if (diff >= OTP_COOLDOWN_MS) return 0;
        return (OTP_COOLDOWN_MS - diff + 999) / 1000;
    }

    @Override
    public void recordOtpSent(String email) {
        if (email != null) {
            LAST_OTP_SENT_MAP.put(email.toLowerCase(), System.currentTimeMillis());
        }
    }
}
