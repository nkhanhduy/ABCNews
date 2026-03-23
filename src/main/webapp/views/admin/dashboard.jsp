<%-- 
  File: dashboard.jsp
  Description: Dashboard tổng quan cho Admin với thống kê và hoạt động gần đây
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!-- Card 1: Giới thiệu -->
<div class="dashboard-intro-card card">
    <div class="card-header">
        <h3><i class="fas fa-tachometer-alt me-2"></i>Chào mừng đến với Trang Quản Trị</h3>
    </div>
    <div class="card-body">
        <p class="intro-text">
            Đây là khu vực quản lý dành cho <strong>Quản trị viên</strong>. 
            Tại đây bạn có thể quản lý tin tức, người dùng, loại tin và newsletter một cách hiệu quả.
        </p>
        <p class="intro-text">
            Sử dụng menu bên trái để truy cập các chức năng quản lý hoặc xem thống kê tổng quan bên dưới.
        </p>
    </div>
</div>

<!-- Card 2-5: Thống kê tổng quan (4 card) -->
<div class="dashboard-stats-grid">
    <div class="stat-card card stat-news">
        <div class="stat-icon">
            <i class="fas fa-newspaper"></i>
        </div>
        <div class="stat-content">
            <h4>Tổng số tin tức</h4>
            <p class="stat-number">${totalNews}</p>
        </div>
    </div>
    
    <div class="stat-card card stat-users">
        <div class="stat-icon">
            <i class="fas fa-users"></i>
        </div>
        <div class="stat-content">
            <h4>Tổng số người dùng</h4>
            <p class="stat-number">${totalUsers}</p>
        </div>
    </div>
    
    <div class="stat-card card stat-categories">
        <div class="stat-icon">
            <i class="fas fa-tags"></i>
        </div>
        <div class="stat-content">
            <h4>Tổng số loại tin</h4>
            <p class="stat-number">${totalCategories}</p>
        </div>
    </div>
    
    <div class="stat-card card stat-newsletters">
        <div class="stat-icon">
            <i class="fas fa-envelope"></i>
        </div>
        <div class="stat-content">
            <h4>Email đăng ký</h4>
            <p class="stat-number">${totalNewsletters}</p>
        </div>
    </div>
</div>

<!-- Card 6-7: Thống kê chi tiết -->
<div class="dashboard-details-grid">
    <!-- Card: Phân bổ người dùng -->
    <div class="detail-card card">
        <div class="card-header">
            <h3><i class="fas fa-user-friends me-2"></i>Phân bổ Người dùng</h3>
        </div>
        <div class="card-body">
            <div class="user-distribution-grid">
                <!-- Admin -->
                <div class="user-dist-item">
                    <div class="user-dist-icon">
                        <i class="fas fa-user-shield"></i>
                    </div>
                    <div class="user-dist-label">Quản trị viên</div>
                    <div class="user-dist-value">
                        <span class="badge bg-danger">${totalAdmins}</span>
                    </div>
                    <div class="user-dist-status">
                        <div class="status-item">
                            <i class="fas fa-check-circle" style="color: #28a745;"></i>
                            <span class="status-label">Hoạt động:</span>
                            <span class="status-value">${adminsEnabled}</span>
                        </div>
                        <div class="status-item">
                            <i class="fas fa-ban" style="color: #dc3545;"></i>
                            <span class="status-label">Bị khóa:</span>
                            <span class="status-value">${adminsDisabled}</span>
                        </div>
                    </div>
                </div>
                
                <!-- Reporter -->
                <div class="user-dist-item">
                    <div class="user-dist-icon">
                        <i class="fas fa-user-edit"></i>
                    </div>
                    <div class="user-dist-label">Phóng viên</div>
                    <div class="user-dist-value">
                        <span class="badge bg-info">${totalReporters}</span>
                    </div>
                    <div class="user-dist-status">
                        <div class="status-item">
                            <i class="fas fa-check-circle" style="color: #28a745;"></i>
                            <span class="status-label">Hoạt động:</span>
                            <span class="status-value">${reportersEnabled}</span>
                        </div>
                        <div class="status-item">
                            <i class="fas fa-ban" style="color: #dc3545;"></i>
                            <span class="status-label">Bị khóa:</span>
                            <span class="status-value">${reportersDisabled}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Card: Phân bổ tin theo loại -->
    <div class="detail-card card">
        <div class="card-header">
            <h3><i class="fas fa-chart-pie me-2"></i>Phân bổ Tin theo Loại</h3>
        </div>
        <div class="card-body">
            <c:forEach var="cat" items="${categories}">
                <c:set var="newsCount" value="${newsCountByCategory[cat.id]}" />
                <c:if test="${newsCount == null}">
                    <c:set var="newsCount" value="0" />
                </c:if>
                <div class="detail-item">
                    <div class="detail-label">${cat.name}</div>
                    <div class="detail-value">
                        <span class="badge bg-primary">${newsCount}</span>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty categories}">
                <p class="text-muted">Chưa có loại tin nào.</p>
            </c:if>
        </div>
    </div>
</div>

<!-- Card 8-9: Hoạt động gần đây -->
<div class="dashboard-activity-grid">
    <!-- Card: Tin xem nhiều nhất -->
    <div class="activity-card card">
        <div class="card-header">
            <h3><i class="fas fa-fire me-2"></i>Top 5 Tin Xem Nhiều Nhất</h3>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty top5HotNews}">
                    <div class="activity-list">
                        <c:forEach var="news" items="${top5HotNews}" varStatus="status">
                            <div class="activity-item">
                                <div class="activity-rank">#${status.index + 1}</div>
                                <div class="activity-content">
                                    <a href="${pageContext.request.contextPath}/detail?id=${news.id}" 
                                       class="activity-title" target="_blank">
                                        ${news.title}
                                    </a>
                                    <div class="activity-meta">
                                        <span><i class="fas fa-eye me-1"></i>${news.viewCount} lượt xem</span>
                                        <span><i class="fas fa-calendar me-1"></i>
                                            <fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy"/>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-muted">Chưa có tin tức nào.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <!-- Card: Tin mới nhất (7 ngày qua) -->
    <div class="activity-card card">
        <div class="card-header">
            <h3><i class="fas fa-clock me-2"></i>Tin Mới Nhất (7 ngày qua)</h3>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty recentNews}">
                    <div class="activity-list">
                        <c:forEach var="news" items="${recentNews}">
                            <div class="activity-item">
                                <div class="activity-content">
                                    <a href="${pageContext.request.contextPath}/detail?id=${news.id}" 
                                       class="activity-title" target="_blank">
                                        ${news.title}
                                    </a>
                                    <div class="activity-meta">
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
                                        <span><i class="fas fa-calendar me-1"></i>
                                            <fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy HH:mm"/>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-muted">Không có tin tức mới trong 7 ngày qua.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<style>
/* Card giới thiệu */
.dashboard-intro-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 30px;
    overflow: hidden;
}

.dashboard-intro-card .card-header {
    background: rgba(255, 255, 255, 0.1);
    padding: 20px 25px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.dashboard-intro-card .card-header h3 {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.dashboard-intro-card .card-body {
    padding: 25px;
}

.dashboard-intro-card .intro-text {
    margin: 0 0 15px 0;
    line-height: 1.8;
    font-size: 1.05rem;
}

.dashboard-intro-card .intro-text:last-child {
    margin-bottom: 0;
}

/* Grid thống kê tổng quan */
.dashboard-stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.stat-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    gap: 15px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.8rem;
    color: #fff;
    flex-shrink: 0;
}

.stat-news .stat-icon {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-users .stat-icon {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-categories .stat-icon {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-newsletters .stat-icon {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
    flex: 1;
    width: 100%;
}

.stat-content h4 {
    margin: 0 0 8px 0;
    font-size: 0.9rem;
    color: #666;
    font-weight: 500;
    text-align: center;
}

.stat-number {
    margin: 0;
    font-size: 2rem;
    font-weight: 700;
    color: #333;
    text-align: center;
}

/* Grid thống kê chi tiết */
.dashboard-details-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.detail-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

.detail-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.detail-card .card-header h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.detail-card .card-body {
    padding: 20px 25px;
}

/* Điều chỉnh padding cho card Phân bổ Người dùng */
.detail-card:has(.user-distribution-grid) .card-body {
    padding: 30px 25px 25px 25px;
}

.detail-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
    border-bottom: none;
}

.detail-label {
    font-weight: 500;
    color: #333;
    display: flex;
    align-items: center;
}

.detail-value .badge {
    padding: 6px 12px;
    font-size: 0.875rem;
    font-weight: 600;
}

/* Phân bổ người dùng - layout 2 cột */
.user-distribution-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    width: 100%;
}

.user-dist-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 25px 20px;
    background: #f8f9fa;
    border-radius: 8px;
    transition: background 0.3s ease;
    width: 100%;
    box-sizing: border-box;
}

.user-dist-item:hover {
    background: #e9ecef;
}

.user-dist-icon {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    color: #fff;
    margin-bottom: 12px;
    flex-shrink: 0;
}

.user-dist-item:first-child .user-dist-icon {
    background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
}

.user-dist-item:last-child .user-dist-icon {
    background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
}

.user-dist-label {
    font-weight: 600;
    color: #333;
    margin-bottom: 10px;
    font-size: 0.95rem;
    width: 100%;
}

.user-dist-value {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.user-dist-value .badge {
    padding: 8px 16px;
    font-size: 1rem;
    font-weight: 700;
}

.user-dist-status {
    margin-top: 15px;
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding-top: 15px;
    border-top: 1px solid #e9ecef;
}

.status-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
    font-size: 0.9rem;
}

.status-item i {
    font-size: 0.85rem;
    width: 16px;
    text-align: center;
}

.status-label {
    flex: 1;
    color: #6c757d;
    text-align: left;
}

.status-value {
    font-weight: 600;
    color: #333;
    min-width: 30px;
    text-align: right;
}

/* Grid hoạt động gần đây */
.dashboard-activity-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 20px;
}

.activity-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

.activity-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.activity-card .card-header h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.activity-card .card-body {
    padding: 20px 25px;
    max-height: 500px;
    overflow-y: auto;
}

.activity-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.activity-item {
    display: flex;
    gap: 15px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 8px;
    transition: background 0.3s ease;
}

.activity-item:hover {
    background: #e9ecef;
}

.activity-rank {
    width: 35px;
    height: 35px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 0.9rem;
    flex-shrink: 0;
}

.activity-content {
    flex: 1;
}

.activity-title {
    display: block;
    font-weight: 600;
    color: #333;
    text-decoration: none;
    margin-bottom: 8px;
    transition: color 0.3s ease;
}

.activity-title:hover {
    color: #667eea;
}

.activity-meta {
    display: flex;
    gap: 15px;
    font-size: 0.85rem;
    color: #666;
}

.activity-meta span {
    display: flex;
    align-items: center;
}

/* Responsive */
@media (max-width: 768px) {
    .dashboard-stats-grid {
        grid-template-columns: 1fr;
    }
    
    .dashboard-details-grid {
        grid-template-columns: 1fr;
    }
    
    .dashboard-activity-grid {
        grid-template-columns: 1fr;
    }
    
    .export-grid {
        grid-template-columns: 1fr;
    }
}
</style>
