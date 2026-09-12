package poly.com.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import poly.com.dao.CategoryDAO;
import poly.com.dao.NewsDAO;
import poly.com.dao.NewsletterDAO;
import poly.com.entity.Category;
import poly.com.entity.News;
import poly.com.entity.Newsletter;
import poly.com.entity.User;
import poly.com.service.ActivityLogService;
import poly.com.util.EmailService;
import poly.com.util.FileUploadHelper;
import poly.com.util.ImagePathHelper;
import poly.com.util.ValidationHelper;

/**
 * Controller quản lý tin tức (News) - dành cho Admin và Reporter
 * Xử lý CRUD: Create, Read, Update, Delete tin tức, upload ảnh, tìm kiếm, phân trang
 */
@WebServlet("/admin/news")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class NewsAdminController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    private NewsDAO newsDAO;
    private CategoryDAO categoryDAO;
    private ActivityLogService activityLogService;

    @Override
    public void init() throws ServletException {
        newsDAO = new NewsDAO();
        categoryDAO = new CategoryDAO();
        activityLogService = new ActivityLogService();
    }

    /**
     * Xử lý request GET: Hiển thị danh sách tin hoặc form chỉnh sửa
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User user = checkLogin(request, response);
        if (user == null) return;

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
                    case "checkId":
                        checkIdExists(request, response);
                        return;
                    case "generateId":
                        generateNewsId(request, response);
                        return;
                    case "searchAjax":
                        searchNewsAjax(request, response);
                        return;
                }
            }
            showNewsList(request, response);
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    /**
     * Xử lý request POST: Tạo mới hoặc cập nhật tin tức
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User user = checkLogin(request, response);
        if (user == null) return;

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
                        doUpdate(request, response);
                        break;
                }
            }
            // Chỉ redirect nếu không có lỗi validation
            if (shouldRedirect) {
                response.sendRedirect(getContextPath(request) + "/admin/news");
            }
        } catch (Exception e) {
            // Xử lý lỗi SQL (như duplicate key) một cách thân thiện hơn
            if (ValidationHelper.isDuplicateKeyException(e)) {
                request.setAttribute("error", "Mã bản tin đã tồn tại. Vui lòng chọn mã khác.");
                try {
                    showNewsList(request, response);
                    return;
                } catch (Exception ex) {
                    handleException(request, response, ex);
                }
            }
            handleException(request, response, e);
        }
    }

    /**
     * Hiển thị danh sách tin tức với phân quyền: Admin thấy tất cả, Phóng viên chỉ thấy tin của mình
     * Hỗ trợ tìm kiếm và lọc
     */
    private void showNewsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String contextPath = getContextPath(request);
        
        // Lấy các tham số tìm kiếm và lọc
        String searchTitle = request.getParameter("searchTitle");
        String filterCategory = request.getParameter("filterCategory");
        String filterAuthor = request.getParameter("filterAuthor");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        
        // Xử lý tìm kiếm và lọc
        List<News> newsList;
        String authorId = null;
        
        // Nếu có filterAuthor, ưu tiên dùng nó (Admin có thể xem tin của user khác)
        if (!isNullOrEmpty(filterAuthor) && user.isRole()) {
            authorId = filterAuthor;
        } else if (!user.isRole()) {
            // Phóng viên chỉ thấy tin của mình
            authorId = user.getId();
        }
        
        // Nếu có tham số tìm kiếm/lọc, sử dụng method searchAndFilter
        if ((searchTitle != null && !searchTitle.trim().isEmpty()) || 
            (filterCategory != null && !filterCategory.trim().isEmpty()) ||
            (authorId != null && !authorId.trim().isEmpty()) ||
            (sortBy != null && !sortBy.trim().isEmpty())) {
            newsList = newsDAO.searchAndFilter(
                searchTitle, 
                filterCategory, 
                authorId, 
                sortBy, 
                sortOrder
            );
        } else {
            // Nếu không có tham số, lấy tất cả hoặc theo author
            if (user.isRole()) {
                newsList = newsDAO.findAll();
            } else {
                newsList = newsDAO.findByAuthor(user.getId());
            }
        }
        
        // Chuẩn hóa đường dẫn ảnh cho danh sách tin
        ImagePathHelper.normalizeImagePaths(newsList, contextPath);
        
        List<Category> categoriesList = categoryDAO.findAll();
        request.setAttribute("newsList", newsList);
        request.setAttribute("categoriesList", categoriesList);
        
        // Truyền lại các tham số tìm kiếm để hiển thị trong form
        request.setAttribute("searchTitle", searchTitle);
        request.setAttribute("filterCategory", filterCategory);
        request.setAttribute("filterAuthor", filterAuthor);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        forwardToAdminView(request, response, "Quản lý Tin tức", "/views/admin/news_crud.jsp");
    }
    
    /**
     * Hiển thị form chỉnh sửa tin tức
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        News newsItem = newsDAO.findById(id);
        
        // Chuẩn hóa đường dẫn ảnh để đảm bảo có contextPath
        if (newsItem != null) {
            String contextPath = getContextPath(request);
            ImagePathHelper.normalizeImagePath(newsItem, contextPath);
        }
        
        request.setAttribute("newsItem", newsItem);
        showNewsList(request, response);
    }

    /**
     * Xóa một tin tức, kiểm tra phân quyền: Phóng viên chỉ được xóa tin của mình
     */
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        if (!isNullOrEmpty(id)) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Lấy thông tin tin tức TRƯỚC KHI xóa (để log)
            News news = newsDAO.findById(id);
            
            if (user != null && !user.isRole()) {
                if (news != null && news.getAuthor() != null && !news.getAuthor().equals(user.getId())) {
                    response.sendRedirect(getContextPath(request) + "/admin/news");
                    return;
                }
            }
            
            newsDAO.delete(id);
            
            // Log xóa tin tức
            if (user != null && news != null) {
                activityLogService.logNewsDelete(user, news.getId(), news.getTitle(), request);
            }
        }
        response.sendRedirect(getContextPath(request) + "/admin/news");
    }

    /**
     * Tạo mới một tin tức
     * @return true nếu thành công (cần redirect), false nếu có lỗi validation (đã forward)
     */
    private boolean doCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        News entity = new News();
        User user = (User) request.getSession().getAttribute("user");
        
        BeanUtils.populate(entity, request.getParameterMap());
        
        // Kiểm tra và tự động tạo mã bản tin bằng UUID v4 nếu chưa có hoặc bị trùng
        String newsId = entity.getId();
        if (newsId == null || newsId.trim().isEmpty() || newsDAO.existsById(newsId.trim())) {
            newsId = UUID.randomUUID().toString();
            entity.setId(newsId);
        }
        
        String imageUrl = FileUploadHelper.saveImage(request);
        if (imageUrl != null) {
            entity.setImage(imageUrl);
        }
        
        entity.setAuthor(user.getId());
        entity.setPostedDate(new Date());
        entity.setViewCount(0);
        entity.setHome(request.getParameter("home") != null); 
        
        newsDAO.insert(entity);
        
        // Kiểm tra checkbox "Gửi email newsletter"
        String sendNewsletter = request.getParameter("sendNewsletter");
        if ("true".equals(sendNewsletter)) {
            try {
                NewsletterDAO newsletterDAO = new NewsletterDAO();
                List<Newsletter> subscribers = newsletterDAO.findByEnabled(true);
                
                if (!subscribers.isEmpty()) {
                    EmailService emailService = new EmailService();
                    String contextPath = getContextPath(request);
                    
                    // Lấy base URL đầy đủ (scheme + serverName + port + contextPath) cho email
                    String scheme = request.getScheme(); // http hoặc https
                    String serverName = request.getServerName(); // localhost hoặc domain
                    int serverPort = request.getServerPort();
                    String baseUrl = scheme + "://" + serverName;
                    if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
                        baseUrl += ":" + serverPort;
                    }
                    baseUrl += contextPath;
                    
                    // Lấy đường dẫn thực tế của thư mục /uploads trên server để embed ảnh
                    String servletContextPath = request.getServletContext().getRealPath("/uploads");
                    
                    // Lấy danh sách email
                    List<String> emailList = new java.util.ArrayList<>();
                    for (Newsletter subscriber : subscribers) {
                        emailList.add(subscriber.getEmail());
                    }
                    
                    // Gửi email (đồng bộ, đơn giản) - truyền baseUrl và servletContextPath để embed ảnh
                    int successCount = emailService.sendBulkNewsletter(emailList, entity, baseUrl, servletContextPath);
                    
                    // Thông báo thành công
                    request.getSession().setAttribute("message", 
                        "Đã tạo tin và gửi email newsletter cho " + successCount + "/" + subscribers.size() + " subscribers!");
                } else {
                    request.getSession().setAttribute("message", 
                        "Đã tạo tin. Không có subscribers để gửi email.");
                }
            } catch (Exception e) {
                // Không block việc tạo tin nếu gửi email lỗi
                e.printStackTrace();
                System.err.println("Lỗi khi gửi email newsletter: " + e.getMessage());
                request.getSession().setAttribute("message", 
                    "Đã tạo tin. Có lỗi khi gửi email newsletter: " + e.getMessage());
            }
        } else {
            request.getSession().setAttribute("message", "Đã tạo tin thành công!");
        }
        
        // Log tạo tin tức
        activityLogService.logNewsCreate(user, entity.getId(), entity.getTitle(), request);
        
        // Thành công, cần redirect
        return true;
    }

    /**
     * Cập nhật thông tin tin tức, kiểm tra phân quyền: Phóng viên chỉ được sửa tin của mình
     */
    private void doUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            return;
        }
        
        News entity = newsDAO.findById(id);
        if (entity == null) {
            return;
        }
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null && !user.isRole() && (entity.getAuthor() == null || !entity.getAuthor().equals(user.getId()))) {
            return;
        }
        
        BeanUtils.populate(entity, request.getParameterMap());
        
        String existingImage = request.getParameter("image");
        String imageUrl = FileUploadHelper.handleImageUpdate(request, existingImage);
        if (imageUrl != null) {
            entity.setImage(imageUrl);
        }
        
        entity.setHome(request.getParameter("home") != null);
        
        newsDAO.update(entity);
        
        // Log cập nhật tin tức
        if (user != null) {
            activityLogService.logNewsUpdate(user, entity.getId(), entity.getTitle(), request);
        }
    }

    /**
     * Kiểm tra xem mã bản tin đã tồn tại chưa (dùng cho AJAX validation)
     */
    private void checkIdExists(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String id = request.getParameter("id");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (id == null || id.trim().isEmpty()) {
            response.getWriter().write("{\"exists\": false}");
            return;
        }
        
        boolean exists = newsDAO.existsById(id.trim());
        response.getWriter().write("{\"exists\": " + exists + "}");
    }
    
    /**
     * Tạo mã bản tin tự động theo chuẩn UUID v4 (RFC 4122)
     */
    private void generateNewsId(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String nextId = UUID.randomUUID().toString();
            response.getWriter().write("{\"success\": true, \"id\": \"" + nextId + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi tạo mã bản tin: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Tìm kiếm và lọc bản tin qua AJAX, trả về HTML fragment
     */
    private void searchNewsAjax(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String contextPath = getContextPath(request);
        
        // Lấy các tham số tìm kiếm và lọc
        String searchTitle = request.getParameter("searchTitle");
        String filterCategory = request.getParameter("filterCategory");
        String filterAuthor = request.getParameter("filterAuthor");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        
        // Xử lý tìm kiếm và lọc
        List<News> newsList;
        String authorId = null;
        
        // Nếu có filterAuthor, ưu tiên dùng nó (Admin có thể xem tin của user khác)
        if (!isNullOrEmpty(filterAuthor) && user.isRole()) {
            authorId = filterAuthor;
        } else if (!user.isRole()) {
            // Phóng viên chỉ thấy tin của mình
            authorId = user.getId();
        }
        
        // Nếu có tham số tìm kiếm/lọc, sử dụng method searchAndFilter
        if ((searchTitle != null && !searchTitle.trim().isEmpty()) || 
            (filterCategory != null && !filterCategory.trim().isEmpty()) ||
            (authorId != null && !authorId.trim().isEmpty()) ||
            (sortBy != null && !sortBy.trim().isEmpty())) {
            newsList = newsDAO.searchAndFilter(
                searchTitle, 
                filterCategory, 
                authorId, 
                sortBy, 
                sortOrder
            );
        } else {
            // Nếu không có tham số, lấy tất cả hoặc theo author
            if (user.isRole()) {
                newsList = newsDAO.findAll();
            } else {
                newsList = newsDAO.findByAuthor(user.getId());
            }
        }
        
        // Chuẩn hóa đường dẫn ảnh cho danh sách tin
        ImagePathHelper.normalizeImagePaths(newsList, contextPath);
        
        List<Category> categoriesList = categoryDAO.findAll();
        
        // Set attributes để JSP có thể render
        request.setAttribute("newsList", newsList);
        request.setAttribute("categoriesList", categoriesList);
        
        // Trả về HTML fragment
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        // Forward đến JSP fragment để render phần table
        request.getRequestDispatcher("/views/admin/news_list_fragment.jsp").forward(request, response);
    }

}