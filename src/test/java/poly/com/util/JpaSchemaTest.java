package poly.com.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import poly.com.entity.ActivityLog;
import poly.com.entity.Category;
import poly.com.entity.News;
import poly.com.entity.Newsletter;
import poly.com.entity.OtpToken;
import poly.com.entity.User;

/**
 * Kiểm thử tính hợp lệ của các Entity mapping trong Hibernate JPA
 */
class JpaSchemaTest {

    @Test
    @DisplayName("Kiểm tra toàn bộ 6 Entity đều được gắn annotation @Entity và @Table chuẩn SQL Server")
    void testEntitiesHaveJpaAnnotations() {
        Class<?>[] entityClasses = {
            User.class,
            News.class,
            Category.class,
            Newsletter.class,
            ActivityLog.class,
            OtpToken.class
        };

        for (Class<?> clazz : entityClasses) {
            assertTrue(clazz.isAnnotationPresent(Entity.class), 
                clazz.getSimpleName() + " phải được đánh dấu @Entity");
            assertTrue(clazz.isAnnotationPresent(Table.class), 
                clazz.getSimpleName() + " phải có annotation @Table chỉ định tên bảng");

            Table table = clazz.getAnnotation(Table.class);
            assertNotNull(table.name(), clazz.getSimpleName() + " tên bảng không được rỗng");
            assertTrue(table.name().length() > 0, clazz.getSimpleName() + " tên bảng hợp lệ");
        }
    }
}
