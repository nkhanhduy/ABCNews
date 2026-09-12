package poly.com.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import poly.com.dao.NewsDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.User;
import poly.com.service.impl.UserServiceImpl;
import poly.com.util.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho UserService sử dụng Mockito
 * Độc lập với Database thật (In-memory Mocking)
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private NewsDAO newsDAO;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private User superAdmin;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId("rep001");
        sampleUser.setEmail("reporter@abcnews.vn");
        sampleUser.setFullname("Phóng viên Test");
        sampleUser.setPassword(PasswordUtil.hashPassword("Password@123"));
        sampleUser.setRole(false);
        sampleUser.setEnabled(true);

        superAdmin = new User();
        superAdmin.setId("super001");
        superAdmin.setEmail("super@abcnews.vn");
        superAdmin.setRole(true);
        superAdmin.setEnabled(true);
    }

    @Test
    @DisplayName("Đăng nhập thành công với email và mật khẩu đúng")
    void testLoginSuccess() {
        when(userDAO.findByEmailAndPassword("reporter@abcnews.vn", "Password@123")).thenReturn(sampleUser);

        User loggedInUser = userService.login("reporter@abcnews.vn", "Password@123");

        assertNotNull(loggedInUser);
        assertEquals("rep001", loggedInUser.getId());
        verify(userDAO, times(1)).findByEmailAndPassword("reporter@abcnews.vn", "Password@123");
    }

    @Test
    @DisplayName("Đăng nhập thất bại khi sai thông tin")
    void testLoginFailed() {
        when(userDAO.findByEmailAndPassword("reporter@abcnews.vn", "WrongPass")).thenReturn(null);

        User loggedInUser = userService.login("reporter@abcnews.vn", "WrongPass");

        assertNull(loggedInUser);
    }

    @Test
    @DisplayName("Chặn người dùng tự xóa tài khoản của chính mình")
    void testPreventSelfDelete() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser("rep001", sampleUser);
        }, "Không được phép tự xóa tài khoản đang đăng nhập");

        verify(userDAO, never()).delete(anyString());
    }

    @Test
    @DisplayName("Chặn Admin thường xóa Admin khác")
    void testAdminCannotDeleteAnotherAdmin() {
        User admin1 = new User();
        admin1.setId("admin001");
        admin1.setRole(true);

        User admin2 = new User();
        admin2.setId("admin002");
        admin2.setRole(true);

        when(userDAO.findById("admin002")).thenReturn(admin2);

        assertThrows(SecurityException.class, () -> {
            userService.deleteUser("admin002", admin1);
        }, "Admin thường không được xóa Admin khác");

        verify(userDAO, never()).delete("admin002");
    }

    @Test
    @DisplayName("Super Admin có thể xóa Admin khác và gỡ author tin bài")
    void testSuperAdminCanDeleteAdmin() {
        User targetAdmin = new User();
        targetAdmin.setId("admin002");
        targetAdmin.setRole(true);

        when(userDAO.findById("admin002")).thenReturn(targetAdmin);
        when(newsDAO.countByAuthor("admin002")).thenReturn(3);

        boolean deleted = userService.deleteUser("admin002", superAdmin);

        assertTrue(deleted);
        verify(newsDAO, times(1)).setAuthorToNull("admin002");
        verify(userDAO, times(1)).delete("admin002");
    }
}
