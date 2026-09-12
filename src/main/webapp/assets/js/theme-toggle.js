/**
 * File: theme-toggle.js
 * Description: Quản lý chuyển đổi giao diện Sáng / Tối (Dark / Light Mode)
 * Lưu trữ trạng thái trong localStorage và tự động khôi phục khi tải lại trang.
 */

(function() {
    'use strict';

    var STORAGE_KEY = 'abcnews_theme';

    // 1. Áp dụng theme ngay lập tức để tránh chớp trắng (FOUC)
    function applySavedTheme() {
        var savedTheme = localStorage.getItem(STORAGE_KEY);
        var prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
        var activeTheme = savedTheme ? savedTheme : (prefersDark ? 'dark' : 'light');

        document.documentElement.setAttribute('data-theme', activeTheme);
        updateToggleIcon(activeTheme);
    }

    // 2. Cập nhật biểu tượng nút
    function updateToggleIcon(theme) {
        var icon = document.getElementById('themeIcon');
        if (!icon) return;

        if (theme === 'dark') {
            icon.className = 'fas fa-sun text-warning';
        } else {
            icon.className = 'fas fa-moon';
        }
    }

    // 3. Xử lý sự kiện click nút
    function setupToggleEvent() {
        var btn = document.getElementById('themeToggleBtn');
        if (!btn) return;

        btn.addEventListener('click', function(e) {
            e.preventDefault();
            var currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
            var newTheme = currentTheme === 'dark' ? 'light' : 'dark';

            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem(STORAGE_KEY, newTheme);
            updateToggleIcon(newTheme);
        });
    }

    // Khởi chạy
    applySavedTheme();

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            setupToggleEvent();
            var current = document.documentElement.getAttribute('data-theme') || 'light';
            updateToggleIcon(current);
        });
    } else {
        setupToggleEvent();
        var current = document.documentElement.getAttribute('data-theme') || 'light';
        updateToggleIcon(current);
    }
})();
