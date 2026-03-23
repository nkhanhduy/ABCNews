<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<header class="admin-header">
    <div class="admin-user-info">
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/admin/profile" 
                   class="user-profile-link" 
                   title="Xem thông tin cá nhân">
                    <i class="fas fa-user-circle me-1"></i>
                    ${sessionScope.user.fullname}
                    <span class="badge bg-${sessionScope.user.role ? 'danger' : 'info'} ms-2">
                        <i class="fas fa-shield-alt me-1"></i>
                        ${sessionScope.user.role ? 'Quản trị viên' : 'Phóng viên'}
                    </span>
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm">
                    <i class="fas fa-sign-out-alt me-1"></i>Đăng xuất
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">
                    <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</header>