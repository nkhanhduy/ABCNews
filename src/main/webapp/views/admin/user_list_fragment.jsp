<%-- 
  File: user_list_fragment.jsp
  Description: Fragment JSP để render danh sách người dùng (dùng cho AJAX)
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<div class="table-responsive">
	<table class="crud-table user-table">
		<thead>
			<tr>
				<th style="width: 10%; text-align: center;">ID</th>
				<th style="text-align: left; padding-left: 15px;">Họ tên</th>
				<th style="text-align: left; padding-left: 15px;">Email</th>
				<th style="width: 14%; text-align: center;">Vai trò</th>
				<th style="width: 8%; text-align: center;">Số tin</th>
				<th style="width: 14%; text-align: center;">Trạng thái</th>
				<th style="width: 18%; text-align: center;">Hành động</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="u" items="${userList}">
				<c:set var="currentUser" value="${sessionScope.user}" />
				<c:set var="isCurrentUser" value="${u.id == currentUser.id}" />
				<c:set var="isSuperAdmin" value="${currentUser.superAdmin}" />
				<c:set var="isUserSuperAdmin" value="${u.superAdmin}" />
				<c:set var="newsCount" value="${newsCountMap[u.id] != null ? newsCountMap[u.id] : 0}" />
				
				<tr data-user-id="${u.id}" class="user-row" style="cursor: pointer;">
					<td style="text-align: center;">${u.id}</td>
					<td style="text-align: left; padding-left: 15px;">${u.fullname}</td>
					<td style="text-align: left; padding-left: 15px;">${u.email}</td>
					<td style="text-align: center;">
						<span style="color: ${isUserSuperAdmin ? 'purple' : (u.role ? 'red' : 'blue')}; font-weight: bold;">
							${isUserSuperAdmin ? 'Super Admin' : (u.role ? 'Admin' : 'Phóng viên')}
						</span>
					</td>
					<td style="text-align: center;">
						<span class="badge ${newsCount > 0 ? 'bg-info' : 'bg-secondary'}">${newsCount}</span>
					</td>
					<td style="text-align: center;">
						<span class="badge ${u.enabled ? 'bg-success' : 'bg-danger'}">
							${u.enabled ? 'Hoạt động' : 'Bị khóa'}
						</span>
					</td>
					<td style="text-align: center;">
						<!-- Nút Sửa -->
						<c:set var="canEdit" value="${isSuperAdmin || !u.superAdmin}" />
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
								<form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:inline; margin:0 3px;" onsubmit="return confirm('Xóa tài khoản này?')">
									<input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
									<input type="hidden" name="action" value="delete">
									<input type="hidden" name="id" value="${u.id}">
									<button type="submit" class="btn btn-sm btn-delete" style="min-width: 70px; border:none; cursor:pointer;">Xóa</button>
								</form>
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
								<form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:inline; margin:0 3px;">
									<input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
									<input type="hidden" name="action" value="toggle">
									<input type="hidden" name="id" value="${u.id}">
									<button type="submit" class="btn btn-sm btn-update" style="min-width: 70px; border:none; cursor:pointer;">
										${u.enabled ? 'Khóa' : 'Mở khóa'}
									</button>
								</form>
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
			<c:if test="${empty userList}">
				<tr>
					<td colspan="7" style="text-align: center; padding: 40px; color: #6c757d;">
						<i class="fas fa-users" style="font-size: 2rem; margin-bottom: 10px; display: block;"></i>
						Không tìm thấy người dùng nào.
					</td>
				</tr>
			</c:if>
		</tbody>
	</table>
</div>
