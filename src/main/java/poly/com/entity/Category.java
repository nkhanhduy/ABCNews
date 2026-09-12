package poly.com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity đại diện cho loại tin (Category) trong hệ thống
 * Chứa thông tin: ID và tên loại tin
 */
@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @Column(name = "Id", length = 50, nullable = false)
    private String id;

    @Column(name = "Name", length = 255, nullable = false)
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