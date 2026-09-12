package poly.com.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Entity đại diện cho tin tức (News) trong hệ thống
 */
@Entity
@Table(name = "News")
public class News {
    @Id
    @Column(name = "Id", length = 50, nullable = false)
    private String id;

    @Column(name = "Title", length = 500, nullable = false)
    private String title;

    @Column(name = "Summary", length = 1000)
    private String summary;

    @Column(name = "Content", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String content;

    @Column(name = "Image", length = 500)
    private String image;

    @Column(name = "PostedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;

    @Column(name = "Author", length = 50)
    private String author;

    @Column(name = "ViewCount")
    private int viewCount;

    @Column(name = "CategoryId", length = 50)
    private String categoryId;

    @Column(name = "Home")
    private boolean home;

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