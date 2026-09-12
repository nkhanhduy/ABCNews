package poly.com.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import poly.com.exception.DuplicateSlugException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho ValidationHelper
 */
public class ValidationHelperTest {

    @Test
    @DisplayName("Kiểm tra hàm isNullOrEmpty với nhiều trường hợp")
    void testIsNullOrEmpty() {
        assertTrue(ValidationHelper.isNullOrEmpty(null));
        assertTrue(ValidationHelper.isNullOrEmpty(""));
        assertTrue(ValidationHelper.isNullOrEmpty("   "));

        assertFalse(ValidationHelper.isNullOrEmpty("abc"));
        assertFalse(ValidationHelper.isNullOrEmpty("  abc  "));
    }

    @Test
    @DisplayName("Kiểm tra isDuplicateKeyException với SQL Server Error Code 2627 và 2601")
    void testIsDuplicateKeyExceptionWithSqlServerCodes() {
        SQLException ex2627 = new SQLException("Violation of PRIMARY KEY constraint", "23000", 2627);
        assertTrue(ValidationHelper.isDuplicateKeyException(ex2627));

        SQLException ex2601 = new SQLException("Cannot insert duplicate key row", "23000", 2601);
        assertTrue(ValidationHelper.isDuplicateKeyException(ex2601));

        // Lồng trong RuntimeException
        RuntimeException wrapped = new RuntimeException("DB error", ex2627);
        assertTrue(ValidationHelper.isDuplicateKeyException(wrapped));
    }

    @Test
    @DisplayName("Kiểm tra isDuplicateKeyException với SQLState 23000")
    void testIsDuplicateKeyExceptionWithSqlState() {
        SQLException exSqlState = new SQLException("Integrity violation", "23000", 0);
        assertTrue(ValidationHelper.isDuplicateKeyException(exSqlState));
    }

    @Test
    @DisplayName("Kiểm tra isDuplicateSlugException với DuplicateSlugException và UX_Categories_Slug")
    void testIsDuplicateSlugException() {
        DuplicateSlugException slugEx = new DuplicateSlugException("Trùng slug", "cong-nghe-ai");
        assertEquals("cong-nghe-ai", slugEx.getSlug());
        assertTrue(ValidationHelper.isDuplicateSlugException(slugEx));
        assertTrue(ValidationHelper.isDuplicateKeyException(slugEx));

        SQLException sqlSlugEx = new SQLException("Cannot insert duplicate key with unique index UX_Categories_Slug", "23000", 2601);
        assertTrue(ValidationHelper.isDuplicateSlugException(sqlSlugEx));

        // Exception không liên quan
        IllegalArgumentException normalEx = new IllegalArgumentException("Dữ liệu không hợp lệ");
        assertFalse(ValidationHelper.isDuplicateSlugException(normalEx));
        assertFalse(ValidationHelper.isDuplicateKeyException(normalEx));
        assertFalse(ValidationHelper.isDuplicateKeyException(null));
    }
}
