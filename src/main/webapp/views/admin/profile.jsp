<%-- 
  File: profile.jsp
  Description: Trang hiển thị thông tin profile của user
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<div class="profile-container">
    <div class="profile-header">
        <h2>
            <i class="fas fa-user-circle me-2"></i>
            Thông tin ${profileUser.role ? 'Quản trị viên' : 'Phóng viên'}
        </h2>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">
            <i class="fas fa-arrow-left me-1"></i>Quay lại
        </a>
    </div>
    
    <div class="profile-content">
        <!-- Ảnh đại diện ở giữa phía trên -->
        <div class="profile-avatar-section">
            <div class="profile-avatar">
                <c:choose>
                    <c:when test="${not empty profileUser.imagePath}">
                        <c:set var="userImageUrl" value="${profileUser.imagePath}" />
                        <c:if test="${!fn:startsWith(userImageUrl, pageContext.request.contextPath) && fn:startsWith(userImageUrl, '/')}">
                            <c:set var="userImageUrl" value="${pageContext.request.contextPath}${profileUser.imagePath}" />
                        </c:if>
                        <img src="${userImageUrl}" 
                             alt="${profileUser.fullname}" 
                             class="avatar-img"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                    </c:when>
                    <c:otherwise>
                        <div class="avatar-placeholder">
                            <i class="fas fa-user fa-5x"></i>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="profile-basic-info">
                <h3 class="profile-name">${profileUser.fullname}</h3>
                <div class="profile-badges">
                    <span class="badge ${profileUser.role ? 'bg-danger' : 'bg-info'}">
                        <i class="fas fa-${profileUser.role ? 'shield-alt' : 'pen'} me-1"></i>
                        ${profileUser.role ? 'Quản trị viên' : 'Phóng viên'}
                    </span>
                    <span class="badge ${profileUser.enabled ? 'bg-success' : 'bg-danger'}">
                        <i class="fas fa-${profileUser.enabled ? 'check-circle' : 'ban'} me-1"></i>
                        ${profileUser.enabled ? 'Hoạt động' : 'Bị khóa'}
                    </span>
                </div>
                
                <c:if test="${!profileUser.role}">
                    <div class="profile-stat">
                        <i class="fas fa-newspaper me-2"></i>
                        <strong>${newsCount}</strong> bài viết đã đăng
                    </div>
                </c:if>
            </div>
        </div>
        
        <!-- 2 Card chứa 6 thông tin chi tiết (mỗi card 3 thông tin) -->
        <div class="row g-4 mt-3">
            <!-- Card 1: 3 thông tin đầu tiên -->
            <div class="col-md-6">
                <div class="info-card">
                    <h5 class="card-title">
                        <i class="fas fa-info-circle me-2"></i>Thông tin cơ bản
                    </h5>
                    <div class="details-list">
                        <div class="detail-item">
                            <div class="detail-label">
                                <i class="fas fa-id-card me-2"></i>Mã người dùng:
                            </div>
                            <div class="detail-value">${profileUser.id}</div>
                        </div>
                        
                        <div class="detail-item">
                            <div class="detail-label">
                                <i class="fas fa-envelope me-2"></i>Email:
                            </div>
                            <div class="detail-value">
                                <a href="mailto:${profileUser.email}">${profileUser.email}</a>
                            </div>
                        </div>
                        
                        <c:choose>
                            <c:when test="${not empty profileUser.mobile}">
                                <div class="detail-item">
                                    <div class="detail-label">
                                        <i class="fas fa-phone me-2"></i>Số điện thoại:
                                    </div>
                                    <div class="detail-value">
                                        <a href="tel:${profileUser.mobile}">${profileUser.mobile}</a>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="detail-item">
                                    <div class="detail-label">
                                        <i class="fas fa-key me-2"></i>Mật khẩu:
                                    </div>
                                    <div class="detail-value">
                                        <span class="text-muted">••••••••</span>
                                        <small class="text-muted ms-2">(Đã được mã hóa)</small>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- Card 2: 3 thông tin còn lại -->
            <div class="col-md-6">
                <div class="info-card">
                    <h5 class="card-title">
                        <i class="fas fa-user-tag me-2"></i>Thông tin cá nhân
                    </h5>
                    <div class="details-list">
                        <c:if test="${not empty profileUser.birthday}">
                            <div class="detail-item">
                                <div class="detail-label">
                                    <i class="fas fa-birthday-cake me-2"></i>Ngày sinh:
                                </div>
                                <div class="detail-value">
                                    <fmt:formatDate value="${profileUser.birthday}" pattern="dd/MM/yyyy" />
                                </div>
                            </div>
                        </c:if>
                        
                        <div class="detail-item">
                            <div class="detail-label">
                                <i class="fas fa-venus-mars me-2"></i>Giới tính:
                            </div>
                            <div class="detail-value">
                                ${profileUser.gender ? 'Nam' : 'Nữ'}
                            </div>
                        </div>
                        
                        <c:choose>
                            <c:when test="${not empty profileUser.mobile}">
                                <div class="detail-item">
                                    <div class="detail-label">
                                        <i class="fas fa-key me-2"></i>Mật khẩu:
                                    </div>
                                    <div class="detail-value">
                                        <span class="text-muted">••••••••</span>
                                        <small class="text-muted ms-2">(Đã được mã hóa)</small>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="detail-item">
                                    <div class="detail-label">
                                        <i class="fas fa-phone me-2"></i>Số điện thoại:
                                    </div>
                                    <div class="detail-value">
                                        <span class="text-muted">Chưa cập nhật</span>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Nút hành động -->
        <div class="profile-actions mt-4">
            <!-- Hiển thị nút chỉnh sửa dựa trên quyền -->
            <c:choose>
                <c:when test="${canEdit}">
                    <!-- Có quyền sửa: hiển thị nút active -->
                    <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${profileUser.id}" 
                       class="btn btn-edit-profile">
                        <i class="fas fa-edit me-1"></i>Chỉnh sửa thông tin
                    </a>
                </c:when>
                <c:otherwise>
                    <!-- Không có quyền sửa: hiển thị nút disabled -->
                    <button type="button" class="btn btn-edit-profile-disabled" disabled>
                        <i class="fas fa-lock me-1"></i>Chỉnh sửa thông tin
                    </button>
                </c:otherwise>
            </c:choose>
            
            <c:if test="${!profileUser.role}">
                <a href="${pageContext.request.contextPath}/admin/news?filterAuthor=${profileUser.id}" 
                   class="btn btn-view-news">
                    <i class="fas fa-newspaper me-1"></i>Xem bài viết
                </a>
            </c:if>
        </div>
    </div>
</div>

<style>
.profile-container {
    padding: 20px;
}

.profile-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding-bottom: 15px;
    border-bottom: 2px solid #e9ecef;
}

.profile-header h2 {
    margin: 0;
    color: #343a40;
    font-weight: 600;
}

.profile-content {
    background: #fff;
    border-radius: 8px;
    padding: 30px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* Ảnh đại diện ở giữa phía trên */
.profile-avatar-section {
    text-align: center;
    margin-bottom: 40px;
    padding-bottom: 30px;
    border-bottom: 2px solid #e9ecef;
}

.profile-avatar {
    margin-bottom: 20px;
    display: flex;
    justify-content: center;
}

.avatar-img {
    width: 180px;
    height: 180px;
    border-radius: 50%;
    object-fit: cover;
    border: 5px solid #007bff;
    box-shadow: 0 6px 12px rgba(0,0,0,0.15);
}

.avatar-placeholder {
    width: 180px;
    height: 180px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    border: 5px solid #007bff;
    box-shadow: 0 6px 12px rgba(0,0,0,0.15);
}

.profile-basic-info {
    margin-top: 20px;
}

.profile-name {
    font-size: 28px;
    font-weight: 600;
    color: #343a40;
    margin-bottom: 12px;
}

.profile-badges {
    display: flex;
    gap: 10px;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    margin-bottom: 15px;
}

.profile-badges .badge {
    padding: 8px 16px;
    font-size: 0.95rem;
    font-weight: 600;
    border-radius: 6px;
    display: inline-flex;
    align-items: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.profile-stat {
    margin-top: 15px;
    padding: 12px 20px;
    background: #f8f9fa;
    border-radius: 8px;
    color: #495057;
    font-size: 15px;
    display: inline-block;
}

/* Card chứa thông tin */
.info-card {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 25px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.08);
    height: 100%;
    border: 1px solid #e9ecef;
    transition: all 0.3s ease;
}

.info-card:hover {
    box-shadow: 0 4px 8px rgba(0,0,0,0.12);
    transform: translateY(-2px);
}

.info-card .card-title {
    color: #343a40;
    font-weight: 600;
    font-size: 18px;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid #007bff;
}

.details-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.detail-item {
    display: flex;
    flex-direction: column;
    padding: 15px;
    background: #fff;
    border-radius: 6px;
    border-left: 4px solid #007bff;
    transition: all 0.2s ease;
}

.detail-item:hover {
    background: #f8f9fa;
    border-left-color: #0056b3;
}

.detail-label {
    font-weight: 600;
    color: #495057;
    margin-bottom: 8px;
    font-size: 13px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.detail-value {
    color: #212529;
    font-size: 16px;
    font-weight: 500;
}

.detail-value a {
    color: #007bff;
    text-decoration: none;
    transition: color 0.2s ease;
}

.detail-value a:hover {
    color: #0056b3;
    text-decoration: underline;
}

.profile-actions {
    display: flex;
    justify-content: center;
    gap: 15px;
    flex-wrap: wrap;
    margin-top: 30px;
    padding-top: 30px;
    border-top: 2px solid #e9ecef;
}

.profile-actions .btn {
    padding: 12px 24px;
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s ease;
    min-width: 180px;
    text-decoration: none;
    display: inline-block;
    border: none;
    cursor: pointer;
}

/* Nút Sửa thông tin - màu xanh lá (success) để phân biệt với nền */
.btn-edit-profile {
    background-color: #28a745 !important;
    color: #fff !important;
    border: 2px solid #28a745 !important;
}

.btn-edit-profile:hover {
    background-color: #218838 !important;
    border-color: #1e7e34 !important;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
}

/* Nút Sửa thông tin bị khóa (cho phóng viên) */
.btn-edit-profile-disabled {
    background-color: #6c757d !important;
    color: #fff !important;
    border: 2px solid #6c757d !important;
    cursor: not-allowed !important;
    opacity: 0.6;
}

.btn-edit-profile-disabled:hover {
    transform: none;
    box-shadow: none;
}

/* Nút Xem bài viết - màu cam/warning để phân biệt */
.btn-view-news {
    background-color: #ff9800 !important;
    color: #fff !important;
    border: 2px solid #ff9800 !important;
}

.btn-view-news:hover {
    background-color: #f57c00 !important;
    border-color: #ef6c00 !important;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(255, 152, 0, 0.3);
}

@media (max-width: 768px) {
    .profile-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 15px;
    }
    
    .profile-content {
        padding: 20px;
    }
    
    .avatar-img,
    .avatar-placeholder {
        width: 120px;
        height: 120px;
    }
    
    .profile-name {
        font-size: 22px;
    }
    
    .info-card {
        margin-bottom: 20px;
    }
    
    .profile-actions .btn {
        min-width: 100%;
    }
}
</style>
