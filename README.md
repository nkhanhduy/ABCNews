[🇻🇳 Tiếng Việt](README.md) | [🇬🇧 English](README.en.md) | [🇩🇪 Deutsch](README.de.md)

# ABC News — Website Báo Điện Tử & Quản Trị Tòa Soạn

<p align="left">
  <a href="https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml"><img src="https://github.com/nkhanhduy/ABCNews/actions/workflows/maven.yml/badge.svg" alt="Java CI Build"></a>
  <img src="https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17 LTS">
  <img src="https://img.shields.io/badge/Jakarta%20EE-10-F37024?style=flat-square&logo=eclipsevert.x&logoColor=white" alt="Jakarta EE 10">
  <img src="https://img.shields.io/badge/Apache%20Tomcat-10.1-F8DC75?style=flat-square&logo=apachetomcat&logoColor=black" alt="Tomcat 10.1">
  <img src="https://img.shields.io/badge/SQL%20Server-2022-CC292B?style=flat-square&logo=microsoftsqlserver&logoColor=white" alt="SQL Server 2022">
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white" alt="Docker">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-2ea44f?style=flat-square" alt="License MIT"></a>
</p>

Đồ án xây dựng website tin tức trực tuyến và hệ thống quản trị nội dung tòa soạn trên nền tảng Java Web với Jakarta Servlet và JSP. Dự án hoàn thiện năm 2025 với giao diện hiện đại, hỗ trợ chế độ Sáng / Tối và cơ chế lưu trữ ảnh an toàn.

**Tác giả:** Nguyễn Khánh Duy  
**Chuyên ngành:** Phát triển Phần mềm — Trường Cao đẳng FPT Polytechnic TP. Hồ Chí Minh  
**Thời gian thực hiện:** 2025

---

## Hình ảnh Giao diện Thực tế

### 1. Phân Hệ Báo Điện Tử

| Trang Chủ Sáng | Trang Chủ Dark Mode |
|:---:|:---:|
| ![Trang chủ Light Mode](.github/images/home_light.png) | ![Trang chủ Dark Mode](.github/images/home_dark.png) |

| Tin Theo Chuyên Mục | Chi Tiết Bài Viết |
|:---:|:---:|
| ![Chuyên mục Công nghệ & AI](.github/images/category_tech.png) | ![Chi tiết bài viết](.github/images/article_detail.png) |

| Khu Vực Thảo Luận & Bình Luận Độc Giả |
|:---:|
| ![Bình luận độc giả](.github/images/feature_comments_public.png) |

### 2. Cổng Xác Thực Hệ Thống

| Trang Đăng Nhập Hệ Thống |
|:---:|
| ![Trang đăng nhập](.github/images/login_page.png) |

### 3. Phân Hệ Quản Trị Tòa Soạn

| Bảng Thống Kê Tổng Quan |
|:---:|
| ![Admin Dashboard](.github/images/admin_dashboard.png) |

| Quản Lý Bài Viết & Soạn Thảo Nội Dung | Quản Lý Người Dùng & Phân Quyền |
|:---:|:---:|
| ![Quản lý tin tức](.github/images/admin_news.png) | ![Quản lý người dùng](.github/images/admin_users.png) |

| Hồ Sơ Tác Giả & Tải Ảnh Đại Diện | Kiểm Duyệt Bình Luận Độc Giả |
|:---:|:---:|
| ![Admin Profile](.github/images/admin_profile.png) | ![Kiểm duyệt bình luận](.github/images/feature_comments_admin.png) |

---

## Các Tính Năng Trong Dự Án

### Phân Hệ Độc Giả
- **Đọc tin tức:** Trang chủ hiển thị tin tiêu điểm, danh sách bài viết mới nhất và tin xem nhiều nhất.
- **Chuyên mục tin:** Phân loại theo 5 danh mục: Kinh tế & Tài chính, Công nghệ & AI, Thể thao Quốc tế, Đời sống & Khoa học, Giáo dục & Kỹ năng.
- **Trang chi tiết bài viết:** Hiển thị nội dung đầy đủ, tác giả, ngày đăng, lượt xem, ước tính thời gian đọc bài và nút chia sẻ nhanh (Facebook, X, Telegram).
- **Đánh dấu & Đọc sau:** Cho phép độc giả lưu các bài viết yêu thích vào danh sách đọc sau trực tiếp qua `localStorage` trình duyệt và quản lý tiện lợi trên ngăn kéo Offcanvas mà không cần đăng nhập.
- **Bình luận & Thảo luận bạn đọc:** Độc giả gửi ý kiến đóng góp cho từng bài viết với định danh rõ ràng, hỗ trợ quy trình kiểm duyệt nội dung văn minh.
- **Chế độ Sáng / Tối:** Chuyển đổi linh hoạt giữa giao diện Sáng và Dark Mode với nút bấm trực tiếp trên thanh điều hướng.
- **Bản tin Newsletter:** Tiếp nhận email đăng ký của độc giả để gửi thông báo tin mới.

### Xác Thực & Bảo Mật
- **Đăng nhập & Đăng xuất:** Xác thực tài khoản cục bộ bằng mật khẩu mã hóa BCrypt; hỗ trợ nút đăng nhập nhanh bằng Google OAuth2.
- **Quên mật khẩu & OTP:** Nhập email để nhận mã OTP 6 số (thời hạn 5 phút) gửi qua Gmail SMTP, xác thực OTP để đặt lại mật khẩu mới.
- **Chống Spam Rate Limiting:** Thiết lập cơ chế đếm ngược 60 giây cho yêu cầu gửi lại OTP qua Gmail SMTP và kiểm soát tần suất gửi bình luận, ngăn chặn hành vi spam API và tiết kiệm hạn ngạch máy chủ.
- **Phòng chống XSS:** Lọc sạch toàn bộ nội dung HTML từ trình soạn thảo và biểu mẫu người dùng bằng thư viện Jsoup (Safelist relaxed), loại bỏ triệt để các mã độc script và sự kiện độc hại.
- **Bộ lọc bảo mật (AuthFilter):** Phân quyền truy cập các đường dẫn `/admin/*` dựa trên vai trò tài khoản (Admin / Phóng viên).

### Phân Hệ Quản Trị Tòa Soạn
- **Bảng điều khiển:** Thống kê số lượng bài viết, tài khoản, chuyên mục, bình luận chờ duyệt và lượt đăng ký bản tin; hiển thị biểu đồ thống kê Chart.js trực quan.
- **Quản lý tin tức:** Thêm mới bài viết với trình soạn thảo trực quan, cập nhật nội dung, xóa bài viết, tìm kiếm và lọc theo chuyên mục, tác giả, ghim bài viết lên Trang nhất.
- **Tự động lưu nháp:** Tự động sao lưu nội dung bài viết mỗi 30 giây vào `localStorage`, phát hiện và cho phép khôi phục bản nháp chưa xuất bản khi mở form soạn thảo.
- **Kiểm duyệt bình luận:** Quản lý ý kiến của độc giả (Chờ duyệt, Đã duyệt, Từ chối), cập nhật trạng thái thời gian thực và đồng bộ nhật ký hoạt động hệ thống.
- **Quản lý loại tin:** Quản lý danh mục bài viết (mã danh mục, tên chuyên mục).
- **Quản lý người dùng:** Danh sách tài khoản người dùng, chỉnh sửa thông tin, phân quyền và chuyển đổi trạng thái hoạt động.
- **Hồ sơ cá nhân:** Xem thông tin cá nhân và cập nhật ảnh đại diện với tính năng xem trước tức thì và lưu trữ phân vùng an toàn (`SafeImageStorage`).
- **Xuất dữ liệu:** Hỗ trợ kết xuất danh sách báo cáo ra các định dạng Excel, CSV, PDF.

---

## Tài khoản Trải nghiệm Nhanh

| Vai trò | Email | Mật khẩu | Quyền hạn |
|---|---|:---:|---|
| **Tổng Biên Tập** | `admin@abcnews.com` | `123456` | Toàn quyền quản trị nội dung, kiểm duyệt bình luận, quản lý người dùng |
| **Phóng Viên** | `reporter1@abcnews.com` | `123456` | Soạn thảo & xuất bản bài viết, quản lý tin cá nhân, bình luận độc giả |

---

## Công nghệ Sử dụng

- **Ngôn ngữ & Nền tảng:** Java 17 LTS, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL)
- **Máy chủ ứng dụng:** Apache Tomcat 10.1
- **Cơ sở dữ liệu:** Microsoft SQL Server 2022 (kết nối qua HikariCP connection pool và Hibernate ORM JPA)
- **Bảo mật & Sanitization:** BCrypt (OWASP password hashing), Google Sign-In API, Jakarta Mail (SMTP TLS gửi OTP), Jsoup 1.17.2 (HTML Whitelist Sanitization)
- **Giao diện:** HTML5, CSS3, Bootstrap 5.3, FontAwesome 6, Chart.js, CKEditor 5
- **Môi trường triển khai:** Docker & Docker Compose

---

## Điểm Nhấn Kiến Trúc & Kỹ Thuật

- **Kiến trúc MVC Chuẩn mực:** Phân chia rõ ràng giữa Controller (Jakarta Servlet), View (JSP & JSTL components tái sử dụng) và Tầng truy xuất dữ liệu (DAO & JPA Hibernate).
- **Tối ưu Cơ sở dữ liệu & Hiệu năng:** Ứng dụng **HikariCP Connection Pool** tốc độ cao giúp giảm thiểu thời gian tạo kết nối; cấu hình Hibernate ORM ánh xạ thực thể chặt chẽ; đánh chỉ mục Index cho bảng tin tức nhằm tăng tốc độ truy vấn theo chuyên mục và lượt đọc.
- **Bảo mật Đa tầng:**
  - Mật khẩu người dùng được băm một chiều an toàn bằng thuật toán **BCrypt** chống tấn công Rainbow Table.
  - Bộ lọc **AuthFilter** kiểm soát phân quyền chặt chẽ (RBAC) cho các endpoint `/admin/*`, ngăn chặn triệt để nguy cơ leo thang đặc quyền.
  - **Chuẩn Hóa Định Danh An Toàn:** Sử dụng chuỗi định danh ngẫu nhiên mã hóa 128-bit chuẩn quốc tế UUID v4 cho toàn bộ bài viết tin tức thay vì mã số tuần tự, triệt tiêu nguy cơ dò đoán ID bài viết.
  - Cơ chế **SafeImageStorage** xử lý upload ảnh an toàn: xác thực định dạng MIME, giới hạn dung lượng, chống tấn công Path Traversal (`../`) và tự động dọn dẹp ảnh cũ khi thay đổi ảnh đại diện.
  - Khôi phục mật khẩu bảo mật qua mã xác thực **OTP 6 số** gửi qua Gmail SMTP với thời hạn 5 phút.
- **Trải nghiệm Người dùng:**
  - Hệ thống Design Tokens bằng biến CSS đồng bộ tone màu Xanh lá hiện đại.
  - Chế độ Dark Mode hoàn thiện với tỷ lệ tương phản cao, bảo vệ mắt và không gây lóa nền.
  - Tích hợp biểu đồ thống kê trực quan **Chart.js** và trình biên tập nội dung phong phú.
  - **Tối Ưu Hóa SEO & Open Graph Protocol:** Tích hợp đầy đủ thẻ Open Graph (`og:title`, `og:image`, `og:description`, `og:url`) và Twitter Card (`summary_large_image`) cho toàn bộ bài viết, đảm bảo hiển thị hình ảnh thumbnail khổ lớn bắt mắt khi chia sẻ qua Facebook, X, Telegram hoặc Zalo.
- **Khởi Chạy Nhanh Với Docker:** Đóng gói trọn vẹn toàn bộ hệ thống bằng **Docker Compose** (Tomcat + SQL Server), tích hợp cơ chế Healthcheck và tự động khởi tạo cơ sở dữ liệu cùng bộ dữ liệu mẫu chỉ với duy nhất một câu lệnh `docker compose up -d`.

---

## Hướng dẫn Cài đặt & Khởi chạy

### Cách 1: Khởi chạy bằng Docker Compose

Yêu cầu: Máy tính đã cài sẵn [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
# 1. Clone mã nguồn
git clone https://github.com/nkhanhduy/ABCNews.git
cd ABCNews

# 2. Khởi chạy ứng dụng và cơ sở dữ liệu SQL Server
docker compose up -d

# 3. Mở trình duyệt truy cập:
# - Trang chủ: http://localhost:8088/home
# - Đăng nhập: http://localhost:8088/login
```

Để dừng hệ thống:
```bash
docker compose down
```

### Cách 2: Khởi chạy Cục bộ

Yêu cầu: JDK 17, Apache Maven 3.8+, SQL Server 2019+ và Apache Tomcat 10.1.

1. Tạo database `ABCNews` trong SQL Server và thực thi kịch bản tại `schema/ABCNews.sql` rồi nạp dữ liệu từ `schema/seed_data.sql`.
2. Sao chép và cấu hình thông tin kết nối trong file `src/main/resources/app.properties`.
3. Đóng gói ứng dụng:
   ```bash
   mvn clean package -DskipTests
   ```
4. Triển khai file `.war` sinh ra trong thư mục `target/` lên máy chủ Apache Tomcat 10.1.

---

## Thông tin Tác giả

- **Họ và tên:** Nguyễn Khánh Duy
- **Chuyên ngành:** Phát triển Phần mềm — Cao đẳng FPT Polytechnic TP. Hồ Chí Minh
- **GitHub:** [github.com/nkhanhduy](https://github.com/nkhanhduy)
- **Email:** khanhndts02168@gmail.com
- **Năm thực hiện:** 2025
