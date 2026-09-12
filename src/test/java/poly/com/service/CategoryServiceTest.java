package poly.com.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import poly.com.dao.CategoryDAO;
import poly.com.entity.Category;
import poly.com.service.impl.CategoryServiceImpl;

/**
 * Kiểm thử đơn vị toàn diện cho CategoryService (xử lý sinh slug, trùng lặp và cập nhật).
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryDAO categoryDAO;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryDAO);
    }

    @Test
    @DisplayName("Tạo Category mới tự động sinh slug duy nhất từ tên")
    void testCreateCategory_Success() {
        Category cat = new Category("TECH", "Công nghệ & AI");

        when(categoryDAO.existsBySlugExcludingId("cong-nghe-ai", null)).thenReturn(false);

        boolean result = categoryService.createCategory(cat);

        assertTrue(result);
        assertEquals("cong-nghe-ai", cat.getSlug());
        verify(categoryDAO).insert(cat);
    }

    @Test
    @DisplayName("Tạo Category trùng slug sẽ tự động thêm hậu tố -2")
    void testCreateCategory_DuplicateSlug_Suffix2() {
        Category cat = new Category("TECH_2", "Công nghệ & AI");

        // "cong-nghe-ai" đã tồn tại
        when(categoryDAO.existsBySlugExcludingId("cong-nghe-ai", null)).thenReturn(true);
        // "cong-nghe-ai-2" chưa tồn tại
        when(categoryDAO.existsBySlugExcludingId("cong-nghe-ai-2", null)).thenReturn(false);

        boolean result = categoryService.createCategory(cat);

        assertTrue(result);
        assertEquals("cong-nghe-ai-2", cat.getSlug());
        verify(categoryDAO).insert(cat);
    }

    @Test
    @DisplayName("Tạo Category trùng nhiều lần sẽ tự động nối tiếp hậu tố -3")
    void testCreateCategory_MultipleDuplicates_Suffix3() {
        Category cat = new Category("TECH_3", "Công nghệ & AI");

        when(categoryDAO.existsBySlugExcludingId("cong-nghe-ai", null)).thenReturn(true);
        when(categoryDAO.existsBySlugExcludingId("cong-nghe-ai-2", null)).thenReturn(true);
        when(categoryDAO.existsBySlugExcludingId("cong-nghe-ai-3", null)).thenReturn(false);

        boolean result = categoryService.createCategory(cat);

        assertTrue(result);
        assertEquals("cong-nghe-ai-3", cat.getSlug());
        verify(categoryDAO).insert(cat);
    }

    @Test
    @DisplayName("Từ chối tạo Category khi tên để trống hoặc null")
    void testCreateCategory_EmptyName_ThrowsException() {
        Category catEmpty = new Category("CAT1", "");
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(catEmpty));

        Category catNull = new Category("CAT2", null);
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(catNull));

        Category catBlank = new Category("CAT3", "   ");
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(catBlank));

        verify(categoryDAO, never()).insert(any());
    }

    @Test
    @DisplayName("Từ chối tạo Category khi tên chỉ toàn ký tự đặc biệt không thể sinh slug")
    void testCreateCategory_InvalidSlugCharacters_ThrowsException() {
        Category catInvalid = new Category("CAT_INVALID", "!!! @@@ ###");
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(catInvalid));

        verify(categoryDAO, never()).insert(any());
    }

    @Test
    @DisplayName("Cập nhật Category đổi tên -> tự động regenerate slug mới")
    void testUpdateCategory_Renamed_RegeneratesSlug() {
        Category existing = new Category("TECH", "Công nghệ", "cong-nghe");
        when(categoryDAO.findById("TECH")).thenReturn(existing);
        when(categoryDAO.existsBySlugExcludingId("cong-nghe-moi", "TECH")).thenReturn(false);

        Category updateRequest = new Category("TECH", "Công nghệ mới");
        boolean result = categoryService.updateCategory(updateRequest);

        assertTrue(result);
        assertEquals("cong-nghe-moi", updateRequest.getSlug());
        verify(categoryDAO).update(updateRequest);
    }

    @Test
    @DisplayName("Cập nhật Category giữ nguyên tên -> bảo toàn slug cũ không bị trùng với chính mình")
    void testUpdateCategory_SameName_PreservesExistingSlug() {
        Category existing = new Category("TECH", "Công nghệ", "cong-nghe");
        when(categoryDAO.findById("TECH")).thenReturn(existing);

        Category updateRequest = new Category("TECH", "Công nghệ");
        boolean result = categoryService.updateCategory(updateRequest);

        assertTrue(result);
        assertEquals("cong-nghe", updateRequest.getSlug());
        verify(categoryDAO).update(updateRequest);
    }

    @Test
    @DisplayName("Tìm kiếm Category theo slug thành công và không tìm thấy")
    void testFindBySlug() {
        Category cat = new Category("SPORT", "Thể thao", "the-thao");
        when(categoryDAO.findBySlug("the-thao")).thenReturn(cat);
        when(categoryDAO.findBySlug("khong-ton-tai")).thenReturn(null);

        Category found = categoryService.findBySlug("the-thao");
        assertNotNull(found);
        assertEquals("SPORT", found.getId());

        Category notFound = categoryService.findBySlug("khong-ton-tai");
        assertNull(notFound);

        assertNull(categoryService.findBySlug(null));
        assertNull(categoryService.findBySlug("   "));
    }

    @Test
    @DisplayName("Xóa Category thành công")
    void testDeleteCategory() {
        boolean result = categoryService.deleteCategory("TECH");
        assertTrue(result);
        verify(categoryDAO).delete("TECH");

        assertFalse(categoryService.deleteCategory(null));
        assertFalse(categoryService.deleteCategory("   "));
    }
}
