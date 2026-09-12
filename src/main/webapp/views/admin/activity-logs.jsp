<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="activity-logs-container">
    <!-- Page Title -->
    <div class="page-title-box mb-4">
        <h1 class="page-title">
            <i class="fas fa-history me-2"></i>Lịch Sử Hoạt Động
        </h1>
    </div>
    
    <!-- Filter Section -->
    <div class="card shadow-sm mb-4">
        <div class="card-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
            <h5 class="mb-0">
                <i class="fas fa-filter me-2"></i>Bộ Lọc Tìm Kiếm
            </h5>
        </div>
        <div class="card-body" style="background-color: #f8f9fa;">
            <div id="filterForm">
                <div class="row g-3">
                    <!-- User Filter -->
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-user me-1"></i>Người dùng
                        </label>
                        <select name="userId" class="form-select">
                            <option value="">-- Tất cả --</option>
                            <c:forEach var="user" items="${allUsers}">
                                <option value="${user.id}" ${userIdFilter == user.id ? 'selected' : ''}>
                                    ${user.fullname} (${user.id})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <!-- Action Filter -->
                    <div class="col-md-2">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-bolt me-1"></i>Hành động
                        </label>
                        <select name="action" class="form-select">
                            <option value="">-- Tất cả --</option>
                            <option value="CREATE" ${actionFilter == 'CREATE' ? 'selected' : ''}>Tạo mới</option>
                            <option value="UPDATE" ${actionFilter == 'UPDATE' ? 'selected' : ''}>Cập nhật</option>
                            <option value="DELETE" ${actionFilter == 'DELETE' ? 'selected' : ''}>Xóa</option>
                            <option value="LOGIN" ${actionFilter == 'LOGIN' ? 'selected' : ''}>Đăng nhập</option>
                            <option value="LOGOUT" ${actionFilter == 'LOGOUT' ? 'selected' : ''}>Đăng xuất</option>
                            <option value="EXPORT" ${actionFilter == 'EXPORT' ? 'selected' : ''}>Xuất dữ liệu</option>
                        </select>
                    </div>
                    
                    <!-- Entity Filter -->
                    <div class="col-md-2">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-cube me-1"></i>Đối tượng
                        </label>
                        <select name="entity" class="form-select">
                            <option value="">-- Tất cả --</option>
                            <option value="NEWS" ${entityFilter == 'NEWS' ? 'selected' : ''}>Tin tức</option>
                            <option value="USER" ${entityFilter == 'USER' ? 'selected' : ''}>Người dùng</option>
                            <option value="CATEGORY" ${entityFilter == 'CATEGORY' ? 'selected' : ''}>Danh mục</option>
                            <option value="NEWSLETTER" ${entityFilter == 'NEWSLETTER' ? 'selected' : ''}>Newsletter</option>
                        </select>
                    </div>
                    
                    <!-- Date From -->
                    <div class="col-md-2">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-calendar me-1"></i>Từ ngày
                        </label>
                        <input type="date" name="fromDate" class="form-control" value="${fromDateStr}">
                    </div>
                    
                    <!-- Date To -->
                    <div class="col-md-2">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-calendar-check me-1"></i>Đến ngày
                        </label>
                        <input type="date" name="toDate" class="form-control" value="${toDateStr}">
                    </div>
                    
                </div>
                
                <!-- Action Buttons -->
                <div class="row mt-3">
                    <div class="col-12">
                        <a href="${pageContext.request.contextPath}/admin/activity-logs" class="btn btn-secondary">
                            <i class="fas fa-sync-alt me-1"></i>Làm mới
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Logs Table -->
    <div class="card shadow-sm">
        <div class="card-header d-flex justify-content-between align-items-center" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
            <h5 class="mb-0">
                <i class="fas fa-list me-2"></i>Danh Sách Hoạt Động
            </h5>
            <span class="badge bg-light text-dark fs-6" id="totalRecordsBadge">
                <i class="fas fa-list-check me-1"></i>${totalRecords} hoạt động
            </span>
        </div>
        <div class="card-body p-0" style="background-color: #f8f9fa;">
            <!-- Loading indicator -->
            <div id="tableLoading" style="display: none; text-align: center; padding: 40px;">
                <i class="fas fa-spinner fa-spin fa-3x text-primary"></i>
                <p class="mt-3">Đang tải dữ liệu...</p>
            </div>
            
            <!-- Table container -->
            <div id="tableContainer">
                <div class="table-responsive">
                <table class="table table-hover table-striped mb-0">
                    <thead style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                        <tr>
                            <th style="width: 50px;">#</th>
                            <th style="width: 150px;">Người dùng</th>
                            <th style="width: 120px;">Hành động</th>
                            <th style="width: 100px;">Đối tượng</th>
                            <th>Mô tả</th>
                            <th style="width: 180px;">Thời gian</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty logs}">
                                <tr>
                                    <td colspan="6" class="text-center py-4 text-muted">
                                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                                        <p class="mb-0">Không có dữ liệu</p>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="log" items="${logs}" varStatus="status">
                                    <tr class="log-row" data-log-id="${log.id}">
                                        <td class="text-center fw-bold">${(currentPage - 1) * pageSize + status.index + 1}</td>
                                        <td>
                                            <div>
                                                <div class="fw-semibold">${log.username}</div>
                                                <small class="text-muted">${log.userId}</small>
                                            </div>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.actionType == 'CREATE'}">
                                                    <span class="badge bg-success">
                                                        <i class="fas fa-plus-circle me-1"></i>Tạo mới
                                                    </span>
                                                </c:when>
                                                <c:when test="${log.actionType == 'UPDATE'}">
                                                    <span class="badge bg-warning">
                                                        <i class="fas fa-edit me-1"></i>Cập nhật
                                                    </span>
                                                </c:when>
                                                <c:when test="${log.actionType == 'DELETE'}">
                                                    <span class="badge bg-danger">
                                                        <i class="fas fa-trash me-1"></i>Xóa
                                                    </span>
                                                </c:when>
                                                <c:when test="${log.actionType == 'LOGIN'}">
                                                    <span class="badge bg-info">
                                                        <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
                                                    </span>
                                                </c:when>
                                                <c:when test="${log.actionType == 'LOGOUT'}">
                                                    <span class="badge bg-secondary">
                                                        <i class="fas fa-sign-out-alt me-1"></i>Đăng xuất
                                                    </span>
                                                </c:when>
                                                <c:when test="${log.actionType == 'EXPORT'}">
                                                    <span class="badge bg-primary">
                                                        <i class="fas fa-download me-1"></i>Xuất dữ liệu
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-dark">${log.actionType}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:if test="${not empty log.entityType}">
                                                <span class="badge bg-light text-dark border">
                                                    <c:choose>
                                                        <c:when test="${log.entityType == 'NEWS'}">
                                                            <i class="fas fa-newspaper me-1"></i>Tin tức
                                                        </c:when>
                                                        <c:when test="${log.entityType == 'USER'}">
                                                            <i class="fas fa-user me-1"></i>User
                                                        </c:when>
                                                        <c:when test="${log.entityType == 'CATEGORY'}">
                                                            <i class="fas fa-tags me-1"></i>Danh mục
                                                        </c:when>
                                                        <c:when test="${log.entityType == 'NEWSLETTER'}">
                                                            <i class="fas fa-envelope me-1"></i>Newsletter
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${log.entityType}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </c:if>
                                        </td>
                                        <td>
                                <div class="text-truncate" style="max-width: 400px;" title="${log.description}">
                                    ${log.description}
                                </div>
                            </td>
                            <td>
                                            <div>
                                                <fmt:formatDate value="${log.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </div>
                                            <small class="text-muted">${log.timeAgo}</small>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
        
        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <div class="card-footer">
                <nav>
                    <ul class="pagination pagination-sm mb-0 justify-content-center">
                        <!-- Previous -->
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage - 1}&userId=${userIdFilter}&action=${actionFilter}&entity=${entityFilter}&fromDate=${fromDateStr}&toDate=${toDateStr}">
                                <i class="fas fa-chevron-left"></i>
                            </a>
                        </li>
                        
                        <!-- Pages -->
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}&userId=${userIdFilter}&action=${actionFilter}&entity=${entityFilter}&fromDate=${fromDateStr}&toDate=${toDateStr}">
                                        ${i}
                                    </a>
                                </li>
                            </c:if>
                        </c:forEach>
                        
                        <!-- Next -->
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage + 1}&userId=${userIdFilter}&action=${actionFilter}&entity=${entityFilter}&fromDate=${fromDateStr}&toDate=${toDateStr}">
                                <i class="fas fa-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
                
                <div class="text-center text-muted mt-2">
                    <small>
                        Trang ${currentPage} / ${totalPages} 
                        (Hiển thị ${(currentPage - 1) * pageSize + 1} - ${currentPage * pageSize > totalRecords ? totalRecords : currentPage * pageSize} / ${totalRecords})
                    </small>
                </div>
            </div>
        </c:if>
    </div>
</div>

<style>
.activity-logs-container {
    padding: 20px;
}

.page-title-box {
    margin-bottom: 30px;
}

.page-title {
    color: #2c3e50;
    font-size: 32px;
    font-weight: 700;
    margin-bottom: 8px;
}

.page-subtitle {
    color: #6c757d;
    font-size: 16px;
    margin: 0;
}

.log-row {
    transition: all 0.3s ease;
}

.log-row:hover {
    background-color: #f8f9fa;
    transform: scale(1.01);
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.user-avatar {
    font-weight: bold;
}

.badge {
    font-size: 0.85rem;
    padding: 0.4em 0.8em;
}

.table-hover tbody tr:hover {
    background-color: #f1f3f5;
}

.pagination .page-item.active .page-link {
    background-color: #667eea;
    border-color: #667eea;
}

.pagination .page-link {
    color: #667eea;
}

.pagination .page-link:hover {
    color: #764ba2;
    background-color: #f8f9fa;
}

.card {
    border: none;
}

.card-header h5 {
    font-weight: 600;
}

.btn-light {
    background-color: white;
    border: 1px solid #dee2e6;
}

.btn-light:hover {
    background-color: #e9ecef;
}

.table {
    background-color: white;
}

.table thead th {
    border: none;
    font-weight: 600;
}

.btn-secondary {
    background-color: #6c757d;
    border-color: #6c757d;
    color: white;
}

.btn-secondary:hover {
    background-color: #5a6268;
    border-color: #545b62;
}
</style>

<script>
// AJAX search giống các trang CRUD khác
document.addEventListener('DOMContentLoaded', function() {
    const userId = document.querySelector('select[name="userId"]');
    const action = document.querySelector('select[name="action"]');
    const entity = document.querySelector('select[name="entity"]');
    const fromDate = document.querySelector('input[name="fromDate"]');
    const toDate = document.querySelector('input[name="toDate"]');
    const tableContainer = document.getElementById('tableContainer');
    const tableLoading = document.getElementById('tableLoading');
    const totalBadge = document.getElementById('totalRecordsBadge');
    
    // Function để load logs qua AJAX
    function loadLogs(page = 1) {
        // Build URL with params
        const params = new URLSearchParams();
        if (userId && userId.value) params.append('userId', userId.value);
        if (action && action.value) params.append('action', action.value);
        if (entity && entity.value) params.append('entity', entity.value);
        if (fromDate && fromDate.value) params.append('fromDate', fromDate.value);
        if (toDate && toDate.value) params.append('toDate', toDate.value);
        params.append('page', page);
        params.append('ajax', 'true');
        
        const url = '${pageContext.request.contextPath}/admin/activity-logs?' + params.toString();
        
        // Show loading
        if (tableContainer) tableContainer.style.display = 'none';
        if (tableLoading) tableLoading.style.display = 'block';
        
        // AJAX call
        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Network error');
                return response.text();
            })
            .then(html => {
                // Delay 300ms để smooth
                setTimeout(() => {
                    if (tableLoading) tableLoading.style.display = 'none';
                    if (tableContainer) {
                        tableContainer.style.display = 'block';
                        tableContainer.innerHTML = html;
                    }
                    
                    // Attach click events to pagination links
                    attachPaginationEvents();
                    
                    // Update badge
                    updateTotalBadge();
                }, 300);
            })
            .catch(error => {
                console.error('Error:', error);
                if (tableLoading) tableLoading.style.display = 'none';
                if (tableContainer) {
                    tableContainer.style.display = 'block';
                    tableContainer.innerHTML = '<div class="alert alert-danger m-3">Lỗi khi tải dữ liệu</div>';
                }
            });
    }
    
    // Function để attach events cho pagination
    function attachPaginationEvents() {
        const paginationLinks = document.querySelectorAll('#tableContainer .pagination a.page-link');
        paginationLinks.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const page = this.getAttribute('data-page');
                if (page && page > 0) {
                    loadLogs(parseInt(page));
                }
            });
        });
    }
    
    // Function để update total badge
    function updateTotalBadge() {
        const paginationText = document.querySelector('#tableContainer .text-muted small');
        if (paginationText && totalBadge) {
            const match = paginationText.textContent.match(/(\d+)\s*\)/);
            if (match) {
                totalBadge.innerHTML = '<i class="fas fa-list-check me-1"></i>' + match[1] + ' hoạt động';
            }
        }
    }
    
    // Auto-search khi thay đổi filter
    if (userId) userId.addEventListener('change', () => loadLogs(1));
    if (action) action.addEventListener('change', () => loadLogs(1));
    if (entity) entity.addEventListener('change', () => loadLogs(1));
    if (fromDate) fromDate.addEventListener('change', () => loadLogs(1));
    if (toDate) toDate.addEventListener('change', () => loadLogs(1));
    
    // Initial pagination events
    attachPaginationEvents();
});
</script>

