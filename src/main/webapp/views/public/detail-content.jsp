<%-- 
  File: detail-content.jsp
  Description: CHỈ chứa phần nội dung của trang chi tiết
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- BẮT BUỘC dùng jakarta --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
  
<%-- 
  Kiểm tra xem đối tượng 'news' (do DetailController gửi sang) có tồn tại không.
--%>
<c:choose>
    <%-- 1. Nếu tìm thấy bản tin (news != null) --%>
    <c:when test="${not empty news}">
        <article>
            <!-- Tiêu đề -->
            <h1 class="mb-4">${news.title}</h1>
            
            <!-- Thông tin meta -->
            <div class="d-flex flex-wrap gap-3 mb-4 text-muted">
                <span><i class="fas fa-calendar-alt me-1"></i><fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy HH:mm" /></span>
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
                <span><i class="fas fa-eye me-1"></i>${news.viewCount} lượt xem</span>
            </div>
            
            <!-- Ảnh (nếu có) -->
            <c:if test="${not empty news.image}">
                <c:set var="imageUrl" value="${news.image}" />
                <c:if test="${!fn:startsWith(imageUrl, pageContext.request.contextPath) && fn:startsWith(imageUrl, '/')}">
                    <c:set var="imageUrl" value="${pageContext.request.contextPath}${news.image}" />
                </c:if>
                <div class="mb-4">
                    <img src="${imageUrl}" alt="${news.title}" 
                         class="img-fluid rounded shadow-sm"
                         onerror="this.src='https://placehold.co/800x400?text=Image+Not+Found'">
                </div>
            </c:if>

            <!-- Tóm tắt -->
            <div class="alert alert-light border-start border-4 border-primary mb-4">
                <p class="mb-0 fst-italic fw-bold">${news.summary}</p>
            </div>
            
            <!-- NỘI DUNG CHÍNH -->
            <div class="news-full-content mb-5" style="line-height: 1.8; font-size: 1.1rem;">
                ${news.content}
            </div>
            
            <hr class="my-5">
            
            <!-- TIN CÙNG LOẠI -->
            <div class="card shadow-sm">
                <div class="card-header bg-secondary text-white">
                    <h5 class="mb-0"><i class="fas fa-list me-2"></i>Tin cùng loại</h5>
                </div>
                <div class="list-group list-group-flush">
                    <c:forEach var="related" items="${relatedNews}">
                        <a href="${pageContext.request.contextPath}/detail?id=${related.id}" class="list-group-item list-group-item-action">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1">${related.title}</h6>
                                <small class="text-muted">
                                    <fmt:formatDate value="${related.postedDate}" pattern="dd/MM/yyyy"/>
                                </small>
                            </div>
                        </a>
                    </c:forEach>
                    <c:if test="${empty relatedNews}">
                        <div class="list-group-item text-muted">Không có tin nào cùng loại.</div>
                    </c:if>
                </div>
            </div>
        </article>
    </c:when>
    
    <%-- 2. Nếu không tìm thấy bản tin (news == null) --%>
    <c:otherwise>
        <div class="alert alert-warning text-center py-5">
            <i class="fas fa-exclamation-triangle fa-3x mb-3 text-warning"></i>
            <h2>Không tìm thấy bản tin</h2>
            <p>Bản tin bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                <i class="fas fa-home me-1"></i>Quay về trang chủ
            </a>
        </div>
    </c:otherwise>
</c:choose>