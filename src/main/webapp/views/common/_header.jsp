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
            <div class="site-header-actions d-flex align-items-center gap-2">
                <!-- Nút Bài viết đã lưu (Bookmarks - Circular Action Button) -->
                <button type="button" class="theme-toggle-btn position-relative" id="openBookmarksBtn" title="Xem bài viết đã lưu" data-bs-toggle="offcanvas" data-bs-target="#bookmarksOffcanvas" aria-controls="bookmarksOffcanvas">
                    <i class="fa-regular fa-bookmark"></i>
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning text-dark" id="bookmarksCountBadge" style="display: none; font-size: 0.65rem; padding: 0.25em 0.5em; border: 2px solid #15803d;">0</span>
                </button>

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
                                             style="width: 28px; height: 28px; border-radius: 50%; object-fit: cover; object-position: center 15%; flex-shrink: 0;"
                                             onerror="this.style.setProperty('display', 'none', 'important');">
                                    </c:when>
                                </c:choose>
                                <span>${sessionScope.user.fullname}</span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow-sm" aria-labelledby="userMenu">
                                <li>
                                    <h6 class="dropdown-header text-muted">
                                        <i class="fa-solid fa-user-shield me-1"></i>${sessionScope.user.role ? 'Quản trị viên' : 'Phóng viên'}
                                    </h6>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/profile">
                                        <i class="fa-solid fa-id-badge me-2 text-success"></i>Hồ sơ cá nhân
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">
                                        <i class="fa-solid fa-gauge-high me-2 text-primary"></i>Bảng điều khiển
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/news">
                                        <i class="fa-solid fa-newspaper me-2 text-info"></i>Quản lý Tin tức
                                    </a>
                                </li>
                                <c:if test="${sessionScope.user.role == true}">
                                    <li>
                                        <a class="dropdown-item d-flex align-items-center justify-content-between" href="${pageContext.request.contextPath}/admin/comments">
                                            <span><i class="fa-solid fa-comments me-2 text-warning"></i>Quản lý Bình luận</span>
                                            <c:if test="${not empty sessionScope.pendingCommentCount && sessionScope.pendingCommentCount > 0}">
                                                <span class="badge bg-warning text-dark rounded-pill">${sessionScope.pendingCommentCount}</span>
                                            </c:if>
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories">
                                            <i class="fa-solid fa-tags me-2" style="color: #8b5cf6;"></i>Quản lý Chuyên mục
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/users">
                                            <i class="fa-solid fa-users me-2 text-success"></i>Quản lý Người dùng
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/newsletters">
                                            <i class="fa-solid fa-envelope me-2 text-danger"></i>Quản lý Newsletter
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/activity-logs">
                                            <i class="fa-solid fa-clock-rotate-left me-2 text-secondary"></i>Lịch sử Hoạt động
                                        </a>
                                    </li>
                                </c:if>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/export-data">
                                        <i class="fa-solid fa-file-export me-2 text-primary"></i>Xuất Dữ liệu
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                        <i class="fa-solid fa-right-from-bracket me-2 text-danger"></i>Đăng xuất
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

<!-- Offcanvas Danh sách bài viết đã lưu (Bookmarks Drawer) -->
<div class="offcanvas offcanvas-end" tabindex="-1" id="bookmarksOffcanvas" aria-labelledby="bookmarksOffcanvasLabel" style="width: 400px; max-width: 90vw;">
    <div class="offcanvas-header border-bottom py-3 px-3">
        <h5 class="offcanvas-title fw-bold d-flex align-items-center gap-2 mb-0" id="bookmarksOffcanvasLabel" style="font-size: 1.05rem;">
            <i class="fas fa-bookmark text-warning"></i>
            <span>Bài viết đã lưu</span>
            <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill" id="offcanvasBadge" style="font-size: 0.75rem;">0 bài</span>
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Đóng"></button>
    </div>
    <div class="offcanvas-body p-3" id="bookmarksListContainer">
        <!-- Danh sách bài viết sẽ được JavaScript render từ localStorage -->
    </div>
    <div class="offcanvas-footer border-top p-3 d-flex justify-content-between align-items-center bg-body-tertiary" id="bookmarksFooter" style="display: none !important;">
        <button type="button" class="btn btn-sm btn-outline-danger" id="clearAllBookmarksBtn">
            <i class="fas fa-trash-alt me-1"></i>Xóa tất cả
        </button>
        <small class="text-muted"><i class="fas fa-cloud-arrow-down me-1"></i>Lưu trên trình duyệt</small>
    </div>
</div>
