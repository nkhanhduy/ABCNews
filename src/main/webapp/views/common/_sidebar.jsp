<%-- 
  File: _sidebar.jsp
  Description: Sidebar tiện ích tin tức - Chuẩn theme Xanh lá sáng đồng bộ
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<div class="d-flex flex-column gap-4">
    <!-- 1. Tin xem nhiều nhất -->
    <div class="card border rounded-3 overflow-hidden shadow-sm">
        <div class="card-header bg-success text-white py-2.5 px-3 border-0">
            <h6 class="mb-0 fw-bold text-uppercase" style="letter-spacing: 0.5px; font-size: 0.88rem;">
                <i class="fas fa-fire me-2"></i>Tin xem nhiều nhất
            </h6>
        </div>
        <div class="list-group list-group-flush">
            <c:forEach var="news" items="${top5HotNews}" varStatus="status">
                <a href="${pageContext.request.contextPath}/detail?id=${news.id}"
                    class="list-group-item list-group-item-action p-3">
                    <div class="d-flex align-items-start gap-2">
                        <span class="badge bg-success-subtle text-success border border-success-subtle rounded-circle flex-shrink-0" style="width: 22px; height: 22px; display: inline-flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.75rem;">
                            ${status.count}
                        </span>
                        <div class="flex-grow-1">
                            <h6 class="mb-1 fw-semibold text-dark" style="font-size: 0.92rem; line-height: 1.4;">
                                ${news.title}
                            </h6>
                            <small class="text-muted">${news.viewCount} lượt xem</small>
                        </div>
                    </div>
                </a>
            </c:forEach>
            <c:if test="${empty top5HotNews}">
                <div class="list-group-item text-muted p-3">Chưa có bài viết nào.</div>
            </c:if>
        </div>
    </div>

    <!-- 2. Tin mới nhất -->
    <div class="card border rounded-3 overflow-hidden shadow-sm">
        <div class="card-header bg-success text-white py-2.5 px-3 border-0">
            <h6 class="mb-0 fw-bold text-uppercase" style="letter-spacing: 0.5px; font-size: 0.88rem;">
                <i class="fas fa-bolt me-2"></i>Tin mới nhất
            </h6>
        </div>
        <div class="list-group list-group-flush">
            <c:forEach var="news" items="${top5NewestNews}">
                <a href="${pageContext.request.contextPath}/detail?id=${news.id}"
                    class="list-group-item list-group-item-action p-3">
                    <h6 class="mb-1 fw-semibold text-dark" style="font-size: 0.92rem; line-height: 1.4;">
                        ${news.title}
                    </h6>
                    <small class="text-muted">
                        <fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy HH:mm" />
                    </small>
                </a>
            </c:forEach>
            <c:if test="${empty top5NewestNews}">
                <div class="list-group-item text-muted p-3">Chưa có bài viết nào.</div>
            </c:if>
        </div>
    </div>

    <!-- 3. Tin bạn đã xem gần đây -->
    <div class="card border rounded-3 overflow-hidden shadow-sm">
        <div class="card-header bg-success text-white py-2.5 px-3 border-0">
            <h6 class="mb-0 fw-bold text-uppercase" style="letter-spacing: 0.5px; font-size: 0.88rem;">
                <i class="fas fa-history me-2"></i>Đã xem gần đây
            </h6>
        </div>
        <div class="list-group list-group-flush">
            <c:forEach var="news" items="${viewedNews}">
                <a href="${pageContext.request.contextPath}/detail?id=${news.id}"
                    class="list-group-item list-group-item-action p-3">
                    <h6 class="mb-1 fw-semibold text-dark" style="font-size: 0.92rem; line-height: 1.4;">
                        ${news.title}
                    </h6>
                    <small class="text-muted">
                        <fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy" />
                    </small>
                </a>
            </c:forEach>
            <c:if test="${empty viewedNews}">
                <div class="list-group-item text-muted p-3">Bạn chưa xem bài viết nào.</div>
            </c:if>
        </div>
    </div>

    <!-- 4. Đăng ký nhận bản tin -->
    <div class="card border rounded-3 overflow-hidden shadow-sm">
        <div class="card-header bg-success text-white py-2.5 px-3 border-0">
            <h6 class="mb-0 fw-bold text-uppercase" style="letter-spacing: 0.5px; font-size: 0.88rem;">
                <i class="fas fa-envelope me-2"></i>Bản tin Email
            </h6>
        </div>
        <div class="card-body p-3">
            <p class="small text-secondary mb-3">
                Đăng ký để nhận tổng hợp các tin tức nóng hổi và phân tích chuyên sâu mỗi sáng.
            </p>

            <c:if test="${not empty sessionScope.newsletterMessage}">
                <div class="alert alert-success alert-dismissible fade show small py-2" role="alert">
                    ${sessionScope.newsletterMessage}
                    <button type="button" class="btn-close py-2" data-bs-dismiss="alert" aria-label="Đóng"></button>
                </div>
                <c:remove var="newsletterMessage" scope="session" />
            </c:if>
            
            <form action="${pageContext.request.contextPath}/newsletter" method="post">
                <div class="mb-2">
                    <input type="email" name="email" class="form-control"
                        placeholder="Nhập email của bạn..." required autocomplete="email">
                </div>
                <button type="submit" class="btn btn-success w-100 fw-semibold">
                    Đăng ký ngay
                </button>
            </form>
        </div>
    </div>
</div>