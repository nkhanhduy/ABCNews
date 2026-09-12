<%-- 
  File: home-content.jsp
  Description: Nội dung trang chủ (tin nổi bật trang nhất) theo phong cách báo chí hiện đại
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
  
<div class="section-title">
    <span>Tin Trang Nhất</span>
</div>

<c:choose>
    <%-- 1. Nếu có tin (Danh sách không rỗng) --%>
    <c:when test="${not empty homeNews}">
        <div class="news-list">
            <c:forEach var="news" items="${homeNews}">
                <article class="news-item">
                    <h3 class="news-title">
                        <a href="${pageContext.request.contextPath}/detail?id=${news.id}">
                            ${news.title}
                        </a>
                    </h3>
                    
                    <div class="news-meta">
                        <span><fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy HH:mm"/></span>
                        <span class="meta-dot">•</span>
                        <span>
                            <c:choose>
                                <c:when test="${not empty news.author}">
                                    ${news.author}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted fst-italic">Ban Biên Tập</span>
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <span class="meta-dot">•</span>
                        <span>${news.viewCount} lượt xem</span>
                    </div>
                    
                    <div class="news-content-preview mt-3">
                        <%-- Hiển thị ảnh nếu có --%>
                        <c:if test="${not empty news.image}">
                            <c:set var="imageUrl" value="${news.image}" />
                            <c:if test="${!fn:startsWith(imageUrl, pageContext.request.contextPath) && fn:startsWith(imageUrl, '/')}">
                                <c:set var="imageUrl" value="${pageContext.request.contextPath}${news.image}" />
                            </c:if>
                            <div class="news-thumb">
                                <a href="${pageContext.request.contextPath}/detail?id=${news.id}">
                                    <img src="${imageUrl}" alt="${news.title}" 
                                         class="news-thumb-img"
                                         loading="lazy"
                                         onerror="this.style.display='none'">
                                </a>
                            </div>
                        </c:if>

                        <div class="news-summary">
                            <p class="summary-text">${news.summary}</p>
                            <a href="${pageContext.request.contextPath}/detail?id=${news.id}" class="read-more-link">
                                Đọc tiếp &rarr;
                            </a>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:when>
    
    <%-- 2. Nếu KHÔNG có tin nào --%>
    <c:otherwise>
        <div class="alert alert-light border p-4 text-center rounded-3">
            <p class="mb-0 text-muted">Hiện tại chưa có bản tin nổi bật nào được xuất bản trên trang nhất.</p>
            <c:if test="${not empty sessionScope.user}">
                <div class="mt-3">
                    <a href="${pageContext.request.contextPath}/admin/news" class="btn btn-primary btn-sm">
                        Quản lý và xuất bản tin ngay
                    </a>
                </div>
            </c:if>
        </div>
    </c:otherwise>
</c:choose>