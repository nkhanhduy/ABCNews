/**
 * Public UX Enhancement Script
 * Framework JS cải thiện trải nghiệm người dùng cho trang công khai
 */

(function() {
    'use strict';

    // ========== News Item Click Enhancement ==========
    function initNewsItemClick() {
        const newsItems = document.querySelectorAll('.news-item, .card.shadow-sm');
        newsItems.forEach(function(item) {
            // Làm toàn bộ card có thể click được
            const link = item.querySelector('h3 a, h5 a, .card-title a');
            if (link) {
                item.style.cursor = 'pointer';
                item.addEventListener('click', function(e) {
                    // Chỉ navigate nếu không click vào button hoặc link khác
                    if (!e.target.closest('a.btn') && !e.target.closest('button')) {
                        window.location.href = link.href;
                    }
                });
            }
        });
    }

    // ========== Image Lazy Loading & Error Handling ==========
    function initImageEnhancements() {
        const images = document.querySelectorAll('img');
        images.forEach(function(img) {
            // Thêm loading="lazy" nếu chưa có
            if (!img.hasAttribute('loading')) {
                img.setAttribute('loading', 'lazy');
            }

            // Xử lý lỗi ảnh với placeholder đẹp hơn
            img.addEventListener('error', function() {
                if (!this.classList.contains('error-handled')) {
                    this.classList.add('error-handled');
                    this.style.display = 'none';
                    const placeholder = document.createElement('div');
                    placeholder.className = 'image-placeholder';
                    placeholder.style.cssText = 'width: 100%; height: 200px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; border-radius: 8px;';
                    placeholder.innerHTML = '<i class="fas fa-image"></i> <span style="margin-left: 10px;">Không thể tải ảnh</span>';
                    this.parentNode.insertBefore(placeholder, this);
                }
            });
        });
    }

    // ========== Smooth Scroll to Top ==========
    function initScrollToTop() {
        // Tạo nút scroll to top
        const scrollBtn = document.createElement('button');
        scrollBtn.innerHTML = '<i class="fas fa-arrow-up"></i>';
        scrollBtn.className = 'scroll-to-top';
        scrollBtn.setAttribute('aria-label', 'Lên đầu trang');
        scrollBtn.style.cssText = 'position: fixed; bottom: 30px; right: 30px; width: 50px; height: 50px; border-radius: 50%; background: linear-gradient(135deg, #28a745, #20c997); color: #fff; border: none; cursor: pointer; box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4); z-index: 1000; display: none; transition: all 0.3s ease; font-size: 20px;';
        
        scrollBtn.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px) scale(1.1)';
            this.style.boxShadow = '0 6px 16px rgba(40, 167, 69, 0.5)';
        });
        
        scrollBtn.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
            this.style.boxShadow = '0 4px 12px rgba(40, 167, 69, 0.4)';
        });
        
        scrollBtn.addEventListener('click', function() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
        
        document.body.appendChild(scrollBtn);

        // Hiện/ẩn nút khi scroll
        window.addEventListener('scroll', function() {
            if (window.pageYOffset > 300) {
                scrollBtn.style.display = 'flex';
                scrollBtn.style.alignItems = 'center';
                scrollBtn.style.justifyContent = 'center';
            } else {
                scrollBtn.style.display = 'none';
            }
        });
    }

    // ========== Reading Progress Bar ==========
    function initReadingProgress() {
        const progressBar = document.createElement('div');
        progressBar.className = 'reading-progress';
        progressBar.style.cssText = 'position: fixed; top: 0; left: 0; height: 4px; background: linear-gradient(90deg, #28a745, #20c997); width: 0%; z-index: 9999; transition: width 0.1s ease;';
        document.body.appendChild(progressBar);

        window.addEventListener('scroll', function() {
            const windowHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
            const scrolled = (window.pageYOffset / windowHeight) * 100;
            progressBar.style.width = scrolled + '%';
        });
    }

    // ========== Link Preview Enhancement ==========
    function initLinkPreviews() {
        const links = document.querySelectorAll('a[href*="/detail"]');
        links.forEach(function(link) {
            link.addEventListener('mouseenter', function() {
                this.style.transition = 'all 0.3s ease';
            });
        });
    }

    // ========== Card Hover Sound Effect (Optional) ==========
    function initCardHoverEffects() {
        const cards = document.querySelectorAll('.card, .news-item');
        cards.forEach(function(card) {
            card.addEventListener('mouseenter', function() {
                this.style.transition = 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)';
            });
        });
    }

    // ========== Newsletter Form Enhancement ==========
    function initNewsletterForm() {
        const newsletterForm = document.querySelector('form[action*="newsletter"]');
        if (newsletterForm) {
            const emailInput = newsletterForm.querySelector('input[type="email"]');
            if (emailInput) {
                emailInput.addEventListener('input', function() {
                    const email = this.value.trim();
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    
                    if (email && emailRegex.test(email)) {
                        this.classList.remove('is-invalid');
                        this.classList.add('is-valid');
                    } else if (email) {
                        this.classList.remove('is-valid');
                        this.classList.add('is-invalid');
                    } else {
                        this.classList.remove('is-valid', 'is-invalid');
                    }
                });
            }
        }
    }

    // ========== Search Enhancement ==========
    function initSearchEnhancement() {
        const searchInputs = document.querySelectorAll('input[type="search"], input[name*="search"], input[placeholder*="tìm"]');
        searchInputs.forEach(function(input) {
            input.addEventListener('focus', function() {
                this.parentElement.classList.add('search-focused');
            });
            
            input.addEventListener('blur', function() {
                this.parentElement.classList.remove('search-focused');
            });
        });
    }

    // ========== Category Menu Enhancement ==========
    // Removed: Loading overlay gây phiền toái, trình duyệt đã có loading indicator riêng

    // ========== Back to Top Animation ==========
    function initBackToTop() {
        // Thêm animation cho các link "Về trang chủ", "Quay lại"
        const backLinks = document.querySelectorAll('a[href*="/home"], a:contains("Quay lại"), a:contains("Về trang chủ")');
        backLinks.forEach(function(link) {
            link.addEventListener('click', function(e) {
                // Smooth scroll nếu là anchor link
                if (this.getAttribute('href').startsWith('#')) {
                    e.preventDefault();
                    const target = document.querySelector(this.getAttribute('href'));
                    if (target) {
                        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    }
                }
            });
        });
    }

    // ========== Image Zoom on Click ==========
    function initImageZoom() {
        const detailImages = document.querySelectorAll('.detail-image, article img');
        detailImages.forEach(function(img) {
            img.style.cursor = 'zoom-in';
            img.addEventListener('click', function() {
                // Tạo modal để xem ảnh phóng to
                const modal = document.createElement('div');
                modal.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.9); z-index: 10000; display: flex; align-items: center; justify-content: center; cursor: zoom-out;';
                
                const modalImg = document.createElement('img');
                modalImg.src = this.src;
                modalImg.style.cssText = 'max-width: 90%; max-height: 90%; object-fit: contain; border-radius: 8px;';
                
                modal.appendChild(modalImg);
                document.body.appendChild(modal);
                
                modal.addEventListener('click', function() {
                    document.body.removeChild(modal);
                });
            });
        });
    }

    // ========== Reading Time Estimate ==========
    function initReadingTime() {
        const articles = document.querySelectorAll('article, .detail-content, .news-full-content');
        articles.forEach(function(article) {
            const text = article.textContent || article.innerText;
            const words = text.trim().split(/\s+/).length;
            const readingTime = Math.ceil(words / 200); // Giả sử đọc 200 từ/phút
            
            const timeBadge = document.createElement('div');
            timeBadge.className = 'reading-time';
            timeBadge.style.cssText = 'display: inline-flex; align-items: center; gap: 5px; padding: 5px 12px; background: #f8f9fa; border-radius: 20px; font-size: 0.85em; color: #6c757d; margin-bottom: 15px;';
            timeBadge.innerHTML = '<i class="fas fa-clock"></i> <span>Khoảng ' + readingTime + ' phút đọc</span>';
            
            // Chèn vào đầu article
            if (article.querySelector('.detail-meta')) {
                article.querySelector('.detail-meta').appendChild(timeBadge);
            } else if (article.querySelector('h1, h2')) {
                article.querySelector('h1, h2').insertAdjacentElement('afterend', timeBadge);
            }
        });
    }

    // ========== Share Button Enhancement ==========
    function initShareButtons() {
        // Có thể thêm share buttons cho bài viết
        const shareButtons = document.querySelectorAll('.share-btn, [data-share]');
        shareButtons.forEach(function(btn) {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                const url = window.location.href;
                const title = document.title;
                
                if (navigator.share) {
                    navigator.share({
                        title: title,
                        url: url
                    });
                } else {
                    // Fallback: copy to clipboard
                    navigator.clipboard.writeText(url).then(function() {
                        alert('Đã sao chép link vào clipboard!');
                    });
                }
            });
        });
    }

    // ========== Initialize Everything ==========
    function init() {
        // Wait for DOM to be ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', init);
            return;
        }

        initNewsItemClick();
        initImageEnhancements();
        initScrollToTop();
        initReadingProgress();
        initLinkPreviews();
        initCardHoverEffects();
        initNewsletterForm();
        initSearchEnhancement();
        // initCategoryMenu(); // Removed: gây phiền toái
        initBackToTop();
        initImageZoom();
        initReadingTime();
        initShareButtons();
    }

    // Start initialization
    init();

})();
