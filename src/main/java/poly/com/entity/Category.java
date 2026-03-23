package poly.com.entity;

/**
 * Entity đại diện cho loại tin (Category) trong hệ thống
 * Chứa thông tin: ID và tên loại tin
 */
public class Category {
    private String id;
    private String name;

    // Default constructor
    public Category() {
    }

    // Constructor with all fields
    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}