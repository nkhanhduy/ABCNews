package poly.com.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.ActivityLogDAO;
import poly.com.dao.UserDAO;
import poly.com.entity.ActivityLog;
import poly.com.entity.User;

/**
 * Controller hiển thị trang lịch sử hoạt động
 * 
 * Chỉ Admin mới có quyền xem (được bảo vệ bởi AuthFilter)
 * 
 * @author Nguyen Duy Khanh
 */
@WebServlet("/admin/activity-logs")
public class ActivityLogsController extends BaseController {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ActivityLogsController.class.getName());
    
    private ActivityLogDAO activityLogDAO = new ActivityLogDAO();
    private UserDAO userDAO = new UserDAO();
    
    private static final int PAGE_SIZE = 20; // Số records mỗi trang
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        User currentUser = checkLogin(request, response);
        if (currentUser == null) {
            return;
        }
        
        // Chỉ Admin mới xem được
        if (!currentUser.isRole()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }
        
        // Lấy filter parameters
        String userIdFilter = request.getParameter("userId");
        String actionFilter = request.getParameter("action");
        String entityFilter = request.getParameter("entity");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");
        String ajaxParam = request.getParameter("ajax");
        
        // Lấy page number
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        // Parse dates
        Date fromDate = null;
        Date toDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            if (fromDateStr != null && !fromDateStr.isEmpty()) {
                fromDate = sdf.parse(fromDateStr);
            }
            if (toDateStr != null && !toDateStr.isEmpty()) {
                toDate = sdf.parse(toDateStr);
                // Set toDate to end of day
                toDate = new Date(toDate.getTime() + 24 * 60 * 60 * 1000 - 1);
            }
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING, "Không thể phân tích định dạng ngày lọc ActivityLog: {0}", e.getMessage());
        }
        
        // Query logs with filters
        List<ActivityLog> logs = activityLogDAO.findWithFilters(
            userIdFilter, actionFilter, entityFilter, fromDate, toDate, page, PAGE_SIZE
        );
        
        // Count total
        int totalRecords = activityLogDAO.countWithFilters(
            userIdFilter, actionFilter, entityFilter, fromDate, toDate
        );
        
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        
        // Set attributes cho JSP
        request.setAttribute("logs", logs);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", PAGE_SIZE);
        
        // Keep filter values
        request.setAttribute("userIdFilter", userIdFilter);
        request.setAttribute("actionFilter", actionFilter);
        request.setAttribute("entityFilter", entityFilter);
        request.setAttribute("fromDateStr", fromDateStr);
        request.setAttribute("toDateStr", toDateStr);
        
        // Nếu là AJAX request, chỉ trả về table fragment
        if ("true".equals(ajaxParam)) {
            request.getRequestDispatcher("/views/admin/activity-logs-table.jsp").forward(request, response);
            return;
        }
        
        // Lấy danh sách users để hiển thị trong dropdown filter (chỉ cần cho lần đầu load)
        List<User> allUsers = userDAO.findAll();
        request.setAttribute("allUsers", allUsers);
        
        // Set view path để render qua admin_layout.jsp
        request.setAttribute("view", "/views/admin/activity-logs.jsp");
        
        // Forward to admin layout
        request.getRequestDispatcher("/views/admin/admin_layout.jsp").forward(request, response);
    }
}

