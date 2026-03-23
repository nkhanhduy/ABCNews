package poly.com.controller;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import poly.com.dao.UserDAO;
import poly.com.entity.User;

/**
 * Controller xử lý xác thực Google OAuth
 * Nhận ID token từ frontend và verify với Google
 */
@WebServlet("/auth/google/verify")
public class GoogleVerifyController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // TODO: Thay YOUR_CLIENT_ID bằng Client ID từ Google
    private static final String CLIENT_ID = "248224711124-mr0usg1vgteil4fbo06hgrmshchtq4ca.apps.googleusercontent.com";
    private static final NetHttpTransport transport = new NetHttpTransport();
    private static final GsonFactory jsonFactory = new GsonFactory();
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String credential = request.getParameter("credential");
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();
        
        if (credential == null || credential.isEmpty()) {
            request.setAttribute("error", "Không nhận được thông tin từ Google!");
            request.getRequestDispatcher("/views/public/login.jsp")
                .forward(request, response);
            return;
        }
        
        try {
            // Verify ID token từ Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
            
            GoogleIdToken idToken = verifier.verify(credential);
            
            if (idToken == null) {
                request.setAttribute("error", "Token không hợp lệ!");
                request.getRequestDispatcher("/views/public/login.jsp")
                    .forward(request, response);
                return;
            }
            
            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            
            // Tìm user theo email (KHÔNG cho tự đăng ký - chỉ link với account đã có)
            User user = userDAO.findByEmail(email);
            
            if (user == null) {
                // Chưa có account → Không cho đăng ký tự động
                request.setAttribute("error", 
                    "Tài khoản với email <strong>" + email + "</strong> chưa được tạo trong hệ thống.<br>" +
                    "Vui lòng liên hệ quản trị viên để được tạo tài khoản.");
                request.getRequestDispatcher("/views/public/login.jsp")
                    .forward(request, response);
                return;
            }
            
            // Kiểm tra tài khoản có bị khóa không
            if (!user.isEnabled()) {
                request.setAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
                request.getRequestDispatcher("/views/public/login.jsp")
                    .forward(request, response);
                return;
            }
            
            // Có account → Link Google với account hiện có (nếu chưa link)
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                user.setAuthProvider("both"); // Có thể dùng cả 2 cách
                if (pictureUrl != null && !pictureUrl.isEmpty()) {
                    user.setImagePath(pictureUrl); // Cập nhật ảnh từ Google
                }
                userDAO.update(user);
            } else {
                // Đã có GoogleId → Cập nhật ảnh nếu có thay đổi
                if (pictureUrl != null && !pictureUrl.isEmpty() && 
                    (user.getImagePath() == null || !user.getImagePath().equals(pictureUrl))) {
                    user.setImagePath(pictureUrl);
                    userDAO.update(user);
                }
            }
            
            // Login thành công
            session.setAttribute("user", user);
            // Redirect đến dashboard (AdminController sẽ phân biệt Admin/Reporter)
            response.sendRedirect(contextPath + "/admin/dashboard");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã có lỗi xảy ra khi xác thực Google: " + e.getMessage());
            request.getRequestDispatcher("/views/public/login.jsp")
                .forward(request, response);
        }
    }
}
