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
            
            <!-- Thông tin meta chuẩn báo chí hiện đại -->
            <div class="d-flex flex-wrap align-items-center gap-2 mb-3 text-muted small">
                <span><fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                <span class="mx-1">•</span>
                <span>Tác giả: <strong>${not empty news.author ? news.author : 'Ban Biên Tập'}</strong></span>
                <span class="mx-1">•</span>
                <span>${news.viewCount} lượt xem</span>
                <span class="mx-1">•</span>
                <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1" id="readingTimeBadge" title="Thời gian đọc ước tính">
                    <span id="readingTimeText">1 phút đọc</span>
                </span>
            </div>
            
            <!-- THANH CHIA SẺ MẠNG XÃ HỘI NHANH (Social Share Bar) -->
            <div class="social-share-bar d-flex align-items-center flex-wrap gap-2 p-2 px-3 mb-4 bg-light rounded-3 border">
                <span class="fw-semibold text-muted small me-2">Chia sẻ:</span>
                
                <!-- Facebook -->
                <a href="javascript:void(0)" class="btn btn-sm btn-outline-primary rounded-pill share-btn" id="shareFacebook" title="Chia sẻ lên Facebook">
                    <i class="fab fa-facebook-f me-1"></i>Facebook
                </a>
                
                <!-- Twitter / X -->
                <a href="javascript:void(0)" class="btn btn-sm btn-outline-dark rounded-pill share-btn" id="shareTwitter" title="Chia sẻ lên X (Twitter)">
                    <i class="fab fa-x-twitter me-1"></i>Twitter
                </a>
                
                <!-- Telegram -->
                <a href="javascript:void(0)" class="btn btn-sm btn-outline-info rounded-pill share-btn" id="shareTelegram" title="Chia sẻ qua Telegram">
                    <i class="fab fa-telegram-plane me-1"></i>Telegram
                </a>
                
                <!-- Sao chép link -->
                <button type="button" class="btn btn-sm btn-outline-secondary rounded-pill ms-auto" id="copyLinkBtn" title="Sao chép liên kết bài viết">
                    <span id="copyLinkText">Sao chép liên kết</span>
                </button>
            </div>
            
            <!-- Ảnh tiêu điểm -->
            <c:if test="${not empty news.image}">
                <c:set var="imageUrl" value="${news.image}" />
                <c:if test="${!fn:startsWith(imageUrl, pageContext.request.contextPath) && fn:startsWith(imageUrl, '/')}">
                    <c:set var="imageUrl" value="${pageContext.request.contextPath}${news.image}" />
                </c:if>
                <div class="mb-4">
                    <img src="${imageUrl}" alt="${news.title}" 
                         class="img-fluid rounded-3 shadow-sm w-100"
                         style="max-height: 520px; object-fit: cover;"
                         onerror="this.src='https://placehold.co/800x400?text=ABC+News'">
                </div>
            </c:if>

            <!-- Tóm tắt Sapo -->
            <c:if test="${not empty news.summary}">
                <div class="alert alert-light border-start border-4 border-success mb-4 p-3 rounded-2">
                    <p class="mb-0 fst-italic fw-semibold text-secondary" style="line-height: 1.7;">${news.summary}</p>
                </div>
            </c:if>
            
            <!-- NỘI DUNG CHÍNH -->
            <div class="news-full-content mb-5" style="line-height: 1.85; font-size: 1.08rem; color: var(--brand-text-body);">
                ${news.content}
            </div>
            
            <!-- BÀI VIẾT CÙNG CHUYÊN MỤC -->
            <div class="section-title mt-5">
                <span>Bài viết cùng chuyên mục</span>
            </div>
            <div class="list-group list-group-flush border rounded-3 overflow-hidden shadow-sm">
                <c:forEach var="related" items="${relatedNews}">
                    <a href="${pageContext.request.contextPath}/detail?id=${related.id}" class="list-group-item list-group-item-action p-3">
                        <div class="d-flex w-100 justify-content-between align-items-center">
                            <h6 class="mb-0 fw-semibold text-dark">${related.title}</h6>
                            <small class="text-muted ms-3 text-nowrap">
                                <fmt:formatDate value="${related.postedDate}" pattern="dd/MM/yyyy"/>
                            </small>
                        </div>
                    </a>
                </c:forEach>
                <c:if test="${empty relatedNews}">
                    <div class="list-group-item text-muted p-3">Không có bài viết liên quan trong chuyên mục này.</div>
                </c:if>
            </div>
        </article>
    </c:when>
    
    <%-- 2. Nếu không tìm thấy bản tin (news == null) --%>
    <c:otherwise>
        <div class="alert alert-warning text-center py-5">
            <i class="fas fa-exclamation-triangle fa-3x mb-3 text-warning"></i>
            <h2>Không tìm thấy bản tin</h2>
            <p>Bản tin bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-success">
                <i class="fas fa-home me-1"></i>Quay về trang chủ
            </a>
        </div>
    </c:otherwise>
</c:choose>

<%-- Script tính thời gian đọc & chia sẻ mạng xã hội --%>
<script>
(function() {
    // 1. Tính toán ước tính thời gian đọc
    function calculateReadingTime() {
        var contentEl = document.querySelector('.news-full-content');
        var timeEl = document.getElementById('readingTimeText');
        if (!contentEl || !timeEl) return;

        var text = contentEl.innerText || contentEl.textContent || '';
        var words = text.trim().split(/\s+/).filter(function(w) { return w.length > 0; }).length;
        // Tốc độ đọc trung bình tiếng Việt khoảng 200 từ/phút
        var minutes = Math.max(1, Math.round(words / 200));
        timeEl.textContent = minutes + ' phút đọc (' + words + ' từ)';
    }

    // 2. Thiết lập nút chia sẻ mạng xã hội
    function setupSocialShare() {
        var currentUrl = encodeURIComponent(window.location.href);
        var pageTitle = encodeURIComponent(document.title);

        var fbBtn = document.getElementById('shareFacebook');
        if (fbBtn) {
            fbBtn.addEventListener('click', function(e) {
                e.preventDefault();
                window.open('https://www.facebook.com/sharer/sharer.php?u=' + currentUrl, 'fb-share', 'width=600,height=500,scrollbars=yes');
            });
        }

        var twitterBtn = document.getElementById('shareTwitter');
        if (twitterBtn) {
            twitterBtn.addEventListener('click', function(e) {
                e.preventDefault();
                window.open('https://twitter.com/intent/tweet?url=' + currentUrl + '&text=' + pageTitle, 'twitter-share', 'width=600,height=500,scrollbars=yes');
            });
        }

        var telegramBtn = document.getElementById('shareTelegram');
        if (telegramBtn) {
            telegramBtn.addEventListener('click', function(e) {
                e.preventDefault();
                window.open('https://t.me/share/url?url=' + currentUrl + '&text=' + pageTitle, 'telegram-share', 'width=600,height=500,scrollbars=yes');
            });
        }

        // Sao chép link vào clipboard
        var copyBtn = document.getElementById('copyLinkBtn');
        var copyText = document.getElementById('copyLinkText');
        if (copyBtn && copyText) {
            copyBtn.addEventListener('click', function(e) {
                e.preventDefault();
                if (navigator.clipboard && window.isSecureContext) {
                    navigator.clipboard.writeText(window.location.href).then(showCopiedSuccess);
                } else {
                    // Fallback
                    var dummy = document.createElement('textarea');
                    document.body.appendChild(dummy);
                    dummy.value = window.location.href;
                    dummy.select();
                    document.execCommand('copy');
                    document.body.removeChild(dummy);
                    showCopiedSuccess();
                }
            });
        }

        function showCopiedSuccess() {
            copyBtn.classList.remove('btn-outline-secondary');
            copyBtn.classList.add('btn-success');
            copyText.textContent = 'Đã sao chép!';
            setTimeout(function() {
                copyBtn.classList.remove('btn-success');
                copyBtn.classList.add('btn-outline-secondary');
                copyText.textContent = 'Sao chép link';
            }, 2500);
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            calculateReadingTime();
            setupSocialShare();
        });
    } else {
        calculateReadingTime();
        setupSocialShare();
    }
})();
</script>