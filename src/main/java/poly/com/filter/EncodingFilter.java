package poly.com.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter thiết lập encoding UTF-8 toàn cục cho tất cả request và response.
 * Đảm bảo dữ liệu tiếng Việt có dấu luôn được truyền và hiển thị chính xác 100%,
 * chống lỗi font (mojibake) trên toàn bộ hệ sinh thái Tomcat / Jakarta EE.
 * 
 * @author ABCNews Development Team
 */
@WebFilter("/*")
public class EncodingFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Thiết lập bảng mã UTF-8 cho dữ liệu đầu vào (Form POST, URL Parameters)
        req.setCharacterEncoding("UTF-8");

        // Thiết lập bảng mã UTF-8 cho phản hồi đầu ra (HTML/JSP/JSON)
        res.setCharacterEncoding("UTF-8");

        // Đảm bảo appBaseUrl luôn sẵn sàng cho toàn bộ JSP / JSTL
        Object ctxAppBaseUrl = req.getServletContext().getAttribute("appBaseUrl");
        if (ctxAppBaseUrl == null) {
            String baseUrl = poly.com.util.ConfigHelper.getAppBaseUrl();
            req.getServletContext().setAttribute("appBaseUrl", baseUrl);
            req.setAttribute("appBaseUrl", baseUrl);
        } else {
            req.setAttribute("appBaseUrl", ctxAppBaseUrl);
        }

        chain.doFilter(request, response);
    }
}
