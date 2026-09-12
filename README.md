[🇻🇳 Tiếng Việt](README.md) | [🇬🇧 English](README.en.md) | [🇩🇪 Deutsch](README.de.md)

# ABC News — Website Báo Điện Tử & Hệ Quản Trị Nội Dung (CMS)

Đồ án thực hành xây dựng website tin tức trực tuyến và hệ thống quản trị nội dung tòa soạn (CMS) bằng nền tảng Java Web (Jakarta Servlet & JSP). Dự án được hoàn thiện năm 2025 và tối ưu hóa giao diện người dùng, hỗ trợ chế độ Sáng / Tối (Dark Mode) và cơ chế lưu trữ ảnh an toàn.

**Tác giả:** Nguyễn Khánh Duy  
**Chuyên ngành:** Phát triển Phần mềm — Trường Cao đẳng FPT Polytechnic TP. Hồ Chí Minh  
**Thời gian thực hiện:** 2025 (Phiên bản tối ưu)

---

## Hình ảnh Giao diện Thực tế

### 1. Phân Hệ Báo Điện Tử (Độc giả xem tin)

| Trang chủ Báo Điện Tử (Giao diện Sáng - Xanh lá) | Chế độ Đọc Ban Đêm (Dark Mode) |
|:---:|:---:|
| ![Trang chủ Light Mode](.github/images/home_light.png) | ![Trang chủ Dark Mode](.github/images/home_dark.png) |

| Xem tin theo Chuyên mục (Công nghệ & AI) | Đọc Chi Tiết Bài Báo & Nút Chia Sẻ |
|:---:|:---:|
| ![Chuyên mục Công nghệ & AI](.github/images/category_tech.png) | ![Chi tiết bài viết](.github/images/article_detail.png) |

### 2. Cổng Xác Thực Hệ Thống

| Trang Đăng Nhập Dùng Chung (Dành cho Quản trị viên & Phóng viên) |
|:---:|
| ![Trang đăng nhập](.github/images/login_page.png) |

### 3. Phân Hệ Quản Trị Tòa Soạn (Admin CMS)

| Bảng Điều Khiển Thống Kê Tổng Quan (Dashboard) |
|:---:|
| ![Admin Dashboard](.github/images/admin_dashboard.png) |

| Quản Lý Bài Viết & Soạn Thảo (CKEditor) | Quản Lý Người Dùng & Phân Quyền |
|:---:|:---:|
| ![Quản lý tin tức](.github/images/admin_news.png) | ![Quản lý người dùng](.github/images/admin_users.png) |

| Hồ Sơ Tác Giả & Tải Ảnh Đại Diện Thông Minh |
|:---:|
| ![Admin Profile](.github/images/admin_profile.png) |


---

## Các Tính Năng Trong Dự Án

### Phân hệ Độc giả (Public)
- **Đọc tin tức:** Trang chủ hiển thị tin tiêu điểm (Top Hot News), danh sách bài viết mới nhất và tin xem nhiều nhất.
- **Chuyên mục tin:** Phân loại theo 5 danh mục (Kinh tế & Tài chính, Công nghệ & AI, Thể thao Quốc tế, Đời sống & Khoa học, Giáo dục & Kỹ năng).
- **Trang chi tiết bài viết:** Hiển thị nội dung đầy đủ, tác giả, ngày đăng, lượt xem, ước tính thời gian đọc bài và nút chia sẻ nhanh (Facebook, X, Telegram).
- **Giao diện đa sắc thái:** Chuyển đổi linh hoạt giữa giao diện Sáng (Light Mode - màu xanh lá chủ đạo) và giao diện Tối (Dark Mode) với nút bấm trực tiếp trên thanh điều hướng.
- **Bản tin Newsletter:** Tiếp nhận email đăng ký của độc giả để gửi thông báo tin mới.

### Xác thực & Bảo mật
- **Đăng nhập & Đăng xuất:** Xác thực tài khoản cục bộ bằng mật khẩu mã hóa BCrypt; hỗ trợ nút đăng nhập nhanh bằng Google OAuth2.
- **Quên mật khẩu & OTP:** Nhập email để nhận mã OTP 6 số (thời hạn 5 phút) gửi qua Gmail SMTP, xác thực OTP để đặt lại mật khẩu mới.
- **Bộ lọc bảo mật (AuthFilter):** Phân quyền truy cập các đường dẫn `/admin/*` dựa trên vai trò tài khoản (Admin / Phóng viên).

### Phân hệ Quản trị (Admin CMS)
- **Tổng quan (Dashboard):** Thống kê số lượng bài viết, tài khoản, chuyên mục và lượt đăng ký bản tin; hiển thị biểu đồ thống kê Chart.js.
- **Quản lý tin tức:** Thêm mới bài viết (tích hợp trình soạn thảo trực quan), cập nhật nội dung, xóa bài viết, tìm kiếm và lọc theo chuyên mục / tác giả, ghim bài viết lên Trang nhất.
- **Quản lý loại tin:** Quản lý danh mục bài viết (mã danh mục, tên chuyên mục).
- **Quản lý người dùng:** Danh sách tài khoản người dùng, chỉnh sửa thông tin, phân quyền (Quản trị viên / Phóng viên) và chuyển đổi trạng thái (Hoạt động / Bị khóa).
- **Hồ sơ cá nhân:** Xem thông tin cá nhân và cập nhật ảnh đại diện với tính năng xem trước tức thì (Instant Preview) và lưu trữ phân vùng an toàn (`SafeImageStorage`).
- **Xuất dữ liệu:** Hỗ trợ kết xuất danh sách báo cáo ra các định dạng Excel, CSV, PDF.

---

## Tài khoản Trải nghiệm

Dữ liệu mẫu nạp sẵn các tài khoản để đăng nhập thử nghiệm:

| Vai trò | Email | Mật khẩu | Phân quyền |
|---|---|:---:|---|
| **Quản trị viên (Admin)** | `admin@abcnews.com` | `123456` | Toàn quyền quản trị nội dung, người dùng, chuyên mục và hồ sơ |
| **Phóng viên (Reporter)** | `reporter1@abcnews.com` | `123456` | Soạn thảo bài viết, quản lý tin cá nhân và cập nhật hồ sơ |

---

## Công nghệ Sử dụng

- **Ngôn ngữ & Nền tảng:** Java 17 LTS, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL)
- **Máy chủ ứng dụng:** Apache Tomcat 10.1
- **Cơ sở dữ liệu:** Microsoft SQL Server 2022 (kết nối qua HikariCP connection pool và Hibernate ORM JPA)
- **Bảo mật:** BCrypt (mã hóa mật khẩu chuẩn OWASP), Google Sign-In API, Jakarta Mail (SMTP TLS gửi OTP)
- **Giao diện:** HTML5, CSS3, Bootstrap 5.3, FontAwesome 6, Chart.js, CKEditor
- **Môi trường triển khai:** Docker & Docker Compose

---

## Điểm Nhấn Kiến Trúc & Kỹ Thuật

- **Mô hình MVC Chuẩn mực (Model 2 Architecture):** Phân chia rõ ràng giữa Controller (Jakarta Servlet), View (JSP & JSTL components tái sử dụng) và Tầng truy xuất dữ liệu (DAO & JPA Hibernate).
- **Tối ưu Cơ sở dữ liệu & Hiệu năng:** Ứng dụng **HikariCP Connection Pool** tốc độ cao giúp giảm thiểu thời gian tạo kết nối; cấu hình Hibernate ORM ánh xạ thực thể chặt chẽ; đánh chỉ mục Index cho bảng tin tức nhằm tăng tốc độ truy vấn theo chuyên mục và lượt đọc.
- **Bảo mật Đa tầng:**
  - Mật khẩu người dùng được băm một chiều an toàn bằng thuật toán **BCrypt** chống tấn công Rainbow Table.
  - Bộ lọc **AuthFilter** kiểm soát phân quyền chặt chẽ (RBAC) cho các endpoint `/admin/*`, ngăn chặn triệt để nguy cơ leo thang đặc quyền.
  - Cơ chế **SafeImageStorage** xử lý upload ảnh an toàn: xác thực định dạng MIME, giới hạn dung lượng, chống tấn công Path Traversal (`../`) và tự động dọn dẹp ảnh cũ khi thay đổi ảnh đại diện.
  - Khôi phục mật khẩu bảo mật qua mã xác thực **OTP 6 số** gửi qua Gmail SMTP với thời hạn 5 phút.
- **Trải nghiệm Người dùng (UX/UI):**
  - Hệ thống Design Tokens bằng biến CSS (CSS Custom Properties) đồng bộ tone màu Xanh lá hiện đại.
  - Chế độ Dark Mode hoàn thiện với tỷ lệ tương phản cao, bảo vệ mắt và không gây lóa nền.
  - Tích hợp biểu đồ thống kê trực quan **Chart.js** và trình biên tập nội dung phong phú **CKEditor 5**.
- **Containerization & Khởi chạy Nhanh:** Đóng gói trọn vẹn toàn bộ hệ thống bằng **Docker Compose** (Tomcat + SQL Server), tích hợp cơ chế Healthcheck và tự động khởi tạo cơ sở dữ liệu cùng bộ dữ liệu mẫu chỉ với duy nhất một câu lệnh `docker compose up -d`.


---

## Hướng dẫn Cài đặt & Khởi chạy

### Cách 1: Khởi chạy bằng Docker Compose (Khuyên dùng)

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

### Cách 2: Khởi chạy Cục bộ (Local)

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
