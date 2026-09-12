<%-- 
  File: news_crud.jsp
  Description: Giao diện CRUD Tin tức (Đã nâng cấp Upload ảnh)
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!-- CSS đã có trong admin_style.css -->

<div class="message-container">
    <c:if test="${not empty message}">
        <div class="message-success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="message-error">${error}</div>
    </c:if>
</div>

<h2>Quản lý Tin tức</h2>
<hr>

<!-- Helper function để lấy tên category từ categoryId -->

<div class="crud-container news-crud-wrapper">
    
    <!-- Card 1: Quản lý Bản tin -->
    <div class="news-form-card card">
        <div class="card-header">
            <h3><i class="fas fa-newspaper me-2"></i>${not empty newsItem ? 'Cập nhật Bản tin' : 'Thêm Bản tin mới'}</h3>
        </div>
        <div class="card-body">
    <!-- === FORM (Thêm/Sửa) === -->
        <c:set var="isEdit" value="${not empty newsItem}" />
        
        <form action="${pageContext.request.contextPath}/admin/news" method="post" enctype="multipart/form-data">
            
            <c:choose>
                <c:when test="${isEdit}">
                    <input type="hidden" name="action" value="update" />
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="action" value="create" />
                </c:otherwise>
            </c:choose>

            <!-- Form 2 cột, mỗi cột 3 trường -->
            <div class="form-two-columns">
                <!-- Cột trái: 3 trường -->
                <div class="form-col-left">
                    <div class="form-group">
                <label for="id">Mã bản tin (Id)</label>
                <input type="text" name="id" id="newsId" value="${newsItem.id}" readonly required
                       style="background-color: #e9ecef; color: #333; font-weight: bold; cursor: not-allowed;">
                <small class="text-muted" style="display: block; margin-top: 5px; font-size: 0.875rem;">
                    <i class="fas fa-info-circle"></i> Mã bản tin sẽ tự động tạo khi bạn chọn loại tin
                </small>
            </div>

                    <div class="form-group">
                        <label for="title">Tiêu đề</label>
                        <input type="text" name="title" id="title" value="<c:out value='${newsItem.title}' />" required spellcheck="false" autocomplete="off" style="white-space: normal !important; text-overflow: clip !important; overflow: visible !important; max-width: none !important; width: 100% !important; min-width: 0 !important; box-sizing: border-box !important;">
                    </div>

                    <div class="form-group">
                        <label for="summary">Tóm tắt</label>
                        <textarea name="summary" rows="4" spellcheck="false">${newsItem.summary}</textarea>
                    </div>
                    
                    <!-- Checkbox row: Hiển thị trên Trang nhất và Gửi email newsletter -->
                    <div class="form-checkbox-row">
                        <div class="form-group-checkbox">
                            <div style="display: flex; align-items: center; gap: 8px;">
                                <input type="checkbox" id="home" name="home" value="true" ${newsItem.home ? 'checked' : ''}>
                                <label for="home" style="margin: 0;">Hiển thị trên Trang nhất</label>
                            </div>
                            <small class="text-muted" style="display: block; margin-top: 5px; font-size: 0.875rem;">
                                <i class="fas fa-info-circle"></i> Tin này sẽ được hiển thị trên trang chủ
                            </small>
                        </div>
                        
                        <div class="form-group-checkbox">
                            <div style="display: flex; align-items: center; gap: 8px;">
                                <input type="checkbox" id="sendNewsletter" name="sendNewsletter" value="true">
                                <label for="sendNewsletter" style="margin: 0;">Gửi email newsletter cho subscribers</label>
                            </div>
                            <small class="text-muted" style="display: block; margin-top: 5px; font-size: 0.875rem;">
                                <i class="fas fa-info-circle"></i> Chỉ gửi email nếu bạn muốn thông báo tin này cho tất cả subscribers
                            </small>
                        </div>
                    </div>
                </div>

                <!-- Cột phải: 3 trường -->
                <div class="form-col-right">
                    <div class="form-group">
                <label for="categoryId">Loại tin</label>
                <select name="categoryId" id="categoryId" required ${isEdit ? 'disabled' : ''}>
                    <option value="">-- Chọn loại tin --</option>
                    <c:forEach var="cat" items="${categoriesList}">
                        <option value="${cat.id}" ${cat.id == newsItem.categoryId ? 'selected' : ''}>
                            ${cat.name} (${cat.id})
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${isEdit}">
                    <input type="hidden" name="categoryId" value="${newsItem.categoryId}">
                </c:if>
            </div>

                    <div class="form-group">
                        <label for="newsContentEditor"><i class="fas fa-edit me-1"></i>Nội dung bài viết (CKEditor WYSIWYG)</label>
                        <textarea name="content" id="newsContentEditor" rows="6" spellcheck="false">${newsItem.content}</textarea>
                    </div>
            
                    <!-- Upload ảnh đại diện -->
                    <div class="form-group">
                <label for="imageFile">Hình ảnh:</label>
                
                <!-- Input chọn file -->
                <input type="file" name="imageFile" accept="image/*">
                
                <!-- Input ẩn để giữ lại đường dẫn ảnh cũ nếu không chọn ảnh mới -->
                <input type="hidden" name="image" value="${newsItem.image}">
                
                <!-- Hiển thị ảnh hiện tại (nếu có) -->
                <c:if test="${not empty newsItem.image}">
                            <div style="margin-top: 10px; text-align: center;">
                                <c:set var="imageUrl" value="${newsItem.image}" />
                                <c:if test="${!fn:startsWith(imageUrl, pageContext.request.contextPath) && fn:startsWith(imageUrl, '/')}">
                                    <c:set var="imageUrl" value="${pageContext.request.contextPath}${newsItem.image}" />
                                </c:if>
                                <img src="${imageUrl}" alt="Ảnh hiện tại" 
                                     style="max-width: 150px; max-height: 150px; border: 2px solid #ddd; padding: 5px; border-radius: 8px; object-fit: cover;"
                                     onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                                <small style="display:none; color:red;">⚠️ Không thể tải ảnh. Vui lòng chọn ảnh mới.</small>
                                <br>
                                <small style="display: block; margin-top: 5px; color: #666;">Ảnh hiện tại</small>
                    </div>
                </c:if>
                    </div>
                </div>
            </div>
            
            <!-- Nút bấm -->
            <div class="button-group">
                <button type="submit" class="btn ${isEdit ? 'btn-update' : 'btn-create'}">
                    ${isEdit ? 'Cập nhật' : 'Thêm mới'}
                </button>
                <a href="${pageContext.request.contextPath}/admin/news" class="btn btn-reset">Làm mới</a>
            </div>
        </form>
        </div>
    </div>

    <!-- Card 2: Tìm kiếm và Bộ lọc -->
    <div class="news-search-card card">
        <div class="card-header">
            <h3><i class="fas fa-search me-2"></i>Tìm kiếm và Bộ lọc</h3>
        </div>
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/admin/news" id="searchForm">
                <div class="search-filter-row">
                    <!-- Tìm kiếm theo tiêu đề -->
                    <div class="search-group">
                        <label for="searchTitle"><i class="fas fa-heading me-1"></i>Tìm kiếm theo tiêu đề</label>
                        <input type="text" 
                               name="searchTitle" 
                               id="searchTitle" 
                               value="${searchTitle}" 
                               placeholder="Nhập từ khóa tìm kiếm...">
                    </div>
                    
                    <!-- Lọc theo loại tin -->
                    <div class="filter-group">
                        <label for="filterCategory"><i class="fas fa-filter me-1"></i>Loại tin</label>
                        <select name="filterCategory" id="filterCategory">
                            <option value="">-- Tất cả loại tin --</option>
                            <c:forEach var="cat" items="${categoriesList}">
                                <option value="${cat.id}" ${cat.id == filterCategory ? 'selected' : ''}>
                                    ${cat.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <!-- Sắp xếp theo -->
                    <div class="filter-group">
                        <label for="sortBy"><i class="fas fa-sort me-1"></i>Sắp xếp theo</label>
                        <select name="sortBy" id="sortBy">
                            <option value="">-- Mặc định --</option>
                            <option value="postedDate" ${sortBy == 'postedDate' ? 'selected' : ''}>Ngày đăng</option>
                            <option value="viewCount" ${sortBy == 'viewCount' ? 'selected' : ''}>Lượt xem</option>
                        </select>
                    </div>
                    
                    <!-- Thứ tự sắp xếp -->
                    <div class="filter-group">
                        <label for="sortOrder"><i class="fas fa-sort-amount-down me-1"></i>Thứ tự</label>
                        <select name="sortOrder" id="sortOrder">
                            <option value="DESC" ${sortOrder == 'DESC' || empty sortOrder ? 'selected' : ''}>Giảm dần</option>
                            <option value="ASC" ${sortOrder == 'ASC' ? 'selected' : ''}>Tăng dần</option>
                        </select>
                    </div>
                </div>
                
                <!-- Nút reset -->
                <div class="search-actions" style="align-self: flex-end;">
                    <a href="${pageContext.request.contextPath}/admin/news" class="btn btn-reset">
                        <i class="fas fa-redo me-1"></i>Làm mới
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- Card 3: Danh sách Bản tin -->
    <div class="news-list-card card">
        <div class="card-header">
            <h3><i class="fas fa-list me-2"></i>Danh sách Bản tin</h3>
            <div class="export-buttons">
                <a href="${pageContext.request.contextPath}/admin/export?type=news&format=csv" class="btn btn-export btn-csv" title="Export CSV">
                    <i class="fas fa-file-csv"></i> CSV
                </a>
                <a href="${pageContext.request.contextPath}/admin/export?type=news&format=excel" class="btn btn-export btn-excel" title="Export Excel">
                    <i class="fas fa-file-excel"></i> Excel
                </a>
                <a href="${pageContext.request.contextPath}/admin/export?type=news&format=pdf" class="btn btn-export btn-pdf" title="Export PDF">
                    <i class="fas fa-file-pdf"></i> PDF
                </a>
            </div>
        </div>
        <div class="card-body" id="newsListContainer">
        <div class="table-responsive">
            <table class="crud-table">
                <thead>
                    <tr>
                        <th>Tiêu đề</th>
                            <th>Loại tin</th>
                        <th>Ngày đăng</th>
                        <th>Tác giả</th>
                        <th>Lượt xem</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="news" items="${newsList}">
                        <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/detail?id=${news.id}" 
                                       class="news-title-link" 
                                       target="_blank"
                                       title="Xem chi tiết tin này">
                                        ${news.title}
                                    </a>
                                </td>
                                <td style="text-align: center;">
                                    <c:set var="categoryName" value="" />
                                    <c:forEach var="cat" items="${categoriesList}">
                                        <c:if test="${cat.id == news.categoryId}">
                                            <c:set var="categoryName" value="${cat.name}" />
                                        </c:if>
                                    </c:forEach>
                                    <c:choose>
                                        <c:when test="${not empty categoryName}">
                                            <span class="badge bg-info" style="display: inline-block;">${categoryName}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">-</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            <td><fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty news.author}">
                                        ${news.author}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted" style="font-style: italic;">Tác giả đã bị xóa</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${news.viewCount}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/news?action=edit&id=${news.id}" 
                                   class="btn btn-sm btn-update">Sửa</a>
                                <a href="${pageContext.request.contextPath}/admin/news?action=delete&id=${news.id}" 
                                   class="btn btn-sm btn-delete" 
                                   onclick="return confirm('Xóa tin này?')">Xóa</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty newsList}">
                        <tr>
                                <td colspan="6">Không có bản tin nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
</div>

<style>
/* Container cho News Management - đảm bảo cards có cùng width */
.news-crud-wrapper {
    display: flex;
    flex-direction: column;
    gap: 30px;
}

/* Card styling cho News Management */
.news-form-card,
.news-search-card,
.news-list-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 0;
    overflow: visible;
    width: 100%;
    box-sizing: border-box;
}

.news-form-card .card-header,
.news-search-card .card-header,
.news-list-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.news-form-card .card-header h3,
.news-search-card .card-header h3,
.news-list-card .card-header h3 {
    margin: 0;
    font-size: 1.3rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.news-form-card .card-body,
.news-search-card .card-body,
.news-list-card .card-body {
    padding: 25px;
}

.search-filter-row {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 20px;
    margin-bottom: 20px;
}

.search-group,
.filter-group {
    display: flex;
    flex-direction: column;
}

.search-group label,
.filter-group label {
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
    font-size: 0.9rem;
}

.search-group input,
.filter-group select {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 14px;
    transition: border-color 0.3s ease;
}

.search-group input:focus,
.filter-group select:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
}

.btn-search {
    background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    font-weight: 500;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.btn-search:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
}

/* Form styling - Layout 2 cột, mỗi cột 3 trường */
.news-form-card .card-body form {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

/* Container 2 cột */
.form-two-columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 30px;
    align-items: start;
}

/* Cột trái và phải */
.form-col-left,
.form-col-right {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.news-form-card .card-body .form-group {
    display: flex;
    flex-direction: column;
}

.news-form-card .card-body .form-group label {
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
}

.news-form-card .card-body .form-group input,
.news-form-card .card-body .form-group select,
.news-form-card .card-body .form-group textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 14px;
    transition: border-color 0.3s ease;
    /* Đảm bảo input không bị ảnh hưởng bởi CSS ellipsis */
    white-space: normal !important;
    text-overflow: clip !important;
    overflow: visible !important;
    max-width: 100% !important;
}

.news-form-card .card-body .form-group input:focus,
.news-form-card .card-body .form-group select:focus,
.news-form-card .card-body .form-group textarea:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* Checkbox row: Hiển thị 2 checkbox cùng hàng, căn đều nội dung */
.form-checkbox-row {
    display: flex;
    gap: 30px;
    align-items: flex-start;
    margin-top: 10px;
}

.news-form-card .card-body .form-group-checkbox {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
    flex: 1;
    min-width: 0;
}

/* Căn giữa checkbox và label */
.news-form-card .card-body .form-group-checkbox input[type="checkbox"] {
    margin: 0;
    vertical-align: middle;
}

.news-form-card .card-body .form-group-checkbox label {
    display: inline-flex;
    align-items: center;
    margin-left: 8px;
    vertical-align: middle;
    cursor: pointer;
}

/* Container cho checkbox và label để căn giữa */
.news-form-card .card-body .form-group-checkbox > *:first-child {
    display: flex;
    align-items: center;
    gap: 8px;
}

.news-form-card .card-body .button-group {
    margin-top: 10px;
}

/* Căn giữa cột Loại tin */
.crud-table th:nth-child(2),
.crud-table td:nth-child(2) {
    text-align: center;
}

/* Cột Tiêu đề: chỉ hiển thị 1 dòng, dài quá dùng ellipsis (chỉ áp dụng cho table, KHÔNG áp dụng cho form) */
table.crud-table th:first-child,
table.crud-table td:first-child {
    max-width: 300px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* Đảm bảo form input KHÔNG bị ảnh hưởng - override mạnh với specificity cao nhất */
.news-form-card input,
.news-form-card input[type="text"],
.news-form-card input[name="title"],
.news-form-card input#title,
.news-form-card textarea {
    white-space: normal !important;
    text-overflow: clip !important;
    overflow: visible !important;
    max-width: 100% !important;
    width: 100% !important;
    min-width: 0 !important;
}

/* Đảm bảo form input không bị ảnh hưởng bởi CSS ellipsis - override mạnh hơn với specificity cao */
.news-form-card .form-group input[type="text"],
.news-form-card .form-group input[type="text"]:focus,
.news-form-card .form-group input#title,
.news-form-card .form-group input#title:focus,
.news-form-card .form-group textarea,
.news-form-card .form-group textarea:focus {
    white-space: normal !important;
    text-overflow: clip !important;
    overflow: visible !important;
    max-width: 100% !important;
    width: 100% !important;
    min-width: 0 !important;
    display: block !important;
}

/* Override tất cả CSS có thể ảnh hưởng đến input title - specificity cao nhất */
.news-form-card .form-group input[name="title"],
.news-form-card .form-group input#title,
input#title[name="title"],
input[name="title"][id="title"] {
    white-space: normal !important;
    text-overflow: clip !important;
    overflow: visible !important;
    max-width: none !important;
    width: 100% !important;
    min-width: 0 !important;
    display: block !important;
    box-sizing: border-box !important;
}

/* Link tiêu đề tin tức */
.news-title-link {
    color: #667eea;
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s ease;
    display: inline-block;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.news-title-link:hover {
    color: #764ba2;
    text-decoration: underline;
    transform: translateX(2px);
}

.news-title-link:active {
    color: #5a67d8;
}

/* Responsive */
@media (max-width: 768px) {
    .form-two-columns {
        grid-template-columns: 1fr;
        gap: 20px;
    }
    
    .search-filter-row {
        grid-template-columns: 1fr;
        gap: 15px;
    }
    
    .search-actions {
        flex-direction: column;
    }
    
    .btn-search,
    .btn-reset {
        width: 100%;
    }
}

@media (max-width: 1024px) and (min-width: 769px) {
    .search-filter-row {
        grid-template-columns: 2fr 1fr;
        gap: 15px;
    }
}
</style>

<script>
// Đảm bảo input title hiển thị đầy đủ nội dung
(function() {
    // Đợi DOM load xong
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', fixTitleInput);
    } else {
        fixTitleInput();
    }
    
    function fixTitleInput() {
        const titleInput = document.getElementById('title');
        if (titleInput) {
            // Lưu giá trị ban đầu từ attribute hoặc value
            let originalValue = titleInput.getAttribute('value');
            if (!originalValue || originalValue.trim() === '') {
                originalValue = titleInput.value;
            }
            
            // Đảm bảo giá trị được set đầy đủ
            if (originalValue && originalValue.length > 0) {
                // Set lại giá trị để đảm bảo không bị truncate
                titleInput.value = originalValue;
                
                // Force re-render với style mạnh
                titleInput.style.setProperty('width', '100%', 'important');
                titleInput.style.setProperty('max-width', 'none', 'important');
                titleInput.style.setProperty('white-space', 'normal', 'important');
                titleInput.style.setProperty('text-overflow', 'clip', 'important');
                titleInput.style.setProperty('overflow', 'visible', 'important');
                titleInput.style.setProperty('min-width', '0', 'important');
                titleInput.style.setProperty('box-sizing', 'border-box', 'important');
                
                // Log để debug (có thể xóa sau)
                console.log('Title input value length:', originalValue.length);
                console.log('Title input value:', originalValue);
            }
        }
    }
})();

// Tự động tạo mã bản tin khi chọn loại tin
(function() {
    const newsIdInput = document.getElementById('newsId');
    const categorySelect = document.getElementById('categoryId');
    const form = newsIdInput ? newsIdInput.closest('form') : null;
    
    if (!newsIdInput || !categorySelect || !form) return;
    
    // Chỉ tự động generate khi không phải chế độ edit
    const isEdit = newsIdInput.value && newsIdInput.value.trim() !== '';
    if (isEdit) {
        // Nếu đang edit, không làm gì cả
        return;
    }
    
    // Khi chọn loại tin, tự động generate mã
    categorySelect.addEventListener('change', function() {
        const categoryId = this.value.trim();
        
        if (categoryId) {
            // Hiển thị loading
            newsIdInput.value = 'Đang tạo mã...';
            newsIdInput.style.backgroundColor = '#e9ecef';
            newsIdInput.style.color = '#333';
            newsIdInput.style.fontWeight = 'bold';
            
            // Gọi AJAX để lấy mã mới
            generateNewsId(categoryId);
        } else {
            // Nếu không chọn loại tin, xóa mã
            newsIdInput.value = '';
        }
    });
    
    // Nếu đã có category được chọn sẵn (khi load lại trang), tự động generate
    if (categorySelect.value && categorySelect.value.trim() !== '') {
        generateNewsId(categorySelect.value.trim());
    }
    
    function generateNewsId(categoryId) {
        const xhr = new XMLHttpRequest();
        const url = '${pageContext.request.contextPath}/admin/news?action=generateId&categoryId=' + encodeURIComponent(categoryId);
        
        xhr.open('GET', url);
        xhr.onload = function() {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.success && response.id) {
                        newsIdInput.value = response.id;
                        // Giữ style xám nhạt và chữ đậm
                        newsIdInput.style.backgroundColor = '#e9ecef';
                        newsIdInput.style.color = '#333';
                        newsIdInput.style.fontWeight = 'bold';
                        // Thêm hiệu ứng nhấp nháy để người dùng chú ý
                        newsIdInput.style.borderColor = '#28a745';
                        setTimeout(function() {
                            newsIdInput.style.borderColor = '';
                        }, 1000);
                    } else {
                        newsIdInput.value = '';
                        newsIdInput.style.backgroundColor = '#e9ecef';
                        newsIdInput.style.color = '#333';
                        newsIdInput.style.fontWeight = 'bold';
                        alert('Lỗi: ' + (response.message || 'Không thể tạo mã bản tin'));
                    }
                } catch (err) {
                    console.error('Lỗi parse JSON:', err);
                    newsIdInput.value = '';
                    newsIdInput.style.backgroundColor = '#e9ecef';
                    newsIdInput.style.color = '#333';
                    newsIdInput.style.fontWeight = 'bold';
                }
            } else {
                newsIdInput.value = '';
                newsIdInput.style.backgroundColor = '#e9ecef';
                newsIdInput.style.color = '#333';
                newsIdInput.style.fontWeight = 'bold';
                console.error('Lỗi khi gọi API:', xhr.status);
            }
        };
        xhr.onerror = function() {
            newsIdInput.value = '';
            newsIdInput.style.backgroundColor = '#e9ecef';
            newsIdInput.style.color = '#333';
            newsIdInput.style.fontWeight = 'bold';
            console.error('Lỗi kết nối');
        };
        xhr.send();
    }
    
    // Validate trước khi submit - đảm bảo có mã bản tin
    form.addEventListener('submit', function(e) {
        const id = newsIdInput.value.trim();
        if (!id || id === 'Đang tạo mã...') {
            e.preventDefault();
            alert('Vui lòng chọn loại tin để tạo mã bản tin!');
            if (categorySelect) {
                categorySelect.focus();
            }
            return false;
        }
    });
})();
</script>

<script>
// Xử lý form tìm kiếm và bộ lọc với AJAX (không reload trang)
(function() {
    const searchForm = document.getElementById('searchForm');
    const searchTitle = document.getElementById('searchTitle');
    const filterCategory = document.getElementById('filterCategory');
    const sortBy = document.getElementById('sortBy');
    const sortOrder = document.getElementById('sortOrder');
    const newsListContainer = document.getElementById('newsListContainer');
    
    if (!searchForm || !newsListContainer) return;
    
    let searchTimeout;
    let isSearching = false;
    
    // Hàm tìm kiếm qua AJAX
    function performSearch() {
        if (isSearching) return;
        
        isSearching = true;
        
        // Lưu nội dung hiện tại để fade out
        const currentContent = newsListContainer.innerHTML;
        
        // Fade out nội dung hiện tại
        newsListContainer.style.opacity = '0.5';
        newsListContainer.style.transition = 'opacity 0.2s ease';
        
        // Hiển thị loading sau một chút
        setTimeout(function() {
            newsListContainer.innerHTML = '<div style="text-align: center; padding: 40px; opacity: 0;" id="loadingDiv"><i class="fas fa-spinner fa-spin fa-2x" style="color: #667eea;"></i><p style="margin-top: 10px; color: #667eea;">Đang tìm kiếm...</p></div>';
            
            // Fade in loading
            const loadingDiv = document.getElementById('loadingDiv');
            if (loadingDiv) {
                loadingDiv.style.transition = 'opacity 0.3s ease';
                loadingDiv.style.opacity = '1';
            }
        }, 200);
        
        // Lấy các giá trị từ form
        const params = new URLSearchParams();
        if (searchTitle && searchTitle.value.trim()) {
            params.append('searchTitle', searchTitle.value.trim());
        }
        if (filterCategory && filterCategory.value) {
            params.append('filterCategory', filterCategory.value);
        }
        if (sortBy && sortBy.value) {
            params.append('sortBy', sortBy.value);
        }
        if (sortOrder && sortOrder.value) {
            params.append('sortOrder', sortOrder.value);
        }
        params.append('action', 'searchAjax');
        
        // Gọi AJAX
        const xhr = new XMLHttpRequest();
        const url = '${pageContext.request.contextPath}/admin/news?' + params.toString();
        
        // Lưu thời gian bắt đầu để đảm bảo loading hiển thị ít nhất 200ms (giảm từ 500ms để nhanh hơn)
        const startTime = Date.now();
        const minDisplayTime = 200; // Tối thiểu 200ms (giảm để tăng tốc độ)
        
        xhr.open('GET', url);
        xhr.onload = function() {
            const elapsed = Date.now() - startTime;
            const remainingTime = Math.max(0, minDisplayTime - elapsed);
            
            setTimeout(function() {
                isSearching = false;
                if (xhr.status === 200) {
                    // Fade out loading trước
                    const loadingDiv = document.getElementById('loadingDiv');
                    if (loadingDiv) {
                        loadingDiv.style.opacity = '0';
                        loadingDiv.style.transition = 'opacity 0.2s ease';
                    }
                    
                    // Cập nhật nội dung danh sách sau khi fade out
                    setTimeout(function() {
                        newsListContainer.innerHTML = xhr.responseText;
                        newsListContainer.style.opacity = '0';
                        newsListContainer.style.transition = 'opacity 0.3s ease';
                        
                        // Fade in nội dung mới
                        setTimeout(function() {
                            newsListContainer.style.opacity = '1';
                        }, 50);
                    }, 200);
                } else {
                    newsListContainer.innerHTML = '<div style="text-align: center; padding: 40px; color: #dc3545;"><i class="fas fa-exclamation-triangle"></i><p style="margin-top: 10px;">Có lỗi xảy ra khi tìm kiếm</p></div>';
                    newsListContainer.style.opacity = '1';
                }
            }, remainingTime);
        };
        xhr.onerror = function() {
            isSearching = false;
            newsListContainer.innerHTML = '<div style="text-align: center; padding: 40px; color: #dc3545;"><i class="fas fa-exclamation-triangle"></i><p style="margin-top: 10px;">Lỗi kết nối</p></div>';
            newsListContainer.style.opacity = '1';
        };
        xhr.send();
    }
    
    // Ngăn form submit mặc định
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        performSearch();
    });
    
    // Tự động lọc khi thay đổi filter (với delay nhỏ)
    [filterCategory, sortBy, sortOrder].forEach(function(element) {
        if (element) {
            element.addEventListener('change', function() {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(function() {
                    performSearch();
                }, 300);
            });
        }
    });
    
    // Tìm kiếm theo tiêu đề chỉ khi ấn nút hoặc Enter (không tự động khi gõ)
    if (searchTitle) {
        // Enter để tìm kiếm ngay lập tức
        searchTitle.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                performSearch();
            }
        });
    }
})();

// Khởi tạo CKEditor 5 cho ô Nội dung
(function() {
    function initCKEditor() {
        var editorElem = document.querySelector('#newsContentEditor');
        if (!editorElem || typeof ClassicEditor === 'undefined') {
            return;
        }
        
        // Tránh khởi tạo nhiều lần nếu đã có CKEditor
        if (editorElem.classList.contains('ck-editor-initialized')) {
            return;
        }
        editorElem.classList.add('ck-editor-initialized');

        ClassicEditor
            .create(editorElem, {
                toolbar: [
                    'heading', '|', 
                    'bold', 'italic', 'underline', '|',
                    'bulletedList', 'numberedList', '|',
                    'blockQuote', 'insertTable', 'link', '|',
                    'undo', 'redo'
                ],
                placeholder: 'Nhập nội dung bài viết chi tiết tại đây...'
            })
            .then(function(editor) {
                var form = editorElem.closest('form');
                if (form) {
                    form.addEventListener('submit', function() {
                        editorElem.value = editor.getData();
                    });
                }
            })
            .catch(function(error) {
                console.error('Lỗi khi tải CKEditor 5:', error);
            });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initCKEditor);
    } else {
        initCKEditor();
    }
})();
</script>

<style>
.ck-editor__editable_inline {
    min-height: 240px;
    max-height: 480px;
    font-size: 0.95rem;
    line-height: 1.6;
    border-radius: 0 0 8px 8px !important;
}
.ck-toolbar {
    border-radius: 8px 8px 0 0 !important;
}
.ck.ck-editor {
    width: 100% !important;
}
</style>