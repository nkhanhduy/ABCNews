<%-- 
  File: _sidebar.jsp
  Description: Sidebar với Bootstrap 5
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<div class="d-flex flex-column gap-3">
	<!-- Tin xem nhiều nhất -->
	<div class="card shadow-sm">
		<div class="card-header bg-success text-white">
			<h5 class="mb-0">
				<i class="fas fa-fire me-2"></i>Tin xem nhiều nhất
			</h5>
		</div>
		<div class="list-group list-group-flush">
			<c:forEach var="news" items="${top5HotNews}">
				<a href="${pageContext.request.contextPath}/detail?id=${news.id}"
					class="list-group-item list-group-item-action">
					<div class="d-flex w-100 justify-content-between">
						<small class="text-muted">${news.title}</small>
					</div> <small class="text-muted"><i class="fas fa-eye me-1"></i>${news.viewCount}
						lượt xem</small>
				</a>
			</c:forEach>
			<c:if test="${empty top5HotNews}">
				<div class="list-group-item text-muted">Chưa có tin nào.</div>
			</c:if>
		</div>
	</div>

	<!-- Tin mới nhất -->
	<div class="card shadow-sm">
		<div class="card-header bg-success text-white">
			<h5 class="mb-0">
				<i class="fas fa-clock me-2"></i>Tin mới nhất
			</h5>
		</div>
		<div class="list-group list-group-flush">
			<c:forEach var="news" items="${top5NewestNews}">
				<a href="${pageContext.request.contextPath}/detail?id=${news.id}"
					class="list-group-item list-group-item-action">
					<div class="d-flex w-100 justify-content-between">
						<small class="text-muted">${news.title}</small>
					</div> <small class="text-muted"> <i class="fas fa-calendar me-1"></i>
						<fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy" />
				</small>
				</a>
			</c:forEach>
			<c:if test="${empty top5NewestNews}">
				<div class="list-group-item text-muted">Chưa có tin nào.</div>
			</c:if>
		</div>
	</div>
	<!-- Tin bạn đã xem -->
	<div class="card shadow-sm">
		<div class="card-header bg-success text-white">
			<h5 class="mb-0">
				<i class="fas fa-history me-2"></i>Tin bạn đã xem
			</h5>
		</div>
		<div class="list-group list-group-flush">
			<c:forEach var="news" items="${viewedNews}">
				<a href="${pageContext.request.contextPath}/detail?id=${news.id}"
					class="list-group-item list-group-item-action">
					<div class="d-flex w-100 justify-content-between">
						<small class="text-muted">${news.title}</small>
					</div>
					<small class="text-muted">
						<i class="fas fa-clock me-1"></i>
						<fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy" />
					</small>
				</a>
			</c:forEach>
			<c:if test="${empty viewedNews}">
				<div class="list-group-item text-muted">Bạn chưa xem bản tin nào.</div>
			</c:if>
		</div>
	</div>

	<!-- Đăng ký nhận tin -->
	<div class="card shadow-sm">
		<div class="card-header bg-success text-white">
			<h5 class="mb-0">
				<i class="fas fa-envelope me-2"></i>Nhận bản tin
			</h5>
		</div>
		<div class="card-body">
			<%-- Hiển thị thông báo từ session --%>
			<c:if test="${not empty sessionScope.newsletterMessage}">
				<div class="alert alert-success alert-dismissible fade show" role="alert">
					<i class="fas fa-check-circle me-2"></i>${sessionScope.newsletterMessage}
					<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
				</div>
				<%-- Xóa message sau khi hiển thị --%>
				<c:remove var="newsletterMessage" scope="session" />
			</c:if>
			
			<form action="${pageContext.request.contextPath}/newsletter"
				method="post">
				<div class="mb-3">
					<input type="email" name="email" class="form-control"
						placeholder="Nhập email của bạn..." required>
				</div>
				<button type="submit" class="btn btn-success w-100">
					<i class="fas fa-paper-plane me-1"></i>Đăng ký
				</button>
			</form>
		</div>
	</div>

</div>