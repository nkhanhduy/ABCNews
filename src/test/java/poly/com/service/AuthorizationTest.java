package poly.com.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import poly.com.entity.News;
import poly.com.entity.User;
import poly.com.util.SecurityHelper;

public class AuthorizationTest {

    private User superAdmin;
    private User regularAdmin;
    private User reporter1;
    private User reporter2;

    private News newsOfReporter1;
    private News newsOfReporter2;

    @BeforeEach
    void setUp() {
        superAdmin = new User();
        superAdmin.setId("super_user");
        superAdmin.setEmail("super@abcnews.vn");
        superAdmin.setRole(true);
        superAdmin.setSuperAdmin(true);
        superAdmin.setEnabled(true);

        regularAdmin = new User();
        regularAdmin.setId("admin_normal");
        regularAdmin.setEmail("admin@abcnews.vn");
        regularAdmin.setRole(true);
        regularAdmin.setSuperAdmin(false);
        regularAdmin.setEnabled(true);

        reporter1 = new User();
        reporter1.setId("rep_01");
        reporter1.setEmail("rep1@abcnews.vn");
        reporter1.setRole(false);
        reporter1.setSuperAdmin(false);
        reporter1.setEnabled(true);

        reporter2 = new User();
        reporter2.setId("rep_02");
        reporter2.setEmail("rep2@abcnews.vn");
        reporter2.setRole(false);
        reporter2.setSuperAdmin(false);
        reporter2.setEnabled(true);

        newsOfReporter1 = new News();
        newsOfReporter1.setId("news-001");
        newsOfReporter1.setTitle("Bài viết 1");
        newsOfReporter1.setAuthor("rep_01");

        newsOfReporter2 = new News();
        newsOfReporter2.setId("news-002");
        newsOfReporter2.setTitle("Bài viết 2");
        newsOfReporter2.setAuthor("rep_02");
    }

    @Test
    @DisplayName("Quyền Quản trị viên: Admin và Super Admin được xác định qua boolean role trong DB, không dựa vào ID")
    void testRoleDetection() {
        assertTrue(SecurityHelper.isSuperAdmin(superAdmin));
        assertFalse(SecurityHelper.isSuperAdmin(regularAdmin));
        assertFalse(SecurityHelper.isSuperAdmin(reporter1));

        assertTrue(SecurityHelper.isAdmin(superAdmin));
        assertTrue(SecurityHelper.isAdmin(regularAdmin));
        assertFalse(SecurityHelper.isAdmin(reporter1));

        assertTrue(SecurityHelper.isReporter(reporter1));
        assertTrue(SecurityHelper.isReporter(reporter2));
        assertFalse(SecurityHelper.isReporter(regularAdmin));
    }

    @Test
    @DisplayName("Kiểm duyệt bình luận: Chỉ Admin mới có quyền duyệt/xóa bình luận, Phóng viên không được phép")
    void testCommentModerationPermission() {
        assertTrue(SecurityHelper.canModerateComments(superAdmin));
        assertTrue(SecurityHelper.canModerateComments(regularAdmin));
        assertFalse(SecurityHelper.canModerateComments(reporter1));
        assertFalse(SecurityHelper.canModerateComments(null));
    }

    @Test
    @DisplayName("Phân quyền bài viết: Phóng viên chỉ được sửa/xóa bài viết của chính mình")
    void testReporterNewsPermissions() {
        // Phóng viên 1 sửa và xóa bài của mình -> HỢP LỆ
        assertTrue(SecurityHelper.canEditNews(reporter1, newsOfReporter1));
        assertTrue(SecurityHelper.canDeleteNews(reporter1, newsOfReporter1));

        // Phóng viên 1 sửa hoặc xóa bài của Phóng viên 2 -> BỊ CHẶN
        assertFalse(SecurityHelper.canEditNews(reporter1, newsOfReporter2));
        assertFalse(SecurityHelper.canDeleteNews(reporter1, newsOfReporter2));

        // Admin có quyền quản trị tất cả bài viết
        assertTrue(SecurityHelper.canEditNews(regularAdmin, newsOfReporter1));
        assertTrue(SecurityHelper.canDeleteNews(regularAdmin, newsOfReporter2));
    }

    @Test
    @DisplayName("Chặn leo thang đặc quyền: Admin thường không được sửa hoặc xóa Super Admin")
    void testPreventPrivilegeEscalationOnSuperAdmin() {
        // Admin thường sửa hoặc xóa Super Admin -> BỊ CHẶN
        assertFalse(SecurityHelper.canUpdateUser(regularAdmin, superAdmin));
        assertFalse(SecurityHelper.canDeleteUser(regularAdmin, superAdmin));
        assertFalse(SecurityHelper.canToggleUserStatus(regularAdmin, superAdmin));

        // Super Admin có quyền quản trị Admin thường
        assertTrue(SecurityHelper.canUpdateUser(superAdmin, regularAdmin));
        assertTrue(SecurityHelper.canDeleteUser(superAdmin, regularAdmin));
        assertTrue(SecurityHelper.canToggleUserStatus(superAdmin, regularAdmin));
    }

    @Test
    @DisplayName("Chặn Admin thường xóa tài khoản Admin khác")
    void testAdminCannotDeleteAnotherAdmin() {
        User anotherAdmin = new User();
        anotherAdmin.setId("admin_002");
        anotherAdmin.setRole(true);
        anotherAdmin.setSuperAdmin(false);

        // Admin thường không thể xóa Admin khác
        assertFalse(SecurityHelper.canDeleteUser(regularAdmin, anotherAdmin));
        // Nhưng Super Admin thì được phép
        assertTrue(SecurityHelper.canDeleteUser(superAdmin, anotherAdmin));
    }

    @Test
    @DisplayName("Chặn người dùng tự xóa tài khoản của chính mình")
    void testPreventSelfDelete() {
        assertFalse(SecurityHelper.canDeleteUser(superAdmin, superAdmin));
        assertFalse(SecurityHelper.canDeleteUser(regularAdmin, regularAdmin));
        assertFalse(SecurityHelper.canDeleteUser(reporter1, reporter1));
    }
}
