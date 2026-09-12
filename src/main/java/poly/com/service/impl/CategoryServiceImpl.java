package poly.com.service.impl;

import java.util.List;

import poly.com.dao.CategoryDAO;
import poly.com.entity.Category;
import poly.com.service.CategoryService;

/**
 * Service Implementation quản lý danh mục (CategoryServiceImpl)
 * 
 * @author ABCNews Development Team
 */
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl() {
        this.categoryDAO = new CategoryDAO();
    }

    public CategoryServiceImpl(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    @Override
    public Category findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return categoryDAO.findById(id.trim());
    }

    @Override
    public boolean createCategory(Category category) {
        if (category == null || category.getId() == null) {
            return false;
        }
        categoryDAO.insert(category);
        return true;
    }

    @Override
    public boolean updateCategory(Category category) {
        if (category == null || category.getId() == null) {
            return false;
        }
        categoryDAO.update(category);
        return true;
    }

    @Override
    public boolean deleteCategory(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        categoryDAO.delete(id.trim());
        return true;
    }

    @Override
    public int countAll() {
        return categoryDAO.countAll();
    }
}
