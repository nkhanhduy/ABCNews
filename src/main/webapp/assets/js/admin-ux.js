/**
 * Admin UX Enhancement Script
 * Cải thiện trải nghiệm người dùng cho khu vực Admin
 */

(function() {
    'use strict';

    // ========== Auto-dismiss Messages ==========
    function initAutoDismissMessages() {
        const messages = document.querySelectorAll('.message-success, .message-error');
        messages.forEach(function(message) {
            // Thêm animation class
            message.style.opacity = '0';
            message.style.transform = 'translateY(-10px)';
            message.style.transition = 'all 0.3s ease';
            
            // Animate in
            setTimeout(function() {
                message.style.opacity = '1';
                message.style.transform = 'translateY(0)';
            }, 100);

            // Auto dismiss sau 5 giây
            setTimeout(function() {
                message.style.opacity = '0';
                message.style.transform = 'translateY(-10px)';
                setTimeout(function() {
                    message.remove();
                }, 300);
            }, 5000);

            // Thêm nút đóng
            const closeBtn = document.createElement('button');
            closeBtn.innerHTML = '<i class="fas fa-times"></i>';
            closeBtn.className = 'message-close';
            closeBtn.setAttribute('aria-label', 'Đóng thông báo');
            closeBtn.style.cssText = 'position: absolute; top: 5px; right: 10px; background: none; border: none; cursor: pointer; font-size: 16px; opacity: 0.7; transition: opacity 0.2s;';
            closeBtn.onmouseover = function() { this.style.opacity = '1'; };
            closeBtn.onmouseout = function() { this.style.opacity = '0.7'; };
            closeBtn.onclick = function(e) {
                e.preventDefault();
                message.style.opacity = '0';
                message.style.transform = 'translateY(-10px)';
                setTimeout(function() {
                    message.remove();
                }, 300);
            };
            message.style.position = 'relative';
            message.style.paddingRight = '40px';
            message.appendChild(closeBtn);
        });
    }

    // ========== Form Validation Feedback ==========
    function initFormValidation() {
        const forms = document.querySelectorAll('form');
        forms.forEach(function(form) {
            const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
            
            inputs.forEach(function(input) {
                // Real-time validation
                input.addEventListener('blur', function() {
                    validateField(input);
                });

                input.addEventListener('input', function() {
                    if (input.classList.contains('is-invalid')) {
                        validateField(input);
                    }
                });
            });

            // Form submit validation
            form.addEventListener('submit', function(e) {
                let isValid = true;
                inputs.forEach(function(input) {
                    if (!validateField(input)) {
                        isValid = false;
                    }
                });

                if (!isValid) {
                    e.preventDefault();
                    // Scroll to first error
                    const firstError = form.querySelector('.is-invalid');
                    if (firstError) {
                        try {
                            firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        } catch (err) {
                            firstError.scrollIntoView();
                        }
                        firstError.focus();
                    }
                } else {
                    // Show loading state
                    showFormLoading(form);
                }
            });
        });
    }

    function validateField(field) {
        const value = field.value.trim();
        let isValid = true;
        let errorMessage = '';

        // Required validation
        if (field.hasAttribute('required') && !value) {
            isValid = false;
            errorMessage = 'Trường này là bắt buộc';
        }

        // Email validation
        if (field.type === 'email' && value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                isValid = false;
                errorMessage = 'Email không hợp lệ';
            }
        }

        // Update UI
        if (isValid) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
            removeFieldError(field);
        } else {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
            showFieldError(field, errorMessage);
        }

        return isValid;
    }

    function showFieldError(field, message) {
        removeFieldError(field);
        const errorDiv = document.createElement('div');
        errorDiv.className = 'field-error';
        errorDiv.style.cssText = 'color: #dc3545; font-size: 12px; margin-top: 5px; display: flex; align-items: center; gap: 5px;';
        errorDiv.innerHTML = '<i class="fas fa-exclamation-circle"></i> ' + message;
        if (field.parentNode) {
            field.parentNode.appendChild(errorDiv);
        }
    }

    function removeFieldError(field) {
        const error = field.parentNode.querySelector('.field-error');
        if (error) {
            error.remove();
        }
    }

    // ========== Loading States ==========
    function showFormLoading(form) {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            const originalText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Đang xử lý...';
            submitBtn.style.opacity = '0.7';
            submitBtn.style.cursor = 'not-allowed';
            
            // Reset sau 10 giây (fallback)
            setTimeout(function() {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalText;
                submitBtn.style.opacity = '1';
                submitBtn.style.cursor = 'pointer';
            }, 10000);
        }
    }

    // ========== Better Delete Confirmations ==========
    function initDeleteConfirmations() {
        const deleteLinks = document.querySelectorAll('a.btn-delete, a[onclick*="confirm"]');
        deleteLinks.forEach(function(link) {
            link.addEventListener('click', function(e) {
                const originalOnclick = link.getAttribute('onclick');
                if (originalOnclick && originalOnclick.includes('confirm')) {
                    return; // Let original onclick handle it
                }
                
                e.preventDefault();
                let itemName = 'mục này';
                const row = link.closest('tr');
                if (row) {
                    const firstCell = row.querySelector('td:first-child');
                    if (firstCell && firstCell.textContent) {
                        itemName = firstCell.textContent.trim() || 'mục này';
                    }
                }
                
                if (confirm('Bạn có chắc chắn muốn xóa "' + itemName + '"?\n\nHành động này không thể hoàn tác.')) {
                    // Show loading
                    link.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Đang xóa...';
                    link.style.pointerEvents = 'none';
                    
                    // Navigate to delete URL
                    window.location.href = link.href;
                }
            });
        });
    }

    // ========== Smooth Scroll ==========
    function initSmoothScroll() {
        // Smooth scroll to top khi click vào messages
        const messageContainer = document.querySelector('.message-container');
        if (messageContainer && messageContainer.querySelector('.message-success, .message-error')) {
            try {
                messageContainer.scrollIntoView({ behavior: 'smooth', block: 'center' });
            } catch (err) {
                messageContainer.scrollIntoView();
            }
        }
    }

    // ========== Mobile Menu Toggle ==========
    function initMobileMenu() {
        // Tạo toggle button cho mobile
        if (window.innerWidth <= 768) {
            const sidebar = document.querySelector('.admin-sidebar');
            const main = document.querySelector('.admin-main');
            
            if (sidebar && !document.querySelector('.mobile-menu-toggle')) {
                const toggleBtn = document.createElement('button');
                toggleBtn.className = 'mobile-menu-toggle';
                toggleBtn.innerHTML = '<i class="fas fa-bars"></i>';
                toggleBtn.setAttribute('aria-label', 'Toggle menu');
                toggleBtn.style.cssText = 'position: fixed; top: 10px; left: 10px; z-index: 1001; background: #343a40; color: #fff; border: none; padding: 10px 15px; border-radius: 5px; cursor: pointer; box-shadow: 0 2px 8px rgba(0,0,0,0.2);';
                
                toggleBtn.addEventListener('click', function() {
                    sidebar.classList.toggle('mobile-open');
                    toggleBtn.classList.toggle('active');
                });

                document.body.appendChild(toggleBtn);

                // Close menu khi click outside
                var clickHandler = function(e) {
                    if (!sidebar.contains(e.target) && !toggleBtn.contains(e.target)) {
                        sidebar.classList.remove('mobile-open');
                        toggleBtn.classList.remove('active');
                    }
                };
                document.addEventListener('click', clickHandler);
            }
        }
    }

    // ========== Table Row Hover Effects ==========
    function initTableEnhancements() {
        const tables = document.querySelectorAll('table.crud-table');
        tables.forEach(function(table) {
            const rows = table.querySelectorAll('tbody tr');
            rows.forEach(function(row) {
                row.addEventListener('mouseenter', function() {
                    this.style.transform = 'scale(1.01)';
                    this.style.transition = 'transform 0.2s ease';
                });
                row.addEventListener('mouseleave', function() {
                    this.style.transform = 'scale(1)';
                });
            });
        });
    }

    // ========== Input Focus Enhancements ==========
    function initInputEnhancements() {
        const inputs = document.querySelectorAll('input, select, textarea');
        inputs.forEach(function(input) {
            input.addEventListener('focus', function() {
                var parent = this.parentElement;
                if (parent && parent.classList) {
                    parent.classList.add('focused');
                }
            });
            input.addEventListener('blur', function() {
                var parent = this.parentElement;
                if (parent && parent.classList) {
                    parent.classList.remove('focused');
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

        initAutoDismissMessages();
        initFormValidation();
        initDeleteConfirmations();
        initSmoothScroll();
        initMobileMenu();
        initTableEnhancements();
        initInputEnhancements();

        // Re-init mobile menu on resize
        let resizeTimer;
        window.addEventListener('resize', function() {
            clearTimeout(resizeTimer);
            resizeTimer = setTimeout(initMobileMenu, 250);
        });
    }

    // Start initialization
    init();

})();
