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

    @Mock
    private RememberMeService rememberMeService;

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
        superAdmin.setSuperAdmin(true);
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

    // ==========================================
    // ADMIN RESET PASSWORD TESTS
    // ==========================================

    @Test
    @DisplayName("Admin Reset: Password rỗng -> Giữ nguyên password hash cũ, không thu hồi token")
    void testAdminResetPassword_BlankKeepsOldHash() {
        String oldHash = sampleUser.getPassword();
        when(userDAO.findById("rep001")).thenReturn(sampleUser);

        User updateForm = new User();
        updateForm.setId("rep001");
        updateForm.setFullname("Phóng viên Test Sửa");
        updateForm.setPassword(""); // blank

        boolean success = userService.updateUser(updateForm, superAdmin);

        assertTrue(success);
        assertEquals(oldHash, updateForm.getPassword(), "Mật khẩu cũ phải được giữ nguyên");
        verify(userDAO, times(1)).update(updateForm);
        verify(rememberMeService, never()).revokeAllTokens(anyString());
    }

    @Test
    @DisplayName("Admin Reset: Nhập password mới hợp lệ -> BCrypt hash và thu hồi Remember-Me tokens")
    void testAdminResetPassword_ValidPasswordUpdatesHashedAndRevokesTokens() {
        when(userDAO.findById("rep001")).thenReturn(sampleUser);

        User updateForm = new User();
        updateForm.setId("rep001");
        updateForm.setFullname("Phóng viên Test Sửa");
        updateForm.setPassword("NewSecurePassword@123");

        boolean success = userService.updateUser(updateForm, superAdmin);

        assertTrue(success);
        assertNotEquals("NewSecurePassword@123", updateForm.getPassword(), "Tuyệt đối không lưu plaintext");
        assertTrue(PasswordUtil.isBCryptHash(updateForm.getPassword()), "Mật khẩu phải được băm chuẩn BCrypt");
        assertTrue(PasswordUtil.verifyPassword("NewSecurePassword@123", updateForm.getPassword()));
        verify(userDAO, times(1)).update(updateForm);
        verify(rememberMeService, times(1)).revokeAllTokens("rep001");
    }

    @Test
    @DisplayName("Admin Reset: Mật khẩu mới dưới 8 ký tự -> Bị từ chối IllegalArgumentException")
    void testAdminResetPassword_ShortPasswordRejected() {
        when(userDAO.findById("rep001")).thenReturn(sampleUser);

        User updateForm = new User();
        updateForm.setId("rep001");
        updateForm.setPassword("12345"); // < 8 ký tự

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(updateForm, superAdmin);
        }, "Mật khẩu dưới 8 ký tự phải bị ném IllegalArgumentException");

        verify(userDAO, never()).update(any(User.class));
        verify(rememberMeService, never()).revokeAllTokens(anyString());
    }

    @Test
    @DisplayName("Admin Reset: Admin thường không được đổi mật khẩu/sửa Super Admin")
    void testAdminResetPassword_AdminCannotResetSuperAdmin() {
        User regularAdmin = new User();
        regularAdmin.setId("admin001");
        regularAdmin.setRole(true);
        regularAdmin.setSuperAdmin(false);

        when(userDAO.findById("super001")).thenReturn(superAdmin);

        User updateForm = new User();
        updateForm.setId("super001");
        updateForm.setPassword("NewPassword@123");

        assertThrows(SecurityException.class, () -> {
            userService.updateUser(updateForm, regularAdmin);
        });

        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Admin Reset qua resetPasswordByAdmin: Thành công băm BCrypt và thu hồi token")
    void testAdminResetPassword_DirectMethodSuccess() {
        when(userDAO.findById("rep001")).thenReturn(sampleUser);
        when(userDAO.updatePassword(eq("rep001"), anyString())).thenReturn(true);

        boolean success = userService.resetPasswordByAdmin("rep001", "AdminResetPass@2026", superAdmin);

        assertTrue(success);
        verify(userDAO, times(1)).updatePassword(eq("rep001"), argThat(PasswordUtil::isBCryptHash));
        verify(rememberMeService, times(1)).revokeAllTokens("rep001");
    }

    @Test
    @DisplayName("Admin Reset qua resetPasswordByAdmin: Phóng viên không được reset người khác")
    void testAdminResetPassword_ReporterCannotResetOthers() {
        assertThrows(SecurityException.class, () -> {
            userService.resetPasswordByAdmin("super001", "SomePassword@123", sampleUser);
        });

        verify(userDAO, never()).updatePassword(anyString(), anyString());
    }

    // ==========================================
    // SELF CHANGE PASSWORD TESTS
    // ==========================================

    @Test
    @DisplayName("Đổi mật khẩu cá nhân: Mật khẩu hiện tại đúng -> Hash BCrypt, lưu DB, thu hồi Remember-Me tokens")
    void testSelfChangePassword_Success() {
        when(userDAO.findById("rep001")).thenReturn(sampleUser);
        when(userDAO.updatePassword(eq("rep001"), anyString())).thenReturn(true);

        boolean success = userService.changePassword("rep001", "Password@123", "NewSecretPass@2026", "NewSecretPass@2026");

        assertTrue(success);
        verify(userDAO, times(1)).updatePassword(eq("rep001"), argThat(hash -> 
            PasswordUtil.isBCryptHash(hash) && PasswordUtil.verifyPassword("NewSecretPass@2026", hash)
        ));
        verify(rememberMeService, times(1)).revokeAllTokens("rep001");
    }

    @Test
    @DisplayName("Đổi mật khẩu cá nhân: Mật khẩu hiện tại sai -> Bị từ chối IllegalArgumentException")
    void testSelfChangePassword_WrongCurrentPassword() {
        when(userDAO.findById("rep001")).thenReturn(sampleUser);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("rep001", "WrongCurrentPass", "NewSecretPass@2026", "NewSecretPass@2026");
        });

        assertEquals("Mật khẩu hiện tại không đúng.", ex.getMessage());
        verify(userDAO, never()).updatePassword(anyString(), anyString());
        verify(rememberMeService, never()).revokeAllTokens(anyString());
    }

    @Test
    @DisplayName("Đổi mật khẩu cá nhân: Mật khẩu mới và xác nhận không khớp -> Bị từ chối")
    void testSelfChangePassword_MismatchConfirm() {
        when(userDAO.findById("rep001")).thenReturn(sampleUser);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("rep001", "Password@123", "NewSecretPass@2026", "DifferentConfirmPass@2026");
        });

        assertEquals("Mật khẩu mới và xác nhận mật khẩu không khớp.", ex.getMessage());
        verify(userDAO, never()).updatePassword(anyString(), anyString());
    }

    @Test
    @DisplayName("Đổi mật khẩu cá nhân: Mật khẩu mới trùng với mật khẩu hiện tại -> Bị từ chối")
    void testSelfChangePassword_SameAsCurrent() {
        when(userDAO.findById("rep001")).thenReturn(sampleUser);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("rep001", "Password@123", "Password@123", "Password@123");
        });

        assertEquals("Mật khẩu mới không được trùng với mật khẩu hiện tại.", ex.getMessage());
        verify(userDAO, never()).updatePassword(anyString(), anyString());
    }

    @Test
    @DisplayName("Đổi mật khẩu cá nhân: Mật khẩu mới dưới 8 ký tự -> Bị từ chối")
    void testSelfChangePassword_ShortPassword() {
        when(userDAO.findById("rep001")).thenReturn(sampleUser);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("rep001", "Password@123", "short1", "short1");
        });

        assertEquals("Mật khẩu mới phải có ít nhất 8 ký tự.", ex.getMessage());
        verify(userDAO, never()).updatePassword(anyString(), anyString());
    }

    @Test
    @DisplayName("Đổi mật khẩu cá nhân: Bỏ trống các trường -> Bị từ chối")
    void testSelfChangePassword_BlankInputs() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("rep001", "", "NewPass@123", "NewPass@123");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("rep001", "Password@123", "", "NewPass@123");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("rep001", "Password@123", "NewPass@123", "");
        });
    }
}
