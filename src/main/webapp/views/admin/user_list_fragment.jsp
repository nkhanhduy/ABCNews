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
								   onclick="return confirm('Xóa tài khoản này?')"
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
