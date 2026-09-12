<%-- 
  File: profile.jsp
  Description: Trang hiển thị thông tin và đổi ảnh đại diện thông minh của user
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
        <!-- Ảnh đại diện thông minh ở giữa phía trên -->
        <div class="profile-avatar-section">
            <div class="profile-avatar-wrapper">
                <div class="profile-avatar-container" style="position: relative; width: 160px; height: 160px; margin: 0 auto;">
                    <div class="profile-avatar" style="width: 160px; height: 160px; border-radius: 50%; overflow: hidden; border: 4px solid #16a34a; box-shadow: 0 8px 24px rgba(22, 163, 74, 0.2); background-color: #f1f5f9; display: flex; align-items: center; justify-content: center;">
                        <c:choose>
                            <c:when test="${not empty profileUser.imagePath}">
                                <c:set var="userImageUrl" value="${profileUser.imagePath}" />
                                <c:if test="${!fn:startsWith(userImageUrl, pageContext.request.contextPath) && fn:startsWith(userImageUrl, '/')}">
                                    <c:set var="userImageUrl" value="${pageContext.request.contextPath}${profileUser.imagePath}" />
                                </c:if>
                                <img id="avatarPreviewImg"
                                     src="${userImageUrl}" 
                                     data-original-src="${userImageUrl}"
                                     alt="${profileUser.fullname}" 
                                     class="avatar-img"
                                     style="width: 160px !important; height: 160px !important; max-width: 160px !important; max-height: 160px !important; border-radius: 50% !important; object-fit: cover !important; object-position: center 15% !important; display: block !important; margin: 0 auto !important;"
                                     onerror="this.style.setProperty('display', 'none', 'important'); var ph = document.getElementById('avatarPlaceholder'); if(ph) { ph.style.setProperty('display', 'flex', 'important'); ph.classList.remove('d-none'); }">
                                <div id="avatarPlaceholder" class="avatar-placeholder d-none" style="width: 160px; height: 160px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #15803d 0%, #16a34a 100%); color: #ffffff;">
                                    <i class="fas fa-user fa-5x"></i>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <img id="avatarPreviewImg"
                                     src="" 
                                     data-original-src=""
                                     alt="${profileUser.fullname}" 
                                     class="avatar-img"
                                     style="display: none !important; width: 160px !important; height: 160px !important; max-width: 160px !important; max-height: 160px !important; border-radius: 50% !important; object-fit: cover !important; object-position: center 15% !important;">
                                <div id="avatarPlaceholder" class="avatar-placeholder" style="width: 160px; height: 160px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #15803d 0%, #16a34a 100%); color: #ffffff;">
                                    <i class="fas fa-user fa-5x"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <%-- Nút overlay đổi ảnh đại diện (nếu có quyền sửa) --%>
                    <c:if test="${canEdit}">
                        <button type="button" class="avatar-upload-trigger" id="btnTriggerUpload" title="Thay đổi ảnh đại diện">
                            <i class="fas fa-camera"></i>
                            <span class="upload-tooltip">Đổi ảnh</span>
                        </button>
                    </c:if>
                </div>

                <%-- Form upload ảnh đại diện thông minh (POST Multipart) --%>
                <c:if test="${canEdit}">
                    <form id="avatarUploadForm" 
                          action="${pageContext.request.contextPath}/admin/profile?_csrf=${sessionScope.CSRF_TOKEN}" 
                          method="post" 
                          enctype="multipart/form-data" 
                          class="avatar-upload-form"
                          style="display: none;">
                        <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                        <input type="hidden" name="targetUserId" value="${profileUser.id}">
                        <input type="file" 
                               id="avatarFileInput" 
                               name="avatarFile" 
                               accept="image/png,image/jpeg,image/webp,image/gif" 
                               style="display: none;">
                    </form>

                    <!-- Modal Căn chỉnh ảnh đại diện trước khi lưu -->
                    <div class="modal fade" id="avatarCropModal" tabindex="-1" aria-labelledby="avatarCropModalLabel" aria-hidden="true" data-bs-backdrop="static">
                        <div class="modal-dialog modal-dialog-centered" style="max-width: 440px;">
                            <div class="modal-content border-0 shadow-lg" style="border-radius: 14px; overflow: hidden; background-color: var(--brand-card-bg, #ffffff);">
                                <div class="modal-header py-3 px-4" style="background: linear-gradient(135deg, #15803d 0%, #16a34a 100%); color: #ffffff;">
                                    <h5 class="modal-title fs-6 fw-bold mb-0" id="avatarCropModalLabel">
                                        <i class="fas fa-crop-alt me-2"></i>Căn chỉnh ảnh đại diện
                                    </h5>
                                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body p-4 text-center">
                                    <p class="small text-muted mb-3 text-start">
                                        <i class="fas fa-info-circle text-success me-1"></i>
                                        <strong>Hướng dẫn:</strong> Kéo ảnh để căn khuôn mặt vào giữa hình tròn. Dùng thanh trượt hoặc con lăn chuột để thu nhỏ/phóng to giúp lấy trọn vẹn đỉnh đầu.
                                    </p>

                                    <!-- Vùng canvas căn chỉnh với lớp phủ hình tròn -->
                                    <div class="cropper-stage-wrapper">
                                        <div class="cropper-stage" id="cropperStage">
                                            <canvas id="cropCanvas" width="300" height="300"></canvas>
                                            <div class="cropper-circle-overlay"></div>
                                        </div>
                                    </div>

                                    <!-- Thanh công cụ Zoom & Căn nhanh -->
                                    <div class="cropper-controls mt-3">
                                        <div class="d-flex align-items-center justify-content-between gap-2 mb-2">
                                            <button type="button" class="btn btn-sm btn-zoom px-2" id="btnZoomOut" title="Thu nhỏ">
                                                <i class="fas fa-search-minus"></i>
                                            </button>
                                            <input type="range" class="form-range flex-grow-1" id="zoomSlider" min="0.3" max="3.0" step="0.05" value="1.0">
                                            <button type="button" class="btn btn-sm btn-zoom px-2" id="btnZoomIn" title="Phóng to">
                                                <i class="fas fa-search-plus"></i>
                                            </button>
                                        </div>

                                        <div class="d-flex justify-content-center gap-2 mt-2 flex-wrap">
                                            <button type="button" class="btn btn-sm btn-tool py-1 px-2" id="btnFitFrame" title="Thu nhỏ để ảnh vừa vặn khung tròn">
                                                <i class="fas fa-compress-arrows-alt me-1 text-primary"></i>Vừa khung
                                            </button>
                                            <button type="button" class="btn btn-sm btn-tool py-1 px-2" id="btnFocusHead" title="Căn góc chụp lấy trọn đỉnh đầu và khuôn mặt">
                                                <i class="fas fa-user-tie me-1 text-success"></i>Căn đỉnh đầu
                                            </button>
                                            <button type="button" class="btn btn-sm btn-tool py-1 px-2" id="btnResetCrop" title="Đặt lại vị trí ban đầu">
                                                <i class="fas fa-redo-alt me-1 text-muted"></i>Đặt lại
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer bg-light py-2 px-4 border-top d-flex justify-content-between align-items-center">
                                    <button type="button" class="btn btn-sm btn-cancel-crop px-3" data-bs-dismiss="modal">
                                        <i class="fas fa-times me-1"></i>Hủy
                                    </button>
                                    <button type="button" class="btn btn-sm btn-apply-crop px-3 fw-bold" id="btnApplyCrop">
                                        <i class="fas fa-check me-1"></i>Áp dụng & Lưu ảnh
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>
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
        <div class="row g-4 mt-2">
            <!-- Card 1: Thông tin cơ bản -->
            <div class="col-md-6">
                <div class="info-card">
                    <h5 class="card-title">
                        <i class="fas fa-id-card-alt me-2"></i>Thông tin cơ bản
                    </h5>
                    <div class="details-list">
                        <div class="detail-item">
                            <div class="detail-label">
                                <i class="fas fa-hashtag me-2"></i>Mã người dùng
                            </div>
                            <div class="detail-value">${profileUser.id}</div>
                        </div>
                        
                        <div class="detail-item">
                            <div class="detail-label">
                                <i class="fas fa-envelope me-2"></i>Địa chỉ Email
                            </div>
                            <div class="detail-value">
                                <a href="mailto:${profileUser.email}">${profileUser.email}</a>
                            </div>
                        </div>
                        
                        <c:choose>
                            <c:when test="${not empty profileUser.mobile}">
                                <div class="detail-item">
                                    <div class="detail-label">
                                        <i class="fas fa-phone me-2"></i>Số điện thoại
                                    </div>
                                    <div class="detail-value">
                                        <a href="tel:${profileUser.mobile}">${profileUser.mobile}</a>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="detail-item">
                                    <div class="detail-label">
                                        <i class="fas fa-phone me-2"></i>Số điện thoại
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
            
            <!-- Card 2: Thông tin cá nhân -->
            <div class="col-md-6">
                <div class="info-card">
                    <h5 class="card-title">
                        <i class="fas fa-user-tag me-2"></i>Thông tin cá nhân
                    </h5>
                    <div class="details-list">
                        <div class="detail-item">
                            <div class="detail-label">
                                <i class="fas fa-birthday-cake me-2"></i>Ngày sinh
                            </div>
                            <div class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty profileUser.birthday}">
                                        <fmt:formatDate value="${profileUser.birthday}" pattern="dd/MM/yyyy" />
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">Chưa cập nhật</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        
                        <div class="detail-item">
                            <div class="detail-label">
                                <i class="fas fa-venus-mars me-2"></i>Giới tính
                            </div>
                            <div class="detail-value">
                                ${profileUser.gender ? 'Nam' : 'Nữ'}
                            </div>
                        </div>
                        
                        <div class="detail-item">
                            <div class="detail-label">
                                <i class="fas fa-shield-alt me-2"></i>Trạng thái tài khoản
                            </div>
                            <div class="detail-value">
                                <c:choose>
                                    <c:when test="${profileUser.enabled}">
                                        <span class="text-success font-weight-bold">Đang hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-danger font-weight-bold">Bị khóa</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%-- Card Đổi mật khẩu dành riêng cho tài khoản đang đăng nhập --%>
        <c:if test="${sessionScope.user.id == profileUser.id}">
            <div class="row g-4 mt-1">
                <div class="col-12">
                    <div class="info-card password-card">
                        <h5 class="card-title">
                            <i class="fas fa-key me-2"></i>Đổi mật khẩu
                        </h5>
                        <form action="${pageContext.request.contextPath}/admin/profile/change-password" 
                              method="post" 
                              class="change-password-form">
                            <input type="hidden" name="_csrf" value="${sessionScope.CSRF_TOKEN}">
                            
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <div class="form-group mb-0">
                                        <label for="currentPassword" class="form-label">
                                            <i class="fas fa-lock-open me-1"></i>Mật khẩu hiện tại
                                        </label>
                                        <input type="password" 
                                               class="form-control" 
                                               id="currentPassword" 
                                               name="currentPassword" 
                                               required 
                                               autocomplete="current-password"
                                               placeholder="Nhập mật khẩu hiện tại">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group mb-0">
                                        <label for="newPassword" class="form-label">
                                            <i class="fas fa-lock me-1"></i>Mật khẩu mới
                                        </label>
                                        <input type="password" 
                                               class="form-control" 
                                               id="newPassword" 
                                               name="newPassword" 
                                               required 
                                               minlength="8" 
                                               autocomplete="new-password"
                                               placeholder="Tối thiểu 8 ký tự">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group mb-0">
                                        <label for="confirmPassword" class="form-label">
                                            <i class="fas fa-check-circle me-1"></i>Xác nhận mật khẩu mới
                                        </label>
                                        <input type="password" 
                                               class="form-control" 
                                               id="confirmPassword" 
                                               name="confirmPassword" 
                                               required 
                                               minlength="8" 
                                               autocomplete="new-password"
                                               placeholder="Nhập lại mật khẩu mới">
                                    </div>
                                </div>
                            </div>
                            
                            <div class="d-flex justify-content-between align-items-center mt-3 pt-2">
                                <small class="text-muted">
                                    <i class="fas fa-info-circle me-1"></i>Mật khẩu mới tối thiểu 8 ký tự và phải khác mật khẩu hiện tại.
                                </small>
                                <button type="submit" class="btn btn-change-password">
                                    <i class="fas fa-shield-alt me-1"></i>Cập nhật mật khẩu
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </c:if>
        
        <!-- Nút hành động -->
        <div class="profile-actions mt-4">
            <%-- Chỉ Quản trị viên có quyền mới thấy nút chỉnh sửa tại Quản lý User, ẩn hoàn toàn với Phóng viên --%>
            <c:if test="${canManageUser}">
                <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${profileUser.id}" 
                   class="btn btn-edit-profile">
                    <i class="fas fa-user-cog me-1"></i>Quản lý tài khoản
                </a>
            </c:if>
            
            <c:if test="${!profileUser.role}">
                <a href="${pageContext.request.contextPath}/admin/news?filterAuthor=${profileUser.id}" 
                   class="btn btn-view-news">
                    <i class="fas fa-newspaper me-1"></i>Xem các bài viết đã đăng
                </a>
            </c:if>
        </div>
    </div>
</div>

<style>
/* ==========================================================================
   CSS Profile & Upload Avatar Thông Minh (Hỗ trợ Light & Dark Mode)
   ========================================================================== */
.profile-container {
    padding: 10px 0;
}

.profile-alert {
    border-radius: 8px;
    font-weight: 500;
    margin-bottom: 20px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.profile-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 2px solid var(--brand-border, #e2e8f0);
}

.profile-header h2 {
    margin: 0;
    color: var(--brand-text, #0f172a);
    font-size: 1.6rem;
    font-weight: 700;
}

.profile-content {
    background: var(--brand-card-bg, #ffffff);
    border: 1px solid var(--brand-border, #e2e8f0);
    border-radius: 12px;
    padding: 32px;
    box-shadow: 0 4px 16px rgba(0,0,0,0.04);
    transition: background-color 0.25s ease, border-color 0.25s ease;
}

/* Khu vực Avatar */
.profile-avatar-section {
    text-align: center;
    margin-bottom: 30px;
    padding-bottom: 25px;
    border-bottom: 1px solid var(--brand-border, #e2e8f0);
}

.profile-avatar-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 14px;
}

.profile-avatar-container {
    position: relative;
    width: 160px;
    height: 160px;
    margin: 0 auto;
}

.profile-avatar {
    position: relative;
    width: 160px;
    height: 160px;
    border-radius: 50%;
    overflow: hidden;
    margin: 0 auto;
    border: 4px solid var(--brand-accent, #16a34a);
    box-shadow: 0 8px 24px rgba(22, 163, 74, 0.2);
    background-color: #f1f5f9;
    display: flex;
    align-items: center;
    justify-content: center;
}

.avatar-img {
    width: 160px !important;
    height: 160px !important;
    max-width: 160px !important;
    max-height: 160px !important;
    border-radius: 50% !important;
    object-fit: cover !important;
    object-position: center 15% !important;
    display: block !important;
    margin: 0 auto !important;
    transition: transform 0.25s ease, filter 0.25s ease;
}

.avatar-placeholder {
    width: 160px;
    height: 160px;
    border-radius: 50%;
    background: linear-gradient(135deg, #15803d 0%, #16a34a 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ffffff;
    border: 4px solid var(--brand-accent, #16a34a);
    box-shadow: 0 8px 24px rgba(22, 163, 74, 0.2);
}

.avatar-placeholder.d-none {
    display: none !important;
}

/* Nút trigger upload ảnh */
.avatar-upload-trigger {
    position: absolute;
    bottom: 4px;
    right: 4px;
    width: 42px;
    height: 42px;
    border-radius: 50%;
    background: var(--brand-accent, #16a34a);
    color: #ffffff;
    border: 3px solid var(--brand-card-bg, #ffffff);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.15rem;
    cursor: pointer;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.25);
    z-index: 10;
    transition: transform 0.2s ease, background-color 0.2s ease;
}

.avatar-upload-trigger:hover {
    background: var(--brand-accent-hover, #15803d);
    transform: scale(1.12);
}

.avatar-upload-trigger .upload-tooltip {
    display: none;
}

/* Cropper Modal Vùng Canvas & Điều khiển */
.cropper-stage-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto;
    padding: 12px;
    background: #0f172a;
    border-radius: 12px;
    user-select: none;
    -webkit-user-select: none;
}

.cropper-stage {
    position: relative;
    width: 300px;
    height: 300px;
    overflow: hidden;
    cursor: grab;
    touch-action: none;
    background: #1e293b;
    border-radius: 8px;
    box-shadow: inset 0 0 10px rgba(0,0,0,0.5);
}

.cropper-stage:active {
    cursor: grabbing;
}

#cropCanvas {
    display: block;
    width: 300px;
    height: 300px;
}

.cropper-circle-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 300px;
    height: 300px;
    pointer-events: none;
    border-radius: 50%;
    box-shadow: 0 0 0 9999px rgba(15, 23, 42, 0.7);
    border: 2px dashed rgba(255, 255, 255, 0.85);
}

.cropper-circle-overlay::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.35);
}

.cropper-controls .form-range::-webkit-slider-thumb {
    background: #16a34a;
}
.cropper-controls .form-range::-moz-range-thumb {
    background: #16a34a;
}

/* ==========================================================================
   Cropper Modal Styling & Button System (Light & Dark Mode)
   ========================================================================== */
#avatarCropModal .modal-content {
    border-radius: 14px;
    overflow: hidden;
    box-shadow: 0 20px 45px rgba(0, 0, 0, 0.25);
    border: none;
}

#avatarCropModal .modal-header {
    background: linear-gradient(135deg, #15803d 0%, #16a34a 100%) !important;
    color: #ffffff !important;
    border-bottom: none;
}

#avatarCropModal .modal-header .modal-title {
    color: #ffffff !important;
    font-weight: 700 !important;
}

#avatarCropModal .modal-footer {
    background: #f8fafc !important;
    border-top: 1px solid var(--brand-border, #e2e8f0) !important;
    padding: 12px 20px !important;
}

#avatarCropModal #btnApplyCrop,
#avatarCropModal .btn-apply-crop {
    background: linear-gradient(135deg, #15803d 0%, #16a34a 100%) !important;
    color: #ffffff !important;
    border: none !important;
    font-size: 0.92rem !important;
    font-weight: 700 !important;
    padding: 8px 22px !important;
    border-radius: 8px !important;
    box-shadow: 0 4px 12px rgba(22, 163, 74, 0.35) !important;
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    transition: all 0.2s ease !important;
    cursor: pointer !important;
}

#avatarCropModal #btnApplyCrop:hover,
#avatarCropModal .btn-apply-crop:hover {
    background: linear-gradient(135deg, #166534 0%, #15803d 100%) !important;
    transform: translateY(-1px) !important;
    box-shadow: 0 6px 16px rgba(22, 163, 74, 0.45) !important;
    color: #ffffff !important;
}

#avatarCropModal #btnApplyCrop i,
#avatarCropModal .btn-apply-crop i {
    color: #ffffff !important;
    margin-right: 6px !important;
}

#avatarCropModal .btn-cancel-crop,
#avatarCropModal .modal-footer button[data-bs-dismiss="modal"] {
    background: #ffffff !important;
    color: #475569 !important;
    border: 1px solid #cbd5e1 !important;
    font-size: 0.9rem !important;
    font-weight: 600 !important;
    padding: 8px 18px !important;
    border-radius: 8px !important;
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    transition: all 0.2s ease !important;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05) !important;
    cursor: pointer !important;
}

#avatarCropModal .btn-cancel-crop:hover,
#avatarCropModal .modal-footer button[data-bs-dismiss="modal"]:hover {
    background: #f1f5f9 !important;
    color: #0f172a !important;
    border-color: #94a3b8 !important;
}

#avatarCropModal .cropper-controls .btn-tool {
    background: #ffffff !important;
    color: #334155 !important;
    border: 1px solid #cbd5e1 !important;
    font-size: 0.82rem !important;
    font-weight: 600 !important;
    padding: 6px 14px !important;
    border-radius: 6px !important;
    display: inline-flex !important;
    align-items: center !important;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05) !important;
    transition: all 0.15s ease !important;
    cursor: pointer !important;
}

#avatarCropModal .cropper-controls .btn-tool:hover {
    background: #f1f5f9 !important;
    color: #0f172a !important;
    border-color: #94a3b8 !important;
    transform: translateY(-1px) !important;
}

#avatarCropModal .cropper-controls .btn-zoom {
    background: #ffffff !important;
    color: #475569 !important;
    border: 1px solid #cbd5e1 !important;
    border-radius: 6px !important;
    padding: 6px 12px !important;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05) !important;
    transition: all 0.15s ease !important;
    cursor: pointer !important;
}

#avatarCropModal .cropper-controls .btn-zoom:hover {
    background: #f1f5f9 !important;
    color: #0f172a !important;
    border-color: #94a3b8 !important;
}

/* Hỗ trợ Dark mode cho Modal Cắt ảnh */
[data-theme="dark"] #avatarCropModal .modal-content {
    background-color: #1e293b !important;
    color: #f1f5f9 !important;
}

[data-theme="dark"] #avatarCropModal .modal-footer {
    background-color: #0f172a !important;
    border-color: #334155 !important;
}

[data-theme="dark"] #avatarCropModal .btn-cancel-crop,
[data-theme="dark"] #avatarCropModal .modal-footer button[data-bs-dismiss="modal"] {
    background: #334155 !important;
    color: #e2e8f0 !important;
    border-color: #475569 !important;
}

[data-theme="dark"] #avatarCropModal .btn-cancel-crop:hover,
[data-theme="dark"] #avatarCropModal .modal-footer button[data-bs-dismiss="modal"]:hover {
    background: #475569 !important;
    color: #ffffff !important;
}

[data-theme="dark"] #avatarCropModal .cropper-controls .btn-tool,
[data-theme="dark"] #avatarCropModal .cropper-controls .btn-zoom {
    background: #334155 !important;
    color: #f1f5f9 !important;
    border-color: #475569 !important;
}

[data-theme="dark"] #avatarCropModal .cropper-controls .btn-tool:hover,
[data-theme="dark"] #avatarCropModal .cropper-controls .btn-zoom:hover {
    background: #475569 !important;
    color: #ffffff !important;
}

/* Thông tin cơ bản */
.profile-basic-info {
    margin-top: 10px;
}

.profile-name {
    font-size: 1.75rem;
    font-weight: 700;
    color: var(--brand-text, #0f172a);
    margin-bottom: 8px;
}

.profile-badges {
    display: flex;
    gap: 8px;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    margin-bottom: 12px;
}

.profile-badges .badge {
    padding: 6px 14px;
    font-size: 0.85rem;
    font-weight: 600;
    border-radius: 6px;
}

.profile-stat {
    margin-top: 8px;
    padding: 8px 18px;
    background: var(--brand-light, #f8fafc);
    border: 1px solid var(--brand-border, #e2e8f0);
    border-radius: 8px;
    color: var(--brand-text-body, #334155);
    font-size: 0.9rem;
    display: inline-block;
}

/* Thẻ Thông tin Chi tiết */
.info-card {
    background: var(--brand-light, #f8fafc);
    border: 1px solid var(--brand-border, #e2e8f0);
    border-radius: 10px;
    padding: 24px;
    height: 100%;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.info-card:hover {
    box-shadow: 0 6px 16px rgba(0,0,0,0.06);
    transform: translateY(-2px);
}

.info-card .card-title {
    color: var(--brand-text, #0f172a);
    font-weight: 700;
    font-size: 1.1rem;
    margin-bottom: 18px;
    padding-bottom: 10px;
    border-bottom: 2px solid var(--brand-accent, #16a34a);
}

.details-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.detail-item {
    display: flex;
    flex-direction: column;
    padding: 12px 16px;
    background: var(--brand-card-bg, #ffffff);
    border: 1px solid var(--brand-border, #e2e8f0);
    border-radius: 8px;
    border-left: 4px solid var(--brand-accent, #16a34a);
}

.detail-label {
    font-weight: 600;
    color: var(--brand-text-muted, #64748b);
    margin-bottom: 4px;
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.detail-value {
    color: var(--brand-text, #0f172a);
    font-size: 1rem;
    font-weight: 600;
}

.detail-value a {
    color: var(--brand-accent, #16a34a);
    text-decoration: none;
    transition: color 0.2s ease;
}

.detail-value a:hover {
    color: var(--brand-accent-hover, #15803d);
    text-decoration: underline;
}

/* Nút hành động cuối trang */
.profile-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
    flex-wrap: wrap;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid var(--brand-border, #e2e8f0);
}

.profile-actions .btn {
    padding: 10px 24px;
    border-radius: 8px;
    font-weight: 600;
    font-size: 0.95rem;
    transition: all 0.2s ease;
    min-width: 180px;
}

.btn-edit-profile {
    background-color: var(--brand-accent, #16a34a) !important;
    color: #ffffff !important;
    border: none !important;
}

.btn-edit-profile:hover {
    background-color: var(--brand-accent-hover, #15803d) !important;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(22, 163, 74, 0.3);
}

.btn-edit-profile-disabled {
    background-color: #94a3b8 !important;
    color: #ffffff !important;
    border: none !important;
    cursor: not-allowed !important;
    opacity: 0.6;
}

.btn-view-news {
    background-color: #f59e0b !important;
    color: #ffffff !important;
    border: none !important;
}

.btn-view-news:hover {
    background-color: #d97706 !important;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

/* Card Đổi mật khẩu */
.password-card {
    border-left: 4px solid var(--brand-accent, #16a34a);
}

.password-card .form-label {
    font-weight: 600;
    color: var(--brand-text-muted, #64748b);
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 6px;
    display: block;
}

.password-card .form-control {
    border: 1px solid var(--brand-border, #cbd5e1);
    background-color: var(--brand-card-bg, #ffffff);
    color: var(--brand-text, #0f172a);
    border-radius: 8px;
    padding: 9px 14px;
    font-size: 0.95rem;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.password-card .form-control:focus {
    border-color: var(--brand-accent, #16a34a);
    box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.2);
    outline: none;
}

.btn-change-password {
    background: var(--brand-accent, #16a34a) !important;
    color: #ffffff !important;
    border: none !important;
    padding: 8px 22px;
    border-radius: 8px;
    font-weight: 600;
    font-size: 0.9rem;
    transition: all 0.2s ease;
}

.btn-change-password:hover {
    background: var(--brand-accent-hover, #15803d) !important;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(22, 163, 74, 0.25);
}

@keyframes fadeInDown {
    from { opacity: 0; transform: translateY(-8px); }
    to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
    .profile-content {
        padding: 20px;
    }
    .profile-actions .btn {
        width: 100%;
    }
}
</style>

<script>
document.addEventListener('DOMContentLoaded', function() {
    var btnTrigger = document.getElementById('btnTriggerUpload');
    var fileInput = document.getElementById('avatarFileInput');
    var previewImg = document.getElementById('avatarPreviewImg');
    var uploadForm = document.getElementById('avatarUploadForm');
    var cropModalEl = document.getElementById('avatarCropModal');
    var cropCanvas = document.getElementById('cropCanvas');
    var cropperStage = document.getElementById('cropperStage');
    var zoomSlider = document.getElementById('zoomSlider');
    var btnZoomIn = document.getElementById('btnZoomIn');
    var btnZoomOut = document.getElementById('btnZoomOut');
    var btnFitFrame = document.getElementById('btnFitFrame');
    var btnFocusHead = document.getElementById('btnFocusHead');
    var btnResetCrop = document.getElementById('btnResetCrop');
    var btnApplyCrop = document.getElementById('btnApplyCrop');

    if (btnTrigger && fileInput && cropModalEl && cropCanvas && cropperStage) {
        var cropModal = (typeof bootstrap !== 'undefined' && bootstrap.Modal) ? 
            (bootstrap.Modal.getInstance(cropModalEl) || new bootstrap.Modal(cropModalEl)) : null;
        var ctx = cropCanvas.getContext('2d');
        var stageW = 300;
        var stageH = 300;
        var img = new Image();
        var currentScale = 1.0;
        var minScale = 0.2;
        var maxScale = 3.5;
        var posX = 0;
        var posY = 0;
        var isDragging = false;
        var startX = 0;
        var startY = 0;

        // Mở file picker khi click nút camera
        btnTrigger.addEventListener('click', function() {
            fileInput.value = '';
            fileInput.click();
        });

        // Xử lý khi người dùng chọn file ảnh
        fileInput.addEventListener('change', function(e) {
            var file = e.target.files && e.target.files[0];
            if (!file) return;

            // Kiểm tra loại file
            var validTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
            if (!validTypes.includes(file.type)) {
                alert('Vui lòng chỉ chọn tệp hình ảnh định dạng JPG, PNG, WEBP hoặc GIF.');
                fileInput.value = '';
                return;
            }

            // Kiểm tra dung lượng (tối đa 5MB)
            if (file.size > 5 * 1024 * 1024) {
                alert('Kích thước ảnh vượt quá giới hạn 5 MB. Vui lòng chọn ảnh nhỏ hơn.');
                fileInput.value = '';
                return;
            }

            // Đọc file và nạp vào Canvas để căn chỉnh
            var reader = new FileReader();
            reader.onload = function(evt) {
                img = new Image();
                img.onload = function() {
                    initCropper();
                    if (cropModal) {
                        cropModal.show();
                    } else if (typeof $ !== 'undefined') {
                        $(cropModalEl).modal('show');
                    }
                };
                img.src = evt.target.result;
            };
            reader.readAsDataURL(file);
        });

        function redraw() {
            if (!img || !img.complete || img.naturalWidth === 0) return;
            ctx.clearRect(0, 0, stageW, stageH);
            ctx.imageSmoothingEnabled = true;
            ctx.imageSmoothingQuality = 'high';
            ctx.drawImage(img, posX, posY, img.width * currentScale, img.height * currentScale);
        }

        function setZoom(newScale, centerPoint) {
            newScale = Math.max(minScale, Math.min(maxScale, newScale));
            var cx = centerPoint ? centerPoint.x : stageW / 2;
            var cy = centerPoint ? centerPoint.y : stageH / 2;
            posX = cx - (cx - posX) * (newScale / currentScale);
            posY = cy - (cy - posY) * (newScale / currentScale);
            currentScale = newScale;
            if (zoomSlider) zoomSlider.value = currentScale.toFixed(2);
            redraw();
        }

        function initCropper() {
            var coverScale = Math.max(stageW / img.width, stageH / img.height);
            minScale = Math.min(0.2, Math.min(stageW / img.width, stageH / img.height) * 0.7);
            maxScale = Math.max(3.5, coverScale * 3.0);
            if (zoomSlider) {
                zoomSlider.min = minScale.toFixed(2);
                zoomSlider.max = maxScale.toFixed(2);
                zoomSlider.step = "0.02";
            }
            // Mặc định tự động căn đỉnh đầu (Head Focus) để không bị mất đỉnh đầu
            applyHeadFocusPreset();
        }

        // Preset 1: Căn đỉnh đầu (Head Focus)
        function applyHeadFocusPreset() {
            currentScale = Math.max(stageW / img.width, stageH / img.height * 0.85);
            posX = (stageW - img.width * currentScale) / 2;
            // Đặt đỉnh đầu cách lề trên khung một khoảng vừa vặn (khoảng 8% stage)
            posY = stageH * 0.08;
            if (zoomSlider) zoomSlider.value = currentScale.toFixed(2);
            redraw();
        }

        // Preset 2: Vừa khung (Fit Frame)
        function applyFitFramePreset() {
            currentScale = Math.min(stageW / img.width, stageH / img.height);
            posX = (stageW - img.width * currentScale) / 2;
            posY = (stageH - img.height * currentScale) / 2;
            if (zoomSlider) zoomSlider.value = currentScale.toFixed(2);
            redraw();
        }

        // Preset 3: Đặt lại (Reset)
        function applyResetPreset() {
            var coverScale = Math.max(stageW / img.width, stageH / img.height);
            currentScale = coverScale;
            posX = (stageW - img.width * currentScale) / 2;
            posY = (stageH - img.height * currentScale) * 0.15; // 15% top bias
            if (zoomSlider) zoomSlider.value = currentScale.toFixed(2);
            redraw();
        }

        // Xử lý kéo thả bằng chuột
        function onPointerDown(clientX, clientY) {
            isDragging = true;
            startX = clientX;
            startY = clientY;
        }

        function onPointerMove(clientX, clientY) {
            if (!isDragging) return;
            var dx = clientX - startX;
            var dy = clientY - startY;
            posX += dx;
            posY += dy;
            startX = clientX;
            startY = clientY;
            redraw();
        }

        function onPointerUp() {
            isDragging = false;
        }

        cropperStage.addEventListener('mousedown', function(e) {
            e.preventDefault();
            onPointerDown(e.clientX, e.clientY);
        });

        window.addEventListener('mousemove', function(e) {
            if (isDragging) {
                e.preventDefault();
                onPointerMove(e.clientX, e.clientY);
            }
        });

        window.addEventListener('mouseup', function() {
            if (isDragging) onPointerUp();
        });

        // Hỗ trợ cảm ứng trên điện thoại / tablet
        cropperStage.addEventListener('touchstart', function(e) {
            if (e.touches.length === 1) {
                onPointerDown(e.touches[0].clientX, e.touches[0].clientY);
            }
        }, { passive: true });

        window.addEventListener('touchmove', function(e) {
            if (isDragging && e.touches.length === 1) {
                onPointerMove(e.touches[0].clientX, e.touches[0].clientY);
            }
        }, { passive: true });

        window.addEventListener('touchend', function() {
            if (isDragging) onPointerUp();
        });

        // Zoom bằng con lăn chuột
        cropperStage.addEventListener('wheel', function(e) {
            e.preventDefault();
            var rect = cropCanvas.getBoundingClientRect();
            var mousePoint = { x: e.clientX - rect.left, y: e.clientY - rect.top };
            var delta = e.deltaY < 0 ? 0.08 : -0.08;
            setZoom(currentScale + delta, mousePoint);
        }, { passive: false });

        // Slider zoom
        if (zoomSlider) {
            zoomSlider.addEventListener('input', function() {
                setZoom(parseFloat(this.value), { x: stageW / 2, y: stageH / 2 });
            });
        }

        if (btnZoomIn) {
            btnZoomIn.addEventListener('click', function() {
                setZoom(currentScale + 0.15, { x: stageW / 2, y: stageH / 2 });
            });
        }

        if (btnZoomOut) {
            btnZoomOut.addEventListener('click', function() {
                setZoom(currentScale - 0.15, { x: stageW / 2, y: stageH / 2 });
            });
        }

        if (btnFitFrame) {
            btnFitFrame.addEventListener('click', applyFitFramePreset);
        }

        if (btnFocusHead) {
            btnFocusHead.addEventListener('click', applyHeadFocusPreset);
        }

        if (btnResetCrop) {
            btnResetCrop.addEventListener('click', applyResetPreset);
        }

        // Xuất ảnh và gửi form
        if (btnApplyCrop) {
            btnApplyCrop.addEventListener('click', function() {
                btnApplyCrop.disabled = true;
                btnApplyCrop.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Đang lưu...';

                // Xuất ảnh vuông 400x400 chất lượng cao
                var exportCanvas = document.createElement('canvas');
                exportCanvas.width = 400;
                exportCanvas.height = 400;
                var expCtx = exportCanvas.getContext('2d');
                expCtx.imageSmoothingEnabled = true;
                expCtx.imageSmoothingQuality = 'high';

                // Tô nền trắng cho ảnh
                expCtx.fillStyle = '#ffffff';
                expCtx.fillRect(0, 0, 400, 400);

                var ratio = 400 / stageW;
                expCtx.drawImage(
                    img,
                    posX * ratio,
                    posY * ratio,
                    img.width * currentScale * ratio,
                    img.height * currentScale * ratio
                );

                exportCanvas.toBlob(function(blob) {
                    if (!blob) {
                        alert('Không thể xuất ảnh đã căn chỉnh. Vui lòng thử lại.');
                        btnApplyCrop.disabled = false;
                        btnApplyCrop.innerHTML = '<i class="fas fa-check me-1"></i>Áp dụng & Lưu ảnh';
                        return;
                    }

                    try {
                        var croppedFile = new File([blob], 'avatar_custom.jpg', { type: 'image/jpeg' });
                        var dt = new DataTransfer();
                        dt.items.add(croppedFile);
                        fileInput.files = dt.files;
                    } catch (err) {
                        console.warn('DataTransfer not supported, falling back to direct upload', err);
                    }

                    // Cập nhật ngay preview trên trang
                    if (previewImg) {
                        previewImg.src = exportCanvas.toDataURL('image/jpeg', 0.92);
                        previewImg.style.setProperty('display', 'block', 'important');
                        var ph = document.getElementById('avatarPlaceholder');
                        if (ph) ph.classList.add('d-none');
                    }

                    if (cropModal) {
                        cropModal.hide();
                    } else if (typeof $ !== 'undefined') {
                        $(cropModalEl).modal('hide');
                    }

                    // Gửi form multipart lên server
                    if (uploadForm) {
                        uploadForm.submit();
                    }
                }, 'image/jpeg', 0.92);
            });
        }
    }

    // Validation form Đổi mật khẩu
    var changePwForm = document.querySelector('.change-password-form');
    if (changePwForm) {
        changePwForm.addEventListener('submit', function(e) {
            var curPw = document.getElementById('currentPassword').value;
            var newPw = document.getElementById('newPassword').value;
            var cfmPw = document.getElementById('confirmPassword').value;

            if (!curPw || !newPw || !cfmPw) {
                e.preventDefault();
                alert('Vui lòng điền đầy đủ các trường mật khẩu.');
                return false;
            }

            if (newPw.length < 8) {
                e.preventDefault();
                alert('Mật khẩu mới phải có ít nhất 8 ký tự.');
                document.getElementById('newPassword').focus();
                return false;
            }

            if (newPw !== cfmPw) {
                e.preventDefault();
                alert('Mật khẩu mới và xác nhận mật khẩu không khớp.');
                document.getElementById('confirmPassword').focus();
                return false;
            }

            if (newPw === curPw) {
                e.preventDefault();
                alert('Mật khẩu mới không được trùng với mật khẩu hiện tại.');
                document.getElementById('newPassword').focus();
                return false;
            }

            var submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Đang xử lý...';
            }
        });
    }
});
</script>
