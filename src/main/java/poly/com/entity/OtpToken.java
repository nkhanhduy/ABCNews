package poly.com.entity;

import java.util.Date;

/**
 * Entity class đại diện cho OTP Token trong hệ thống
 * 
 * OTP được sử dụng cho chức năng forgot password.
 * Mỗi OTP có thời gian hết hạn và chỉ được sử dụng 1 lần.
 * 
 * @author ABCNews Development Team
 */
public class OtpToken {
    
    /** ID tự tăng trong database */
    private int id;
    
    /** User ID (foreign key đến Users table) */
    private String userId;
    
    /** Mã OTP 6 số (VD: "123456") */
    private String otpCode;
    
    /** Thời gian hết hạn */
    private Date expiryTime;
    
    /** Thời gian tạo OTP */
    private Date createdAt;
    
    /** Đánh dấu OTP đã được sử dụng chưa (true = đã dùng, false = chưa dùng) */
    private boolean isUsed;
    
    /** Số lần nhập sai (giới hạn 3 lần) */
    private int attempts;
    
    // Constructors
    
    public OtpToken() {
    }
    
    public OtpToken(String userId, String otpCode, Date expiryTime) {
        this.userId = userId;
        this.otpCode = otpCode;
        this.expiryTime = expiryTime;
        this.createdAt = new Date();
        this.isUsed = false;
        this.attempts = 0;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getOtpCode() {
        return otpCode;
    }
    
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
    
    public Date getExpiryTime() {
        return expiryTime;
    }
    
    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isUsed() {
        return isUsed;
    }
    
    public void setUsed(boolean used) {
        isUsed = used;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
    
    @Override
    public String toString() {
        return "OtpToken{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", otpCode='" + otpCode + '\'' +
                ", expiryTime=" + expiryTime +
                ", createdAt=" + createdAt +
                ", isUsed=" + isUsed +
                ", attempts=" + attempts +
                '}';
    }
}

