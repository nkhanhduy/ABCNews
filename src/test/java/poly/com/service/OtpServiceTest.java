package poly.com.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import poly.com.dao.OtpTokenDAO;
import poly.com.entity.OtpToken;
import poly.com.service.impl.OtpServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OtpServiceTest {

    @Mock
    private OtpTokenDAO otpTokenDAO;

    private OtpServiceImpl otpService;
    private OtpToken activeToken;

    @BeforeEach
    void setUp() {
        otpService = new OtpServiceImpl(otpTokenDAO);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5); // còn hạn 5 phút

        activeToken = new OtpToken("user01", "123456", cal.getTime());
        activeToken.setId(100);
        activeToken.setAttempts(0);
        activeToken.setUsed(false);
    }

    @Test
    @DisplayName("Xác thực OTP thành công: Đúng mã, còn hạn, chưa dùng -> Tiêu thụ OTP nguyên tử")
    void testVerifyOtp_Success() {
        when(otpTokenDAO.findLatestByUserId("user01")).thenReturn(activeToken);
        when(otpTokenDAO.consumeOtp(100)).thenReturn(true);

        OtpVerificationResult result = otpService.verifyOtp("user01", "123456");

        assertTrue(result.isSuccess());
        assertEquals(OtpVerificationResult.Status.SUCCESS, result.getStatus());
        verify(otpTokenDAO, times(1)).consumeOtp(100);
        verify(otpTokenDAO, never()).incrementAttempts(anyInt());
    }

    @Test
    @DisplayName("Sai OTP lần 1: Tăng số lần thử, còn 2 lượt")
    void testVerifyOtp_WrongCode_Attempt1() {
        when(otpTokenDAO.findLatestByUserId("user01")).thenReturn(activeToken);

        OtpVerificationResult result = otpService.verifyOtp("user01", "999999");

        assertFalse(result.isSuccess());
        assertEquals(OtpVerificationResult.Status.INVALID_CODE, result.getStatus());
        assertEquals(2, result.getRemainingAttempts());
        verify(otpTokenDAO, times(1)).incrementAttempts(100);
        verify(otpTokenDAO, never()).lockOtp(anyInt());
        verify(otpTokenDAO, never()).consumeOtp(anyInt());
    }

    @Test
    @DisplayName("Sai OTP lần 2: Tăng số lần thử, còn 1 lượt")
    void testVerifyOtp_WrongCode_Attempt2() {
        activeToken.setAttempts(1);
        when(otpTokenDAO.findLatestByUserId("user01")).thenReturn(activeToken);

        OtpVerificationResult result = otpService.verifyOtp("user01", "999999");

        assertFalse(result.isSuccess());
        assertEquals(OtpVerificationResult.Status.INVALID_CODE, result.getStatus());
        assertEquals(1, result.getRemainingAttempts());
        verify(otpTokenDAO, times(1)).incrementAttempts(100);
        verify(otpTokenDAO, never()).lockOtp(anyInt());
    }

    @Test
    @DisplayName("Sai OTP lần 3: Khóa mã OTP ngay lập tức")
    void testVerifyOtp_WrongCode_Attempt3_LocksOtp() {
        activeToken.setAttempts(2);
        when(otpTokenDAO.findLatestByUserId("user01")).thenReturn(activeToken);

        OtpVerificationResult result = otpService.verifyOtp("user01", "999999");

        assertFalse(result.isSuccess());
        assertEquals(OtpVerificationResult.Status.MAX_ATTEMPTS_EXCEEDED, result.getStatus());
        assertEquals(0, result.getRemainingAttempts());
        verify(otpTokenDAO, times(1)).incrementAttempts(100);
        verify(otpTokenDAO, times(1)).lockOtp(100);
        verify(otpTokenDAO, never()).consumeOtp(anyInt());
    }

    @Test
    @DisplayName("Chặn xác thực khi OTP đã bị khóa (attempts >= 3) dù người dùng nhập đúng mã")
    void testVerifyOtp_AttemptsExceeded_BlocksEvenWithCorrectCode() {
        activeToken.setAttempts(3);
        when(otpTokenDAO.findLatestByUserId("user01")).thenReturn(activeToken);

        OtpVerificationResult result = otpService.verifyOtp("user01", "123456");

        assertFalse(result.isSuccess());
        assertEquals(OtpVerificationResult.Status.MAX_ATTEMPTS_EXCEEDED, result.getStatus());
        verify(otpTokenDAO, never()).consumeOtp(anyInt());
        verify(otpTokenDAO, never()).incrementAttempts(anyInt());
    }

    @Test
    @DisplayName("Từ chối mã OTP đã hết hạn thời gian hiệu lực")
    void testVerifyOtp_Expired() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1); // Đã hết hạn 1 phút trước
        activeToken.setExpiryTime(cal.getTime());

        when(otpTokenDAO.findLatestByUserId("user01")).thenReturn(activeToken);

        OtpVerificationResult result = otpService.verifyOtp("user01", "123456");

        assertFalse(result.isSuccess());
        assertEquals(OtpVerificationResult.Status.EXPIRED, result.getStatus());
        verify(otpTokenDAO, never()).consumeOtp(anyInt());
    }

    @Test
    @DisplayName("Từ chối mã OTP đã được sử dụng trước đó")
    void testVerifyOtp_AlreadyUsed() {
        activeToken.setUsed(true);
        when(otpTokenDAO.findLatestByUserId("user01")).thenReturn(activeToken);

        OtpVerificationResult result = otpService.verifyOtp("user01", "123456");

        assertFalse(result.isSuccess());
        assertEquals(OtpVerificationResult.Status.ALREADY_USED, result.getStatus());
        verify(otpTokenDAO, never()).consumeOtp(anyInt());
    }

    @Test
    @DisplayName("Chống Race Condition: Tiêu thụ nguyên tử thất bại thì không xác thực thành công")
    void testVerifyOtp_AtomicConsumeFails() {
        when(otpTokenDAO.findLatestByUserId("user01")).thenReturn(activeToken);
        // Giả lập một request song song khác đã consume trước
        when(otpTokenDAO.consumeOtp(100)).thenReturn(false);

        OtpVerificationResult result = otpService.verifyOtp("user01", "123456");

        assertFalse(result.isSuccess());
        assertEquals(OtpVerificationResult.Status.ALREADY_USED, result.getStatus());
    }
}
