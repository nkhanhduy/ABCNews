<%-- 
  File: category_crud.jsp
  Description: Giao diện CRUD Loại tin
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!-- CSS đã có trong admin_style.css -->

<h2>Quản lý Loại tin</h2>
<hr>

<!-- Hiển thị thông báo -->
<div class="message-container">
    <c:if test="${not empty message}">
        <div class="message-success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="message-error">${error}</div>
    </c:if>
</div>

<div class="crud-container category-crud-wrapper">

    <!-- Card 1: Quản lý Loại tin -->
    <div class="category-form-card card">
        <div class="card-header">
            <h3><i class="fas fa-tags me-2"></i>${not empty categoryItem ? 'Cập nhật Loại tin' : 'Thêm Loại tin mới'}</h3>
        </div>
        <div class="card-body">
        <c:set var="isEdit" value="${not empty categoryItem}" />
        
        <form action="${pageContext.request.contextPath}/admin/categories" method="post">
            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
            <c:choose>
                <c:when test="${isEdit}">
                    <input type="hidden" name="action" value="update" />
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="action" value="create" />
                </c:otherwise>
            </c:choose>

            <div class="form-group">
                <label for="id">Mã loại tin</label>
                <input type="text" name="id" value="${categoryItem.id}" ${isEdit ? 'readonly' : 'required'}>
            </div>

            <div class="form-group">
                <label for="name">Tên loại tin</label>
                <input type="text" name="name" value="${categoryItem.name}" required>
            </div>

            <c:if test="${isEdit}">
                <div class="form-group">
                    <label for="slug">Đường dẫn thân thiện (Slug)</label>
                    <input type="text" id="slug" name="slug" value="${categoryItem.slug}" readonly style="background-color: #f1f3f5; color: #495057; cursor: not-allowed;">
                    <small class="text-muted" style="font-size: 0.85rem; display: block; margin-top: 4px;">
                        <i class="fas fa-info-circle me-1"></i>Tự động đồng bộ theo tên loại tin
                    </small>
                </div>
            </c:if>
            
            <div class="button-group">
                <button type="submit" class="btn ${isEdit ? 'btn-update' : 'btn-create'}">
                    ${isEdit ? 'Cập nhật' : 'Thêm mới'}
                </button>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-reset">Làm mới</a>
            </div>
        </form>
        </div>
    </div>

    <!-- Card 2: Danh sách Loại tin -->
    <div class="category-list-card card">
        <div class="card-header">
            <h3><i class="fas fa-list me-2"></i>Danh sách Loại tin</h3>
            <div class="export-buttons">
                <a href="${pageContext.request.contextPath}/admin/export?type=categories&format=csv" class="btn btn-export btn-csv" title="Export CSV">
                    <i class="fas fa-file-csv"></i> CSV
                </a>
                <a href="${pageContext.request.contextPath}/admin/export?type=categories&format=excel" class="btn btn-export btn-excel" title="Export Excel">
                    <i class="fas fa-file-excel"></i> Excel
                </a>
                <a href="${pageContext.request.contextPath}/admin/export?type=categories&format=pdf" class="btn btn-export btn-pdf" title="Export PDF">
                    <i class="fas fa-file-pdf"></i> PDF
                </a>
            </div>
        </div>
        <div class="card-body">
        <div class="table-responsive">
            <table class="crud-table">
                <thead>
                    <tr>
                        <th>Mã loại</th>
                        <th>Tên loại tin</th>
                        <th>Đường dẫn (Slug)</th>
                        <th>Số lượng tin</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cat" items="${categoriesList}">
                            <c:set var="newsCount" value="${newsCountMap[cat.id]}" />
                            <c:if test="${newsCount == null}">
                                <c:set var="newsCount" value="0" />
                            </c:if>
                        <tr>
                            <td>${cat.id}</td>
                            <td><strong>${cat.name}</strong></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/category/${cat.slug}" target="_blank" class="badge bg-light text-dark border text-decoration-none" title="Xem trước chuyên mục public">
                                    <code>${cat.slug}</code> <i class="fas fa-external-link-alt ms-1" style="font-size: 0.7rem;"></i>
                                </a>
                            </td>
                            <td>
                                <span class="badge bg-primary">
                                    ${newsCount}
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/categories?action=edit&id=${cat.id}" 
                                   class="btn btn-sm btn-update">Sửa</a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/categories" style="display:inline; margin:0 3px;" onsubmit="return confirm('Xóa loại tin này?')">
                                    <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${cat.id}">
                                    <button type="submit" class="btn btn-sm btn-delete" style="border:none; cursor:pointer;">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                        <c:if test="${empty categoriesList}">
                            <tr>
                                <td colspan="4">Không có loại tin nào.</td>
                            </tr>
                        </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
</div>

<style>
/* Container cho Category Management */
.category-crud-wrapper {
    display: flex;
    flex-direction: column;
    gap: 30px;
}

/* Card styling cho Category Management */
.category-form-card,
.category-list-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 0;
    overflow: visible;
    width: 100%;
    box-sizing: border-box;
}

.category-form-card .card-header,
.category-list-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.category-form-card .card-header h3,
.category-list-card .card-header h3 {
    margin: 0;
    font-size: 1.3rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.category-form-card .card-body,
.category-list-card .card-body {
    padding: 25px;
}

.category-form-card .card-body .form-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 20px;
}

.category-form-card .card-body .form-group label {
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
}

.category-form-card .card-body .form-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 14px;
    transition: border-color 0.3s ease;
}

.category-form-card .card-body .form-group input:focus {
    outline: none;
    border-color: #f5576c;
    box-shadow: 0 0 0 3px rgba(245, 87, 108, 0.1);
}

.category-form-card .card-body .button-group {
    margin-top: 10px;
}

/* Badge styling cho số lượng tin */
.category-list-card .badge {
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 0.875rem;
    font-weight: 500;
    display: inline-block;
}

.category-list-card .badge.bg-primary {
    background-color: #007bff !important;
    color: #fff;
}

/* Căn giữa cột số lượng tin */
.category-list-card table th:nth-child(3),
.category-list-card table td:nth-child(3) {
    text-align: center;
}
</style>