package poly.com.service;

import java.util.List;
import poly.com.entity.Category;

/**
 * Service Interface quản lý danh mục loại tin (CategoryService)
 * 
 * @author ABCNews Development Team
 */
public interface CategoryService {

    List<Category> getAllCategories();

    Category findById(String id);

    boolean createCategory(Category category);

    boolean updateCategory(Category category);

    boolean deleteCategory(String id);

    int countAll();
}
