/**
 * Quản lý danh sách bài viết đã lưu (Bookmarks / Read Later)
 * Lưu trữ độc lập trên localStorage của trình duyệt, không yêu cầu đăng nhập.
 * 
 * @author ABCNews Development Team
 */
(function() {
    const STORAGE_KEY = 'abcnews_saved_bookmarks';

    function getBookmarks() {
        try {
            const data = localStorage.getItem(STORAGE_KEY);
            return data ? JSON.parse(data) : [];
        } catch (e) {
            console.error('Lỗi đọc bookmarks từ localStorage:', e);
            return [];
        }
    }

    function saveBookmarks(list) {
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
            updateBadges();
            renderDrawer();
        } catch (e) {
            console.error('Lỗi lưu bookmarks vào localStorage:', e);
        }
    }

    function isBookmarked(newsId) {
        if (!newsId) return false;
        const list = getBookmarks();
        return list.some(item => item.id === newsId);
    }

    function toggleBookmark(article) {
        if (!article || !article.id) return false;
        let list = getBookmarks();
        const existingIndex = list.findIndex(item => item.id === article.id);
        let nowSaved = false;

        if (existingIndex >= 0) {
            // Đã lưu -> Hủy lưu
            list.splice(existingIndex, 1);
            nowSaved = false;
        } else {
            // Chưa lưu -> Thêm vào đầu danh sách
            list.unshift({
                id: article.id,
                title: article.title,
                image: article.image,
                category: article.category || 'Tin tức',
                date: article.date || new Date().toLocaleDateString('vi-VN'),
                url: article.url || ('/detail?id=' + encodeURIComponent(article.id)),
                savedAt: Date.now()
            });
            nowSaved = true;
        }

        saveBookmarks(list);
        updateDetailButtonState(nowSaved);
        return nowSaved;
    }

    function updateBadges() {
        const list = getBookmarks();
        const count = list.length;
        const badge = document.getElementById('bookmarksCountBadge');
        const offBadge = document.getElementById('offcanvasBadge');
        const footer = document.getElementById('bookmarksFooter');

        if (badge) {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'inline-block' : 'none';
        }
        if (offBadge) {
            offBadge.textContent = count + ' bài';
        }
        if (footer) {
            if (count > 0) {
                footer.style.setProperty('display', 'flex', 'important');
            } else {
                footer.style.setProperty('display', 'none', 'important');
            }
        }
    }

    function updateDetailButtonState(saved) {
        const btn = document.getElementById('bookmarkBtn');
        const icon = document.getElementById('bookmarkIcon');
        const text = document.getElementById('bookmarkText');
        if (!btn) return;

        if (saved) {
            btn.classList.remove('btn-outline-success');
            btn.classList.add('btn-success');
            if (icon) {
                icon.className = 'fas fa-bookmark me-1';
            }
            if (text) {
                text.textContent = 'Đã lưu bài';
            }
            btn.title = 'Bấm để bỏ lưu bài viết này';
        } else {
            btn.classList.remove('btn-success');
            btn.classList.add('btn-outline-success');
            if (icon) {
                icon.className = 'far fa-bookmark me-1';
            }
            if (text) {
                text.textContent = 'Lưu bài viết';
            }
            btn.title = 'Lưu bài viết vào danh sách đọc sau';
        }
    }

    function renderDrawer() {
        const container = document.getElementById('bookmarksListContainer');
        if (!container) return;

        const list = getBookmarks();
        if (list.length === 0) {
            container.innerHTML = `
                <div class="text-center py-5 text-muted">
                    <div class="mb-3" style="font-size: 3rem; opacity: 0.35;">
                        <i class="far fa-bookmark"></i>
                    </div>
                    <h6 class="fw-bold text-secondary mb-1">Chưa có bài viết nào được lưu</h6>
                    <p class="small text-muted mb-0">Khi đọc báo, bạn bấm <strong>"Lưu bài viết"</strong> để dễ dàng tìm lại và đọc sau.</p>
                </div>
            `;
            return;
        }

        let html = '<div class="list-group list-group-flush gap-2">';
        list.forEach(item => {
            html += `
                <div class="list-group-item p-2.5 rounded-3 border d-flex gap-2.5 align-items-center position-relative shadow-sm" style="transition: transform 0.15s ease;">
                    ${item.image ? `
                        <div class="flex-shrink-0 rounded-2 overflow-hidden" style="width: 72px; height: 54px; background: #eee;">
                            <img src="${item.image}" alt="${escapeHtml(item.title)}" style="width: 100%; height: 100%; object-fit: cover;" onerror="this.style.display='none';">
                        </div>
                    ` : ''}
                    <div class="flex-grow-1 min-w-0 pe-2">
                        <a href="${item.url}" class="text-decoration-none text-dark fw-semibold d-block text-truncate" style="font-size: 0.88rem; line-height: 1.35;" title="${escapeHtml(item.title)}">
                            ${escapeHtml(item.title)}
                        </a>
                        <div class="d-flex align-items-center gap-2 mt-1 text-muted" style="font-size: 0.75rem;">
                            <span><i class="far fa-clock me-1"></i>${item.date}</span>
                        </div>
                    </div>
                    <button type="button" class="btn btn-sm btn-link text-danger p-1 flex-shrink-0" onclick="window.ABCBookmarks.remove('${item.id}')" title="Xóa bài này">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            `;
        });
        html += '</div>';
        container.innerHTML = html;
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
    }

    function init() {
        updateBadges();
        renderDrawer();

        // Khởi tạo nút trên trang chi tiết nếu có
        const btn = document.getElementById('bookmarkBtn');
        if (btn) {
            const articleId = btn.getAttribute('data-id');
            const saved = isBookmarked(articleId);
            updateDetailButtonState(saved);

            btn.addEventListener('click', function(e) {
                e.preventDefault();
                const article = {
                    id: btn.getAttribute('data-id'),
                    title: btn.getAttribute('data-title'),
                    image: btn.getAttribute('data-image'),
                    category: btn.getAttribute('data-category'),
                    date: btn.getAttribute('data-date'),
                    url: window.location.href
                };
                toggleBookmark(article);
            });
        }

        // Nút xóa tất cả
        const clearBtn = document.getElementById('clearAllBookmarksBtn');
        if (clearBtn) {
            clearBtn.addEventListener('click', function() {
                if (confirm('Bạn có chắc chắn muốn xóa toàn bộ danh sách bài viết đã lưu?')) {
                    saveBookmarks([]);
                }
            });
        }

        // Lắng nghe sự kiện mở Drawer để re-render
        const offcanvasEl = document.getElementById('bookmarksOffcanvas');
        if (offcanvasEl) {
            offcanvasEl.addEventListener('show.bs.offcanvas', function() {
                renderDrawer();
            });
        }
    }

    window.ABCBookmarks = {
        remove: function(id) {
            let list = getBookmarks();
            list = list.filter(item => item.id !== id);
            saveBookmarks(list);
            const btn = document.getElementById('bookmarkBtn');
            if (btn && btn.getAttribute('data-id') === id) {
                updateDetailButtonState(false);
            }
        }
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
