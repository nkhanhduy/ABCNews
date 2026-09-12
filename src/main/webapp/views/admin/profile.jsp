<%-- 
  File: profile.jsp
  Description: Trang hiển thị thông tin và đổi ảnh đại diện thông minh của user
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<div class="profile-container">
    <%-- Flash Toast Alerts --%>
    <c:if test="${not empty toastSuccess}">
        <div class="alert alert-success alert-dismissible fade show profile-alert" role="alert">
            <i class="fas fa-check-circle me-2"></i>
            <strong>Thành công!</strong> ${toastSuccess}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty toastError}">
        <div class="alert alert-danger alert-dismissible fade show profile-alert" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>
            <strong>Lỗi!</strong> ${toastError}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

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
                                     style="width: 160px !important; height: 160px !important; max-width: 160px !important; max-height: 160px !important; border-radius: 50% !important; object-fit: cover !important; display: block !important; margin: 0 auto !important;"
                                     onerror="this.style.display='none'; var ph = document.getElementById('avatarPlaceholder'); if(ph) ph.classList.remove('d-none');">
                                <div id="avatarPlaceholder" class="avatar-placeholder d-none" style="width: 160px; height: 160px; border-radius: 50%; align-items: center; justify-content: center; background: linear-gradient(135deg, #15803d 0%, #16a34a 100%); color: #ffffff;">
                                    <i class="fas fa-user fa-5x"></i>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <img id="avatarPreviewImg"
                                     src="" 
                                     data-original-src=""
                                     alt="${profileUser.fullname}" 
                                     class="avatar-img"
                                     style="display: none !important; width: 160px !important; height: 160px !important; max-width: 160px !important; max-height: 160px !important; border-radius: 50% !important; object-fit: cover !important;">
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
                          action="${pageContext.request.contextPath}/admin/profile" 
                          method="post" 
                          enctype="multipart/form-data" 
                          class="avatar-upload-form">
                        <input type="hidden" name="targetUserId" value="${profileUser.id}">
                        <input type="file" 
                               id="avatarFileInput" 
                               name="avatarFile" 
                               accept="image/png,image/jpeg,image/webp,image/gif" 
                               style="display: none;">
                        
                        <%-- Khối xác nhận sau khi chọn ảnh --%>
                        <div id="avatarActionBox" class="avatar-action-box" style="display: none;">
                            <div class="file-meta">
                                <span id="fileNameLabel" class="file-name"></span>
                                <span id="fileSizeLabel" class="file-size text-muted"></span>
                            </div>
                            <div class="action-buttons">
                                <button type="submit" id="btnSaveAvatar" class="btn btn-save-avatar">
                                    <i class="fas fa-cloud-upload-alt me-1"></i>Lưu ảnh đại diện
                                </button>
                                <button type="button" id="btnCancelAvatar" class="btn btn-cancel-avatar">
                                    <i class="fas fa-times me-1"></i>Hủy
                                </button>
                            </div>
                        </div>
                    </form>
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
                                        <i class="fas fa-key me-2"></i>Mật khẩu
                                    </div>
                                    <div class="detail-value">
                                        <span class="text-muted">••••••••</span>
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
                        
                        <c:choose>
                            <c:when test="${not empty profileUser.mobile}">
                                <div class="detail-item">
                                    <div class="detail-label">
                                        <i class="fas fa-key me-2"></i>Mật khẩu
                                    </div>
                                    <div class="detail-value">
                                        <span class="text-muted">••••••••</span>
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
        </div>
        
        <!-- Nút hành động -->
        <div class="profile-actions mt-4">
            <c:choose>
                <c:when test="${canEdit}">
                    <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${profileUser.id}" 
                       class="btn btn-edit-profile">
                        <i class="fas fa-user-edit me-1"></i>Chỉnh sửa hồ sơ
                    </a>
                </c:when>
                <c:otherwise>
                    <button type="button" class="btn btn-edit-profile-disabled" disabled>
                        <i class="fas fa-lock me-1"></i>Chỉnh sửa thông tin
                    </button>
                </c:otherwise>
            </c:choose>
            
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

/* Box xác nhận đổi ảnh */
.avatar-action-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    background: var(--brand-light, #f8fafc);
    border: 1px dashed var(--brand-accent, #16a34a);
    padding: 12px 20px;
    border-radius: 10px;
    animation: fadeInDown 0.3s ease;
}

.avatar-action-box .file-name {
    font-weight: 600;
    color: var(--brand-text, #0f172a);
    font-size: 0.9rem;
    max-width: 260px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.avatar-action-box .file-size {
    font-size: 0.8rem;
    margin-left: 6px;
}

.avatar-action-box .action-buttons {
    display: flex;
    gap: 8px;
}

.btn-save-avatar {
    background: var(--brand-accent, #16a34a) !important;
    color: #ffffff !important;
    border: none;
    padding: 6px 16px;
    border-radius: 6px;
    font-size: 0.85rem;
    font-weight: 600;
    transition: background-color 0.2s ease, transform 0.15s ease;
}

.btn-save-avatar:hover {
    background: var(--brand-accent-hover, #15803d) !important;
    transform: translateY(-1px);
}

.btn-cancel-avatar {
    background: transparent !important;
    color: var(--brand-text-muted, #64748b) !important;
    border: 1px solid var(--brand-border, #cbd5e1);
    padding: 6px 14px;
    border-radius: 6px;
    font-size: 0.85rem;
    transition: all 0.2s ease;
}

.btn-cancel-avatar:hover {
    background: #e2e8f0 !important;
    color: #0f172a !important;
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
    var actionBox = document.getElementById('avatarActionBox');
    var previewImg = document.getElementById('avatarPreviewImg');
    var fileNameLabel = document.getElementById('fileNameLabel');
    var fileSizeLabel = document.getElementById('fileSizeLabel');
    var btnCancel = document.getElementById('btnCancelAvatar');
    var uploadForm = document.getElementById('avatarUploadForm');
    var btnSave = document.getElementById('btnSaveAvatar');

    if (!btnTrigger || !fileInput) return;

    var originalSrc = previewImg ? previewImg.getAttribute('data-original-src') : '';

    // Mở file picker khi click nút camera
    btnTrigger.addEventListener('click', function() {
        fileInput.click();
    });

    // Xử lý khi người dùng chọn file
    fileInput.addEventListener('change', function(e) {
        var file = e.target.files[0];
        if (!file) return;

        // Kiểm tra loại file
        var validTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
        if (!validTypes.includes(file.type)) {
            alert('Vui lòng chỉ chọn tệp hình ảnh định dạng JPG, PNG, WEBP hoặc GIF.');
            fileInput.value = '';
            return;
        }

        // Kiểm tra dung lượng (tối đa 5MB)
        var maxSize = 5 * 1024 * 1024;
        if (file.size > maxSize) {
            alert('Kích thước ảnh vượt quá giới hạn 5 MB. Vui lòng chọn ảnh nhỏ hơn.');
            fileInput.value = '';
            return;
        }

        // Hiển thị tên và kích thước file
        if (fileNameLabel) fileNameLabel.textContent = file.name;
        if (fileSizeLabel) {
            var sizeInKB = (file.size / 1024).toFixed(1);
            fileSizeLabel.textContent = '(' + (sizeInKB > 1024 ? (sizeInKB / 1024).toFixed(2) + ' MB' : sizeInKB + ' KB') + ')';
        }

        // Preview ảnh đại diện tức thì
        var reader = new FileReader();
        reader.onload = function(event) {
            if (previewImg) {
                previewImg.src = event.target.result;
                previewImg.style.setProperty('display', 'block', 'important');
                var placeholder = document.getElementById('avatarPlaceholder');
                if (placeholder) {
                    placeholder.classList.add('d-none');
                }
            }
        };
        reader.readAsDataURL(file);

        // Hiển thị thanh thao tác Lưu / Hủy
        if (actionBox) actionBox.style.display = 'flex';
    });

    // Nút Hủy
    if (btnCancel) {
        btnCancel.addEventListener('click', function() {
            fileInput.value = '';
            if (actionBox) actionBox.style.display = 'none';
            if (previewImg) {
                var placeholder = document.getElementById('avatarPlaceholder');
                if (originalSrc && originalSrc.trim() !== '') {
                    previewImg.src = originalSrc;
                    previewImg.style.setProperty('display', 'block', 'important');
                    if (placeholder) placeholder.classList.add('d-none');
                } else {
                    previewImg.style.setProperty('display', 'none', 'important');
                    if (placeholder) placeholder.classList.remove('d-none');
                }
            }
        });
    }

    // Hiệu ứng khi nhấn nút Lưu
    if (uploadForm) {
        uploadForm.addEventListener('submit', function() {
            if (btnSave) {
                btnSave.disabled = true;
                btnSave.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Đang tải lên...';
            }
        });
    }
});
</script>
