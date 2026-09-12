<%-- 
  File: user_crud.jsp
  Description: Giao diện CRUD User
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!-- CSS đã có trong admin_style.css -->

<div class="message-container">
	<c:if test="${not empty message}">
		<div class="message-success">${message}</div>
	</c:if>
	<c:if test="${not empty error}">
		<div class="message-error">${error}</div>
	</c:if>
</div>

<h2>Quản lý Người dùng</h2>
<hr>

<div class="user-management-container">
	<!-- Card 1: Form thêm/sửa User -->
	<div class="user-form-card">
		<div class="card-header">
			<h3><i class="fas fa-user-plus me-2"></i>${isEdit ? 'Cập nhật User' : 'Thêm User mới'}</h3>
		</div>
		<div class="card-body">
		<c:set var="isEdit" value="${not empty userItem}" />

		<form action="${pageContext.request.contextPath}/admin/users"
			method="post" enctype="multipart/form-data">
			<c:choose>
				<c:when test="${isEdit}">
					<input type="hidden" name="action" value="update" />
				</c:when>
				<c:otherwise>
					<input type="hidden" name="action" value="create" />
				</c:otherwise>
			</c:choose>
			<!-- Hidden input để truyền trạng thái edit sang JavaScript -->
			<input type="hidden" id="isEditMode" value="${isEdit ? 'true' : 'false'}" />

			<div class="form-row">
				<!-- Cột trái -->
				<div class="form-col-left">
					<div class="form-group">
						<label for="userId">
							<i class="fas fa-user-tag me-1"></i>Username (ID)
						</label>
						<input type="text" name="id" id="userId" 
						       value="${userItem.id}" readonly required
						       class="form-input-readonly">
						<small class="form-hint">
							<i class="fas fa-info-circle"></i> Username sẽ tự động tạo khi bạn chọn vai trò
						</small>
					</div>

					<div class="form-group">
						<label for="password">
							<i class="fas fa-lock me-1"></i>Mật khẩu
						</label>
						<input type="password" name="password" id="password"
						       value="${userItem.password}" required
						       placeholder="Nhập mật khẩu">
					</div>

					<div class="form-group">
						<label for="fullname">
							<i class="fas fa-user me-1"></i>Họ và tên
						</label>
						<input type="text" name="fullname" id="fullname"
						       value="${userItem.fullname}" required
						       placeholder="Nhập họ và tên">
					</div>

					<div class="form-group">
						<label for="email">
							<i class="fas fa-envelope me-1"></i>Email
						</label>
						<input type="email" name="email" id="email"
						       value="${userItem.email}" required
						       placeholder="example@email.com">
					</div>
				</div>

				<!-- Cột phải -->
				<div class="form-col-right">
					<div class="form-group">
						<label for="userRole">
							<i class="fas fa-user-shield me-1"></i>Vai trò
						</label>
						<select name="role" id="userRole" ${isEdit ? 'disabled' : ''} class="form-select">
							<option value="false" ${!userItem.role ? 'selected' : ''}>Phóng viên</option>
							<option value="true" ${userItem.role ? 'selected' : ''}>Quản trị viên</option>
							<c:set var="currentUser" value="${sessionScope.user}" />
							<c:set var="currentUserIdLower" value="${fn:toLowerCase(currentUser.id)}" />
							<c:set var="isCurrentSuperAdmin" value="${fn:startsWith(currentUserIdLower, 'super') || currentUserIdLower == 'superadmin'}" />
							<c:if test="${isCurrentSuperAdmin}">
								<option value="super" ${fn:startsWith(fn:toLowerCase(userItem.id), 'super') ? 'selected' : ''}>Super Admin</option>
							</c:if>
						</select>
						<c:if test="${isEdit}">
							<input type="hidden" name="role" value="${userItem.role}">
						</c:if>
						<c:if test="${isCurrentSuperAdmin}">
							<small class="form-hint">
								<i class="fas fa-info-circle"></i>
								Super Admin có quyền cao nhất, có thể xóa admin khác
							</small>
						</c:if>
					</div>

					<div class="form-group">
						<label for="birthday">
							<i class="fas fa-calendar me-1"></i>Ngày sinh
						</label>
						<fmt:formatDate value="${userItem.birthday}" pattern="yyyy-MM-dd" var="formattedDob" />
						<input type="date" name="birthday" id="birthday" value="${formattedDob}">
					</div>

					<div class="form-group">
						<label for="gender">
							<i class="fas fa-venus-mars me-1"></i>Giới tính
						</label>
						<select name="gender" id="gender" class="form-select">
							<option value="true" ${userItem.gender ? 'selected' : ''}>Nam</option>
							<option value="false" ${!userItem.gender ? 'selected' : ''}>Nữ</option>
						</select>
					</div>

					<div class="form-group">
						<label for="imageFile">
							<i class="fas fa-image me-1"></i>Ảnh đại diện
						</label>
						<input type="file" name="imageFile" id="imageFile" accept="image/*" class="form-file">
						<input type="hidden" name="imagePath" value="${userItem.imagePath}">
						
						<c:if test="${not empty userItem.imagePath}">
							<div class="current-image-preview">
								<c:set var="userImageUrl" value="${userItem.imagePath}" />
								<c:if test="${!fn:startsWith(userImageUrl, pageContext.request.contextPath) && fn:startsWith(userImageUrl, '/')}">
									<c:set var="userImageUrl" value="${pageContext.request.contextPath}${userItem.imagePath}" />
								</c:if>
								<img src="${userImageUrl}" alt="Ảnh hiện tại" 
								     onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
								<small class="image-error" style="display:none; color:red;"><i class="fas fa-exclamation-triangle me-1"></i> Không thể tải ảnh. Vui lòng chọn ảnh mới.</small>
								<small class="image-label">Ảnh hiện tại</small>
							</div>
						</c:if>
					</div>
				</div>
			</div>

			<div class="button-group" style="grid-column: 1 / -1; margin-top: 20px;">
				<button type="submit"
					class="btn ${isEdit ? 'btn-update' : 'btn-create'}">
					<i class="fas fa-${isEdit ? 'save' : 'plus'} me-1"></i>${isEdit ? 'Cập nhật' : 'Thêm mới'}</button>
				<a href="${pageContext.request.contextPath}/admin/users"
					class="btn btn-reset">
					<i class="fas fa-redo me-1"></i>Làm mới</a>
			</div>
		</form>
		</div>
	</div>

	<!-- Card 2: Tìm kiếm và Bộ lọc -->
	<div class="user-search-card card">
		<div class="card-header">
			<h3><i class="fas fa-search me-2"></i>Tìm kiếm và Bộ lọc</h3>
		</div>
		<div class="card-body">
			<form method="get" action="${pageContext.request.contextPath}/admin/users" id="searchForm">
				<div class="search-filter-row">
					<!-- Tìm kiếm theo từ khóa -->
					<div class="search-group">
						<label for="searchKeyword"><i class="fas fa-search me-1"></i>Tìm kiếm</label>
						<input type="text" 
						       name="searchKeyword" 
						       id="searchKeyword" 
						       value="${searchKeyword}" 
						       placeholder="Nhập ID, tên hoặc email...">
					</div>
					
					<!-- Lọc theo vai trò -->
					<div class="filter-group">
						<label for="filterRole"><i class="fas fa-user-shield me-1"></i>Vai trò</label>
						<select name="filterRole" id="filterRole">
							<option value="">-- Tất cả vai trò --</option>
							<option value="super" ${filterRole == 'super' ? 'selected' : ''}>Super Admin</option>
							<option value="true" ${filterRole == 'true' ? 'selected' : ''}>Quản trị viên</option>
							<option value="false" ${filterRole == 'false' ? 'selected' : ''}>Phóng viên</option>
						</select>
					</div>
					
					<!-- Lọc theo trạng thái -->
					<div class="filter-group">
						<label for="filterEnabled"><i class="fas fa-toggle-on me-1"></i>Trạng thái</label>
						<select name="filterEnabled" id="filterEnabled">
							<option value="">-- Tất cả trạng thái --</option>
							<option value="true" ${filterEnabled == 'true' ? 'selected' : ''}>Hoạt động</option>
							<option value="false" ${filterEnabled == 'false' ? 'selected' : ''}>Bị khóa</option>
						</select>
					</div>
					
					<!-- Sắp xếp theo -->
					<div class="filter-group">
						<label for="sortBy"><i class="fas fa-sort me-1"></i>Sắp xếp theo</label>
						<select name="sortBy" id="sortBy">
							<option value="">-- Mặc định --</option>
							<option value="id" ${sortBy == 'id' ? 'selected' : ''}>ID</option>
							<option value="fullname" ${sortBy == 'fullname' ? 'selected' : ''}>Họ tên</option>
							<option value="email" ${sortBy == 'email' ? 'selected' : ''}>Email</option>
						</select>
					</div>
					
					<!-- Thứ tự sắp xếp -->
					<div class="filter-group">
						<label for="sortOrder"><i class="fas fa-sort-amount-down me-1"></i>Thứ tự</label>
						<select name="sortOrder" id="sortOrder">
							<option value="ASC" ${sortOrder == 'ASC' || empty sortOrder ? 'selected' : ''}>Tăng dần</option>
							<option value="DESC" ${sortOrder == 'DESC' ? 'selected' : ''}>Giảm dần</option>
						</select>
					</div>
				</div>
				
				<!-- Nút reset -->
				<div class="search-actions" style="align-self: flex-end;">
					<a href="${pageContext.request.contextPath}/admin/users" class="btn btn-reset">
						<i class="fas fa-redo me-1"></i>Làm mới
					</a>
				</div>
			</form>
		</div>
	</div>

	<!-- Card 3: Danh sách Người dùng -->
	<div class="user-list-card">
		<div class="card-header">
			<h3><i class="fas fa-users me-2"></i>Danh sách Người dùng</h3>
			<div class="export-buttons">
				<a href="${pageContext.request.contextPath}/admin/export?type=users&format=csv" class="btn btn-export btn-csv" title="Export CSV">
					<i class="fas fa-file-csv"></i> CSV
				</a>
				<a href="${pageContext.request.contextPath}/admin/export?type=users&format=excel" class="btn btn-export btn-excel" title="Export Excel">
					<i class="fas fa-file-excel"></i> Excel
				</a>
				<a href="${pageContext.request.contextPath}/admin/export?type=users&format=pdf" class="btn btn-export btn-pdf" title="Export PDF">
					<i class="fas fa-file-pdf"></i> PDF
				</a>
			</div>
		</div>
		<div class="card-body" id="userListContainer">
		<div class="table-responsive">
			<table class="crud-table user-table">
				<thead>
					<tr>
						<th>ID</th>
						<th>Họ tên</th>
						<th>Email</th>
						<th>Vai trò</th>
						<th>Số tin</th>
						<th>Trạng thái</th>
						<th>Hành động</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="u" items="${userList}">
						<c:set var="currentUser" value="${sessionScope.user}" />
						<c:set var="isCurrentUser" value="${u.id == currentUser.id}" />
						<c:set var="currentUserIdLower" value="${fn:toLowerCase(currentUser.id)}" />
						<c:set var="isSuperAdmin" value="${fn:startsWith(currentUserIdLower, 'super') || currentUserIdLower == 'superadmin'}" />
						<c:set var="userIdLower" value="${fn:toLowerCase(u.id)}" />
						<c:set var="isUserSuperAdmin" value="${fn:startsWith(userIdLower, 'super') || userIdLower == 'superadmin'}" />
						<c:set var="newsCount" value="${newsCountMap[u.id] != null ? newsCountMap[u.id] : 0}" />
						
						<tr data-user-id="${u.id}" class="user-row" style="cursor: pointer;">
							<td style="text-align: center;">${u.id}</td>
							<td style="text-align: center;">${u.fullname}</td>
							<td style="text-align: center;">${u.email}</td>
							<td style="text-align: center;">
								<span style="color: ${isUserSuperAdmin ? 'purple' : (u.role ? 'red' : 'blue')}; font-weight: bold;">
									${isUserSuperAdmin ? 'Super Admin' : (u.role ? 'Admin' : 'Phóng viên')}
								</span>
							</td>
							<td style="text-align: center;">
								<a href="${pageContext.request.contextPath}/admin/news?filterAuthor=${u.id}" 
								   onclick="event.stopPropagation();" 
								   style="color: #007bff; text-decoration: none;">
									${newsCount}
								</a>
							</td>
							<td style="text-align: center;">
								<c:choose>
									<c:when test="${u.enabled}">
										<span class="badge badge-success" style="background-color: #28a745; color: white; padding: 4px 8px; border-radius: 4px; font-size: 0.875rem; display: inline-block; min-width: 100px; text-align: center;">
											<i class="fas fa-check-circle"></i> Hoạt động
										</span>
									</c:when>
									<c:otherwise>
										<span class="badge badge-danger" style="background-color: #dc3545; color: white; padding: 4px 8px; border-radius: 4px; font-size: 0.875rem; display: inline-block; min-width: 100px; text-align: center;">
											<i class="fas fa-ban"></i> Bị khóa
										</span>
									</c:otherwise>
								</c:choose>
							</td>
							<td onclick="event.stopPropagation();" style="text-align: center;">
								<!-- Nút Sửa -->
								<c:set var="canEdit" value="${isCurrentUser || isSuperAdmin || !isUserSuperAdmin}" />
								<c:choose>
									<c:when test="${canEdit}">
										<a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${u.id}" 
										   class="btn btn-sm btn-update" style="min-width: 70px; margin: 0 3px;">Sửa</a>
									</c:when>
									<c:otherwise>
										<span class="btn btn-sm btn-update" style="background-color: #6c757d !important; border-color: #6c757d !important; color: #fff !important; opacity: 0.6 !important; cursor: not-allowed !important; pointer-events: none !important; min-width: 70px; margin: 0 3px; display: inline-block;" 
										      title="Bạn không có quyền sửa Super Admin">Sửa</span>
									</c:otherwise>
								</c:choose>
								
								<!-- Nút Xóa -->
								<c:set var="canDelete" value="${!isCurrentUser && (isSuperAdmin || !u.role)}" />
								<c:choose>
									<c:when test="${canDelete}">
										<a href="${pageContext.request.contextPath}/admin/users?action=delete&id=${u.id}"
										   class="btn btn-sm btn-delete" 
										   onclick="return confirm('Xóa user này?')"
										   style="min-width: 70px; margin: 0 3px;">Xóa</a>
									</c:when>
									<c:otherwise>
										<span class="btn btn-sm btn-delete" style="background-color: #6c757d !important; border-color: #6c757d !important; color: #fff !important; opacity: 0.6 !important; cursor: not-allowed !important; pointer-events: none !important; min-width: 70px; margin: 0 3px; display: inline-block;" 
										      title="${isCurrentUser ? 'Bạn không thể xóa chính mình' : (u.role && !isSuperAdmin ? 'Bạn không có quyền xóa Admin' : 'Bạn không có quyền xóa Super Admin')}">Xóa</span>
									</c:otherwise>
								</c:choose>
								
								<!-- Nút Khóa/Mở khóa -->
								<c:set var="canToggle" value="${!isCurrentUser && (isSuperAdmin || !u.role)}" />
								<c:choose>
									<c:when test="${canToggle}">
										<a href="${pageContext.request.contextPath}/admin/users?action=toggle&id=${u.id}"
										   class="btn btn-sm btn-update"
										   style="min-width: 70px; margin: 0 3px;">
										   ${u.enabled ? 'Khóa' : 'Mở khóa'}
										</a>
									</c:when>
									<c:otherwise>
										<span class="btn btn-sm btn-update" style="background-color: #6c757d !important; border-color: #6c757d !important; color: #fff !important; opacity: 0.6 !important; cursor: not-allowed !important; pointer-events: none !important; min-width: 70px; margin: 0 3px; display: inline-block;" 
										      title="${isCurrentUser ? 'Bạn không thể khóa/mở khóa chính mình' : (u.role && !isSuperAdmin ? 'Bạn không có quyền khóa/mở khóa Admin' : 'Bạn không có quyền khóa/mở khóa Super Admin')}">
										   ${u.enabled ? 'Khóa' : 'Mở khóa'}
										</span>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
</div>

<style>
/* Layout mới: 2 card chồng lên nhau */
.user-management-container {
    display: flex;
    flex-direction: column;
    gap: 30px;
}

.user-form-card,
.user-list-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    border: 1px solid #e9ecef;
    overflow: visible;
}

.user-form-card .card-header,
.user-list-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255,255,255,0.1);
}

.user-form-card .card-header h3,
.user-list-card .card-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.user-form-card .card-body,
.user-list-card .card-body {
    padding: 25px;
}

/* Form trong card - 2 cột đều nhau */
.user-form-card .card-body form {
    display: block;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 30px;
    align-items: start;
}

.form-col-left,
.form-col-right {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.user-form-card .card-body .form-group {
    display: flex;
    flex-direction: column;
}

.user-form-card .card-body .form-group.full-width {
    grid-column: 1 / -1;
}

/* Style cho hàng có thể click */
.user-row:hover {
    background-color: #f8f9fa !important;
    transition: background-color 0.2s;
}

.user-row {
    transition: background-color 0.2s;
}

/* Căn giữa nội dung các cột */
table.user-table th,
table.user-table td {
    text-align: center !important;
    vertical-align: middle;
}

table.user-table th {
    text-align: center !important;
}

/* Nút hành động trong bảng người dùng - đồng đều kích thước */
table.user-table td:last-child {
    text-align: center;
}

table.user-table td:last-child .btn {
    min-width: 70px;
    margin: 0 3px;
    display: inline-block;
    text-align: center;
}

/* Nút bị khóa theo vai trò - màu xám rõ ràng */
table.user-table td:last-child .btn[style*="opacity: 0.5"],
table.user-table td:last-child span.btn {
    background-color: #6c757d !important;
    border-color: #6c757d !important;
    color: #fff !important;
    opacity: 0.6 !important;
    cursor: not-allowed !important;
    pointer-events: none !important;
}

/* Badge trạng thái - đồng đều kích thước */
table.user-table .badge {
    min-width: 100px;
    text-align: center;
    display: inline-block;
}

/* Tối ưu form */
.user-form-card .form-group label {
    font-weight: 600;
    color: #495057;
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    font-size: 0.95rem;
}

.user-form-card .form-group label i {
    margin-right: 6px;
    color: #667eea;
}

.user-form-card .form-group input,
.user-form-card .form-group select {
    padding: 10px 12px;
    border: 1px solid #ced4da;
    border-radius: 6px;
    font-size: 0.95rem;
    transition: all 0.2s ease;
    width: 100%;
}

.user-form-card .form-group input:focus,
.user-form-card .form-group select:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.user-form-card .form-group input.form-input-readonly {
    background-color: #e9ecef;
    color: #495057;
    font-weight: 600;
    cursor: not-allowed;
}

.user-form-card .form-group select.form-select {
    cursor: pointer;
}

.user-form-card .form-group input.form-file {
    padding: 8px;
    border: 2px dashed #ced4da;
    border-radius: 6px;
    background-color: #f8f9fa;
    cursor: pointer;
}

.user-form-card .form-group input.form-file:hover {
    border-color: #667eea;
    background-color: #fff;
}

.user-form-card .form-group .form-hint {
    display: flex;
    align-items: center;
    margin-top: 6px;
    font-size: 0.85rem;
    color: #6c757d;
}

.user-form-card .form-group .form-hint i {
    margin-right: 6px;
    color: #17a2b8;
}

/* Image preview */
.current-image-preview {
    margin-top: 12px;
    text-align: center;
    padding: 12px;
    background-color: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
}

.current-image-preview img {
    max-width: 150px;
    max-height: 150px;
    border: 2px solid #ddd;
    padding: 5px;
    border-radius: 8px;
    object-fit: cover;
    display: block;
    margin: 0 auto 8px;
}

.current-image-preview .image-label {
    display: block;
    margin-top: 8px;
    color: #6c757d;
    font-size: 0.85rem;
}

.current-image-preview .image-error {
    display: block;
    margin-top: 8px;
    color: #dc3545;
    font-size: 0.85rem;
}

/* Button group */
.button-group {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    padding-top: 20px;
    border-top: 1px solid #e9ecef;
    margin-top: 20px;
}

.button-group .btn {
    padding: 10px 24px;
    font-weight: 600;
    border-radius: 6px;
    transition: all 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 6px;
}

.button-group .btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}


/* Card tìm kiếm và bộ lọc */
.user-search-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 30px;
    overflow: visible;
    width: 100%;
    box-sizing: border-box;
}

.user-search-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.user-search-card .card-header h3 {
    margin: 0;
    font-size: 1.3rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.user-search-card .card-body {
    padding: 25px;
}

.search-filter-row {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
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
    box-sizing: border-box;
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
    display: inline-flex;
    align-items: center;
    gap: 6px;
}

.btn-search:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
}

.btn-reset {
    background-color: var(--secondary-color);
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    font-weight: 500;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    text-decoration: none;
}

.btn-reset:hover {
    background-color: #5a6268;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(108, 117, 125, 0.3);
}

@media (max-width: 768px) {
    .form-row {
        grid-template-columns: 1fr;
        gap: 20px;
    }
    
    .search-filter-row {
        grid-template-columns: 1fr;
        gap: 15px;
    }
}
</style>

<script>
// AJAX Search - Tìm kiếm không reload trang
(function() {
    const searchForm = document.getElementById('searchForm');
    const searchKeyword = document.getElementById('searchKeyword');
    const filterRole = document.getElementById('filterRole');
    const filterEnabled = document.getElementById('filterEnabled');
    const sortBy = document.getElementById('sortBy');
    const sortOrder = document.getElementById('sortOrder');
    const userListContainer = document.getElementById('userListContainer');
    
    if (!searchForm || !userListContainer) return;
    
    let searchTimeout;
    let isSearching = false;
    
    // Hàm để attach event listeners cho user rows (dùng lại sau khi AJAX update)
    function attachUserRowListeners() {
        // Click vào hàng để xem profile - sử dụng event delegation
        const tbody = document.querySelector('#userListContainer tbody');
        if (tbody) {
            // Xóa listener cũ nếu có và thêm lại
            tbody.removeEventListener('click', handleRowClick);
            tbody.addEventListener('click', handleRowClick);
        }
    }
    
    // Handler cho click vào row
    function handleRowClick(e) {
        const row = e.target.closest('tr.user-row');
        if (!row) return;
        
        // Không trigger nếu click vào buttons hoặc link
        if (!e.target.closest('a') && !e.target.closest('button') && !e.target.closest('.btn')) {
            const userId = row.getAttribute('data-user-id');
            if (userId) {
                window.location.href = '${pageContext.request.contextPath}/admin/profile?id=' + userId;
            }
        }
    }
    
    
    // Gọi lần đầu khi trang load
    attachUserRowListeners();
    
    // Hàm tìm kiếm qua AJAX
    function performSearch() {
        if (isSearching) return;
        
        isSearching = true;
        
        // Lưu vị trí scroll hiện tại
        const scrollPosition = window.pageYOffset || document.documentElement.scrollTop;
        
        // Fade out nội dung hiện tại
        userListContainer.style.opacity = '0.5';
        userListContainer.style.transition = 'opacity 0.2s ease';
        
        // Hiển thị loading sau một chút
        setTimeout(function() {
            userListContainer.innerHTML = '<div style="text-align: center; padding: 40px; opacity: 0;" id="loadingDiv"><i class="fas fa-spinner fa-spin fa-2x" style="color: #667eea;"></i><p style="margin-top: 10px; color: #667eea;">Đang tìm kiếm...</p></div>';
            
            // Fade in loading
            const loadingDiv = document.getElementById('loadingDiv');
            if (loadingDiv) {
                loadingDiv.style.transition = 'opacity 0.3s ease';
                loadingDiv.style.opacity = '1';
            }
        }, 200);
        
        // Lấy các giá trị từ form
        const params = new URLSearchParams();
        if (searchKeyword && searchKeyword.value.trim()) {
            params.append('searchKeyword', searchKeyword.value.trim());
        }
        if (filterRole && filterRole.value) {
            params.append('filterRole', filterRole.value);
        }
        if (filterEnabled && filterEnabled.value) {
            params.append('filterEnabled', filterEnabled.value);
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
        const url = '${pageContext.request.contextPath}/admin/users?' + params.toString();
        
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
                        userListContainer.innerHTML = xhr.responseText;
                        userListContainer.style.opacity = '0';
                        userListContainer.style.transition = 'opacity 0.3s ease';
                        
                        // Attach lại event listeners cho các row mới
                        attachUserRowListeners();
                        
                        // Fade in nội dung mới
                        setTimeout(function() {
                            userListContainer.style.opacity = '1';
                            
                            // Khôi phục vị trí scroll (không scroll về đầu trang)
                            window.scrollTo(0, scrollPosition);
                        }, 50);
                    }, 200);
                } else {
                    userListContainer.innerHTML = '<div style="text-align: center; padding: 40px; color: #dc3545;"><i class="fas fa-exclamation-triangle"></i><p style="margin-top: 10px;">Có lỗi xảy ra khi tìm kiếm</p></div>';
                    userListContainer.style.opacity = '1';
                    window.scrollTo(0, scrollPosition);
                }
            }, remainingTime);
        };
        xhr.onerror = function() {
            isSearching = false;
            userListContainer.innerHTML = '<div style="text-align: center; padding: 40px; color: #dc3545;"><i class="fas fa-exclamation-triangle"></i><p style="margin-top: 10px;">Lỗi kết nối</p></div>';
            userListContainer.style.opacity = '1';
            window.scrollTo(0, scrollPosition);
        };
        xhr.send();
    }
    
    // Ngăn form submit mặc định
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        performSearch();
    });
    
    // Tự động lọc khi thay đổi filter (với delay nhỏ)
    [filterRole, filterEnabled, sortBy, sortOrder].forEach(function(element) {
        if (element) {
            element.addEventListener('change', function() {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(function() {
                    performSearch();
                }, 300);
            });
        }
    });
    
    // Tìm kiếm theo từ khóa chỉ khi ấn nút hoặc Enter (không tự động khi gõ)
    if (searchKeyword) {
        // Enter để tìm kiếm ngay lập tức
        searchKeyword.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                performSearch();
            }
        });
    }
})();

// Click vào hàng để xem profile (backup - sẽ được gọi lại sau AJAX)
(function() {
    document.querySelectorAll('tbody tr.user-row').forEach(function(row) {
        row.addEventListener('click', function(e) {
            // Không trigger nếu click vào buttons hoặc link
            if (!e.target.closest('a') && !e.target.closest('button') && !e.target.closest('.btn')) {
                const userId = this.getAttribute('data-user-id');
                if (userId) {
                    window.location.href = '${pageContext.request.contextPath}/admin/profile?id=' + userId;
                }
            }
        });
    });
})();


// Tự động tạo Username (ID) khi chọn vai trò
(function() {
    const userIdInput = document.getElementById('userId');
    const roleSelect = document.getElementById('userRole');
    const form = userIdInput ? userIdInput.closest('form') : null;
    
    if (!userIdInput || !roleSelect || !form) return;
    
    // Chỉ tự động generate khi không phải chế độ edit (check disabled state)
    const isEdit = roleSelect.disabled;
    if (isEdit) {
        // Nếu đang edit, không làm gì cả
        return;
    }
    
    // Hàm generate userId
    function generateUserId(role) {
        if (!role || role.trim() === '') {
            return;
        }
        
        // Hiển thị loading
        userIdInput.value = 'Đang tạo mã...';
        userIdInput.style.backgroundColor = '#e9ecef';
        userIdInput.style.color = '#333';
        userIdInput.style.fontWeight = 'bold';
        
        const xhr = new XMLHttpRequest();
        const url = '${pageContext.request.contextPath}/admin/users?action=generateUserId&role=' + encodeURIComponent(role);
        
        xhr.open('GET', url);
        xhr.onload = function() {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.success && response.id) {
                        userIdInput.value = response.id;
                        // Giữ style xám nhạt và chữ đậm
                        userIdInput.style.backgroundColor = '#e9ecef';
                        userIdInput.style.color = '#333';
                        userIdInput.style.fontWeight = 'bold';
                        // Thêm hiệu ứng nhấp nháy để người dùng chú ý
                        userIdInput.style.borderColor = '#28a745';
                        setTimeout(function() {
                            userIdInput.style.borderColor = '';
                        }, 1000);
                    } else {
                        userIdInput.value = '';
                        userIdInput.style.backgroundColor = '#e9ecef';
                        userIdInput.style.color = '#333';
                        userIdInput.style.fontWeight = 'bold';
                        alert('Lỗi: ' + (response.message || 'Không thể tạo mã người dùng'));
                    }
                } catch (err) {
                    console.error('Lỗi parse JSON:', err);
                    userIdInput.value = '';
                    userIdInput.style.backgroundColor = '#e9ecef';
                    userIdInput.style.color = '#333';
                    userIdInput.style.fontWeight = 'bold';
                }
            } else {
                userIdInput.value = '';
                userIdInput.style.backgroundColor = '#e9ecef';
                userIdInput.style.color = '#333';
                userIdInput.style.fontWeight = 'bold';
                console.error('Lỗi khi gọi API:', xhr.status);
            }
        };
        xhr.onerror = function() {
            userIdInput.value = '';
            userIdInput.style.backgroundColor = '#e9ecef';
            userIdInput.style.color = '#333';
            userIdInput.style.fontWeight = 'bold';
            console.error('Lỗi kết nối');
        };
        xhr.send();
    }
    
    // Khi chọn vai trò, tự động generate mã
    roleSelect.addEventListener('change', function() {
        const role = this.value.trim();
        
        if (role) {
            // Gọi AJAX để lấy mã mới
            generateUserId(role);
        } else {
            // Nếu không chọn vai trò, xóa mã
            userIdInput.value = '';
        }
    });
    
    // Nếu đã có role được chọn sẵn (khi load lại trang), tự động generate
    if (roleSelect.value && roleSelect.value.trim() !== '') {
        generateUserId(roleSelect.value.trim());
    }
    
    function generateUserId(role) {
        const xhr = new XMLHttpRequest();
        const url = '${pageContext.request.contextPath}/admin/users?action=generateUserId&role=' + encodeURIComponent(role);
        
        xhr.open('GET', url);
        xhr.onload = function() {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.success && response.id) {
                        userIdInput.value = response.id;
                        // Giữ style xám nhạt và chữ đậm
                        userIdInput.style.backgroundColor = '#e9ecef';
                        userIdInput.style.color = '#333';
                        userIdInput.style.fontWeight = 'bold';
                        // Thêm hiệu ứng nhấp nháy để người dùng chú ý
                        userIdInput.style.borderColor = '#28a745';
                        setTimeout(function() {
                            userIdInput.style.borderColor = '';
                        }, 1000);
                    } else {
                        userIdInput.value = '';
                        userIdInput.style.backgroundColor = '#e9ecef';
                        userIdInput.style.color = '#333';
                        userIdInput.style.fontWeight = 'bold';
                        alert('Lỗi: ' + (response.message || 'Không thể tạo mã người dùng'));
                    }
                } catch (err) {
                    console.error('Lỗi parse JSON:', err);
                    userIdInput.value = '';
                    userIdInput.style.backgroundColor = '#e9ecef';
                    userIdInput.style.color = '#333';
                    userIdInput.style.fontWeight = 'bold';
                }
            } else {
                userIdInput.value = '';
                userIdInput.style.backgroundColor = '#e9ecef';
                userIdInput.style.color = '#333';
                userIdInput.style.fontWeight = 'bold';
                console.error('Lỗi khi gọi API:', xhr.status);
            }
        };
        xhr.onerror = function() {
            userIdInput.value = '';
            userIdInput.style.backgroundColor = '#e9ecef';
            userIdInput.style.color = '#333';
            userIdInput.style.fontWeight = 'bold';
            console.error('Lỗi kết nối');
        };
        xhr.send();
    }
    
    // Validate trước khi submit - đảm bảo có mã người dùng
    form.addEventListener('submit', function(e) {
        const id = userIdInput.value.trim();
        if (!id || id === 'Đang tạo mã...') {
            e.preventDefault();
            alert('Vui lòng chọn vai trò để tạo Username!');
            if (roleSelect) {
                roleSelect.focus();
            }
            return false;
        }
    });
})();
</script>