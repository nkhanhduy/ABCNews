<%-- 
  File: admin_layout.jsp (CỦA ADMIN)
  Description: Layout chính cho khu vực quản trị
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - Trang Quản Trị</title>
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome Icons --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <!-- CSS Hệ thống Biến màu ABCNews & Theme Xanh Lá -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2026.3">
    <!-- Framework CSS chung - Dropdown, Buttons, Cards -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/framework.css?v=2026.3">
    <!-- Tải CSS của Admin -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin_style.css?v=2026.3">
    <!-- UI Animations -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/animations.css?v=2026.3">
    <!-- Dark Mode CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dark-mode.css?v=2026.3">
    <!-- Theme Toggle JS -->
    <script src="${pageContext.request.contextPath}/assets/js/theme-toggle.js"></script>
</head>
<body>

    <div class="admin-wrapper">
        
        <!-- 1. Sidebar (Menu "phần thân") -->
        <jsp:include page="/views/admin/admin_menu.jsp" />

        <!-- 2. Khu vực nội dung chính -->
        <div class="admin-main">
            
            <!-- 2.1. Header (Phần đầu) -->
            <jsp:include page="/views/admin/_header.jsp" />

            <!-- 2.2. Nội dung thay đổi -->
            <main class="admin-content">
                <div class="content-box">
                    <!-- 
                      Nội dung chính sẽ được nạp vào đây 
                      (ví dụ: dashboard.jsp, news_crud.jsp...)
                    -->
                    <jsp:include page="${view}" />
                </div>
                
                <!-- Footer -->
                <footer class="admin-footer">
                    <p class="mb-0">
                        &copy; 2025 ABC News CMS | Phát triển bởi <strong>Nguyễn Khánh Duy</strong> — FPT Polytechnic TP. Hồ Chí Minh
                    </p>
                </footer>
            </main>

        </div>
    </div>

    <%-- Bootstrap 5 JS Bundle --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    <%-- Chart.js - Biểu đồ thống kê trực quan --%>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
    <%-- Trình soạn thảo bài viết --%>
    <script src="https://cdn.ckeditor.com/ckeditor5/41.1.0/classic/ckeditor.js"></script>

    <%-- Custom Animations JS --%>
    <script src="${pageContext.request.contextPath}/assets/js/animations.js"></script>
    <%-- AJAX Navigation - Load content động như YouTube --%>
    <script src="${pageContext.request.contextPath}/assets/js/ajax-navigation.js"></script>
    <%-- Admin UX Enhancements --%>
    <script src="${pageContext.request.contextPath}/assets/js/admin-ux.js"></script>
</body>
</html>