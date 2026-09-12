package poly.com.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Entity đại diện cho email đăng ký newsletter
 * Chứa thông tin: Email, trạng thái kích hoạt, ngày đăng ký
 */
@Entity
@Table(name = "Newsletters")
public class Newsletter {
    @Id
    @Column(name = "Email", length = 255, nullable = false)
    private String email;

    @Column(name = "Enabled", nullable = false)
    private boolean enabled;

    @Column(name = "SubscribedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date subscribedDate;

    // Default constructor
    public Newsletter() {
    }

    // Constructor with all fields
    public Newsletter(String email, boolean enabled, Date subscribedDate) {
        this.email = email;
        this.enabled = enabled;
        this.subscribedDate = subscribedDate;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getSubscribedDate() {
        return subscribedDate;
    }

    public void setSubscribedDate(Date subscribedDate) {
        this.subscribedDate = subscribedDate;
    }
}