package poly.com.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Lớp tiện ích quản lý cấu hình hệ thống (ConfigHelper)
 * 
 * Nạp cấu hình từ biến môi trường hệ thống (Environment Variables),
 * thuộc tính hệ thống (System Properties) và file cấu hình classpath "app.properties".
 * 
 * @author Nguyen Duy Khanh
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
        // Format: db.host -> DB_HOST, mail.smtp.user -> MAIL_SMTP_USER, app.base.url -> APP_BASE_URL
        String envKey = key.replace('.', '_').replace('-', '_').toUpperCase();
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        if (!key.equals(envKey)) {
            String directEnv = System.getenv(key);
            if (directEnv != null && !directEnv.trim().isEmpty()) {
                return directEnv.trim();
            }
        }

        // 2. Kiểm tra System Properties (-Dapp.base.url=... hoặc -DAPP_BASE_URL=...)
        String sysPropValue = System.getProperty(key);
        if (sysPropValue != null && !sysPropValue.trim().isEmpty()) {
            return sysPropValue.trim();
        }
        String sysPropEnvValue = System.getProperty(envKey);
        if (sysPropEnvValue != null && !sysPropEnvValue.trim().isEmpty()) {
            return sysPropEnvValue.trim();
        }

        // 3. Kiểm tra file app.properties
        String propValue = properties.getProperty(key);
        if (propValue != null && !propValue.trim().isEmpty()) {
            return propValue.trim();
        }
        String propEnvValue = properties.getProperty(envKey);
        if (propEnvValue != null && !propEnvValue.trim().isEmpty()) {
            return propEnvValue.trim();
        }

        return defaultValue;
    }

    /**
     * Lấy App Base URL cho ứng dụng (dùng cho Canonical URL, OpenGraph, Twitter Card, SEO).
     * 
     * Thứ tự ưu tiên:
     * 1. Biến môi trường hệ điều hành: APP_BASE_URL
     * 2. System Property: app.base.url hoặc APP_BASE_URL
     * 3. File cấu hình app.properties: app.base.url
     * 4. Giá trị mặc định an toàn cho môi trường phát triển: http://localhost:8088
     * 
     * Tự động loại bỏ dấu gạch chéo cuối (trailing slash) nếu có để chuẩn hóa việc nối đường dẫn.
     * 
     * @return Chuỗi Base URL đã chuẩn hóa (ví dụ: "http://localhost:8088" hoặc "https://abcnews.vn")
     */
    public static String getAppBaseUrl() {
        String baseUrl = get("app.base.url", null);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = get("APP_BASE_URL", "http://localhost:8088");
        }
        baseUrl = baseUrl.trim();
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
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

    /**
     * Lấy URL kết nối database SQL Server
     */
    public static String getDbUrl() {
        String directUrl = get("db.url", null);
        if (directUrl != null && !directUrl.trim().isEmpty()) {
            return directUrl.trim();
        }
        String host = get("db.host", "localhost");
        String port = get("db.port", "1433");
        String name = get("db.name", "ABCNews");
        return String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=false;trustServerCertificate=true;sendStringParametersAsUnicode=true;characterEncoding=UTF-8;", host, port, name);
    }

    /**
     * Lấy tài khoản database SQL Server
     */
    public static String getDbUsername() {
        return get("db.user", get("db.username", "sa"));
    }

    /**
     * Lấy mật khẩu database SQL Server
     */
    public static String getDbPassword() {
        String pass = get("db.password", null);
        if (pass == null) {
            pass = get("DB_PASSWORD", "");
        }
        return pass;
    }
}
