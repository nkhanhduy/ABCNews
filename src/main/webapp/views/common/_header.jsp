<%-- 
  File: _header.jsp
  Description: Header chuẩn phong cách báo điện tử hiện đại, thống nhất theme Deep Slate
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<header class="site-header">
    <div class="container">
        <div class="d-flex justify-content-between align-items-center">
            <!-- Brand Logo -->
            <div class="d-flex align-items-center">
                <a href="${pageContext.request.contextPath}/home" class="site-logo">
                    <span>ABC</span><span class="logo-badge">News</span>
                </a>
            </div>
            
            <!-- Actions Right -->
            <div class="site-header-actions">
                <!-- Nút chuyển đổi Dark / Light Mode -->
                <button type="button" class="theme-toggle-btn" id="themeToggleBtn" title="Chuyển đổi giao diện Sáng / Tối">
                    <i class="fas fa-moon" id="themeIcon"></i>
                </button>

                <c:choose>
                    <%-- 1. Nếu ĐÃ ĐĂNG NHẬP --%>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="dropdown">
                            <button class="user-dropdown-btn dropdown-toggle" type="button" id="userMenu" data-bs-toggle="dropdown" aria-expanded="false">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.user.imagePath}">
                                        <c:set var="headerPubUserImg" value="${sessionScope.user.imagePath}" />
                                        <c:if test="${!fn:startsWith(headerPubUserImg, pageContext.request.contextPath) && fn:startsWith(headerPubUserImg, '/')}">
                                            <c:set var="headerPubUserImg" value="${pageContext.request.contextPath}${sessionScope.user.imagePath}" />
                                        </c:if>
                                        <img src="${headerPubUserImg}" 
                                             alt="${sessionScope.user.fullname}" 
                                             class="header-user-avatar" 
                                             onerror="this.style.display='none';">
                                    </c:when>
                                </c:choose>
                                <span>${sessionScope.user.fullname}</span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow-sm" aria-labelledby="userMenu">
                                <li>
                                    <h6 class="dropdown-header text-muted">
                                        ${sessionScope.user.role ? 'Quản trị viên' : 'Phóng viên'}
                                    </h6>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/profile">
                                        <i class="fas fa-id-badge me-2 text-success"></i>Hồ sơ cá nhân
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">
                                        Bảng điều khiển
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/news">
                                        Quản lý Tin tức
                                    </a>
                                </li>
                                <c:if test="${sessionScope.user.role == true}">
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories">
                                            Quản lý Chuyên mục
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/users">
                                            Quản lý Người dùng
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/newsletters">
                                            Quản lý Newsletter
                                        </a>
                                    </li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                        Đăng xuất
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:when>
                    
                    <%-- 2. Nếu CHƯA ĐĂNG NHẬP: Kiểm tra xem có đang ở trang Login / Auth không để tránh lỗi logic nút trùng lặp --%>
                    <c:otherwise>
                        <c:set var="reqUri" value="${pageContext.request.requestURI}" />
                        <c:choose>
                            <%-- Đang ở trang auth -> Hiển thị nút Về trang chủ --%>
                            <c:when test="${fn:contains(reqUri, 'login') || fn:contains(reqUri, 'forgot-password') || fn:contains(reqUri, 'verify-otp')}">
                                <a href="${pageContext.request.contextPath}/home" class="btn-back-home">
                                    Về trang chủ &rarr;
                                </a>
                            </c:when>
                            <%-- Đang ở trang đọc báo thông thường -> Hiển thị nút Đăng nhập --%>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/login" class="btn-header-login">
                                    Đăng nhập
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</header>
