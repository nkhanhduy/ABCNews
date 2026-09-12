<%-- 
  File: dashboard_reporter.jsp
  Description: Dashboard tổng quan cho Phóng viên với thống kê tin tức của họ
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
            Đây là khu vực quản lý dành cho <strong>Phóng viên</strong>. 
            Tại đây bạn có thể xem thống kê về các bài viết của mình và quản lý tin tức một cách hiệu quả.
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
            <h4>Tổng số tin của tôi</h4>
            <p class="stat-number">${myTotalNews}</p>
        </div>
    </div>
    
    <div class="stat-card card stat-views">
        <div class="stat-icon">
            <i class="fas fa-eye"></i>
        </div>
        <div class="stat-content">
            <h4>Tổng lượt xem</h4>
            <p class="stat-number">${totalViews}</p>
        </div>
    </div>
    
    <div class="stat-card card stat-recent">
        <div class="stat-icon">
            <i class="fas fa-calendar-week"></i>
        </div>
        <div class="stat-content">
            <h4>Tin trong 7 ngày</h4>
            <p class="stat-number">${newsIn7Days}</p>
        </div>
    </div>
    
    <div class="stat-card card stat-home">
        <div class="stat-icon">
            <i class="fas fa-star"></i>
        </div>
        <div class="stat-content">
            <h4>Tin trên trang nhất</h4>
            <p class="stat-number">${newsOnHome}</p>
        </div>
    </div>
</div>

<!-- Card 6-7: Thống kê chi tiết -->
<div class="dashboard-details-grid">
    <!-- Card: Phân bổ tin theo loại của tôi -->
    <div class="detail-card card">
        <div class="card-header">
            <h3><i class="fas fa-chart-pie me-2"></i>Phân bổ Tin của Tôi theo Loại</h3>
        </div>
        <div class="card-body">
            <c:forEach var="cat" items="${categories}">
                <c:set var="newsCount" value="${myNewsCountByCategory[cat.id]}" />
                <c:if test="${newsCount == null}">
                    <c:set var="newsCount" value="0" />
                </c:if>
                <c:if test="${newsCount > 0}">
                    <div class="detail-item">
                        <div class="detail-label">${cat.name}</div>
                        <div class="detail-value">
                            <span class="badge bg-primary">${newsCount}</span>
                        </div>
                    </div>
                </c:if>
            </c:forEach>
            <c:if test="${myTotalNews == 0}">
                <p class="text-muted">Bạn chưa có tin tức nào.</p>
            </c:if>
        </div>
    </div>
    
    <!-- Card: Phân tích hiệu suất -->
    <div class="detail-card card">
        <div class="card-header">
            <h3><i class="fas fa-chart-line me-2"></i>Phân tích Hiệu suất</h3>
        </div>
        <div class="card-body">
            <div class="performance-item">
                <div class="performance-label">
                    <i class="fas fa-star me-2"></i>Tỷ lệ tin trên trang nhất
                </div>
                <div class="performance-value">
                    <div class="percentage-display">
                        <span class="percentage-number">
                            <fmt:formatNumber value="${homeNewsPercentage}" pattern="#0.0" />
                        </span>
                        <span class="percentage-symbol">%</span>
                    </div>
                    <div class="performance-detail">
                        ${newsOnHome} / ${myTotalNews} tin
                    </div>
                </div>
            </div>
            <c:if test="${myTotalNews == 0}">
                <p class="text-muted">Bạn chưa có tin tức nào để phân tích.</p>
            </c:if>
        </div>
    </div>
</div>

<!-- Card 8-9: Hoạt động gần đây -->
<div class="dashboard-activity-grid">
    <!-- Card: Top 5 tin xem nhiều nhất của tôi -->
    <div class="activity-card card">
        <div class="card-header">
            <h3><i class="fas fa-fire me-2"></i>Top 5 Tin Xem Nhiều Nhất của Tôi</h3>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty myTop5HotNews}">
                    <div class="activity-list">
                        <c:forEach var="news" items="${myTop5HotNews}" varStatus="status">
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
                    <p class="text-muted">Bạn chưa có tin tức nào.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <!-- Card: Tin mới nhất của tôi -->
    <div class="activity-card card">
        <div class="card-header">
            <h3><i class="fas fa-clock me-2"></i>Tin Mới Xuất Bản Của Tôi</h3>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty myRecentNews}">
                    <div class="activity-list">
                        <c:forEach var="news" items="${myRecentNews}">
                            <div class="activity-item">
                                <div class="activity-content">
                                    <a href="${pageContext.request.contextPath}/detail?id=${news.id}" 
                                       class="activity-title" target="_blank">
                                        ${news.title}
                                    </a>
                                    <div class="activity-meta">
                                        <span><i class="fas fa-eye me-1"></i>${news.viewCount} lượt xem</span>
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

.stat-views .stat-icon {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-recent .stat-icon {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-home .stat-icon {
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

/* Phân tích hiệu suất */
.performance-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 30px 20px;
    text-align: center;
}

.performance-label {
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    font-size: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.performance-value {
    width: 100%;
}

.percentage-display {
    display: flex;
    align-items: baseline;
    justify-content: center;
    gap: 5px;
    margin-bottom: 15px;
}

.percentage-number {
    font-size: 3rem;
    font-weight: 700;
    color: #667eea;
    line-height: 1;
}

.percentage-symbol {
    font-size: 1.5rem;
    font-weight: 600;
    color: #667eea;
}

.performance-detail {
    font-size: 0.9rem;
    color: #666;
    font-weight: 500;
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
}
</style>
