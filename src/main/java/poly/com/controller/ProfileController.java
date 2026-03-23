package poly.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.NewsDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.User;
import poly.com.util.ImagePathHelper;

/**
 * Controller xử lý xem profile của user
 * Có thể xem profile của chính mình hoặc của user khác (nếu là admin)
 */
@WebServlet("/admin/profile")
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UserDAO userDAO;
    private NewsDAO newsDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        newsDAO = new NewsDAO();
    }

    /**
     * Hiển thị trang profile của user
     * Nếu không có tham số id, hiển thị profile của user đang đăng nhập
     * Nếu có tham số id, chỉ admin mới được xem profile của user khác
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String userId = request.getParameter("id");
            User profileUser;
            
            // Nếu không có id, hiển thị profile của chính user đang đăng nhập
            if (userId == null || userId.isEmpty()) {
                profileUser = currentUser;
            } else {
                // Nếu có id, kiểm tra quyền
                // Chỉ admin mới được xem profile của user khác
                if (!currentUser.isRole()) {
                    // Nếu không phải admin, chỉ được xem profile của chính mình
                    profileUser = currentUser;
                } else {
                    // Admin có thể xem profile của bất kỳ user nào
                    profileUser = userDAO.findById(userId);
                    if (profileUser == null) {
                        profileUser = currentUser; // Nếu không tìm thấy, hiển thị profile của chính mình
                    }
                }
            }
            
            // Chuẩn hóa đường dẫn ảnh cho user
            String contextPath = request.getContextPath();
            if (profileUser != null) {
                ImagePathHelper.normalizeUserImagePath(profileUser, contextPath);
            }
            
            // Đếm số tin tức của user (nếu là phóng viên)
            int newsCount = 0;
            if (profileUser != null && !profileUser.isRole()) {
                newsCount = newsDAO.findByAuthor(profileUser.getId()).size();
            }
            
            // Kiểm tra quyền để hiển thị nút chỉnh sửa
            boolean canEdit = false;
            if (currentUser != null && profileUser != null) {
                boolean isCurrentUser = currentUser.getId().equals(profileUser.getId());
                boolean isCurrentUserSuperAdmin = currentUser.isSuperAdmin();
                boolean isProfileUserSuperAdmin = profileUser.isSuperAdmin();
                
                // Có thể sửa nếu:
                // 1. Là chính mình, hoặc
                // 2. Là Super Admin (có thể sửa tất cả), hoặc
                // 3. Là Admin thường và profileUser không phải Super Admin
                if (isCurrentUser || isCurrentUserSuperAdmin || 
                    (currentUser.isRole() && !isProfileUserSuperAdmin)) {
                    canEdit = true;
                }
            }
            
            request.setAttribute("profileUser", profileUser);
            request.setAttribute("newsCount", newsCount);
            request.setAttribute("canEdit", canEdit);
            request.setAttribute("pageTitle", "Thông tin " + profileUser.getFullname());
            request.setAttribute("view", "/views/admin/profile.jsp");
            
            request.getRequestDispatcher("/views/admin/admin_layout.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}
