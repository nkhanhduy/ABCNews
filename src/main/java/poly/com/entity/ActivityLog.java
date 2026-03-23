package poly.com.entity;

import java.util.Date;

/**
 * Entity đại diện cho một bản ghi lịch sử hoạt động
 * 
 * @author ABCNews Development Team
 */
public class ActivityLog {
    private int id;
    private String userId;
    private String username;
    private String userEmail;
    private String actionType;      // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT
    private String entityType;      // NEWS, USER, CATEGORY, NEWSLETTER
    private String entityId;
    private String description;
    private String oldData;         // JSON format
    private String newData;         // JSON format
    private Date createdAt;
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

