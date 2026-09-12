package poly.com.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.NewsDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.User;
import poly.com.service.ActivityLogService;
import poly.com.util.FileUploadHelper;
import poly.com.util.ImagePathHelper;
import poly.com.util.PasswordUtil;
import poly.com.util.ValidationHelper;

/**
 * Controller quản lý người dùng (User) - chỉ dành cho Admin
 * Xử lý CRUD: Create, Read, Update, Delete user, khóa/mở khóa tài khoản, upload ảnh đại diện
 */
@WebServlet("/admin/users")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class UserAdminController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private UserDAO userDAO;
    private ActivityLogService activityLogService;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        activityLogService = new ActivityLogService();
        
        // Cấu hình BeanUtils để xử lý Date (nếu form gửi chuỗi ngày tháng)
        DateConverter converter = new DateConverter(null);
        converter.setPattern("yyyy-MM-dd");
        ConvertUtils.register(converter, Date.class);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền Admin
        if (!checkAdminRole(request, response)) return;

        String action = request.getParameter("action");
        try {
            if (action != null) {
                switch (action) {
                    case "delete":
                        doDelete(request, response);
                        return;
                    case "edit":
                        showEditForm(request, response);
                        return;
                    case "toggle":
                        toggleStatus(request, response);
                        return;
                    case "generateUserId":
                        generateUserId(request, response);
                        return;
                    case "searchAjax":
                        searchUsersAjax(request, response);
                        return;
                }
            }
            showList(request, response);
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkAdminRole(request, response)) return;

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            boolean shouldRedirect = true;
            if (action != null) {
                switch (action) {
                    case "create":
                        shouldRedirect = doCreate(request, response);
                        break;
                    case "update":
                        shouldRedirect = doUpdate(request, response);
                        break;
                }
            }
            // Chỉ redirect nếu không có lỗi validation (đã forward)
            if (shouldRedirect) {
                response.sendRedirect(getContextPath(request) + "/admin/users");
            }
        } catch (Exception e) {
            // Xử lý lỗi SQL (như duplicate key) một cách thân thiện hơn
            if (ValidationHelper.isDuplicateKeyException(e)) {
                request.setAttribute("error", "Mã người dùng đã tồn tại. Vui lòng chọn mã khác.");
                try {
                    showList(request, response);
                    return;
                } catch (Exception ex) {
                    handleException(request, response, ex);
                }
            }
            handleException(request, response, e);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = getContextPath(request);
        
        // Lấy các tham số tìm kiếm và lọc
        String searchKeyword = request.getParameter("searchKeyword");
        String filterRole = request.getParameter("filterRole");
        String filterEnabled = request.getParameter("filterEnabled");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        
        // Xử lý filterEnabled (convert String sang Boolean)
        Boolean enabledFilter = null;
        if (filterEnabled != null && !filterEnabled.trim().isEmpty()) {
            if ("true".equalsIgnoreCase(filterEnabled)) {
                enabledFilter = true;
            } else if ("false".equalsIgnoreCase(filterEnabled)) {
                enabledFilter = false;
            }
        }
        
        // Xử lý tìm kiếm và lọc
        List<User> list;
        if ((searchKeyword != null && !searchKeyword.trim().isEmpty()) || 
            (filterRole != null && !filterRole.trim().isEmpty()) ||
            enabledFilter != null ||
            (sortBy != null && !sortBy.trim().isEmpty())) {
            // Có tham số tìm kiếm/lọc → sử dụng searchAndFilter
            list = userDAO.searchAndFilter(searchKeyword, filterRole, enabledFilter, sortBy, sortOrder);
        } else {
            // Không có tham số → lấy tất cả
            list = userDAO.findAll();
        }
        
        // Chuẩn hóa đường dẫn ảnh cho danh sách user
        ImagePathHelper.normalizeUserImagePaths(list, contextPath);
        
        // Tính số tin cho mỗi user
        NewsDAO newsDAO = new NewsDAO();
        Map<String, Integer> newsCountMap = new HashMap<>();
        for (User user : list) {
            int count = newsDAO.countByAuthor(user.getId());
            newsCountMap.put(user.getId(), count);
        }
        
        // Truyền các tham số tìm kiếm về JSP để giữ lại giá trị trong form
        request.setAttribute("userList", list);
        request.setAttribute("newsCountMap", newsCountMap);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("filterRole", filterRole);
        request.setAttribute("filterEnabled", filterEnabled);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        forwardToAdminView(request, response, "Quản lý Người dùng", "/views/admin/user_crud.jsp");
    }

    /**
     * Xử lý AJAX search request - trả về HTML fragment của danh sách user
     */
    private void searchUsersAjax(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String contextPath = getContextPath(request);
        
        // Lấy các tham số tìm kiếm và lọc
        String searchKeyword = request.getParameter("searchKeyword");
        String filterRole = request.getParameter("filterRole");
        String filterEnabled = request.getParameter("filterEnabled");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        
        // Xử lý filterEnabled (convert String sang Boolean)
        Boolean enabledFilter = null;
        if (filterEnabled != null && !filterEnabled.trim().isEmpty()) {
            if ("true".equalsIgnoreCase(filterEnabled)) {
                enabledFilter = true;
            } else if ("false".equalsIgnoreCase(filterEnabled)) {
                enabledFilter = false;
            }
        }
        
        // Xử lý tìm kiếm và lọc
        List<User> list;
        if ((searchKeyword != null && !searchKeyword.trim().isEmpty()) || 
            (filterRole != null && !filterRole.trim().isEmpty()) ||
            enabledFilter != null ||
            (sortBy != null && !sortBy.trim().isEmpty())) {
            // Có tham số tìm kiếm/lọc → sử dụng searchAndFilter
            list = userDAO.searchAndFilter(searchKeyword, filterRole, enabledFilter, sortBy, sortOrder);
        } else {
            // Không có tham số → lấy tất cả
            list = userDAO.findAll();
        }
        
        // Chuẩn hóa đường dẫn ảnh cho danh sách user
        ImagePathHelper.normalizeUserImagePaths(list, contextPath);
        
        // Tính số tin cho mỗi user
        NewsDAO newsDAO = new NewsDAO();
        Map<String, Integer> newsCountMap = new HashMap<>();
        for (User user : list) {
            int count = newsDAO.countByAuthor(user.getId());
            newsCountMap.put(user.getId(), count);
        }
        
        // Set attributes để JSP fragment có thể render
        request.setAttribute("userList", list);
        request.setAttribute("newsCountMap", newsCountMap);
        
        // Trả về HTML fragment
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        // Forward đến JSP fragment để render phần table
        request.getRequestDispatcher("/views/admin/user_list_fragment.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = getContextPath(request);
        String id = request.getParameter("id");
        
        if (isNullOrEmpty(id)) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        User user = userDAO.findById(id);
        if (user == null) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        // Kiểm tra quyền: Admin thường không được sửa Super Admin
        if (!currentUser.isSuperAdmin() && user.isSuperAdmin()) {
            // Admin thường không thể sửa Super Admin
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        // Chuẩn hóa đường dẫn ảnh cho user đang edit
        ImagePathHelper.normalizeUserImagePath(user, contextPath);
        
        request.setAttribute("userItem", user);
        showList(request, response);
    }

    // Sửa lỗi visibility: protected thay vì private để HttpServlet gọi được nếu cần (hoặc cứ để private nếu chỉ gọi nội bộ doGet)
    // Nhưng để an toàn và đúng chuẩn override nếu có, protected là tốt nhất. 
    // Tuy nhiên ở đây doDelete là hàm riêng ta tự viết, private cũng được, 
    // nhưng để tránh lỗi "reduce visibility" nếu lỡ trùng tên hàm cha, ta dùng protected.
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String contextPath = getContextPath(request);
        
        if (isNullOrEmpty(id)) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        // Không cho phép tự xóa chính mình
        if (id.equals(currentUser.getId())) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        // Lấy thông tin user cần xóa
        User userToDelete = userDAO.findById(id);
        if (userToDelete == null) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        // Quy tắc xóa:
        // - Super Admin: Có thể xóa tất cả (admin và phóng viên), trừ chính mình
        // - Admin thường: Chỉ có thể xóa phóng viên, không thể xóa admin
        
        // Kiểm tra và xử lý bài viết của user trước khi xóa
        NewsDAO newsDAO = new NewsDAO();
        int newsCount = newsDAO.countByAuthor(id);
        
        if (currentUser.isSuperAdmin()) {
            // Super Admin có thể xóa tất cả (đã check không xóa chính mình ở trên)
            // Set author = NULL cho tất cả bài viết của user này
            if (newsCount > 0) {
                newsDAO.setAuthorToNull(id);
            }
            userDAO.delete(id);
            
            // Log xóa user
            activityLogService.logUserDelete(currentUser, userToDelete.getId(), userToDelete.getFullname(), request);
        } else if (userToDelete.isRole()) {
            // Admin thường không thể xóa admin khác
            response.sendRedirect(contextPath + "/admin/users");
            return;
        } else {
            // Admin thường có thể xóa phóng viên
            // Set author = NULL cho tất cả bài viết của user này
            if (newsCount > 0) {
                newsDAO.setAuthorToNull(id);
            }
            userDAO.delete(id);
            
            // Log xóa user
            activityLogService.logUserDelete(currentUser, userToDelete.getId(), userToDelete.getFullname(), request);
        }
        
        response.sendRedirect(contextPath + "/admin/users");
    }

    /**
     * Toggle trạng thái enabled của user (khóa/mở)
     */
    private void toggleStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String contextPath = getContextPath(request);
        
        if (isNullOrEmpty(id)) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        // Không cho phép tự khóa chính mình
        if (id.equals(currentUser.getId())) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        User user = userDAO.findById(id);
        if (user == null) {
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        // Kiểm tra quyền: Super Admin có thể khóa tất cả, Admin chỉ có thể khóa phóng viên
        if (!currentUser.isSuperAdmin() && user.isRole()) {
            // Admin thường không thể khóa admin khác
            response.sendRedirect(contextPath + "/admin/users");
            return;
        }
        
        // Toggle status
        boolean newStatus = !user.isEnabled();
        user.setEnabled(newStatus);
        userDAO.update(user);
        
        // Log khóa/mở khóa tài khoản
        if (newStatus) {
            activityLogService.logUserUnlock(currentUser, user.getId(), user.getFullname(), request);
        } else {
            activityLogService.logUserLock(currentUser, user.getId(), user.getFullname(), request);
        }
        
        response.sendRedirect(contextPath + "/admin/users");
    }

    /**
     * Tạo mới một người dùng
     * @return true nếu thành công (cần redirect), false nếu có lỗi validation (đã forward)
     */
    private boolean doCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = new User();
        BeanUtils.populate(user, request.getParameterMap());
        
        // Lấy user hiện tại để check quyền
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return false;
        }
        
        // Xử lý checkbox Gender và Role trước
        user.setGender(request.getParameter("gender") != null && request.getParameter("gender").equals("true"));
        
        // Xử lý role: "super" -> Super Admin (role = true), "true" -> Admin, "false" -> Reporter
        String roleParam = request.getParameter("role");
        boolean isSuperAdmin = "super".equalsIgnoreCase(roleParam);
        
        // Bảo vệ: Chỉ Super Admin mới có thể tạo Super Admin
        if (isSuperAdmin && !currentUser.isSuperAdmin()) {
            request.setAttribute("error", "Bạn không có quyền tạo Super Admin. Chỉ Super Admin mới có thể tạo Super Admin.");
            request.setAttribute("userItem", user);
            showList(request, response);
            return false;
        }
        
        user.setRole(isSuperAdmin || (roleParam != null && roleParam.equals("true")));
        
        // Mặc định enabled = true khi tạo user mới
        user.setEnabled(true);
        
        // Kiểm tra email đã tồn tại chưa (tránh trùng email khi đăng nhập bằng Gmail)
        String email = user.getEmail();
        if (!ValidationHelper.isNullOrEmpty(email)) {
            if (userDAO.existsByEmail(email.trim())) {
                request.setAttribute("error", "Email \"" + email + "\" đã tồn tại. Vui lòng sử dụng email khác.");
                request.setAttribute("userItem", user);
                showList(request, response);
                return false;
            }
        }
        
        // Kiểm tra và tự động tạo mã người dùng nếu chưa có hoặc bị trùng
        String userId = user.getId();
        if (ValidationHelper.isNullOrEmpty(userId) || userDAO.existsById(userId.trim())) {
            // Tự động tạo mã mới dựa trên role (super, true, false)
            String roleType = isSuperAdmin ? "super" : (user.isRole() ? "true" : "false");
            userId = userDAO.getNextUserId(roleType);
            user.setId(userId);
        }
        
        // Xử lý upload ảnh
        String imageUrl = FileUploadHelper.saveImage(request);
        if (imageUrl != null) {
            user.setImagePath(imageUrl);
        }
        
        try {
            userDAO.insert(user);
            
            // Log tạo user
            if (currentUser != null) {
                activityLogService.logUserCreate(currentUser, user.getId(), user.getFullname(), request);
            }
            
            // Thành công, cần redirect
            return true;
        } catch (Exception e) {
            // Xử lý lỗi duplicate key từ database (race condition)
            if (ValidationHelper.isDuplicateKeyException(e)) {
                // Thử tạo lại mã mới
                try {
                    String roleType = isSuperAdmin ? "super" : (user.isRole() ? "true" : "false");
                    userId = userDAO.getNextUserId(roleType);
                    user.setId(userId);
                    userDAO.insert(user);
                    
                    // Log tạo user
                    if (currentUser != null) {
                        activityLogService.logUserCreate(currentUser, user.getId(), user.getFullname(), request);
                    }
                    
                    return true; // Thành công sau khi tạo lại mã
                } catch (Exception ex) {
                    request.setAttribute("error", "Mã người dùng đã tồn tại. Có thể người khác vừa tạo mã này.");
                    request.setAttribute("userItem", user);
                    showList(request, response);
                    return false; // Đã forward, không cần redirect
                }
            }
            throw e; // Nếu không phải lỗi duplicate, throw lại exception
        }
    }

    /**
     * Cập nhật thông tin người dùng
     * @return true nếu thành công (cần redirect), false nếu có lỗi validation (đã forward)
     */
    private boolean doUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String contextPath = getContextPath(request);
        
        if (isNullOrEmpty(id)) {
            response.sendRedirect(contextPath + "/admin/users");
            return true;
        }
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(contextPath + "/admin/users");
            return true;
        }
        
        User user = userDAO.findById(id);
        if (user == null) {
            return true; // User không tồn tại, redirect
        }
        
        // Kiểm tra quyền: Admin thường không được sửa Super Admin
        if (!currentUser.isSuperAdmin() && user.isSuperAdmin()) {
            // Admin thường không thể sửa Super Admin
            response.sendRedirect(contextPath + "/admin/users");
            return true;
        }
        
        String oldPassword = user.getPassword();
        String oldEmail = user.getEmail(); // Lưu email cũ để so sánh
        
        BeanUtils.populate(user, request.getParameterMap());
        
        // Bảo vệ mật khẩu khi cập nhật: Nếu để trống thì giữ nguyên mật khẩu cũ
        String inputPassword = request.getParameter("password");
        if (inputPassword == null || inputPassword.trim().isEmpty()) {
            user.setPassword(oldPassword);
        } else if (!inputPassword.equals(oldPassword)) {
            user.setPassword(PasswordUtil.ensureHashed(inputPassword));
        } else {
            user.setPassword(oldPassword);
        }
        
        user.setGender(request.getParameter("gender") != null && request.getParameter("gender").equals("true"));
        
        // Xử lý role: 
        // - Super Admin không được thay đổi role (luôn là true)
        // - Admin thường có thể sửa role của phóng viên
        String roleParam = request.getParameter("role");
        if (user.isSuperAdmin()) {
            // Super Admin luôn có role = true, không được thay đổi
            user.setRole(true);
        } else {
            // Với user không phải Super Admin, có thể thay đổi role
            user.setRole(roleParam != null && roleParam.equals("true"));
        }
        
        // Kiểm tra email trùng (nếu email mới khác email cũ)
        String newEmail = user.getEmail();
        if (!ValidationHelper.isNullOrEmpty(newEmail) && !newEmail.trim().equalsIgnoreCase(oldEmail)) {
            if (userDAO.existsByEmail(newEmail.trim())) {
                request.setAttribute("error", "Email \"" + newEmail + "\" đã tồn tại. Vui lòng sử dụng email khác.");
                request.setAttribute("userItem", user);
                showList(request, response);
                return false; // Đã forward, không cần redirect
            }
        }

        // Xử lý upload ảnh
        String existingImage = request.getParameter("imagePath");
        String imageUrl = FileUploadHelper.handleImageUpdate(request, existingImage);
        if (imageUrl != null) {
            user.setImagePath(imageUrl);
        }

        userDAO.update(user);
        
        // Log cập nhật user
        if (currentUser != null) {
            activityLogService.logUserUpdate(currentUser, user.getId(), user.getFullname(), request);
        }
        
        // Cập nhật lại session nếu user đang cập nhật chính mình
        if (currentUser != null && currentUser.getId().equals(id)) {
            // Lấy lại user mới từ database để đảm bảo có đầy đủ thông tin
            User updatedUser = userDAO.findById(id);
            if (updatedUser != null) {
                // Chuẩn hóa đường dẫn ảnh cho user trong session
                ImagePathHelper.normalizeUserImagePath(updatedUser, contextPath);
                // Cập nhật lại session
                session.setAttribute("user", updatedUser);
            }
        }
        
        // Thành công, cần redirect
        return true;
    }
    
    /**
     * Tạo mã người dùng tự động dựa trên role (dùng cho AJAX)
     */
    private void generateUserId(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String roleParam = request.getParameter("role");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (roleParam == null || roleParam.trim().isEmpty()) {
            response.getWriter().write("{\"success\": false, \"message\": \"Vui lòng chọn vai trò\"}");
            return;
        }
        
        try {
            // roleParam có thể là: "super", "true", "false"
            String nextId = userDAO.getNextUserId(roleParam.trim());
            if (nextId != null) {
                response.getWriter().write("{\"success\": true, \"id\": \"" + nextId + "\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Không thể tạo mã người dùng\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi tạo mã người dùng: " + e.getMessage() + "\"}");
        }
    }
}