<%-- 
  File: admin_menu.jsp (CỦA ADMIN)
  Description: Sidebar menu cho trang quản trị với Bootstrap
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<nav class="admin-sidebar">
    <div class="sidebar-header">
        <h2 class="mb-0">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="text-white text-decoration-none">
                <i class="fas fa-cog me-2"></i>Trang Quản Trị
            </a>
        </h2>
    </div>
    
    <ul class="admin-menu">
        <li>
            <a href="${pageContext.request.contextPath}/home">
                <i class="fas fa-home me-2"></i>Xem Trang Public
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/dashboard" 
               class="${view.contains('dashboard.jsp') ? 'active' : ''}">
               <i class="fas fa-tachometer-alt me-2"></i>Tổng quan
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/news" 
               class="${view.contains('news_crud.jsp') ? 'active' : ''}">
               <i class="fas fa-newspaper me-2"></i>Quản lý Tin
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/comments" 
               class="${view.contains('comment_crud.jsp') ? 'active' : ''} d-flex align-items-center justify-content-between">
               <span><i class="fas fa-comments me-2"></i>Quản lý Bình luận</span>
               <c:if test="${not empty sessionScope.pendingCommentCount && sessionScope.pendingCommentCount > 0}">
                   <span class="badge bg-warning text-dark rounded-pill">${sessionScope.pendingCommentCount}</span>
               </c:if>
            </a>
        </li>
        
        <%-- Chỉ Admin (role=true) mới thấy các mục này --%>
        <c:if test="${sessionScope.user.role == true}">
             <li>
                <a href="${pageContext.request.contextPath}/admin/categories"
                   class="${view.contains('category_crud.jsp') ? 'active' : ''}">
                   <i class="fas fa-tags me-2"></i>Quản lý Loại tin
                </a>
            </li>
             <li>
                <a href="${pageContext.request.contextPath}/admin/users"
                   class="${view.contains('user_crud.jsp') ? 'active' : ''}">
                   <i class="fas fa-users me-2"></i>Quản lý User
                </a>
            </li>
             <li>
                <a href="${pageContext.request.contextPath}/admin/newsletters"
                   class="${view.contains('newsletter_crud.jsp') ? 'active' : ''}">
                   <i class="fas fa-envelope me-2"></i>Quản lý Newsletter
                </a>
            </li>
            <%-- Lịch Sử Hoạt Động - Chỉ Admin --%>
            <li>
                <a href="${pageContext.request.contextPath}/admin/activity-logs"
                   class="${view.contains('activity-logs.jsp') ? 'active' : ''}">
                   <i class="fas fa-history me-2"></i>Lịch Sử Hoạt Động
                </a>
            </li>
        </c:if>
        
        <%-- CẢ Admin và Reporter đều có menu Xuất Dữ liệu --%>
        <li>
            <a href="${pageContext.request.contextPath}/admin/export-data"
               class="${view.contains('export-data.jsp') ? 'active' : ''}">
               <i class="fas fa-download me-2"></i>Xuất Dữ liệu
            </a>
        </li>
    </ul>
</nav>