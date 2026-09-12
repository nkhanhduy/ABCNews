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
    public Category findBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return null;
        }
        return categoryDAO.findBySlug(slug.trim());
    }

    @Override
    public boolean existsBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }
        return categoryDAO.existsBySlug(slug.trim());
    }

    @Override
    public String generateUniqueSlug(String name, String currentCategoryId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại tin không được để trống");
        }

        String baseSlug = poly.com.util.SlugUtil.toSlug(name);
        if (baseSlug.isEmpty() || !poly.com.util.SlugUtil.isValidSlug(baseSlug)) {
            throw new IllegalArgumentException("Tên loại tin không hợp lệ để tạo đường dẫn thân thiện (slug)");
        }

        String excludeId = (currentCategoryId != null && !currentCategoryId.trim().isEmpty()) 
                ? currentCategoryId.trim() : null;

        // Nếu baseSlug chưa tồn tại (loại trừ danh mục hiện tại), sử dụng trực tiếp
        if (!categoryDAO.existsBySlugExcludingId(baseSlug, excludeId)) {
            return baseSlug;
        }

        // Nếu đã tồn tại, nối hậu tố -2, -3, ...
        int counter = 2;
        String candidate = baseSlug + "-" + counter;
        while (categoryDAO.existsBySlugExcludingId(candidate, excludeId)) {
            counter++;
            candidate = baseSlug + "-" + counter;
            if (counter > 1000) {
                throw new IllegalStateException("Không thể tạo slug duy nhất cho loại tin");
            }
        }
        return candidate;
    }

    @Override
    public boolean createCategory(Category category) {
        if (category == null || category.getId() == null || category.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã loại tin không được để trống");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại tin không được để trống");
        }

        category.setId(category.getId().trim());
        category.setName(category.getName().trim());

        // Tự động sinh slug duy nhất từ Name
        String uniqueSlug = generateUniqueSlug(category.getName(), null);
        category.setSlug(uniqueSlug);

        categoryDAO.insert(category);
        return true;
    }

    @Override
    public boolean updateCategory(Category category) {
        if (category == null || category.getId() == null || category.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã loại tin không được để trống");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại tin không được để trống");
        }

        String id = category.getId().trim();
        Category existing = categoryDAO.findById(id);
        if (existing == null) {
            return false;
        }

        String newName = category.getName().trim();
        category.setId(id);
        category.setName(newName);

        // Chiến lược cập nhật: Nếu tên thay đổi, tự động tạo slug mới theo tên mới
        if (!newName.equalsIgnoreCase(existing.getName())) {
            String newSlug = generateUniqueSlug(newName, id);
            category.setSlug(newSlug);
        } else {
            // Tên không đổi: giữ nguyên slug cũ nếu đã có, hoặc tạo mới nếu chưa có
            if (existing.getSlug() != null && !existing.getSlug().trim().isEmpty()) {
                category.setSlug(existing.getSlug().trim());
            } else {
                String newSlug = generateUniqueSlug(newName, id);
                category.setSlug(newSlug);
            }
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
