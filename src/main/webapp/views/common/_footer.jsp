<%-- 
  File: _footer.jsp
  Description: Footer dự án ABC News - Thông tin phát triển bởi Nguyễn Duy Khánh (FPT Polytechnic TP. HCM)
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<footer class="site-footer">
    <div class="container">
        <div class="row g-4">
            <!-- Cột 1: Giới thiệu dự án & Tác giả -->
            <div class="col-lg-5 col-md-6">
                <div class="d-flex align-items-center mb-3">
                    <a href="${pageContext.request.contextPath}/home" class="site-logo">
                        <span>ABC</span><span class="logo-badge">News</span>
                    </a>
                </div>
                <p class="text-secondary small mb-2">
                    Nền tảng báo chí điện tử & hệ thống quản trị nội dung tòa soạn CMS đa tầng, phục vụ đọc báo đa phương tiện và quản lý bài viết trực tuyến.
                </p>
                <p class="text-secondary small mb-0">
                    Dự án phát triển bởi: <strong>Nguyễn Khánh Duy</strong><br>
                    Sinh viên Chuyên ngành Phát triển Phần mềm — <strong>FPT Polytechnic TP. Hồ Chí Minh</strong>
                </p>
            </div>

            <!-- Cột 2: Chuyên mục nhanh -->
            <div class="col-lg-3 col-md-6">
                <h5 class="footer-title">Chuyên mục</h5>
                <ul class="footer-links small">
                    <li><a href="${pageContext.request.contextPath}/category?id=TECH">Công nghệ & AI</a></li>
                    <li><a href="${pageContext.request.contextPath}/category?id=ECONOMY">Kinh tế & Tài chính</a></li>
                    <li><a href="${pageContext.request.contextPath}/category?id=SPORT">Thể thao Quốc tế</a></li>
                    <li><a href="${pageContext.request.contextPath}/category?id=LIFE">Đời sống & Khoa học</a></li>
                    <li><a href="${pageContext.request.contextPath}/category?id=EDUCATION">Giáo dục & Kỹ năng</a></li>
                </ul>
            </div>

            <!-- Cột 3: Thông tin đào tạo & Liên hệ -->
            <div class="col-lg-4 col-md-12">
                <h5 class="footer-title">Thông tin & Liên hệ</h5>
                <p class="text-secondary small mb-2">
                    Cơ sở: <strong>Cao đẳng FPT Polytechnic TP. Hồ Chí Minh</strong><br>
                    Địa chỉ: Công viên Phần mềm Quang Trung, Phường Tân Chánh Hiệp, Quận 12, TP. Hồ Chí Minh
                </p>
                <p class="text-secondary small mb-2">
                    Email liên hệ: <strong>khanhndts02168@gmail.com</strong>
                </p>
                <p class="text-secondary small mb-0">
                    Mã nguồn & Portfolio: <a href="https://github.com/nkhanhduy/ABCNews" target="_blank" class="text-decoration-underline text-light">github.com/nkhanhduy/ABCNews</a>
                </p>
            </div>
        </div>

        <!-- Dòng bản quyền đáy trang -->
        <div class="footer-bottom text-center">
            <p class="mb-0">
                &copy; 2026 ABC News — Đồ án Thực hành Phát triển Ứng dụng Web Java | Tác giả: <strong>Nguyễn Khánh Duy</strong> (FPT Polytechnic TP. HCM).
            </p>
        </div>
    </div>
</footer>
