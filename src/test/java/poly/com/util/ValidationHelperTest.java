package poly.com.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
