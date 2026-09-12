package poly.com.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Entity class đại diện cho OTP Token trong hệ thống
 */
@Entity
@Table(name = "OtpTokens")
public class OtpToken {
    
    /** ID tự tăng trong database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    /** User ID (foreign key đến Users table) */
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;
    
    /** Mã OTP 6 số (VD: "123456") */
    @Column(name = "otp_code", length = 6, nullable = false)
    private String otpCode;
    
    /** Thời gian hết hạn */
    @Column(name = "expiry_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryTime;
    
    /** Thời gian tạo OTP */
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    /** Đánh dấu OTP đã được sử dụng chưa (true = đã dùng, false = chưa dùng) */
    @Column(name = "is_used")
    private boolean isUsed;
    
    /** Số lần nhập sai (giới hạn 3 lần) */
    @Column(name = "attempts")
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

