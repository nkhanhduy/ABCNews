<%-- 
  File: newsletter_crud.jsp
  Description: Giao diện Quản lý Newsletter
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!-- CSS đã có trong admin_style.css -->

<h2>Quản lý Newsletter</h2>
<hr>

<div class="crud-container newsletter-crud-wrapper">
    
    <!-- Card: Thống kê Newsletter -->
    <div class="newsletter-stats-card card">
        <div class="card-header">
            <h3><i class="fas fa-chart-bar me-2"></i>Thống kê Newsletter</h3>
        </div>
        <div class="card-body">
            <div class="stats-grid">
                <div class="stat-item">
                    <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                        <i class="fas fa-envelope"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Tổng số email</div>
                        <div class="stat-value">${totalNewsletters}</div>
                    </div>
                </div>
                <div class="stat-item">
                    <div class="stat-icon" style="background: linear-gradient(135deg, #28a745 0%, #20c997 100%);">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Đang hoạt động</div>
                        <div class="stat-value">${enabledCount}</div>
                    </div>
                </div>
                <div class="stat-item">
                    <div class="stat-icon" style="background: linear-gradient(135deg, #6c757d 0%, #495057 100%);">
                        <i class="fas fa-ban"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Đã hủy</div>
                        <div class="stat-value">${disabledCount}</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Card: Tìm kiếm và Bộ lọc -->
    <div class="newsletter-search-card card">
        <div class="card-header">
            <h3><i class="fas fa-search me-2"></i>Tìm kiếm và Bộ lọc</h3>
        </div>
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/admin/newsletters" id="searchForm">
                <div class="search-filter-row" style="grid-template-columns: 2fr 1fr auto;">
                    <!-- Tìm kiếm theo email -->
                    <div class="search-group">
                        <label for="searchKeyword"><i class="fas fa-envelope me-1"></i>Tìm kiếm email</label>
                        <input type="text" 
                               name="searchKeyword" 
                               id="searchKeyword" 
                               value="${searchKeyword}" 
                               placeholder="Nhập email để tìm kiếm...">
                    </div>
                    
                    <!-- Lọc theo trạng thái -->
                    <div class="filter-group">
                        <label for="filterEnabled"><i class="fas fa-toggle-on me-1"></i>Trạng thái</label>
                        <select name="filterEnabled" id="filterEnabled">
                            <option value="">-- Tất cả trạng thái --</option>
                            <option value="true" ${filterEnabled == 'true' ? 'selected' : ''}>Hoạt động</option>
                            <option value="false" ${filterEnabled == 'false' ? 'selected' : ''}>Đã hủy</option>
                        </select>
                    </div>
                    
                    <!-- Nút reset -->
                    <div class="search-actions" style="align-self: flex-end;">
                        <a href="${pageContext.request.contextPath}/admin/newsletters" class="btn btn-reset">
                            <i class="fas fa-redo me-1"></i>Làm mới
                        </a>
                    </div>
                </div>
            </form>
        </div>
    </div>
    
    <!-- Card: Danh sách Email đăng ký -->
    <div class="newsletter-list-card card">
        <div class="card-header">
            <h3><i class="fas fa-envelope me-2"></i>Danh sách Email đăng ký nhận tin</h3>
            <div class="export-buttons">
                <a href="${pageContext.request.contextPath}/admin/export?type=newsletters&format=csv" class="btn btn-export btn-csv" title="Export CSV">
                    <i class="fas fa-file-csv"></i> CSV
                </a>
                <a href="${pageContext.request.contextPath}/admin/export?type=newsletters&format=excel" class="btn btn-export btn-excel" title="Export Excel">
                    <i class="fas fa-file-excel"></i> Excel
                </a>
                <a href="${pageContext.request.contextPath}/admin/export?type=newsletters&format=pdf" class="btn btn-export btn-pdf" title="Export PDF">
                    <i class="fas fa-file-pdf"></i> PDF
                </a>
            </div>
        </div>
        <div class="card-body" id="newsletterListContainer">
            <jsp:include page="/views/admin/newsletter_list_fragment.jsp" />
        </div>
    </div>
</div>

<style>
/* Container cho Newsletter Management */
.newsletter-crud-wrapper {
    display: flex;
    flex-direction: column;
    gap: 30px;
}

/* Card styling cho Newsletter Management */
.newsletter-list-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 0;
    overflow: visible;
    width: 100%;
    box-sizing: border-box;
}

.newsletter-list-card .card-header {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.newsletter-list-card .card-header h3 {
    margin: 0;
    font-size: 1.3rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.newsletter-list-card .card-body {
    padding: 25px;
}

/* Badge styling - đồng đều kích thước */
.newsletter-list-card .badge {
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 0.875rem;
    font-weight: 500;
    min-width: 120px;
    text-align: center;
    display: inline-block;
}

.newsletter-list-card .badge.bg-success {
    background-color: #28a745 !important;
    color: #fff;
}

.newsletter-list-card .badge.bg-secondary {
    background-color: #6c757d !important;
    color: #fff;
}

/* Card tìm kiếm */
.newsletter-search-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 30px;
    overflow: visible;
    width: 100%;
    box-sizing: border-box;
}

.newsletter-search-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.newsletter-search-card .card-header h3 {
    margin: 0;
    font-size: 1.3rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.newsletter-search-card .card-body {
    padding: 25px;
}

.newsletter-search-card .search-filter-row {
    display: grid;
    gap: 20px;
    margin-bottom: 0;
}

.newsletter-search-card .filter-group {
    display: flex;
    flex-direction: column;
}

.newsletter-search-card .filter-group label {
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
    font-size: 0.9rem;
}

.newsletter-search-card .filter-group select {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 14px;
    transition: border-color 0.3s ease;
    box-sizing: border-box;
    background-color: #fff;
}

.newsletter-search-card .filter-group select:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* Card thống kê */
.newsletter-stats-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 30px;
    overflow: visible;
    width: 100%;
    box-sizing: border-box;
}

.newsletter-stats-card .card-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 15px 25px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.newsletter-stats-card .card-header h3 {
    margin: 0;
    font-size: 1.3rem;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.newsletter-stats-card .card-body {
    padding: 25px;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.stat-icon {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 1.5rem;
    flex-shrink: 0;
}

.stat-content {
    flex: 1;
}

.stat-label {
    font-size: 0.9rem;
    color: #6c757d;
    margin-bottom: 5px;
}

.stat-value {
    font-size: 1.8rem;
    font-weight: 700;
    color: #333;
}

.newsletter-search-card .search-group {
    display: flex;
    flex-direction: column;
}

.newsletter-search-card .search-group label {
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
    font-size: 0.9rem;
}

.newsletter-search-card .search-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 14px;
    transition: border-color 0.3s ease;
    box-sizing: border-box;
}

.newsletter-search-card .search-group input:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.newsletter-search-card .search-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
}

.newsletter-search-card .btn-search {
    background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    font-weight: 500;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 6px;
}

.newsletter-search-card .btn-search:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
}

.newsletter-search-card .btn-reset {
    background-color: var(--secondary-color);
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    font-weight: 500;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    text-decoration: none;
}

.newsletter-search-card .btn-reset:hover {
    background-color: #5a6268;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(108, 117, 125, 0.3);
}


@media (max-width: 768px) {
    .newsletter-search-card .search-filter-row {
        grid-template-columns: 1fr !important;
        gap: 15px;
    }
    
    .newsletter-search-card .search-actions {
        justify-content: stretch;
    }
    
    .newsletter-search-card .search-actions .btn {
        flex: 1;
    }
    
    .stats-grid {
        grid-template-columns: 1fr;
    }
}
</style>

<script>
// AJAX Search - Tìm kiếm không reload trang
(function() {
    const searchForm = document.getElementById('searchForm');
    const searchKeyword = document.getElementById('searchKeyword');
    const newsletterListContainer = document.getElementById('newsletterListContainer');
    
    if (!searchForm || !newsletterListContainer) return;
    
    let searchTimeout;
    let isSearching = false;
    
    // Hàm tìm kiếm qua AJAX
    function performSearch() {
        if (isSearching) return;
        
        isSearching = true;
        
        // Lưu vị trí scroll hiện tại
        const scrollPosition = window.pageYOffset || document.documentElement.scrollTop;
        
        // Fade out nội dung hiện tại
        newsletterListContainer.style.opacity = '0.5';
        newsletterListContainer.style.transition = 'opacity 0.2s ease';
        
        // Hiển thị loading sau một chút
        setTimeout(function() {
            newsletterListContainer.innerHTML = '<div style="text-align: center; padding: 40px; opacity: 0;" id="loadingDiv"><i class="fas fa-spinner fa-spin fa-2x" style="color: #667eea;"></i><p style="margin-top: 10px; color: #667eea;">Đang tìm kiếm...</p></div>';
            
            // Fade in loading
            const loadingDiv = document.getElementById('loadingDiv');
            if (loadingDiv) {
                loadingDiv.style.transition = 'opacity 0.3s ease';
                loadingDiv.style.opacity = '1';
            }
        }, 100);
        
        // Lấy các giá trị từ form
        const filterEnabled = document.getElementById('filterEnabled');
        const params = new URLSearchParams();
        if (searchKeyword && searchKeyword.value.trim()) {
            params.append('searchKeyword', searchKeyword.value.trim());
        }
        if (filterEnabled && filterEnabled.value) {
            params.append('filterEnabled', filterEnabled.value);
        }
        params.append('action', 'searchAjax');
        
        // Gọi AJAX
        const xhr = new XMLHttpRequest();
        const url = '${pageContext.request.contextPath}/admin/newsletters?' + params.toString();
        
        // Lưu thời gian bắt đầu để đảm bảo loading hiển thị ít nhất 200ms (nhanh hơn)
        const startTime = Date.now();
        const minDisplayTime = 200; // Tối thiểu 200ms
        
        xhr.open('GET', url);
        xhr.onload = function() {
            const elapsed = Date.now() - startTime;
            const remainingTime = Math.max(0, minDisplayTime - elapsed);
            
            setTimeout(function() {
                isSearching = false;
                if (xhr.status === 200) {
                    // Fade out loading trước
                    const loadingDiv = document.getElementById('loadingDiv');
                    if (loadingDiv) {
                        loadingDiv.style.opacity = '0';
                        loadingDiv.style.transition = 'opacity 0.2s ease';
                    }
                    
                    // Cập nhật nội dung danh sách sau khi fade out
                    setTimeout(function() {
                        newsletterListContainer.innerHTML = xhr.responseText;
                        newsletterListContainer.style.opacity = '0';
                        newsletterListContainer.style.transition = 'opacity 0.3s ease';
                        
                        // Fade in nội dung mới
                        setTimeout(function() {
                            newsletterListContainer.style.opacity = '1';
                            
                            // Khôi phục vị trí scroll (không scroll về đầu trang)
                            window.scrollTo(0, scrollPosition);
                        }, 50);
                    }, 150);
                } else {
                    newsletterListContainer.innerHTML = '<div style="text-align: center; padding: 40px; color: #dc3545;"><i class="fas fa-exclamation-triangle"></i><p style="margin-top: 10px;">Có lỗi xảy ra khi tìm kiếm</p></div>';
                    newsletterListContainer.style.opacity = '1';
                    window.scrollTo(0, scrollPosition);
                }
            }, remainingTime);
        };
        xhr.onerror = function() {
            isSearching = false;
            newsletterListContainer.innerHTML = '<div style="text-align: center; padding: 40px; color: #dc3545;"><i class="fas fa-exclamation-triangle"></i><p style="margin-top: 10px;">Lỗi kết nối</p></div>';
            newsletterListContainer.style.opacity = '1';
            window.scrollTo(0, scrollPosition);
        };
        xhr.send();
    }
    
    // Ngăn form submit mặc định
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        performSearch();
    });
    
    // Tìm kiếm khi nhấn Enter
    if (searchKeyword) {
        searchKeyword.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                performSearch();
            }
        });
        
        // Tự động tìm kiếm khi gõ (với debounce 500ms)
        searchKeyword.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(function() {
                performSearch();
            }, 500);
        });
    }
    
    // Auto-filter on change cho dropdown
    if (filterEnabled) {
        filterEnabled.addEventListener('change', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(function() {
                performSearch();
            }, 300);
        });
    }
})();
</script>