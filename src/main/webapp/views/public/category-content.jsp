<%-- 
  File: category-content.jsp
  Description: CHỈ chứa phần nội dung của trang Loại tin
  File này sẽ được nạp vào ${view} trong layout.jsp
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>


<!-- Hiển thị tên loại tin -->
<div class="d-flex align-items-center mb-4">
    <h1 class="mb-0 me-3">
        <i class="fas fa-tag text-primary me-2"></i>${categoryName}
    </h1>
    <div class="flex-grow-1 border-bottom border-2 border-primary"></div>
</div>
  
<!-- 
  PHẦN ĐỘNG:
  Lặp qua danh sách 'categoryNews' mà CategoryController gửi sang.
-->
<c:choose>
    <c:when test="${not empty categoryNews}">
        <div class="row g-4">
            <c:forEach var="news" items="${categoryNews}">
                <div class="col-12">
                    <div class="card shadow-sm h-100">
                        <div class="row g-0">
                            <c:if test="${not empty news.image}">
                                <c:set var="imageUrl" value="${news.image}" />
                                <c:if test="${!fn:startsWith(imageUrl, pageContext.request.contextPath) && fn:startsWith(imageUrl, '/')}">
                                    <c:set var="imageUrl" value="${pageContext.request.contextPath}${news.image}" />
                                </c:if>
                                <div class="col-md-4">
                                    <a href="${pageContext.request.contextPath}/detail?id=${news.id}">
                                        <img src="${imageUrl}" class="img-fluid rounded-start h-100" style="object-fit: cover; min-height: 200px;" alt="${news.title}" onerror="this.src='https://placehold.co/400x300?text=No+Image'">
                                    </a>
                                </div>
                            </c:if>
                            <div class="${not empty news.image ? 'col-md-8' : 'col-12'}">
                                <div class="card-body d-flex flex-column h-100">
                                    <h5 class="card-title">
                                        <a href="${pageContext.request.contextPath}/detail?id=${news.id}" class="text-decoration-none text-dark">
                                            ${news.title}
                                        </a>
                                    </h5>
                                    <div class="text-muted small mb-2">
                                        <span class="me-3"><i class="fas fa-calendar-alt me-1"></i><fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                                        <span><i class="fas fa-user me-1"></i>
                                            <c:choose>
                                                <c:when test="${not empty news.author}">
                                                    ${news.author}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted" style="font-style: italic;">Tác giả đã bị xóa</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                    <p class="card-text flex-grow-1">${news.summary}</p>
                                    <a href="${pageContext.request.contextPath}/detail?id=${news.id}" class="btn btn-primary btn-sm mt-auto align-self-start">
                                        <i class="fas fa-arrow-right me-1"></i>Đọc tiếp
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-info text-center py-5">
            <i class="fas fa-info-circle fa-3x mb-3 text-info"></i>
            <h5>Chưa có bản tin nào trong chuyên mục này.</h5>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-primary mt-3">
                <i class="fas fa-home me-1"></i>Về trang chủ
            </a>
        </div>
    </c:otherwise>
</c:choose>
