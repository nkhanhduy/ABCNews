package poly.com.service;

import poly.com.entity.OtpToken;

/**
 * Kết quả xác thực OTP trả về cho Controller
 */
public class OtpVerificationResult {

    public enum Status {
        SUCCESS,
        NOT_FOUND,
        EXPIRED,
        ALREADY_USED,
        MAX_ATTEMPTS_EXCEEDED,
        INVALID_CODE
    }

    private final Status status;
    private final String message;
    private final int remainingAttempts;
    private final OtpToken token;

    public OtpVerificationResult(Status status, String message, int remainingAttempts, OtpToken token) {
        this.status = status;
        this.message = message;
        this.remainingAttempts = remainingAttempts;
        this.token = token;
    }

    public static OtpVerificationResult success(String message, OtpToken token) {
        return new OtpVerificationResult(Status.SUCCESS, message, 0, token);
    }

    public static OtpVerificationResult error(Status status, String message, int remainingAttempts, OtpToken token) {
        return new OtpVerificationResult(status, message, remainingAttempts, token);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public OtpToken getToken() {
        return token;
    }
}
