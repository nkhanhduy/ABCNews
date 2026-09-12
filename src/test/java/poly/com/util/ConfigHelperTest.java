package poly.com.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho ConfigHelper: kiểm tra cấu hình Base URL và chuẩn hóa đường dẫn.
 */
public class ConfigHelperTest {

    @Test
    @DisplayName("Kiểm tra getAppBaseUrl mặc định hoặc từ file cấu hình")
    void testGetAppBaseUrlDefault() {
        String baseUrl = ConfigHelper.getAppBaseUrl();
        assertNotNull(baseUrl);
        assertFalse(baseUrl.isEmpty());
        assertFalse(baseUrl.endsWith("/"), "Base URL không được kết thúc bằng dấu gạch chéo");
    }

    @Test
    @DisplayName("Kiểm tra getAppBaseUrl ưu tiên System Property và tự động cắt trailing slash")
    void testGetAppBaseUrlSystemProperty() {
        String originalProp = System.getProperty("app.base.url");
        try {
            System.setProperty("app.base.url", "https://abcnews.vn///");
            String result = ConfigHelper.getAppBaseUrl();
            assertEquals("https://abcnews.vn", result, "Phải lấy từ System Property và cắt hết trailing slashes");
        } finally {
            if (originalProp != null) {
                System.setProperty("app.base.url", originalProp);
            } else {
                System.clearProperty("app.base.url");
            }
        }
    }
}
