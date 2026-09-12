package poly.com.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Kiểm thử toàn diện cho tiện ích SlugUtil.
 */
class SlugUtilTest {

    @ParameterizedTest(name = "Chuyển \"{0}\" -> \"{1}\"")
    @CsvSource(value = {
        "Công nghệ, cong-nghe",
        "Điện thoại, dien-thoai",
        "Đời sống & Xã hội, doi-song-xa-hoi",
        "'  AI / Machine Learning  ', ai-machine-learning",
        "Thể thao 24/7, the-thao-24-7",
        "Hello---World, hello-world",
        "ĐẶC BIỆT, dac-biet",
        "___Tin---Mới___, tin-moi",
        "Đất nước Việt Nam, dat-nuoc-viet-nam",
        "'  Công nghệ & AI !!! ', cong-nghe-ai",
        "AI / Trí tuệ nhân tạo, ai-tri-tue-nhan-tao"
    })
    @DisplayName("Kiểm tra sinh slug chuẩn tiếng Việt và ký tự đặc biệt")
    void testToSlugValidInputs(String input, String expectedSlug) {
        assertEquals(expectedSlug, SlugUtil.toSlug(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n", "!!!", "---", "___", "@#$%^&*()"})
    @DisplayName("Kiểm tra đầu vào rỗng hoặc chỉ chứa ký tự đặc biệt trả về rỗng")
    void testToSlugEmptyOrInvalidInputs(String input) {
        assertEquals("", SlugUtil.toSlug(input));
    }

    @Test
    @DisplayName("Kiểm tra tính hợp lệ của hàm isValidSlug")
    void testIsValidSlug() {
        assertTrue(SlugUtil.isValidSlug("cong-nghe"));
        assertTrue(SlugUtil.isValidSlug("cong-nghe-2"));
        assertTrue(SlugUtil.isValidSlug("ai-tri-tue-nhan-tao"));
        assertTrue(SlugUtil.isValidSlug("the-thao-24-7"));

        assertFalse(SlugUtil.isValidSlug(null));
        assertFalse(SlugUtil.isValidSlug(""));
        assertFalse(SlugUtil.isValidSlug("   "));
        assertFalse(SlugUtil.isValidSlug("-cong-nghe"));
        assertFalse(SlugUtil.isValidSlug("cong-nghe-"));
        assertFalse(SlugUtil.isValidSlug("cong--nghe"));
        assertFalse(SlugUtil.isValidSlug("cong_nghe"));
        assertFalse(SlugUtil.isValidSlug("Công-nghệ"));
        assertFalse(SlugUtil.isValidSlug("../category"));
        assertFalse(SlugUtil.isValidSlug("cong-nghe/ai"));
    }
}
