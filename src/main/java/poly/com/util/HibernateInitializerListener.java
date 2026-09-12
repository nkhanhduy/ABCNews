package poly.com.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * ServletContextListener tự động khởi tạo Hibernate JPA và kiểm tra tự sinh bảng
 * khi ứng dụng web được triển khai (deploy) trên server Tomcat.
 * 
 * @author Nguyen Duy Khanh
 */
@WebListener
public class HibernateInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("==========================================================");
        System.out.println(">> ABCNews đang khởi động ứng dụng...");
        try {
            // Nạp cấu hình Base URL cho toàn bộ ứng dụng (Canonical URL, SEO, Open Graph)
            String appBaseUrl = ConfigHelper.getAppBaseUrl();
            sce.getServletContext().setAttribute("appBaseUrl", appBaseUrl);
            System.out.println(">> App Base URL đã nạp: " + appBaseUrl);

            // Kích hoạt Hibernate EntityManagerFactory để tự động sinh/cập nhật bảng CSDL
            JpaUtil.getEntityManagerFactory();
            System.out.println(">> Hibernate Schema DDL: Cơ chế tự tạo/cập nhật bảng đã sẵn sàng!");
        } catch (Throwable t) {
            System.err.println(">> Lưu ý khi kết nối CSDL qua Hibernate: " + t.getMessage());
        }
        System.out.println("==========================================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">> ABCNews đang dừng ứng dụng - giải phóng tài nguyên...");
        try {
            JpaUtil.shutdown();
            JDBCHelper.closeDataSource();
        } catch (Exception e) {
            System.err.println("Error releasing resources: " + e.getMessage());
        }
    }
}
