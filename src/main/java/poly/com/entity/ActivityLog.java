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
import jakarta.persistence.Transient;

/**
 * Entity đại diện cho một bản ghi lịch sử hoạt động
 */
@Entity
@Table(name = "ActivityLogs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "username", length = 100)
    private String username;

    @Transient
    private String userEmail;

    @Column(name = "action_type", length = 20, nullable = false)
    private String actionType;      // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT

    @Column(name = "entity_type", length = 50)
    private String entityType;      // NEWS, USER, CATEGORY, NEWSLETTER

    @Column(name = "entity_id", length = 50)
    private String entityId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "old_data", columnDefinition = "NVARCHAR(MAX)")
    private String oldData;         // JSON format

    @Column(name = "new_data", columnDefinition = "NVARCHAR(MAX)")
    private String newData;         // JSON format

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Transient
    private String timeAgo;         // "5 phút trước", "2 giờ trước"
    
    // Constructors
    public ActivityLog() {
    }
    
    /**
     * Constructor đơn giản cho log nhanh
     */
    public ActivityLog(String userId, String username, String actionType, String description) {
        this.userId = userId;
        this.username = username;
        this.actionType = actionType;
        this.description = description;
        this.createdAt = new Date();
    }
    
    /**
     * Constructor đầy đủ
     */
    public ActivityLog(String userId, String username, String actionType, String entityType,
                      String entityId, String description, String oldData, String newData) {
        this.userId = userId;
        this.username = username;
        this.actionType = actionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.oldData = oldData;
        this.newData = newData;
        this.createdAt = new Date();
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public String getEntityId() {
        return entityId;
    }
    
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOldData() {
        return oldData;
    }
    
    public void setOldData(String oldData) {
        this.oldData = oldData;
    }
    
    public String getNewData() {
        return newData;
    }
    
    public void setNewData(String newData) {
        this.newData = newData;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getTimeAgo() {
        return timeAgo;
    }
    
    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
    
    @Override
    public String toString() {
        return "ActivityLog{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", actionType='" + actionType + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    /**
     * Các action types
     */
    public static class ActionType {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String EXPORT = "EXPORT";
        public static final String LOGIN_FAILED = "LOGIN_FAILED";
        public static final String LOCK = "LOCK";       // Khóa tài khoản hoặc hủy newsletter
        public static final String UNLOCK = "UNLOCK";   // Mở khóa tài khoản hoặc kích hoạt newsletter
    }
    
    /**
     * Các entity types
     */
    public static class EntityType {
        public static final String NEWS = "NEWS";
        public static final String USER = "USER";
        public static final String CATEGORY = "CATEGORY";
        public static final String NEWSLETTER = "NEWSLETTER";
    }
}

