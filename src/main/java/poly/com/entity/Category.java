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

    @Column(name = "Slug", length = 200, nullable = false, unique = true)
    private String slug;

    // Default constructor
    public Category() {
    }

    // Constructor with ID and Name (backward compatibility)
    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Constructor with all fields
    public Category(String id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return java.util.Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}