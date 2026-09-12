package poly.com.util;

import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Lớp tiện ích JPA (JpaUtil)
 * Quản lý EntityManagerFactory singleton và kết nối Hibernate ORM.
 * Tự động đồng bộ cấu hình với ConfigHelper (12-Factor App).
 * 
 * @author ABCNews Development Team
 */
public class JpaUtil {

    private static EntityManagerFactory emf;

    static {
        try {
            initEntityManagerFactory();
        } catch (Throwable ex) {
            System.err.println("Cảnh báo: Khởi tạo ban đầu EntityManagerFactory: " + ex.getMessage());
        }
    }

    /**
     * Khởi tạo EntityManagerFactory với cấu hình kết nối động từ ConfigHelper
     */
    public static synchronized EntityManagerFactory initEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            Map<String, Object> properties = new HashMap<>();
            
            // Đọc thông số kết nối từ ConfigHelper (hỗ trợ biến môi trường & app.properties)
            properties.put("jakarta.persistence.jdbc.url", ConfigHelper.getDbUrl());
            properties.put("jakarta.persistence.jdbc.user", ConfigHelper.getDbUsername());
            properties.put("jakarta.persistence.jdbc.password", ConfigHelper.getDbPassword());
            properties.put("jakarta.persistence.jdbc.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");

            emf = Persistence.createEntityManagerFactory("ABCNewsPU", properties);
            System.out.println(">> Hibernate ORM đã khởi tạo thành công persistence-unit ABCNewsPU (hbm2ddl.auto=update)");
        }
        return emf;
    }

    /**
     * Lấy EntityManagerFactory hiện tại
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            return initEntityManagerFactory();
        }
        return emf;
    }

    /**
     * Tạo một EntityManager mới cho transaction/query
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Đóng EntityManagerFactory khi tắt ứng dụng
     */
    public static synchronized void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println(">> Đã đóng EntityManagerFactory JPA thành công.");
        }
    }
}
