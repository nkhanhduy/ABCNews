package poly.com.service;

import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import poly.com.dao.ActivityLogDAO;
import poly.com.entity.ActivityLog;
import poly.com.entity.User;

/**
 * Service để log các hoạt động trong hệ thống
 * 
 * @author Nguyen Duy Khanh
 */
public class ActivityLogService {
    
    private ActivityLogDAO activityLogDAO = new ActivityLogDAO();
    
    /**
     * Log một hoạt động đơn giản
     * 
     * @param user User thực hiện action
     * @param actionType Loại action (CREATE, UPDATE, DELETE...)
     * @param description Mô tả
     * @param request HttpServletRequest để lấy IP, User-Agent
     */
    public void log(User user, String actionType, String description, HttpServletRequest request) {
        if (user == null) {
            return; // Không log nếu không có user
        }
        
        ActivityLog log = new ActivityLog();
        log.setUserId(user.getId());
        log.setUsername(user.getFullname());
        log.setActionType(actionType);
        log.setDescription(description);
        log.setCreatedAt(new Date()); // Set thời gian hiện tại
        
        activityLogDAO.createLog(log);
    }
    
    /**
     * Log hoạt động với entity
     * 
     * @param user User thực hiện action
     * @param actionType Loại action
     * @param entityType Loại entity (NEWS, USER...)
     * @param entityId ID của entity
     * @param description Mô tả
     * @param request HttpServletRequest
     */
    public void log(User user, String actionType, String entityType, String entityId,
                   String description, HttpServletRequest request) {
        if (user == null) {
            return;
        }
        
        ActivityLog log = new ActivityLog();
        log.setUserId(user.getId());
        log.setUsername(user.getFullname());
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setCreatedAt(new Date()); // Set thời gian hiện tại
        
        activityLogDAO.createLog(log);
    }
    
    /**
     * Log hoạt động với old/new data
     * 
     * @param user User thực hiện action
     * @param actionType Loại action
     * @param entityType Loại entity
     * @param entityId ID của entity
     * @param description Mô tả
     * @param oldData Dữ liệu cũ (JSON string)
     * @param newData Dữ liệu mới (JSON string)
     * @param request HttpServletRequest
     */
    public void log(User user, String actionType, String entityType, String entityId,
                   String description, String oldData, String newData, HttpServletRequest request) {
        if (user == null) {
            return;
        }
        
        ActivityLog log = new ActivityLog();
        log.setUserId(user.getId());
        log.setUsername(user.getFullname());
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setOldData(oldData);
        log.setNewData(newData);
        log.setCreatedAt(new Date()); // Set thời gian hiện tại
        
        activityLogDAO.createLog(log);
    }
    
    /**
     * Log đăng nhập thành công
     * 
     * @param user User đăng nhập
     * @param request HttpServletRequest
     */
    public void logLogin(User user, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.LOGIN, 
            "Đăng nhập thành công", request);
    }
    
    /**
     * Log đăng nhập thất bại
     * 
     * @param email Email đăng nhập
     * @param request HttpServletRequest
     */
    public void logLoginFailed(String email, HttpServletRequest request) {
        ActivityLog log = new ActivityLog();
        log.setUserId("UNKNOWN");
        log.setUsername(email);
        log.setActionType(ActivityLog.ActionType.LOGIN_FAILED);
        log.setDescription("Đăng nhập thất bại với email: " + email);
        log.setCreatedAt(new Date()); // Set thời gian hiện tại
        
        activityLogDAO.createLog(log);
    }
    
    /**
     * Log đăng xuất
     * 
     * @param user User đăng xuất
     * @param request HttpServletRequest
     */
    public void logLogout(User user, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.LOGOUT, 
            "Đăng xuất", request);
    }
    
    /**
     * Log tạo tin tức
     * 
     * @param user User tạo tin
     * @param newsId ID tin tức
     * @param newsTitle Tiêu đề tin
     * @param request HttpServletRequest
     */
    public void logNewsCreate(User user, String newsId, String newsTitle, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.CREATE, ActivityLog.EntityType.NEWS, newsId,
            "Tạo tin tức: \"" + newsTitle + "\"", request);
    }
    
    /**
     * Log sửa tin tức
     * 
     * @param user User sửa tin
     * @param newsId ID tin tức
     * @param newsTitle Tiêu đề tin
     * @param request HttpServletRequest
     */
    public void logNewsUpdate(User user, String newsId, String newsTitle, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.UPDATE, ActivityLog.EntityType.NEWS, newsId,
            "Cập nhật tin tức: \"" + newsTitle + "\"", request);
    }
    
    /**
     * Log xóa tin tức
     * 
     * @param user User xóa tin
     * @param newsId ID tin tức
     * @param newsTitle Tiêu đề tin
     * @param request HttpServletRequest
     */
    public void logNewsDelete(User user, String newsId, String newsTitle, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.DELETE, ActivityLog.EntityType.NEWS, newsId,
            "Xóa tin tức: \"" + newsTitle + "\"", request);
    }
    
    /**
     * Log tạo user
     * 
     * @param currentUser User thực hiện action
     * @param newUserId ID user mới
     * @param newUserFullname Tên user mới
     * @param request HttpServletRequest
     */
    public void logUserCreate(User currentUser, String newUserId, String newUserFullname, HttpServletRequest request) {
        log(currentUser, ActivityLog.ActionType.CREATE, ActivityLog.EntityType.USER, newUserId,
            "Tạo tài khoản: \"" + newUserFullname + "\" (" + newUserId + ")", request);
    }
    
    /**
     * Log sửa user
     * 
     * @param currentUser User thực hiện action
     * @param targetUserId ID user bị sửa
     * @param targetUserFullname Tên user bị sửa
     * @param request HttpServletRequest
     */
    public void logUserUpdate(User currentUser, String targetUserId, String targetUserFullname, HttpServletRequest request) {
        log(currentUser, ActivityLog.ActionType.UPDATE, ActivityLog.EntityType.USER, targetUserId,
            "Cập nhật tài khoản: \"" + targetUserFullname + "\" (" + targetUserId + ")", request);
    }
    
    /**
     * Log xóa user
     * 
     * @param currentUser User thực hiện action
     * @param targetUserId ID user bị xóa
     * @param targetUserFullname Tên user bị xóa
     * @param request HttpServletRequest
     */
    public void logUserDelete(User currentUser, String targetUserId, String targetUserFullname, HttpServletRequest request) {
        log(currentUser, ActivityLog.ActionType.DELETE, ActivityLog.EntityType.USER, targetUserId,
            "Xóa tài khoản: \"" + targetUserFullname + "\" (" + targetUserId + ")", request);
    }
    
    /**
     * Log export data
     * 
     * @param user User export
     * @param entityType Loại data export (NEWS, USERS...)
     * @param format Format (CSV, Excel, PDF)
     * @param request HttpServletRequest
     */
    public void logExport(User user, String entityType, String format, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.EXPORT, entityType, null,
            "Xuất dữ liệu " + entityType + " ra " + format, request);
    }
    
    // ==================== CATEGORY LOGS ====================
    
    /**
     * Log tạo category
     */
    public void logCategoryCreate(User user, String categoryId, String categoryName, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.CREATE, ActivityLog.EntityType.CATEGORY, categoryId,
            "Tạo loại tin: \"" + categoryName + "\" (" + categoryId + ")", request);
    }
    
    /**
     * Log sửa category
     */
    public void logCategoryUpdate(User user, String categoryId, String categoryName, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.UPDATE, ActivityLog.EntityType.CATEGORY, categoryId,
            "Cập nhật loại tin: \"" + categoryName + "\" (" + categoryId + ")", request);
    }
    
    /**
     * Log xóa category
     */
    public void logCategoryDelete(User user, String categoryId, String categoryName, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.DELETE, ActivityLog.EntityType.CATEGORY, categoryId,
            "Xóa loại tin: \"" + categoryName + "\" (" + categoryId + ")", request);
    }
    
    // ==================== NEWSLETTER LOGS ====================
    
    /**
     * Log xóa newsletter
     */
    public void logNewsletterDelete(User user, String email, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.DELETE, ActivityLog.EntityType.NEWSLETTER, email,
            "Xóa email đăng ký: " + email, request);
    }
    
    /**
     * Log hủy newsletter (disable)
     */
    public void logNewsletterDisable(User user, String email, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.LOCK, ActivityLog.EntityType.NEWSLETTER, email,
            "Hủy đăng ký newsletter: " + email, request);
    }
    
    /**
     * Log kích hoạt newsletter (enable)
     */
    public void logNewsletterEnable(User user, String email, HttpServletRequest request) {
        log(user, ActivityLog.ActionType.UNLOCK, ActivityLog.EntityType.NEWSLETTER, email,
            "Kích hoạt đăng ký newsletter: " + email, request);
    }
    
    // ==================== USER LOCK/UNLOCK LOGS ====================
    
    /**
     * Log khóa tài khoản user
     */
    public void logUserLock(User currentUser, String targetUserId, String targetUserFullname, HttpServletRequest request) {
        log(currentUser, ActivityLog.ActionType.LOCK, ActivityLog.EntityType.USER, targetUserId,
            "Khóa tài khoản: \"" + targetUserFullname + "\" (" + targetUserId + ")", request);
    }
    
    /**
     * Log mở khóa tài khoản user
     */
    public void logUserUnlock(User currentUser, String targetUserId, String targetUserFullname, HttpServletRequest request) {
        log(currentUser, ActivityLog.ActionType.UNLOCK, ActivityLog.EntityType.USER, targetUserId,
            "Mở khóa tài khoản: \"" + targetUserFullname + "\" (" + targetUserId + ")", request);
    }
}

