<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<header class="admin-header">
    <div class="d-flex align-items-center gap-3">
        <!-- Nút chuyển đổi Dark / Light Mode trong Admin -->
        <button type="button" class="theme-toggle-btn" id="themeToggleBtn" title="Chuyển đổi giao diện Sáng / Tối" style="border: 1px solid var(--brand-border, #cbd5e1); color: var(--brand-text, #334155); width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0;">
            <i class="fas fa-moon" id="themeIcon"></i>
        </button>

        <div class="admin-user-info d-flex align-items-center gap-2">
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/admin/profile" 
                   class="user-profile-link" 
                   title="Xem thông tin cá nhân"
                   style="white-space: nowrap; flex-shrink: 0; display: inline-flex; align-items: center; gap: 8px;">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user.imagePath}">
                            <c:set var="headerUserImg" value="${sessionScope.user.imagePath}" />
                            <c:if test="${!fn:startsWith(headerUserImg, pageContext.request.contextPath) && fn:startsWith(headerUserImg, '/')}">
                                <c:set var="headerUserImg" value="${pageContext.request.contextPath}${sessionScope.user.imagePath}" />
                            </c:if>
                            <img src="${headerUserImg}" 
                                 alt="${sessionScope.user.fullname}" 
                                 class="header-user-avatar"
                                 style="width: 28px !important; height: 28px !important; max-width: 28px !important; max-height: 28px !important; border-radius: 50% !important; object-fit: cover !important; flex-shrink: 0 !important; display: inline-block !important; vertical-align: middle !important;"
                                 onerror="this.style.display='none'; this.nextElementSibling.style.display='inline-block';">
                            <i class="fas fa-user-circle me-1" style="display: none;"></i>
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-user-circle me-1"></i>
                        </c:otherwise>
                    </c:choose>
                    <span class="user-fullname">${sessionScope.user.fullname}</span>
                    <span class="badge bg-${sessionScope.user.role ? 'danger' : 'info'} ms-1">
                        <i class="fas fa-${sessionScope.user.role ? 'shield-alt' : 'pen'} me-1"></i>
                        ${sessionScope.user.role ? 'Quản trị viên' : 'Phóng viên'}
                    </span>
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm text-nowrap" style="white-space: nowrap; flex-shrink: 0;">
                    <i class="fas fa-sign-out-alt me-1"></i>Đăng xuất
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-success btn-sm text-nowrap" style="white-space: nowrap; flex-shrink: 0;">
                    <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
                </a>
            </c:otherwise>
        </c:choose>
        </div>
    </div>
</header>