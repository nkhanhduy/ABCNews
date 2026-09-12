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
            
            <!-- Thông tin meta -->
            <div class="d-flex flex-wrap align-items-center gap-3 mb-3 text-muted">
                <span><i class="fas fa-calendar-alt me-1"></i><fmt:formatDate value="${news.postedDate}" pattern="dd/MM/yyyy HH:mm" /></span>
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
                <span><i class="fas fa-eye me-1"></i>${news.viewCount} lượt xem</span>
                
                <!-- Ước tính thời gian đọc bài viết -->
                <span class="badge bg-light text-secondary border d-inline-flex align-items-center" id="readingTimeBadge" title="Thời gian đọc ước tính">
                    <i class="fas fa-clock text-primary me-1"></i>
                    <span id="readingTimeText">1 phút đọc</span>
                </span>
            </div>
            
            <!-- THANH CHIA SẺ MẠNG XÃ HỘI NHANH (Social Share Bar) -->
            <div class="social-share-bar d-flex align-items-center flex-wrap gap-2 p-2 px-3 mb-4 bg-light rounded-3 border">
                <span class="fw-semibold text-muted small me-2"><i class="fas fa-share-alt me-1"></i>Chia sẻ:</span>
                
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
                    <i class="fas fa-link me-1"></i><span id="copyLinkText">Sao chép link</span>
                </button>
            </div>
            
            <!-- Ảnh (nếu có) -->
            <c:if test="${not empty news.image}">
                <c:set var="imageUrl" value="${news.image}" />
                <c:if test="${!fn:startsWith(imageUrl, pageContext.request.contextPath) && fn:startsWith(imageUrl, '/')}">
                    <c:set var="imageUrl" value="${pageContext.request.contextPath}${news.image}" />
                </c:if>
                <div class="mb-4">
                    <img src="${imageUrl}" alt="${news.title}" 
                         class="img-fluid rounded shadow-sm"
                         onerror="this.src='https://placehold.co/800x400?text=Image+Not+Found'">
                </div>
            </c:if>

            <!-- Tóm tắt -->
            <div class="alert alert-light border-start border-4 border-primary mb-4">
                <p class="mb-0 fst-italic fw-bold">${news.summary}</p>
            </div>
            
            <!-- NỘI DUNG CHÍNH -->
            <div class="news-full-content mb-5" style="line-height: 1.8; font-size: 1.1rem;">
                ${news.content}
            </div>
            
            <hr class="my-5">
            
            <!-- TIN CÙNG LOẠI -->
            <div class="card shadow-sm">
                <div class="card-header bg-secondary text-white">
                    <h5 class="mb-0"><i class="fas fa-list me-2"></i>Tin cùng loại</h5>
                </div>
                <div class="list-group list-group-flush">
                    <c:forEach var="related" items="${relatedNews}">
                        <a href="${pageContext.request.contextPath}/detail?id=${related.id}" class="list-group-item list-group-item-action">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1">${related.title}</h6>
                                <small class="text-muted">
                                    <fmt:formatDate value="${related.postedDate}" pattern="dd/MM/yyyy"/>
                                </small>
                            </div>
                        </a>
                    </c:forEach>
                    <c:if test="${empty relatedNews}">
                        <div class="list-group-item text-muted">Không có tin nào cùng loại.</div>
                    </c:if>
                </div>
            </div>
        </article>
    </c:when>
    
    <%-- 2. Nếu không tìm thấy bản tin (news == null) --%>
    <c:otherwise>
        <div class="alert alert-warning text-center py-5">
            <i class="fas fa-exclamation-triangle fa-3x mb-3 text-warning"></i>
            <h2>Không tìm thấy bản tin</h2>
            <p>Bản tin bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
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