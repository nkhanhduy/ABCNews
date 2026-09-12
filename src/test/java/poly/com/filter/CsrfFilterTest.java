package poly.com.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.util.CsrfUtil;

@ExtendWith(MockitoExtension.class)
public class CsrfFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private FilterChain chain;

    private CsrfFilter csrfFilter;

    @BeforeEach
    void setUp() {
        csrfFilter = new CsrfFilter();
        when(request.getContextPath()).thenReturn("/ABCNews");
    }

    @Test
    @DisplayName("GET Request an toàn: Cho phép đi qua và tự sinh CSRF token nếu có session")
    void testGetRequest_PassesAndInitializesToken() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/ABCNews/admin/users");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_SESSION_ATTR)).thenReturn(null);

        csrfFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(session, times(1)).setAttribute(eq(CsrfUtil.CSRF_SESSION_ATTR), anyString());
    }

    @Test
    @DisplayName("POST Request thiếu CSRF token trong khu vực /admin/*: Bị từ chối HTTP 403")
    void testPostRequest_MissingCsrfToken_Forbidden() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/ABCNews/admin/users");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_SESSION_ATTR)).thenReturn("valid_secret_csrf_123");
        when(request.getParameter("_csrf")).thenReturn(null);
        when(request.getHeader("X-CSRF-TOKEN")).thenReturn(null);

        csrfFilter.doFilter(request, response, chain);

        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("POST Request có CSRF token sai lệch: Bị từ chối HTTP 403")
    void testPostRequest_InvalidCsrfToken_Forbidden() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/ABCNews/admin/users");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_SESSION_ATTR)).thenReturn("valid_secret_csrf_123");
        when(request.getParameter("_csrf")).thenReturn("attacker_tampered_csrf_token");

        csrfFilter.doFilter(request, response, chain);

        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("POST Request có CSRF token hợp lệ qua form parameter _csrf: Cho phép thực hiện")
    void testPostRequest_ValidParameterToken_Accepted() throws ServletException, IOException {
        String token = "my_secure_csrf_token_abc";
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/ABCNews/admin/news");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter("_csrf")).thenReturn(token);

        csrfFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    @DisplayName("POST Request có CSRF token hợp lệ qua header X-CSRF-TOKEN (AJAX): Cho phép thực hiện")
    void testPostRequest_ValidHeaderToken_Accepted() throws ServletException, IOException {
        String token = "ajax_csrf_token_xyz";
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/ABCNews/admin/comments");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_SESSION_ATTR)).thenReturn(token);
        when(request.getHeader("X-CSRF-TOKEN")).thenReturn(token);

        csrfFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    @DisplayName("Các endpoint công khai được miễn trừ CSRF (như Google OAuth, login, verify-otp) vẫn hoạt động bình thường")
    void testExemptEndpoints_PassWithoutToken() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/ABCNews/auth/google/verify");

        csrfFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }
}
