<%-- 
  File: category-content.jsp
  Description: Nội dung hiển thị danh sách tin tức theo chuyên mục
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!-- Tiêu đề Chuyên mục chuẩn tòa soạn -->
<div class="section-title">
    <span>Chuyên mục: ${categoryName}</span>
</div>
  
<c:choose>
    <c:when test="${not empty categoryNews}">
        <div class="row g-4">
            <c:forEach var="news" items="${categoryNews}">
                <div class="col-12">
                    <div class="card border shadow-sm rounded-3 overflow-hidden">
                        <div class="row g-0">
                            <c:if test="${not empty news.image}">
                                <c:set var="imageUrl" value="${news.image}" />
                                <c:if test="${!fn:startsWith(imageUrl, pageContext.request.contextPath) && fn:startsWith(imageUrl, '/')}">
                                    <c:set var="imageUrl" value="${pageContext.request.contextPath}${news.image}" />
                                </c:if>
                                <div class="col-md-4">
                                    <a href="${pageContext.request.contextPath}/detail?id=${news.id}">
                                        <img src="${imageUrl}" 
                                             class="img-fluid w-100 h-100" 
                                             style="object-fit: cover; min-height: 200px;" 
                                             alt="${news.title}" 
                                             loading="lazy"
                                             onerror="this.src='https://placehold.co/400x300?text=ABC+News'">
                                    </a>
                                </div>
                            </c:if>
                            <div class="${not empty news.image ? 'col-md-8' : 'col-12'}">
                                <div class="card-body d-flex flex-column h-100 p-4">
                                    <h4 class="card-title fw-bold mb-2">
                                        <a href="${pageContext.request.contextPath}/detail?id=${news.id}" class="text-decoration-none text-dark">
                                            ${news.title}
                                        </a>
                                    </h4>
                                    <div class="text-muted small mb-3">
                                        <span><fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                                        <span class="mx-2">•</span>
                                        <span>
                                            <c:choose>
                                                <c:when test="${not empty news.author}">
                                                    ${news.author}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="fst-italic">Ban Biên Tập</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                        <span class="mx-2">•</span>
                                        <span>${news.viewCount} lượt xem</span>
                                    </div>
                                    <p class="card-text text-secondary flex-grow-1">${news.summary}</p>
                                    <div class="mt-2">
                                        <a href="${pageContext.request.contextPath}/detail?id=${news.id}" class="read-more-link fw-semibold">
                                            Xem chi tiết &rarr;
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-light border p-5 text-center rounded-3">
            <h5 class="fw-bold mb-2">Chưa có bài viết nào trong chuyên mục này.</h5>
            <p class="text-muted mb-3">Nội dung đang được ban biên tập tổng hợp và cập nhật trong thời gian sớm nhất.</p>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-success btn-sm">
                Quay lại Trang chủ
            </a>
        </div>
    </c:otherwise>
</c:choose>
