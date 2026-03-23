package poly.com.entity;

import java.util.Date;

/**
 * Entity đại diện cho tin tức (News) trong hệ thống
 * Chứa thông tin: ID, tiêu đề, nội dung, ảnh, ngày đăng, tác giả, lượt xem, danh mục, trạng thái trang nhất
 */
public class News {
    private String id;
    private String title;
    private String summary; // Cột bạn đã thêm vào CSDL
    private String content;
    private String image;
    private Date postedDate;
    private String author; // Mã phóng viên (tham chiếu đến Users.id)
    private int viewCount;
    private String categoryId; // Mã loại tin (tham chiếu đến Categories.id)
    private boolean home; // true = Trang nhất

    // Default constructor
    public News() {
    }

    // Constructor with all fields
    public News(String id, String title, String summary, String content, String image, Date postedDate, String author, int viewCount, String categoryId, boolean home) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.image = image;
        this.postedDate = postedDate;
        this.author = author;
        this.viewCount = viewCount;
        this.categoryId = categoryId;
        this.home = home;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }
}