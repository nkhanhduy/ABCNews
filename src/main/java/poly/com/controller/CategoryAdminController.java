package poly.com.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import poly.com.dao.CategoryDAO;
import poly.com.dao.NewsDAO;
import poly.com.entity.Category;
import poly.com.entity.User;
import poly.com.exception.DuplicateSlugException;
import poly.com.service.ActivityLogService;
import poly.com.service.CategoryService;
import poly.com.service.impl.CategoryServiceImpl;
import poly.com.util.ValidationHelper;

/**
 * Controller quản lý loại tin (Category) - chỉ dành cho Admin
 * Xử lý CRUD: Create, Read, Update, Delete các loại tin
 */
@WebServlet("/admin/categories")
public class CategoryAdminController extends BaseController {
    private static final long serialVersionUID = 1L;
       
    private CategoryService categoryService;
    private NewsDAO newsDAO;
    private ActivityLogService activityLogService;

    /**
     * Khởi tạo CategoryService và NewsDAO khi servlet được load
     */
    @Override
    public void init() throws ServletException {
        categoryService = new CategoryServiceImpl();
        newsDAO = new NewsDAO();
        activityLogService = new ActivityLogService();
    }

    /**
     * Xử lý request GET: Hiển thị danh sách loại tin hoặc form chỉnh sửa
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkAdminRole(request, response)) return;
        
        String action = request.getParameter("action");
        try {
            if (action != null) {
                switch (action) {
                    case "edit":
                        showEditForm(request, response);
                        return;
                }
            }
            showCategoryList(request, response);
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    /**
     * Xử lý request POST: Tạo mới, cập nhật hoặc xóa loại tin
     */
    @Override
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
                        shouldRedirect = handleCreate(request, response);
                        break;
                    case "update":
                        handleUpdate(request, response);
                        break;
                    case "delete":
                        handleDelete(request, response);
                        return;
                }
            }
            // Chỉ redirect nếu không có lỗi validation (đã forward)
            if (shouldRedirect) {
                response.sendRedirect(getContextPath(request) + "/admin/categories");
            }
        } catch (DuplicateSlugException e) {
            request.setAttribute("error", "Đường dẫn thân thiện (slug) \"" + e.getSlug() + "\" đã tồn tại. Vui lòng chọn tên loại tin khác.");
            try {
                showCategoryList(request, response);
                return;
            } catch (Exception ex) {
                handleException(request, response, ex);
            }
        } catch (Exception e) {
            // Xử lý lỗi SQL (như duplicate key hoặc duplicate slug) một cách thân thiện hơn
            if (ValidationHelper.isDuplicateSlugException(e)) {
                request.setAttribute("error", "Đường dẫn thân thiện (slug) đã tồn tại. Vui lòng chọn tên loại tin khác.");
                try {
                    showCategoryList(request, response);
                    return;
                } catch (Exception ex) {
                    handleException(request, response, ex);
                }
            }
            if (ValidationHelper.isDuplicateKeyException(e)) {
                request.setAttribute("error", "Mã loại tin đã tồn tại. Vui lòng chọn mã khác.");
                try {
                    showCategoryList(request, response);
                    return;
                } catch (Exception ex) {
                    handleException(request, response, ex);
                }
            }
            handleException(request, response, e);
        }
    }

    /**
     * Hiển thị danh sách tất cả loại tin
     */
    private void showCategoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categoriesList = categoryService.getAllCategories();
        
        // Tạo Map để lưu số lượng tin tức cho mỗi category
        Map<String, Integer> newsCountMap = new HashMap<>();
        for (Category category : categoriesList) {
            int count = newsDAO.countByCategoryId(category.getId());
            newsCountMap.put(category.getId(), count);
        }
        
        request.setAttribute("categoriesList", categoriesList);
        request.setAttribute("newsCountMap", newsCountMap);
        forwardToAdminView(request, response, "Quản lý Loại tin", "/views/admin/category_crud.jsp");
    }
    
    /**
     * Hiển thị form chỉnh sửa loại tin
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        Category categoryItem = categoryService.findById(id);
        request.setAttribute("categoryItem", categoryItem);
        showCategoryList(request, response);
    }
    
    /**
     * Xóa một loại tin khỏi database
     * Kiểm tra foreign key constraint: không cho xóa nếu còn tin tức sử dụng category này
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String id = request.getParameter("id");
        if (!isNullOrEmpty(id)) {
            // Lấy thông tin category trước khi xóa (để log)
            Category category = categoryService.findById(id);
            
            // Kiểm tra xem có tin tức nào đang sử dụng category này không
            int newsCount = newsDAO.countByCategoryId(id);
            if (newsCount > 0) {
                // Không thể xóa category đang có tin tức
                request.setAttribute("error", "Không thể xóa loại tin này vì đang có " + newsCount + " tin tức sử dụng. Vui lòng xóa hoặc chuyển các tin tức sang loại tin khác trước.");
                try {
                    showCategoryList(request, response);
                    return;
                } catch (Exception e) {
                    handleException(request, response, e);
                    return;
                }
            }
            // Nếu không có tin tức nào, mới cho phép xóa
            categoryService.deleteCategory(id);
            
            // Log xóa category
            if (category != null) {
                User currentUser = (User) request.getSession().getAttribute("user");
                if (currentUser != null) {
                    activityLogService.logCategoryDelete(currentUser, category.getId(), category.getName(), request);
                }
            }
        }
        response.sendRedirect(getContextPath(request) + "/admin/categories");
    }
    
    /**
     * Tạo mới một loại tin
     * Xử lý race condition: Kiểm tra mã trùng trước khi insert và xử lý lỗi duplicate key
     * @return true nếu thành công (cần redirect), false nếu có lỗi validation (đã forward)
     */
    private boolean handleCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Category entity = new Category();
        BeanUtils.populate(entity, request.getParameterMap());
        
        // Kiểm tra mã loại tin đã tồn tại chưa (tránh race condition)
        String categoryId = entity.getId();
        if (ValidationHelper.isNullOrEmpty(categoryId)) {
            request.setAttribute("error", "Vui lòng nhập mã loại tin.");
            request.setAttribute("categoryItem", entity);
            showCategoryList(request, response);
            return false;
        }

        String name = entity.getName();
        if (ValidationHelper.isNullOrEmpty(name)) {
            request.setAttribute("error", "Vui lòng nhập tên loại tin.");
            request.setAttribute("categoryItem", entity);
            showCategoryList(request, response);
            return false;
        }
        
        categoryId = categoryId.trim();
        name = name.trim();

        if (categoryId.length() > 50) {
            request.setAttribute("error", "Mã loại tin không được vượt quá 50 ký tự.");
            request.setAttribute("categoryItem", entity);
            showCategoryList(request, response);
            return false;
        }

        if (name.length() > 200) {
            request.setAttribute("error", "Tên loại tin không được vượt quá 200 ký tự.");
            request.setAttribute("categoryItem", entity);
            showCategoryList(request, response);
            return false;
        }

        entity.setId(categoryId);
        entity.setName(name);

        // Kiểm tra trong database với synchronized để tránh race condition
        synchronized (this) {
            if (categoryService.existsById(categoryId)) {
                request.setAttribute("error", "Mã loại tin \"" + categoryId + "\" đã tồn tại. Vui lòng chọn mã khác.");
                request.setAttribute("categoryItem", entity);
                showCategoryList(request, response);
                return false;
            }
            
            // Nếu mã chưa tồn tại, thực hiện tạo loại tin (tự sinh slug duy nhất trong Service)
            try {
                categoryService.createCategory(entity);
                
                // Log tạo category
                User currentUser = (User) request.getSession().getAttribute("user");
                if (currentUser != null) {
                    activityLogService.logCategoryCreate(currentUser, entity.getId(), entity.getName(), request);
                }
                
                return true;
            } catch (DuplicateSlugException e) {
                request.setAttribute("error", "Đường dẫn thân thiện (slug) \"" + e.getSlug() + "\" đã tồn tại. Vui lòng chọn tên loại tin khác.");
                request.setAttribute("categoryItem", entity);
                showCategoryList(request, response);
                return false;
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", e.getMessage());
                request.setAttribute("categoryItem", entity);
                showCategoryList(request, response);
                return false;
            } catch (Exception e) {
                // Xử lý lỗi duplicate slug hoặc duplicate key từ database (race condition)
                if (ValidationHelper.isDuplicateSlugException(e)) {
                    request.setAttribute("error", "Đường dẫn thân thiện (slug) của loại tin đã tồn tại. Vui lòng chọn tên khác.");
                    request.setAttribute("categoryItem", entity);
                    showCategoryList(request, response);
                    return false;
                }
                if (ValidationHelper.isDuplicateKeyException(e)) {
                    request.setAttribute("error", "Mã loại tin \"" + categoryId + "\" đã tồn tại. Vui lòng chọn mã khác.");
                    request.setAttribute("categoryItem", entity);
                    showCategoryList(request, response);
                    return false;
                }
                throw e;
            }
        }
    }

    /**
     * Cập nhật thông tin loại tin
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (isNullOrEmpty(id)) {
            return;
        }
        
        Category entity = categoryService.findById(id);
        if (entity != null) {
            String newName = request.getParameter("name");
            if (ValidationHelper.isNullOrEmpty(newName)) {
                request.setAttribute("error", "Tên loại tin không được để trống.");
                showEditForm(request, response);
                return;
            }

            newName = newName.trim();
            if (newName.length() > 200) {
                request.setAttribute("error", "Tên loại tin không được vượt quá 200 ký tự.");
                showEditForm(request, response);
                return;
            }

            entity.setName(newName);
            try {
                categoryService.updateCategory(entity);
                
                // Log cập nhật category
                User currentUser = (User) request.getSession().getAttribute("user");
                if (currentUser != null) {
                    activityLogService.logCategoryUpdate(currentUser, entity.getId(), entity.getName(), request);
                }
            } catch (DuplicateSlugException e) {
                request.setAttribute("error", "Đường dẫn thân thiện (slug) \"" + e.getSlug() + "\" đã tồn tại. Vui lòng đặt tên loại tin khác.");
                showEditForm(request, response);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", e.getMessage());
                showEditForm(request, response);
            } catch (Exception e) {
                if (ValidationHelper.isDuplicateSlugException(e)) {
                    request.setAttribute("error", "Đường dẫn thân thiện (slug) của loại tin đã tồn tại. Vui lòng đặt tên loại tin khác.");
                    showEditForm(request, response);
                    return;
                }
                throw e;
            }
        }
    }
}