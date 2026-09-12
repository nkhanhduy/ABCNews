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
 * Entity đại diện cho bình luận của độc giả dưới mỗi bài viết
 * Hỗ trợ quy trình kiểm duyệt (Moderated Comments)
 * 
 * @author ABCNews Development Team
 */
@Entity
@Table(name = "Comments")
public class Comment {

    public static final int STATUS_PENDING = 0;   // Chờ kiểm duyệt
    public static final int STATUS_APPROVED = 1;  // Đã duyệt & hiển thị công khai
    public static final int STATUS_REJECTED = 2;  // Bị từ chối / vi phạm tiêu chuẩn

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "NewsId", length = 50, nullable = false)
    private String newsId;

    @Column(name = "AuthorName", length = 100, nullable = false)
    private String authorName;

    @Column(name = "AuthorEmail", length = 150)
    private String authorEmail;

    @Column(name = "Content", length = 1000, nullable = false)
    private String content;

    @Column(name = "CreatedDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "Status", nullable = false)
    private int status = STATUS_PENDING;

    // Các trường transient hỗ trợ hiển thị giao diện nhanh chóng
    @Transient
    private String newsTitle;

    @Transient
    private String timeAgo;

    public Comment() {
        this.createdDate = new Date();
        this.status = STATUS_PENDING;
    }

    public Comment(String newsId, String authorName, String authorEmail, String content) {
        this.newsId = newsId;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.content = content;
        this.createdDate = new Date();
        this.status = STATUS_PENDING;
    }

    public Comment(Integer id, String newsId, String authorName, String authorEmail, String content, Date createdDate, int status) {
        this.id = id;
        this.newsId = newsId;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.content = content;
        this.createdDate = createdDate;
        this.status = status;
    }

    // Getters và Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    /**
     * Lấy nhãn chữ tiếng Việt của trạng thái
     */
    public String getStatusText() {
        return switch (this.status) {
            case STATUS_APPROVED -> "Đã duyệt";
            case STATUS_REJECTED -> "Từ chối";
            default -> "Chờ duyệt";
        };
    }

    /**
     * Lấy class CSS badge Bootstrap tương ứng với trạng thái
     */
    public String getStatusBadgeClass() {
        return switch (this.status) {
            case STATUS_APPROVED -> "badge bg-success";
            case STATUS_REJECTED -> "badge bg-danger";
            default -> "badge bg-warning text-dark";
        };
    }

    /**
     * Lấy ký tự viết tắt đại diện làm Avatar độc giả
     */
    public String getAvatarInitial() {
        if (authorName == null || authorName.trim().isEmpty()) {
            return "U";
        }
        String[] parts = authorName.trim().split("\\s+");
        String lastPart = parts[parts.length - 1];
        return lastPart.substring(0, 1).toUpperCase();
    }
}
