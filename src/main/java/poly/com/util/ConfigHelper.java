package poly.com.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Lớp tiện ích quản lý cấu hình hệ thống (ConfigHelper)
 * 
 * Áp dụng nguyên tắc Zero Hardcoded Secrets & chuẩn 12-Factor App:
 * 1. Ưu tiên đọc từ biến môi trường hệ điều hành (Environment Variables)
 * 2. Nếu không có biến môi trường, đọc từ file classpath: "app.properties"
 * 3. Nếu không tìm thấy, sử dụng giá trị mặc định an toàn (fallback)
 * 
 * @author ABCNews Development Team
 */
public class ConfigHelper {

    private static final String CONFIG_FILE = "app.properties";
    private static final Properties properties = new Properties();
    private static boolean isLoaded = false;

    static {
        loadProperties();
    }

    /**
     * Tải các thông số cấu hình từ file app.properties trong Classpath
     */
    private static synchronized void loadProperties() {
        if (isLoaded) {
            return;
        }

        try (InputStream input = ConfigHelper.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                isLoaded = true;
            } else {
                System.err.println("[CẢNH BÁO] Không tìm thấy file " + CONFIG_FILE + " trong classpath. Ứng dụng sẽ sử dụng biến môi trường hoặc cấu hình mặc định.");
            }
        } catch (Exception e) {
            System.err.println("[LỖI] Không thể đọc file cấu hình " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Lấy giá trị cấu hình theo khóa (key).
     * Thứ tự ưu tiên:
     * 1. Biến môi trường (ví dụ: DB_HOST cho db.host)
     * 2. Giá trị trong file app.properties
     * 3. Trả về defaultValue nếu không tìm thấy ở cả 2 nguồn trên
     * 
     * @param key Tên khóa cấu hình (ví dụ: "db.host", "mail.smtp.user")
     * @param defaultValue Giá trị mặc định nếu không cấu hình
     * @return Giá trị chuỗi đã được cấu hình
     */
    public static String get(String key, String defaultValue) {
        if (key == null) {
            return defaultValue;
        }

        // 1. Kiểm tra biến môi trường hệ thống (System Environment)
        // Format: db.host -> DB_HOST, mail.smtp.user -> MAIL_SMTP_USER
        String envKey = key.replace('.', '_').replace('-', '_').toUpperCase();
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        // 2. Kiểm tra System Properties (-Ddb.host=...)
        String sysPropValue = System.getProperty(key);
        if (sysPropValue != null && !sysPropValue.trim().isEmpty()) {
            return sysPropValue.trim();
        }

        // 3. Kiểm tra file app.properties
        String propValue = properties.getProperty(key);
        if (propValue != null && !propValue.trim().isEmpty()) {
            return propValue.trim();
        }

        return defaultValue;
    }

    /**
     * Lấy giá trị cấu hình theo khóa, nếu không có trả về null
     * 
     * @param key Tên khóa cấu hình
     * @return Giá trị chuỗi hoặc null
     */
    public static String get(String key) {
        return get(key, null);
    }

    /**
     * Lấy giá trị cấu hình dạng số nguyên (int)
     * 
     * @param key Tên khóa cấu hình
     * @param defaultValue Giá trị số mặc định
     * @return Giá trị int
     */
    public static int getInt(String key, int defaultValue) {
        String value = get(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("[CẢNH BÁO] Khóa cấu hình " + key + " không phải là số nguyên hợp lệ: " + value);
            return defaultValue;
        }
    }

    /**
     * Lấy giá trị cấu hình dạng boolean
     * 
     * @param key Tên khóa cấu hình
     * @param defaultValue Giá trị boolean mặc định
     * @return Giá trị boolean
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key, null);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
