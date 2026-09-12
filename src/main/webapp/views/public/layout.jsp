<%-- 
  File: layout.jsp
  Description: Layout CHÍNH cho trang đọc giả. 
  Nó nạp _header, _menu, _sidebar, _footer và nạp ${view} (nội dung) vào giữa.
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
    
    <title>
        <%-- Lấy tiêu đề động mà Controller gửi sang --%>
        <c:choose>
            <c:when test="${not empty pageTitle}">
                ${pageTitle} - ABC News
            </c:when>
            <c:otherwise>
                ABC News - Tin tức nóng hổi
            </c:otherwise>
        </c:choose>
    </title>
    
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome Icons --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <%-- Custom CSS --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <!-- Framework CSS chung - Dropdown, Buttons, Cards -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/framework.css">
    <!-- Public Style Framework - Hiệu ứng hover cho bài báo -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public-style.css">
    <!-- UI Animations -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/animations.css">
    <!-- Dark Mode CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dark-mode.css">
    <!-- Theme Toggle JS (Áp dụng theme ngay lập tức để tránh FOUC) -->
    <script src="${pageContext.request.contextPath}/assets/js/theme-toggle.js"></script>
</head>
<body>

    <!-- 1. Header -->
    <jsp:include page="/views/common/_header.jsp" />

    <!-- 2. Menu -->
    <jsp:include page="/views/common/_menu.jsp" />

    <!-- 3. Main Wrapper -->
    <div class="container my-4">
        <div class="row g-4">
            <!-- 3.1. Main Content (Nội dung thay đổi) -->
            <main class="col-lg-9 col-xl-9 col-md-12">
                <div class="bg-white rounded shadow-sm p-4">
                    <!-- 
                      Đây là nơi nội dung chính (view) sẽ được nạp vào.
                      Controller đã set: setAttribute("view", "/views/public/home-content.jsp")
                      hoặc setAttribute("view", "/views/public/category-content.jsp")
                    -->
                    <c:choose>
                        <c:when test="${not empty view}">
                            <jsp:include page="${view}" />
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning">Lỗi: Không tìm thấy nội dung trang (view).</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
            
            <!-- 3.2. Sidebar -->
            <aside class="col-lg-3 col-xl-3 col-md-12">
                <jsp:include page="/views/common/_sidebar.jsp" />
            </aside>
        </div>
    </div>

    <!-- 4. Footer -->
    <jsp:include page="/views/common/_footer.jsp" />

    <%-- Bootstrap 5 JS Bundle --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    <%-- Custom Animations JS --%>
    <script src="${pageContext.request.contextPath}/assets/js/animations.js"></script>
    <%-- AJAX Navigation - Load content động như YouTube --%>
    <script src="${pageContext.request.contextPath}/assets/js/ajax-navigation.js"></script>
    <%-- Public UX Enhancements - Framework JS cho trang công khai --%>
    <script src="${pageContext.request.contextPath}/assets/js/public-ux.js"></script>
</body>
</html>