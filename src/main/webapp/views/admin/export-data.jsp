<%-- 
  File: export-data.jsp
  Description: Trang xuất dữ liệu với form chọn module và format
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="export-data-page">
    <!-- Card Form chọn xuất -->
    <div class="export-form-card card">
        <div class="card-header">
            <h3><i class="fas fa-file-export me-2"></i>Chọn Dữ Liệu Cần Xuất</h3>
        </div>
        <div class="card-body">
            <form id="exportForm" class="export-form">
                <!-- Chọn Module -->
                <div class="form-section">
                    <h4 class="section-title"><i class="fas fa-folder-open me-2"></i>Chọn dữ liệu xuất 
                        <c:if test="${sessionScope.user.role}"><span class="badge bg-secondary-subtle text-secondary ms-2" style="font-size: 0.72rem;">Chọn nhiều mục</span></c:if>
                    </h4>
                    <div class="module-grid">
                        <div class="module-option">
                            <input type="checkbox" name="type" value="news" id="type-news" checked>
                            <label for="type-news" class="module-label">
                                <div class="module-icon news-icon">
                                    <i class="fas fa-newspaper"></i>
                                </div>
                                <div class="module-info">
                                    <c:choose>
                                        <c:when test="${sessionScope.user.role}">
                                            <div class="module-name">Tin tức</div>
                                            <div class="module-count">${totalNews} bản tin</div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="module-name">Tin tức của tôi</div>
                                            <div class="module-count">${totalNews} bản tin</div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="module-check">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                            </label>
                        </div>

                        <!-- CHỈ ADMIN mới thấy các module này -->
                        <c:if test="${sessionScope.user.role}">
                            <div class="module-option">
                                <input type="checkbox" name="type" value="users" id="type-users">
                                <label for="type-users" class="module-label">
                                    <div class="module-icon users-icon">
                                        <i class="fas fa-users"></i>
                                    </div>
                                    <div class="module-info">
                                        <div class="module-name">Người dùng</div>
                                        <div class="module-count">${totalUsers} người dùng</div>
                                    </div>
                                    <div class="module-check">
                                        <i class="fas fa-check-circle"></i>
                                    </div>
                                </label>
                            </div>

                            <div class="module-option">
                                <input type="checkbox" name="type" value="categories" id="type-categories">
                                <label for="type-categories" class="module-label">
                                    <div class="module-icon categories-icon">
                                        <i class="fas fa-tags"></i>
                                    </div>
                                    <div class="module-info">
                                        <div class="module-name">Loại tin</div>
                                        <div class="module-count">${totalCategories} loại tin</div>
                                    </div>
                                    <div class="module-check">
                                        <i class="fas fa-check-circle"></i>
                                    </div>
                                </label>
                            </div>

                            <div class="module-option">
                                <input type="checkbox" name="type" value="newsletters" id="type-newsletters">
                                <label for="type-newsletters" class="module-label">
                                    <div class="module-icon newsletters-icon">
                                        <i class="fas fa-envelope"></i>
                                    </div>
                                    <div class="module-info">
                                        <div class="module-name">Newsletter</div>
                                        <div class="module-count">${totalNewsletters} email</div>
                                    </div>
                                    <div class="module-check">
                                        <i class="fas fa-check-circle"></i>
                                    </div>
                                </label>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Chọn Khoảng Thời Gian -->
                <div class="form-section">
                    <h4 class="section-title"><i class="fas fa-calendar-alt me-2"></i>Chọn Khoảng Thời Gian</h4>
                    <div class="time-range-options">
                        <div class="time-option">
                            <input type="radio" name="timeRange" value="all" id="time-all" checked>
                            <label for="time-all" class="time-label">
                                <i class="fas fa-infinity"></i>
                                <span>Tất cả</span>
                            </label>
                        </div>
                        <div class="time-option">
                            <input type="radio" name="timeRange" value="3days" id="time-3days">
                            <label for="time-3days" class="time-label">
                                <i class="fas fa-calendar-day"></i>
                                <span>3 ngày gần đây</span>
                            </label>
                        </div>
                        <div class="time-option">
                            <input type="radio" name="timeRange" value="7days" id="time-7days">
                            <label for="time-7days" class="time-label">
                                <i class="fas fa-calendar-week"></i>
                                <span>1 tuần gần đây</span>
                            </label>
                        </div>
                        <div class="time-option">
                            <input type="radio" name="timeRange" value="30days" id="time-30days">
                            <label for="time-30days" class="time-label">
                                <i class="fas fa-calendar"></i>
                                <span>1 tháng gần đây</span>
                            </label>
                        </div>
                    </div>
                </div>

                <!-- Chọn Format -->
                <div class="form-section">
                    <h4 class="section-title"><i class="fas fa-file-alt me-2"></i>Chọn Định Dạng</h4>
                    <div class="format-grid">
                        <div class="format-option">
                            <input type="radio" name="format" value="csv" id="format-csv" checked>
                            <label for="format-csv" class="format-label">
                                <div class="format-icon csv-icon">
                                    <i class="fas fa-file-csv"></i>
                                </div>
                                <div class="format-info">
                                    <div class="format-name">CSV</div>
                                    <div class="format-desc">Excel-friendly, UTF-8</div>
                                </div>
                                <div class="format-check">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                            </label>
                        </div>

                        <div class="format-option">
                            <input type="radio" name="format" value="excel" id="format-excel">
                            <label for="format-excel" class="format-label">
                                <div class="format-icon excel-icon">
                                    <i class="fas fa-file-excel"></i>
                                </div>
                                <div class="format-info">
                                    <div class="format-name">Excel</div>
                                    <div class="format-desc">Với styling & borders</div>
                                </div>
                                <div class="format-check">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                            </label>
                        </div>

                        <div class="format-option">
                            <input type="radio" name="format" value="pdf" id="format-pdf">
                            <label for="format-pdf" class="format-label">
                                <div class="format-icon pdf-icon">
                                    <i class="fas fa-file-pdf"></i>
                                </div>
                                <div class="format-info">
                                    <div class="format-name">PDF</div>
                                    <div class="format-desc">Chuyên nghiệp, in ấn</div>
                                </div>
                                <div class="format-check">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>

                <!-- Nút Xuất -->
                <div class="form-actions">
                    <button type="button" class="btn-export-large" onclick="handleExport()">
                        <i class="fas fa-download me-2"></i>Xuất Dữ Liệu
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Card Hướng dẫn -->
    <div class="export-history-card card">
        <div class="card-header">
            <h3><i class="fas fa-history me-2"></i>Hướng Dẫn</h3>
        </div>
        <div class="card-body">
            <div class="guide-list">
                <div class="guide-item">
                    <div class="guide-icon"><i class="fas fa-check-circle"></i></div>
                    <div class="guide-text">
                        <c:choose>
                            <c:when test="${sessionScope.user.role}">
                                <strong>Bước 1:</strong> Chọn các mục dữ liệu cần kết xuất
                            </c:when>
                            <c:otherwise>
                                <strong>Bước 1:</strong> Chọn mục "Tin tức" để xuất các bài viết của bạn
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="guide-item">
                    <div class="guide-icon"><i class="fas fa-check-circle"></i></div>
                    <div class="guide-text">
                        <strong>Bước 2:</strong> Chọn định dạng tệp mong muốn: Excel, CSV hoặc PDF
                    </div>
                </div>
                <div class="guide-item">
                    <div class="guide-icon"><i class="fas fa-check-circle"></i></div>
                    <div class="guide-text">
                        <strong>Bước 3:</strong> Nhấn nút "Xuất Dữ Liệu" để tải tệp về máy
                    </div>
                </div>
                <div class="guide-item">
                    <div class="guide-icon"><i class="fas fa-info-circle"></i></div>
                    <div class="guide-text">
                        <c:choose>
                            <c:when test="${sessionScope.user.role}">
                                <strong>Lưu ý:</strong> Nếu chọn nhiều module, hệ thống sẽ tải xuống nhiều file riêng biệt
                            </c:when>
                            <c:otherwise>
                                <strong>Lưu ý:</strong> Bạn chỉ có thể xuất các tin tức do chính bạn viết
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
/* Export Data Page Styles */
.export-data-page {
    display: flex;
    flex-direction: column;
    gap: 25px;
}

/* Form Card */
.export-form-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.export-form-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 20px 25px;
}

.export-form-card .card-header h3 {
    margin: 0;
    font-size: 1.3rem;
    font-weight: 600;
}

.export-form-card .card-body {
    padding: 30px;
}

/* Form Sections */
.form-section {
    margin-bottom: 35px;
}

.form-section:last-of-type {
    margin-bottom: 0;
}

.section-title {
    margin: 0 0 20px 0;
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    display: flex;
    align-items: center;
}

/* Module Grid */
.module-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 15px;
}

.module-option {
    position: relative;
}

.module-option input[type="checkbox"] {
    position: absolute;
    opacity: 0;
    pointer-events: none;
}

.module-label {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 20px;
    background: #f8f9fa;
    border: 2px solid #dee2e6;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.module-label:hover {
    background: #e9ecef;
    border-color: #adb5bd;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.module-option input:checked + .module-label {
    background: #fff;
    border-color: #667eea;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.module-icon {
    width: 50px;
    height: 50px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    color: #fff;
    flex-shrink: 0;
}

.news-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.users-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.categories-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.newsletters-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.module-info {
    flex: 1;
}

.module-name {
    font-weight: 600;
    font-size: 1rem;
    color: #333;
    margin-bottom: 4px;
}

.module-count {
    font-size: 0.85rem;
    color: #666;
}

.module-check {
    font-size: 1.5rem;
    color: #dee2e6;
    transition: color 0.3s ease;
}

.module-option input:checked + .module-label .module-check {
    color: #667eea;
}

/* Format Grid */
.format-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
}

.format-option {
    position: relative;
}

.format-option input[type="radio"] {
    position: absolute;
    opacity: 0;
    pointer-events: none;
}

.format-label {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 18px;
    background: #f8f9fa;
    border: 2px solid #dee2e6;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.format-label:hover {
    background: #e9ecef;
    border-color: #adb5bd;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.format-option input:checked + .format-label {
    background: #fff;
    border-color: var(--primary-color);
    box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.format-icon {
    width: 45px;
    height: 45px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.3rem;
    color: #fff;
    flex-shrink: 0;
}

.csv-icon { background: linear-gradient(135deg, #28a745 0%, #20c997 100%); }
.excel-icon { background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); }
.pdf-icon { background: linear-gradient(135deg, #dc3545 0%, #c82333 100%); }

.format-info {
    flex: 1;
}

.format-name {
    font-weight: 600;
    font-size: 0.95rem;
    color: #333;
    margin-bottom: 3px;
}

.format-desc {
    font-size: 0.8rem;
    color: #666;
}

.format-check {
    font-size: 1.3rem;
    color: #dee2e6;
    transition: color 0.3s ease;
}

.format-option input:checked + .format-label .format-check {
    color: var(--primary-color);
}

/* Time Range Options */
.time-range-options {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 12px;
}

.time-option {
    position: relative;
}

.time-option input[type="radio"] {
    position: absolute;
    opacity: 0;
    pointer-events: none;
}

.time-label {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 15px 10px;
    background: #fff;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: center;
}

.time-label:hover {
    background: #f8f9fa;
    border-color: #667eea;
    transform: translateY(-2px);
    box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
}

.time-option input:checked + .time-label {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-color: #667eea;
    color: #fff;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.time-label i {
    font-size: 1.5rem;
    color: #667eea;
    transition: color 0.3s ease;
}

.time-option input:checked + .time-label i {
    color: #fff;
}

.time-label span {
    font-size: 0.85rem;
    font-weight: 500;
    color: #333;
    transition: color 0.3s ease;
}

.time-option input:checked + .time-label span {
    color: #fff;
}

/* Form Actions */
.form-actions {
    margin-top: 30px;
    text-align: center;
}

.btn-export-large {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 15px 40px;
    font-size: 1.1rem;
    font-weight: 600;
    color: #fff;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-export-large:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-export-large:active {
    transform: translateY(-1px);
}

/* History/Guide Card */
.export-history-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.export-history-card .card-header {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: #fff;
    padding: 15px 25px;
}

.export-history-card .card-header h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 600;
}

.export-history-card .card-body {
    padding: 25px;
}

.guide-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.guide-item {
    display: flex;
    gap: 15px;
    align-items: flex-start;
}

.guide-icon {
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
    color: #28a745;
    flex-shrink: 0;
}

.guide-item:last-child .guide-icon {
    color: #17a2b8;
}

.guide-text {
    flex: 1;
    line-height: 1.6;
    color: #555;
}

.guide-text strong {
    color: #333;
}

/* Responsive */
@media (max-width: 768px) {
    .module-grid,
    .format-grid,
    .time-range-options {
        grid-template-columns: 1fr;
    }
    
    .time-label {
        flex-direction: row;
        justify-content: center;
        padding: 12px 15px;
    }
    
    .time-label i {
        font-size: 1.2rem;
    }
    
    .time-label span {
        font-size: 0.9rem;
    }
}
</style>

<script>
function handleExport() {
    // Lấy tất cả module được chọn
    const selectedTypes = Array.from(document.querySelectorAll('input[name="type"]:checked'))
        .map(input => input.value);
    
    // Kiểm tra có chọn module nào không
    if (selectedTypes.length === 0) {
        alert('Vui lòng chọn ít nhất một module để xuất!');
        return;
    }
    
    // Lấy format được chọn
    const formatInput = document.querySelector('input[name="format"]:checked');
    if (!formatInput) {
        alert('Vui lòng chọn định dạng file!');
        return;
    }
    const format = formatInput.value;
    
    // Hiển thị loading
    const btn = event.target;
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang xuất...';
    
    // Lấy time range
    const timeRange = document.querySelector('input[name="timeRange"]:checked').value;
    
    // Tạo URL với tất cả parameters (gộp vào 1 file)
    const params = new URLSearchParams();
    selectedTypes.forEach(type => params.append('type', type));
    params.append('format', format);
    params.append('timeRange', timeRange);
    
    const url = '${pageContext.request.contextPath}/admin/export?' + params.toString();
    
    // Tải file (1 file duy nhất chứa tất cả modules đã chọn)
    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    iframe.src = url;
    document.body.appendChild(iframe);
    
    // Xóa iframe sau 2 giây
    setTimeout(() => {
        document.body.removeChild(iframe);
    }, 2000);
    
    // Reset button sau khi export
    setTimeout(() => {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-check me-2"></i>Xuất thành công!';
        
        setTimeout(() => {
            btn.innerHTML = originalText;
        }, 2000);
    }, 1000);
}
</script>

