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
    
    <%-- Xác định Base URL đầy đủ cho SEO & Open Graph Tags --%>
    <c:set var="reqPort" value="${pageContext.request.serverPort}" />
    <c:set var="portPart" value="${(pageContext.request.scheme eq 'http' and reqPort eq 80) or (pageContext.request.scheme eq 'https' and reqPort eq 443) ? '' : ':'.concat(reqPort)}" />
    <c:set var="siteBaseUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}${portPart}${pageContext.request.contextPath}" />
    
    <c:choose>
        <c:when test="${not empty news}">
            <c:set var="pageCanonicalUrl" value="${siteBaseUrl}/detail?id=${news.id}" />
            <c:set var="metaDescription" value="${news.summary}" />
            <c:choose>
                <c:when test="${fn:startsWith(news.image, 'http://') or fn:startsWith(news.image, 'https://')}">
                    <c:set var="metaImage" value="${news.image}" />
                </c:when>
                <c:when test="${fn:startsWith(news.image, '/')}">
                    <c:set var="metaImage" value="${pageContext.request.scheme}://${pageContext.request.serverName}${portPart}${news.image}" />
                </c:when>
                <c:when test="${not empty news.image}">
                    <c:set var="metaImage" value="${siteBaseUrl}/${news.image}" />
                </c:when>
                <c:otherwise>
                    <c:set var="metaImage" value="${siteBaseUrl}/assets/images/default-thumbnail.jpg" />
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:set var="pageCanonicalUrl" value="${siteBaseUrl}${pageContext.request.servletPath}" />
            <c:set var="metaDescription" value="ABC News - Kênh thông tin điện tử hàng đầu, liên tục cập nhật tin tức thời sự, kinh tế, công nghệ AI, thể thao và đời sống 24/7." />
            <c:set var="metaImage" value="${siteBaseUrl}/assets/images/logo.png" />
        </c:otherwise>
    </c:choose>

    <!-- SEO Canonical URL -->
    <link rel="canonical" href="${pageCanonicalUrl}">

    <!-- Standard Meta Description & Robots -->
    <meta name="description" content="<c:out value='${metaDescription}' />">
    <meta name="robots" content="index, follow">

    <!-- Open Graph (Facebook, Zalo, LinkedIn) -->
    <meta property="og:locale" content="vi_VN">
    <meta property="og:type" content="${not empty news ? 'article' : 'website'}">
    <meta property="og:site_name" content="ABC News">
    <meta property="og:title" content="<c:out value='${not empty news ? news.title : (not empty pageTitle ? pageTitle : \"ABC News - Tin tức nóng hổi\")}' />">
    <meta property="og:description" content="<c:out value='${metaDescription}' />">
    <meta property="og:url" content="${pageCanonicalUrl}">
    <c:if test="${not empty metaImage}">
        <meta property="og:image" content="${metaImage}">
        <meta property="og:image:alt" content="<c:out value='${not empty news ? news.title : \"ABC News\"}' />">
    </c:if>
    <c:if test="${not empty news}">
        <meta property="article:published_time" content="<fmt:formatDate value='${news.postedDate}' pattern='yyyy-MM-dd\'T\'HH:mm:ssXXX' />">
        <c:if test="${not empty news.author}">
            <meta property="article:author" content="${news.author}">
        </c:if>
    </c:if>

    <!-- Twitter / X Card -->
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:site" content="@ABCNews">
    <meta name="twitter:title" content="<c:out value='${not empty news ? news.title : (not empty pageTitle ? pageTitle : \"ABC News\")}' />">
    <meta name="twitter:description" content="<c:out value='${metaDescription}' />">
    <c:if test="${not empty metaImage}">
        <meta name="twitter:image" content="${metaImage}">
    </c:if>
    
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome Icons --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <%-- Custom CSS --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2026.2">
    <!-- Framework CSS chung - Dropdown, Buttons, Cards -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/framework.css?v=2026.2">
    <!-- Public Style Framework - Hiệu ứng hover cho bài báo -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public-style.css?v=2026.2">
    <!-- UI Animations -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/animations.css?v=2026.2">
    <!-- Dark Mode CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dark-mode.css?v=2026.2">
    <!-- Theme Toggle JS (Áp dụng theme ngay lập tức để tránh FOUC) -->
    <script src="${pageContext.request.contextPath}/assets/js/theme-toggle.js?v=2026.2"></script>
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
                <div class="main-content-card">
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