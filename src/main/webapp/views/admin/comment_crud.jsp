<%-- 
  File: comment_crud.jsp
  Description: Giao diện Quản lý & Kiểm duyệt Bình Luận (Moderated Comments) - Phiên bản Cohesive & Professional Icons
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<style>
/* --- Tối ưu hóa bố cục trang Kiểm duyệt bình luận (Cohesive Layout) --- */
.comments-page-wrapper {
    width: 100%;
}

/* 1. Header & Segmented Pills */
.comments-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 8px;
}

.comments-header-row h2 {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--brand-text, #0f172a);
}

.comments-header-row .subtitle {
    color: var(--brand-text-muted, #64748b);
    font-size: 0.88rem;
    margin-top: 2px;
}

/* Bộ nút trạng thái góc trên phải - Thuần chữ tinh tế, không kèm icon */
.status-filter-pills {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    background: #f1f5f9;
    padding: 4px;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
}

.status-pill {
    display: inline-flex;
    align-items: center;
    padding: 6px 14px;
    font-size: 0.84rem;
    font-weight: 600;
    border-radius: 6px;
    color: #475569 !important;
    text-decoration: none !important;
    transition: all 0.15s ease;
    white-space: nowrap;
}

.status-pill:hover {
    color: #0f172a !important;
    background: rgba(255, 255, 255, 0.7);
}

.status-pill.active {
    background: #ffffff;
    color: #0f172a !important;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.status-pill .pill-count {
    margin-left: 6px;
    padding: 1px 7px;
    border-radius: 10px;
    font-size: 0.74rem;
    background: #e2e8f0;
    color: #334155;
    font-weight: 700;
}

.status-pill.active .pill-count {
    background: #16a34a;
    color: #ffffff;
}

/* 2. Dải 4 Thẻ Thống Kê Nhanh - Thiết kế Metric Cards chuẩn Enterprise (Không hộp vuông thô) */
.comments-stats-strip {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 20px;
}

@media (max-width: 992px) {
    .comments-stats-strip {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 576px) {
    .comments-stats-strip {
        grid-template-columns: 1fr;
    }
}

.comment-stat-card {
    background: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    padding: 14px 18px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-height: 96px;
    text-decoration: none !important;
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
    position: relative;
    overflow: hidden;
}

.comment-stat-card:hover {
    border-color: #cbd5e1;
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

.comment-stat-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: transparent;
    transition: all 0.2s ease;
}

.comment-stat-card.stat-pending::before { background: #f59e0b; }
.comment-stat-card.stat-approved::before { background: #16a34a; }
.comment-stat-card.stat-rejected::before { background: #ef4444; }
.comment-stat-card.stat-all::before { background: #6366f1; }

.stat-card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
}

.stat-card-header .stat-label {
    font-size: 0.82rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.4px;
    color: #64748b;
}

.stat-icon-circle {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.95rem;
    transition: transform 0.2s ease;
}

.comment-stat-card:hover .stat-icon-circle {
    transform: scale(1.1);
}

.stat-pending .stat-icon-circle { background-color: #fef3c7; color: #d97706; }
.stat-approved .stat-icon-circle { background-color: #dcfce7; color: #16a34a; }
.stat-rejected .stat-icon-circle { background-color: #fee2e2; color: #dc2626; }
.stat-all .stat-icon-circle { background-color: #e0e7ff; color: #4f46e5; }

.stat-card-body {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
}

.stat-card-body .stat-value {
    font-size: 1.85rem;
    font-weight: 800;
    color: #0f172a;
    line-height: 1;
}

.stat-badge-hint {
    font-size: 0.72rem;
    font-weight: 600;
    padding: 3px 8px;
    border-radius: 12px;
}

.stat-pending .stat-badge-hint { background-color: #fffbeb; color: #b45309; }
.stat-approved .stat-badge-hint { background-color: #f0fdf4; color: #15803d; }
.stat-rejected .stat-badge-hint { background-color: #fef2f2; color: #b91c1c; }
.stat-all .stat-badge-hint { background-color: #eef2ff; color: #4338ca; }

/* Trạng thái kích hoạt (Active filter card) */
.comment-stat-card.stat-pending.active {
    border-color: #f59e0b !important;
    box-shadow: 0 0 0 1px #f59e0b, 0 6px 16px rgba(245, 158, 11, 0.15);
    background-color: #fffdf5;
}
.comment-stat-card.stat-approved.active {
    border-color: #16a34a !important;
    box-shadow: 0 0 0 1px #16a34a, 0 6px 16px rgba(22, 163, 74, 0.15);
    background-color: #f0fdf4;
}
.comment-stat-card.stat-rejected.active {
    border-color: #ef4444 !important;
    box-shadow: 0 0 0 1px #ef4444, 0 6px 16px rgba(239, 68, 68, 0.15);
    background-color: #fff5f5;
}
.comment-stat-card.stat-all.active {
    border-color: #6366f1 !important;
    box-shadow: 0 0 0 1px #6366f1, 0 6px 16px rgba(99, 102, 241, 0.15);
    background-color: #f5f5ff;
}

/* 3. Thanh tìm kiếm & bộ lọc tích hợp (Integrated Search Toolbar) */
.comments-toolbar {
    margin-bottom: 16px;
}

.search-form-flex {
    display: flex;
    align-items: center;
    gap: 10px;
    width: 100%;
}

.search-input-wrapper {
    position: relative;
    flex: 1;
}

.search-input-wrapper .search-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #94a3b8;
    font-size: 0.88rem;
}

.search-input-wrapper .search-input {
    width: 100%;
    padding: 9px 36px 9px 36px;
    border-radius: 6px;
    border: 1px solid #cbd5e1;
    font-size: 0.9rem;
    background: #ffffff;
    color: #0f172a;
    outline: none;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-input-wrapper .search-input:focus {
    border-color: #16a34a;
    box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.15);
}

.search-input-wrapper .clear-search-btn {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #94a3b8;
    text-decoration: none;
    cursor: pointer;
}

.search-input-wrapper .clear-search-btn:hover {
    color: #dc2626;
}

.btn-search {
    background: #16a34a !important;
    border: 1px solid #16a34a !important;
    color: #ffffff !important;
    font-weight: 600;
    padding: 9px 18px;
    border-radius: 6px;
    font-size: 0.88rem;
    display: inline-flex;
    align-items: center;
    cursor: pointer;
    white-space: nowrap;
    box-shadow: 0 2px 4px rgba(22, 163, 74, 0.2);
}

.btn-search:hover {
    background: #15803d !important;
}

/* 4. Định dạng bảng bình luận (Table Layout Fitted) */
.comments-table-wrapper {
    border: 1px solid #dee2e6;
    border-radius: 8px;
    overflow: hidden;
    background: #ffffff;
}

table.comments-table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 0;
}

table.comments-table th {
    background: #f8fafc;
    color: #475569;
    font-weight: 700;
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    padding: 12px 14px;
    border-bottom: 1px solid #dee2e6;
    vertical-align: middle;
}

table.comments-table td {
    padding: 12px 14px;
    vertical-align: middle;
    border-bottom: 1px solid #f1f5f9;
}

table.comments-table tr:last-child td {
    border-bottom: none;
}

table.comments-table tr:hover td {
    background-color: #f8fafc;
}

/* Avatar chữ cái đầu */
.comment-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: #ffffff;
    font-weight: 700;
    font-size: 0.9rem;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    box-shadow: 0 2px 5px rgba(16, 185, 129, 0.25);
}

.comment-id-cell {
    text-align: center;
    font-weight: 700;
    color: #64748b;
    font-size: 0.85rem;
}

.comment-author-name {
    font-weight: 700;
    color: #0f172a;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 140px;
    font-size: 0.88rem;
}

.comment-author-email {
    font-size: 0.76rem;
    color: #64748b;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 140px;
}

.comment-time-date {
    font-weight: 600;
    font-size: 0.85rem;
    color: #1e293b;
}

.comment-time-ago {
    color: #64748b;
    font-size: 0.76rem;
}

.comments-pagination-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 16px;
    background: #f8fafc;
    border-top: 1px solid #dee2e6;
}

/* Hộp trích dẫn bình luận */
.comment-quote-box {
    background: #f8fafc;
    border-left: 3px solid #cbd5e1;
    border-radius: 4px;
    padding: 8px 12px;
    margin-bottom: 5px;
    font-size: 0.9rem;
    color: #1e293b;
    line-height: 1.45;
    word-break: break-word;
}

.comment-quote-box.border-pending { border-left-color: #f59e0b; }
.comment-quote-box.border-approved { border-left-color: #16a34a; }
.comment-quote-box.border-rejected { border-left-color: #ef4444; }

.comment-article-link {
    font-size: 0.82rem;
    color: #16a34a !important;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    max-width: 380px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-weight: 500;
}

.comment-article-link:hover {
    text-decoration: underline !important;
}

/* Badge trạng thái phong cách Dot hiện đại (Không Emoji) */
.badge-status-pill {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 0.78rem;
    font-weight: 600;
    line-height: 1.4;
    white-space: nowrap;
}

.badge-status-pill .status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    display: inline-block;
}

.badge-status-pill.status-pending {
    background-color: #fffbeb;
    color: #b45309;
    border: 1px solid #fde68a;
}
.badge-status-pill.status-pending .status-dot {
    background-color: #f59e0b;
}

.badge-status-pill.status-approved {
    background-color: #f0fdf4;
    color: #15803d;
    border: 1px solid #bbf7d0;
}
.badge-status-pill.status-approved .status-dot {
    background-color: #16a34a;
}

.badge-status-pill.status-rejected {
    background-color: #fef2f2;
    color: #b91c1c;
    border: 1px solid #fecaca;
}
.badge-status-pill.status-rejected .status-dot {
    background-color: #ef4444;
}

/* Nút hành động nhanh */
.comment-actions {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
}

.btn-action-approve {
    background: #16a34a !important;
    border: 1px solid #16a34a !important;
    color: #ffffff !important;
    padding: 5px 10px;
    font-size: 0.8rem;
    border-radius: 5px;
    font-weight: 600;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    white-space: nowrap;
}

.btn-action-approve:hover {
    background: #15803d !important;
}

.btn-action-reject {
    background: #d97706 !important;
    border: 1px solid #d97706 !important;
    color: #ffffff !important;
    padding: 5px 10px;
    font-size: 0.8rem;
    border-radius: 5px;
    font-weight: 600;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    white-space: nowrap;
}

.btn-action-reject:hover {
    background: #b45309 !important;
}

.btn-action-delete {
    background: #dc2626 !important;
    border: 1px solid #dc2626 !important;
    color: #ffffff !important;
    padding: 5px 9px;
    font-size: 0.8rem;
    border-radius: 5px;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
}

.btn-action-delete:hover {
    background: #b91c1c !important;
}

/* ================= DARK MODE OVERRIDES ================= */
[data-theme="dark"] .comments-header-row h2 {
    color: #f8fafc !important;
}

[data-theme="dark"] .status-filter-pills {
    background-color: #0f172a !important;
    border-color: #334155 !important;
}

[data-theme="dark"] .status-pill {
    color: #cbd5e1 !important;
}

[data-theme="dark"] .status-pill:hover {
    background-color: #1e293b !important;
    color: #ffffff !important;
}

[data-theme="dark"] .status-pill.active {
    background-color: #334155 !important;
    color: #ffffff !important;
}

[data-theme="dark"] .status-pill .pill-count {
    background-color: #1e293b !important;
    color: #94a3b8 !important;
}

[data-theme="dark"] .status-pill.active .pill-count {
    background-color: #16a34a !important;
    color: #ffffff !important;
}

[data-theme="dark"] .comment-stat-card {
    background-color: #1e293b !important;
    border-color: #334155 !important;
}

[data-theme="dark"] .comment-stat-card.stat-pending.active {
    background-color: #241c0c !important;
    border-color: #f59e0b !important;
    box-shadow: 0 0 0 1px #f59e0b, 0 6px 16px rgba(0, 0, 0, 0.4);
}
[data-theme="dark"] .comment-stat-card.stat-approved.active {
    background-color: #0f291e !important;
    border-color: #4ade80 !important;
    box-shadow: 0 0 0 1px #4ade80, 0 6px 16px rgba(0, 0, 0, 0.4);
}
[data-theme="dark"] .comment-stat-card.stat-rejected.active {
    background-color: #2a1215 !important;
    border-color: #f87171 !important;
    box-shadow: 0 0 0 1px #f87171, 0 6px 16px rgba(0, 0, 0, 0.4);
}
[data-theme="dark"] .comment-stat-card.stat-all.active {
    background-color: #1a1b3a !important;
    border-color: #818cf8 !important;
    box-shadow: 0 0 0 1px #818cf8, 0 6px 16px rgba(0, 0, 0, 0.4);
}

[data-theme="dark"] .stat-card-header .stat-label { color: #94a3b8 !important; }
[data-theme="dark"] .stat-card-body .stat-value { color: #f8fafc !important; }

[data-theme="dark"] .stat-pending .stat-icon-circle { background-color: rgba(245, 158, 11, 0.2) !important; color: #fbbf24 !important; }
[data-theme="dark"] .stat-approved .stat-icon-circle { background-color: rgba(22, 163, 74, 0.2) !important; color: #4ade80 !important; }
[data-theme="dark"] .stat-rejected .stat-icon-circle { background-color: rgba(239, 68, 68, 0.2) !important; color: #f87171 !important; }
[data-theme="dark"] .stat-all .stat-icon-circle { background-color: rgba(99, 102, 241, 0.2) !important; color: #a5b4fc !important; }

[data-theme="dark"] .stat-pending .stat-badge-hint { background-color: rgba(245, 158, 11, 0.15) !important; color: #fbbf24 !important; }
[data-theme="dark"] .stat-approved .stat-badge-hint { background-color: rgba(22, 163, 74, 0.15) !important; color: #4ade80 !important; }
[data-theme="dark"] .stat-rejected .stat-badge-hint { background-color: rgba(239, 68, 68, 0.15) !important; color: #f87171 !important; }
[data-theme="dark"] .stat-all .stat-badge-hint { background-color: rgba(99, 102, 241, 0.15) !important; color: #a5b4fc !important; }

[data-theme="dark"] .comments-table-wrapper {
    background-color: #1e293b !important;
    border-color: #334155 !important;
}

[data-theme="dark"] .search-input-wrapper .search-input {
    background-color: #1e293b !important;
    border-color: #475569 !important;
    color: #f8fafc !important;
}

[data-theme="dark"] table.comments-table th {
    background-color: #162032 !important;
    color: #cbd5e1 !important;
    border-color: #334155 !important;
}

[data-theme="dark"] table.comments-table td {
    background-color: #1e293b !important;
    border-color: #334155 !important;
    color: #cbd5e1 !important;
}

[data-theme="dark"] table.comments-table tr:hover td {
    background-color: #243248 !important;
}

[data-theme="dark"] .comment-quote-box {
    background-color: #162032 !important;
    color: #e2e8f0 !important;
}

[data-theme="dark"] .comment-article-link {
    color: #4ade80 !important;
}

[data-theme="dark"] .badge-status-pill.status-pending {
    background-color: rgba(245, 158, 11, 0.12) !important;
    color: #fbbf24 !important;
    border-color: rgba(245, 158, 11, 0.25) !important;
}
[data-theme="dark"] .badge-status-pill.status-approved {
    background-color: rgba(22, 163, 74, 0.12) !important;
    color: #4ade80 !important;
    border-color: rgba(22, 163, 74, 0.25) !important;
}
[data-theme="dark"] .badge-status-pill.status-rejected {
    background-color: rgba(239, 68, 68, 0.12) !important;
    color: #f87171 !important;
    border-color: rgba(239, 68, 68, 0.25) !important;
}

[data-theme="dark"] .comment-id-cell {
    color: #94a3b8 !important;
}

[data-theme="dark"] .comment-author-name {
    color: #f8fafc !important;
}

[data-theme="dark"] .comment-author-email {
    color: #94a3b8 !important;
}

[data-theme="dark"] .comment-time-date {
    color: #f8fafc !important;
}

[data-theme="dark"] .comment-time-ago {
    color: #94a3b8 !important;
}

[data-theme="dark"] .comments-pagination-bar {
    background-color: #162032 !important;
    border-top-color: #334155 !important;
}

[data-theme="dark"] .comments-pagination-bar .page-link {
    background-color: #1e293b;
    border-color: #334155;
    color: #cbd5e1;
}

[data-theme="dark"] .comments-pagination-bar .page-item.active .page-link {
    background-color: #16a34a;
    border-color: #16a34a;
    color: #ffffff;
}

[data-theme="dark"] #deleteCommentModal .modal-content {
    background-color: #1e293b;
    color: #f8fafc;
    border: 1px solid #334155 !important;
}

[data-theme="dark"] #deleteCommentModal .modal-body p {
    color: #f8fafc !important;
}

[data-theme="dark"] #deleteCommentModal .modal-body .bg-light {
    background-color: #0f172a !important;
    border-color: #334155 !important;
    color: #cbd5e1 !important;
}

[data-theme="dark"] #deleteCommentModal .modal-body strong {
    color: #f8fafc !important;
}

[data-theme="dark"] #deleteCommentModal .modal-footer {
    background-color: #162032 !important;
    border-top-color: #334155 !important;
}
</style>

<div class="comments-page-wrapper">
    <!-- Thông báo hệ thống Flash Message -->
    <div class="message-container">
        <c:if test="${not empty sessionScope.message}">
            <div class="message-success">
                <i class="fa-solid fa-circle-check me-2"></i>${sessionScope.message}
            </div>
            <c:remove var="message" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.error}">
            <div class="message-error">
                <i class="fa-solid fa-circle-exclamation me-2"></i>${sessionScope.error}
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>
    </div>

    <!-- Header & Bộ nút chuyển trạng thái -->
    <div class="comments-header-row">
        <div>
            <h2><i class="fa-solid fa-clipboard-check text-success me-2"></i>Kiểm Duyệt Bình Luận</h2>
            <div class="subtitle">Xem xét và phê duyệt ý kiến của độc giả trước khi hiển thị trên website</div>
        </div>

        <!-- Segmented Status Pills (Thuần chữ và số lượng, không Icon) -->
        <div class="status-filter-pills">
            <a href="${pageContext.request.contextPath}/admin/comments?status=0${not empty keyword ? '&keyword='.concat(keyword) : ''}" 
               class="status-pill ${currentStatus == 0 ? 'active' : ''}">
                Chờ duyệt <span class="pill-count">${countPending}</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/comments?status=1${not empty keyword ? '&keyword='.concat(keyword) : ''}" 
               class="status-pill ${currentStatus == 1 ? 'active' : ''}">
                Đã duyệt <span class="pill-count">${countApproved}</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/comments?status=2${not empty keyword ? '&keyword='.concat(keyword) : ''}" 
               class="status-pill ${currentStatus == 2 ? 'active' : ''}">
                Từ chối <span class="pill-count">${countRejected}</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/comments?status=-1${not empty keyword ? '&keyword='.concat(keyword) : ''}" 
               class="status-pill ${currentStatus == -1 ? 'active' : ''}">
                Tất cả <span class="pill-count">${countAll}</span>
            </a>
        </div>
    </div>
    <hr class="mt-2 mb-3">

    <!-- 1. Dải 4 Thẻ Thống Kê Nhanh (Click để lọc tức thì - Chuẩn Metric Card Hiện Đại) -->
    <div class="comments-stats-strip">
        <a href="${pageContext.request.contextPath}/admin/comments?status=0" 
           class="comment-stat-card stat-pending ${currentStatus == 0 ? 'active' : ''}"
           title="Lọc danh sách Chờ kiểm duyệt">
            <div class="stat-card-header">
                <span class="stat-label">Chờ duyệt</span>
                <div class="stat-icon-circle">
                    <i class="fa-solid fa-hourglass-half"></i>
                </div>
            </div>
            <div class="stat-card-body">
                <span class="stat-value">${countPending}</span>
                <span class="stat-badge-hint">Cần xử lý</span>
            </div>
        </a>

        <a href="${pageContext.request.contextPath}/admin/comments?status=1" 
           class="comment-stat-card stat-approved ${currentStatus == 1 ? 'active' : ''}"
           title="Lọc danh sách Đã phê duyệt">
            <div class="stat-card-header">
                <span class="stat-label">Đã phê duyệt</span>
                <div class="stat-icon-circle">
                    <i class="fa-solid fa-circle-check"></i>
                </div>
            </div>
            <div class="stat-card-body">
                <span class="stat-value">${countApproved}</span>
                <span class="stat-badge-hint">Đang hiển thị</span>
            </div>
        </a>

        <a href="${pageContext.request.contextPath}/admin/comments?status=2" 
           class="comment-stat-card stat-rejected ${currentStatus == 2 ? 'active' : ''}"
           title="Lọc danh sách Đã từ chối">
            <div class="stat-card-header">
                <span class="stat-label">Đã từ chối</span>
                <div class="stat-icon-circle">
                    <i class="fa-solid fa-circle-xmark"></i>
                </div>
            </div>
            <div class="stat-card-body">
                <span class="stat-value">${countRejected}</span>
                <span class="stat-badge-hint">Đã ẩn</span>
            </div>
        </a>

        <a href="${pageContext.request.contextPath}/admin/comments?status=-1" 
           class="comment-stat-card stat-all ${currentStatus == -1 ? 'active' : ''}"
           title="Xem tất cả bình luận">
            <div class="stat-card-header">
                <span class="stat-label">Tổng số ý kiến</span>
                <div class="stat-icon-circle">
                    <i class="fa-solid fa-comments"></i>
                </div>
            </div>
            <div class="stat-card-body">
                <span class="stat-value">${countAll}</span>
                <span class="stat-badge-hint">Toàn bộ</span>
            </div>
        </a>
    </div>

    <!-- 2. Thanh tìm kiếm & Làm mới -->
    <div class="comments-toolbar">
        <form method="get" action="${pageContext.request.contextPath}/admin/comments" class="search-form-flex">
            <input type="hidden" name="status" value="${currentStatus}">
            <div class="search-input-wrapper">
                <i class="fa-solid fa-magnifying-glass search-icon"></i>
                <input type="text" name="keyword" value="<c:out value='${keyword}'/>" 
                       class="search-input" 
                       placeholder="Tìm kiếm theo tên độc giả, email hoặc nội dung bình luận...">
                <c:if test="${not empty keyword}">
                    <a href="${pageContext.request.contextPath}/admin/comments?status=${currentStatus}" class="clear-search-btn" title="Xóa tìm kiếm">
                        <i class="fa-solid fa-circle-xmark"></i>
                    </a>
                </c:if>
            </div>
            <button type="submit" class="btn-search">
                <i class="fa-solid fa-magnifying-glass me-1"></i>Tìm kiếm
            </button>
            <c:if test="${not empty keyword || currentStatus != 0}">
                <a href="${pageContext.request.contextPath}/admin/comments" class="btn btn-reset" style="padding: 9px 16px; font-size: 0.88rem; white-space: nowrap;">
                    <i class="fa-solid fa-rotate me-1"></i>Làm mới
                </a>
            </c:if>
        </form>
    </div>

    <!-- 3. Bảng Danh sách Bình luận -->
    <div class="comments-table-wrapper">
        <table class="comments-table">
            <thead>
                <tr>
                    <th style="width: 45px; text-align: center;">#</th>
                    <th style="width: 180px;">Độc giả</th>
                    <th>Nội dung bình luận & Bản tin</th>
                    <th style="width: 125px; text-align: center;">Thời gian</th>
                    <th style="width: 110px; text-align: center;">Trạng thái</th>
                    <th style="width: 145px; text-align: center;">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${comments}">
                    <tr>
                        <td class="comment-id-cell">
                            ${c.id}
                        </td>
                        <td>
                            <div style="display: flex; align-items: center; gap: 10px;">
                                <div class="comment-avatar">
                                    ${c.avatarInitial}
                                </div>
                                <div style="min-width: 0;">
                                    <div class="comment-author-name" title="${c.authorName}">
                                        <c:out value="${c.authorName}"/>
                                    </div>
                                    <div class="comment-author-email" title="${c.authorEmail}">
                                        <c:out value="${not empty c.authorEmail ? c.authorEmail : 'Không để lại email'}"/>
                                    </div>
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="comment-quote-box ${c.status == 1 ? 'border-approved' : (c.status == 2 ? 'border-rejected' : 'border-pending')}">
                                <c:out value="${c.content}"/>
                            </div>
                            <div style="display: flex; align-items: center; gap: 4px;">
                                <i class="fa-regular fa-newspaper text-muted" style="font-size: 0.78rem;"></i>
                                <span style="font-size: 0.78rem;" class="text-muted">Bài viết:</span>
                                <a href="${pageContext.request.contextPath}/detail?id=${c.newsId}" target="_blank" class="comment-article-link" title="Xem bài viết gốc">
                                    <c:out value="${not empty c.newsTitle ? c.newsTitle : c.newsId}"/>
                                    <i class="fa-solid fa-arrow-up-right-from-square ms-1" style="font-size: 0.72rem;"></i>
                                </a>
                            </div>
                        </td>
                        <td style="text-align: center;">
                            <div class="comment-time-date">
                                <fmt:formatDate value="${c.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
                            </div>
                            <small class="comment-time-ago">${c.timeAgo}</small>
                        </td>
                        <td style="text-align: center;">
                            <c:choose>
                                <c:when test="${c.status == 1}">
                                    <span class="badge-status-pill status-approved">
                                        <span class="status-dot"></span>Đã duyệt
                                    </span>
                                </c:when>
                                <c:when test="${c.status == 2}">
                                    <span class="badge-status-pill status-rejected">
                                        <span class="status-dot"></span>Đã từ chối
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge-status-pill status-pending">
                                        <span class="status-dot"></span>Chờ duyệt
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td style="text-align: center;">
                            <div class="comment-actions">
                                <c:if test="${c.status != 1}">
                                    <a href="${pageContext.request.contextPath}/admin/comments?action=approve&id=${c.id}&status=${currentStatus}" 
                                       class="btn-action-approve" 
                                       title="Phê duyệt">
                                        <i class="fa-solid fa-check"></i> Duyệt
                                    </a>
                                </c:if>
                                <c:if test="${c.status != 2}">
                                    <a href="${pageContext.request.contextPath}/admin/comments?action=reject&id=${c.id}&status=${currentStatus}" 
                                       class="btn-action-reject" 
                                       title="Từ chối">
                                        <i class="fa-solid fa-xmark"></i> Từ chối
                                    </a>
                                </c:if>
                                <button type="button" 
                                        class="btn-action-delete" 
                                        onclick="confirmDeleteComment('${c.id}', '${fn:escapeXml(c.authorName)}')"
                                        title="Xóa vĩnh viễn">
                                    <i class="fa-regular fa-trash-can"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty comments}">
                    <tr>
                        <td colspan="6" style="text-align: center; padding: 48px 20px;">
                            <div style="display: flex; flex-direction: column; align-items: center; justify-content: center;">
                                <i class="fa-regular fa-comment-dots fa-3x mb-3 text-secondary" style="opacity: 0.35;"></i>
                                <h5 style="color: #475569; font-weight: 600; margin-bottom: 4px;">Không tìm thấy bình luận nào</h5>
                                <p style="color: #94a3b8; font-size: 0.88rem; margin-bottom: 0;">Hiện chưa có dữ liệu bình luận phù hợp với trạng thái hoặc từ khóa tìm kiếm đã chọn.</p>
                            </div>
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <!-- Phân Trang (Pagination) -->
        <c:if test="${totalPages > 1}">
            <div class="comments-pagination-bar">
                <span class="small text-muted">
                    Hiển thị <strong>${fn:length(comments)}</strong> / <strong>${totalItems}</strong> bình luận
                </span>
                <nav aria-label="Comment pagination">
                    <ul class="pagination pagination-sm mb-0">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/comments?status=${currentStatus}&keyword=${keyword}&page=${currentPage - 1}">
                                <i class="fa-solid fa-chevron-left"></i>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="p">
                            <li class="page-item ${currentPage == p ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/comments?status=${currentStatus}&keyword=${keyword}&page=${p}">${p}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/comments?status=${currentStatus}&keyword=${keyword}&page=${currentPage + 1}">
                                <i class="fa-solid fa-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        </c:if>
    </div>
</div>

<!-- Modal Xác Nhận Xóa Bình Luận -->
<div class="modal fade" id="deleteCommentModal" tabindex="-1" aria-labelledby="deleteCommentModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg" style="border-radius: 12px; overflow: hidden;">
            <div class="modal-header bg-danger text-white py-3 px-4">
                <h5 class="modal-title fs-6 fw-bold mb-0" id="deleteCommentModalLabel">
                    <i class="fa-solid fa-triangle-exclamation me-2"></i>Xác Nhận Xóa Bình Luận
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Đóng"></button>
            </div>
            <div class="modal-body p-4">
                <p class="mb-2 text-dark fw-medium">Bạn có chắc chắn muốn xóa vĩnh viễn bình luận này không?</p>
                <div class="p-3 bg-light rounded-3 border text-muted small">
                    <strong class="text-dark d-block mb-1 fs-6" id="modalAuthorName"></strong>
                    Hành động này sẽ xóa vĩnh viễn bình luận và không thể hoàn tác.
                </div>
            </div>
            <div class="modal-footer bg-light py-2.5 px-4 border-top">
                <button type="button" class="btn btn-sm btn-secondary px-3" data-bs-dismiss="modal">Hủy bỏ</button>
                <a href="#" id="modalDeleteBtn" class="btn btn-sm btn-danger px-3">
                    <i class="fa-regular fa-trash-can me-1"></i>Xác nhận xóa
                </a>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDeleteComment(commentId, authorName) {
    var modalEl = document.getElementById('deleteCommentModal');
    var authorEl = document.getElementById('modalAuthorName');
    var btnEl = document.getElementById('modalDeleteBtn');
    
    if (authorEl) authorEl.textContent = "Bình luận của: " + authorName;
    if (btnEl) {
        btnEl.href = "${pageContext.request.contextPath}/admin/comments?action=delete&id=" + commentId + "&status=${currentStatus}";
    }
    
    var modal = new bootstrap.Modal(modalEl);
    modal.show();
}
</script>
