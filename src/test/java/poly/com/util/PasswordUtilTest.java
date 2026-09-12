package poly.com.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho PasswordUtil
 * Kiểm tra tính đúng đắn của thuật toán mã hóa BCrypt
 */
public class PasswordUtilTest {

    @Test
    @DisplayName("Kiểm tra hash mật khẩu thành công và có độ dài chuẩn 60 ký tự")
    void testHashPasswordSuccess() {
        String plain = "Secret@123";
        String hashed = PasswordUtil.hashPassword(plain);

        assertNotNull(hashed);
        assertEquals(60, hashed.length());
        assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2b$") || hashed.startsWith("$2y$"));
    }

    @Test
    @DisplayName("Kiểm tra xác thực mật khẩu chính xác")
    void testVerifyPasswordSuccess() {
        String plain = "MyPassword2026";
        String hashed = PasswordUtil.hashPassword(plain);

        assertTrue(PasswordUtil.verifyPassword(plain, hashed));
    }

    @Test
    @DisplayName("Kiểm tra xác thực mật khẩu sai phải trả về false")
    void testVerifyPasswordFail() {
        String plain = "CorrectPassword";
        String wrong = "WrongPassword";
        String hashed = PasswordUtil.hashPassword(plain);

        assertFalse(PasswordUtil.verifyPassword(wrong, hashed));
        assertFalse(PasswordUtil.verifyPassword(null, hashed));
        assertFalse(PasswordUtil.verifyPassword(plain, null));
    }

    @Test
    @DisplayName("Kiểm tra hàm nhận diện chuỗi BCrypt hash")
    void testIsBCryptHash() {
        String validHash = PasswordUtil.hashPassword("test12345");
        String plainText = "123456";
        String shortText = "$2a$10$short";

        assertTrue(PasswordUtil.isBCryptHash(validHash));
        assertFalse(PasswordUtil.isBCryptHash(plainText));
        assertFalse(PasswordUtil.isBCryptHash(shortText));
        assertFalse(PasswordUtil.isBCryptHash(null));
    }

    @Test
    @DisplayName("Kiểm tra ensureHashed: Không băm 2 lần nếu đã là BCrypt hash")
    void testEnsureHashed() {
        String plain = "Password123";
        String hashedOnce = PasswordUtil.ensureHashed(plain);
        String hashedTwice = PasswordUtil.ensureHashed(hashedOnce);

        assertEquals(hashedOnce, hashedTwice, "Nếu đã là BCrypt hash thì giữ nguyên chuỗi, không băm lặp lại");
    }
}
