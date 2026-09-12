package poly.com.service.impl;

import java.util.List;

import poly.com.dao.CategoryDAO;
import poly.com.entity.Category;
import poly.com.service.CategoryService;

/**
 * Service Implementation quản lý danh mục (CategoryServiceImpl)
 * 
 * @author Nguyen Duy Khanh
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

    private static final int MAX_SLUG_LENGTH = 200;

    @Override
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return categoryDAO.existsById(id.trim());
    }

    @Override
    public String generateUniqueSlug(String name, String currentCategoryId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại tin không được để trống");
        }

        String baseSlug = poly.com.util.SlugUtil.toSlug(name);
        if (baseSlug.isEmpty()) {
            throw new IllegalArgumentException("Tên loại tin không hợp lệ để tạo đường dẫn thân thiện (slug)");
        }

        // Đảm bảo baseSlug không vượt quá MAX_SLUG_LENGTH và không kết thúc bằng '-'
        if (baseSlug.length() > MAX_SLUG_LENGTH) {
            baseSlug = baseSlug.substring(0, MAX_SLUG_LENGTH).replaceAll("-+$", "");
        }

        if (!poly.com.util.SlugUtil.isValidSlug(baseSlug)) {
            throw new IllegalArgumentException("Tên loại tin không hợp lệ để tạo đường dẫn thân thiện (slug)");
        }

        String excludeId = (currentCategoryId != null && !currentCategoryId.trim().isEmpty()) 
                ? currentCategoryId.trim() : null;

        // Nếu baseSlug chưa tồn tại (loại trừ danh mục hiện tại), sử dụng trực tiếp
        if (!categoryDAO.existsBySlugExcludingId(baseSlug, excludeId)) {
            return baseSlug;
        }

        // Nếu đã tồn tại, nối hậu tố -2, -3, ... đảm bảo tổng độ dài luôn <= MAX_SLUG_LENGTH (200)
        int counter = 2;
        while (true) {
            String suffix = "-" + counter;
            int maxBaseLength = MAX_SLUG_LENGTH - suffix.length();
            String prefix = baseSlug;
            if (prefix.length() > maxBaseLength) {
                prefix = prefix.substring(0, maxBaseLength).replaceAll("-+$", "");
            }
            String candidate = prefix + suffix;
            if (!categoryDAO.existsBySlugExcludingId(candidate, excludeId)) {
                return candidate;
            }
            counter++;
            if (counter > 1000) {
                throw new IllegalStateException("Không thể tạo slug duy nhất cho loại tin");
            }
        }
    }

    @Override
    public boolean createCategory(Category category) {
        if (category == null || category.getId() == null || category.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã loại tin không được để trống");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại tin không được để trống");
        }

        String id = category.getId().trim();
        String name = category.getName().trim();

        if (id.length() > 50) {
            throw new IllegalArgumentException("Mã loại tin không được vượt quá 50 ký tự");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Tên loại tin không được vượt quá 200 ký tự");
        }

        category.setId(id);
        category.setName(name);

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
        String newName = category.getName().trim();

        if (id.length() > 50) {
            throw new IllegalArgumentException("Mã loại tin không được vượt quá 50 ký tự");
        }
        if (newName.length() > 200) {
            throw new IllegalArgumentException("Tên loại tin không được vượt quá 200 ký tự");
        }

        Category existing = categoryDAO.findById(id);
        if (existing == null) {
            return false;
        }

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
