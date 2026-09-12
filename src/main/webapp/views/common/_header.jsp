<%-- 
  File: _header.jsp
  Description: Header với Bootstrap 5
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<header class="bg-success text-white shadow-sm">
    <div class="container">
        <div class="d-flex justify-content-between align-items-center py-3">
            <h1 class="mb-0">
                <a href="${pageContext.request.contextPath}/home" class="text-white text-decoration-none fw-bold">
                    <i class="fas fa-newspaper me-2"></i>ABC News
                </a>
            </h1>
            
            <div class="d-flex align-items-center gap-3">
                <!-- Nút chuyển đổi Dark / Light Mode -->
                <button type="button" class="btn btn-outline-light btn-sm rounded-circle theme-toggle-btn" id="themeToggleBtn" title="Chuyển đổi giao diện Sáng / Tối" style="width: 36px; height: 36px; display: inline-flex; align-items: center; justify-content: center; padding: 0;">
                    <i class="fas fa-moon" id="themeIcon"></i>
                </button>

                <c:choose>
                    <%-- 1. Nếu ĐÃ ĐĂNG NHẬP (sessionScope.user tồn tại) --%>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="dropdown">
                            <button class="btn btn-light btn-sm dropdown-toggle" type="button" id="userMenu" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-user-circle me-1"></i>${sessionScope.user.fullname}
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userMenu">
                                <li><h6 class="dropdown-header">
                                    <i class="fas fa-user-tag me-1"></i>${sessionScope.user.role ? 'Quản trị viên' : 'Phóng viên'}
                                </h6></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">
                                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                                </a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/news">
                                    <i class="fas fa-newspaper me-2"></i>Quản lý Tin
                                </a></li>
                                <%-- Chỉ Admin mới thấy các mục này --%>
                                <c:if test="${sessionScope.user.role == true}">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories">
                                        <i class="fas fa-tags me-2"></i>Quản lý Loại tin
                                    </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/users">
                                        <i class="fas fa-users me-2"></i>Quản lý User
                                    </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/newsletters">
                                        <i class="fas fa-envelope me-2"></i>Quản lý Newsletter
                                    </a></li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                    <i class="fas fa-sign-out-alt me-2"></i>Đăng xuất
                                </a></li>
                            </ul>
                        </div>
                    </c:when>
                    
                    <%-- 2. Nếu CHƯA ĐĂNG NHẬP (sessionScope.user rỗng) --%>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-light btn-sm">
                            <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</header>
