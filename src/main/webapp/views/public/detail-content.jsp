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
                <c:if test="${not empty newsCategory}">
                    <span class="mx-1">•</span>
                    <a href="${pageContext.request.contextPath}/category/${newsCategory.slug}" class="badge bg-primary-subtle text-primary border border-primary-subtle px-2 py-1 text-decoration-none" title="Xem chuyên mục ${newsCategory.name}">
                        <i class="fas fa-tag me-1"></i>${newsCategory.name}
                    </a>
                </c:if>
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

                <!-- Bookmark / Lưu bài viết -->
                <button type="button" class="btn btn-sm btn-outline-success rounded-pill bookmark-btn ms-1" id="bookmarkBtn" 
                        data-id="${news.id}" 
                        data-title="<c:out value='${news.title}' />" 
                        data-image="${news.image}" 
                        data-category="${news.categoryId}"
                        data-date="<fmt:formatDate value='${news.postedDate}' pattern='dd/MM/yyyy' />" 
                        title="Lưu bài viết vào danh sách đọc sau">
                    <i class="far fa-bookmark me-1" id="bookmarkIcon"></i>
                    <span id="bookmarkText">Lưu bài viết</span>
                </button>
                
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
            
            <!-- KHU VỰC BÌNH LUẬN & Ý KIẾN ĐỘC GIẢ (MODERATED COMMENTS) -->
            <div class="comments-section mt-5 pt-4 border-top" id="comments-section">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h4 class="fw-bold mb-0 text-dark">
                        <i class="fas fa-comments text-success me-2"></i>Ý Kiến Độc Giả 
                        <span class="badge bg-success bg-opacity-10 text-success fs-6 rounded-pill ms-2">${commentCount}</span>
                    </h4>
                    <small class="text-muted"><i class="fas fa-shield-alt text-success me-1"></i>Kiểm duyệt văn minh</small>
                </div>

                <%-- Thông báo gửi bình luận --%>
                <c:if test="${not empty sessionScope.commentSuccess}">
                    <div class="alert alert-success alert-dismissible fade show shadow-sm d-flex align-items-center mb-4" role="alert">
                        <i class="fas fa-check-circle fs-5 me-2 flex-shrink-0"></i>
                        <div>${sessionScope.commentSuccess}</div>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="commentSuccess" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.commentError}">
                    <div class="alert alert-danger alert-dismissible fade show shadow-sm d-flex align-items-center mb-4" role="alert">
                        <i class="fas fa-exclamation-circle fs-5 me-2 flex-shrink-0"></i>
                        <div>${sessionScope.commentError}</div>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="commentError" scope="session"/>
                </c:if>

                <!-- Form Gửi Bình Luận -->
                <div class="card border-0 shadow-sm rounded-3 mb-4 bg-light">
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/comment" method="post" id="commentSubmitForm">
                            <input type="hidden" name="newsId" value="${news.id}">
                            <div class="row g-3 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label small fw-semibold text-dark mb-1">
                                        <i class="fas fa-user text-muted me-1"></i>Họ và tên <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" name="authorName" class="form-control" placeholder="Họ và tên của bạn" required maxlength="100">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label small fw-semibold text-dark mb-1">
                                        <i class="fas fa-envelope text-muted me-1"></i>Email
                                    </label>
                                    <input type="email" name="authorEmail" class="form-control" placeholder="name@example.com (tùy chọn)" maxlength="150">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-dark mb-1">
                                    <i class="fas fa-pen text-muted me-1"></i>Nội dung bình luận <span class="text-danger">*</span>
                                </label>
                                <textarea name="content" class="form-control" rows="3" placeholder="Viết bình luận của bạn..." required maxlength="1000"></textarea>
                            </div>
                            <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-2">
                                <small class="text-muted">
                                    <i class="fas fa-shield-halved me-1 text-success"></i>Bình luận sẽ được kiểm duyệt trước khi hiển thị.
                                </small>
                                <button type="submit" class="btn btn-success px-4 fw-semibold">
                                    <i class="fas fa-paper-plane me-1"></i> Gửi bình luận
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Danh Sách Bình Luận Đã Duyệt -->
                <div class="approved-comments-wrapper mb-5">
                    <c:forEach var="c" items="${comments}">
                        <div class="comment-item p-3 mb-3 bg-white rounded-3 border shadow-sm">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <div class="d-flex align-items-center">
                                    <div class="rounded-circle bg-success bg-opacity-10 text-success fw-bold d-flex align-items-center justify-content-center me-2"
                                         style="width: 36px; height: 36px; font-size: 0.9rem;">
                                        ${c.avatarInitial}
                                    </div>
                                    <div>
                                        <span class="fw-bold text-dark">${c.authorName}</span>
                                        <span class="badge bg-light text-muted border ms-2" style="font-size: 0.75rem;">Độc giả</span>
                                    </div>
                                </div>
                                <small class="text-muted">
                                    <i class="far fa-clock me-1"></i><fmt:formatDate value="${c.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </small>
                            </div>
                            <div class="comment-body ps-5 text-secondary" style="font-size: 0.95rem; line-height: 1.6;">
                                <c:out value="${c.content}"/>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty comments}">
                        <div class="text-center py-4 bg-light rounded-3 border text-muted">
                            <i class="far fa-comment-dots fa-2x mb-2 text-secondary opacity-50"></i>
                            <p class="mb-0 small">Chưa có bình luận nào cho bài viết này. Hãy là người đầu tiên chia sẻ góc nhìn!</p>
                        </div>
                    </c:if>
                </div>
            </div>
            
            <!-- BÀI VIẾT CÙNG CHUYÊN MỤC -->
            <div class="section-title mt-4">
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