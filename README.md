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

Dự án cá nhân xây dựng website tin tức trực tuyến và hệ thống quản trị nội dung tòa soạn trên nền tảng Java Web với Jakarta Servlet và JSP. Dự án được thực hiện trong quá trình học tập (năm 2025) với giao diện hiện đại, hỗ trợ chế độ Sáng / Tối và cơ chế lưu trữ ảnh an toàn.

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
- **Chuyên mục tin:** Phân loại theo 5 danh mục: Kinh tế & Tài chính, Công nghệ & AI, Thể thao Quốc tế, Đời sống & Khoa học, Giáo dục & Kỹ năng. Hỗ trợ URL thân thiện chuẩn SEO (SEO-friendly category URLs using Vietnamese-aware slugs) theo định dạng `/category/{slug}` (ví dụ: `/category/cong-nghe-ai`), tự động sinh slug duy nhất và tương thích chuyển hướng từ URL ID cũ.
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

- **Ngôn ngữ & Nền tảng:** Java 17 LTS, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL 3.0)
- **Data Access chính:** JDBC kết hợp **HikariCP Connection Pool** hiệu năng cao
- **ORM / Entity Mapping:** Hibernate ORM 6 (Jakarta Persistence) dùng cho định nghĩa cấu trúc Entity và validation schema
- **Máy chủ ứng dụng:** Apache Tomcat 10.1
- **Cơ sở dữ liệu:** Microsoft SQL Server 2022
- **Xác thực & Mã hóa:** BCrypt (OWASP password hashing), 256-bit SecureRandom Remember-Me Token (SHA-256 hashed in DB), Google OAuth2 Client API, Jakarta Mail (Gmail SMTP TLS gửi OTP)
- **Bảo mật Web:** CSRF Filter (token-based), Jsoup 1.17.2 (HTML Whitelist Sanitization chống XSS), SafeImageStorage chống Path Traversal
- **Testing:** JUnit 5 (Jupiter), Mockito (In-memory Mocking & Verification)
- **Giao diện:** HTML5, CSS3, Bootstrap 5.3, FontAwesome 6, Chart.js, CKEditor 5
- **Môi trường triển khai:** Docker & Docker Compose

---

## Kiến Trúc & Thiết Kế Bảo Mật (Security Design)

Hệ thống được thiết kế và tái cấu trúc theo các tiêu chuẩn bảo mật phòng thủ chiều sâu (Defense-in-Depth):

### 1. Cơ Chế Remember-Me An Toàn (Persistent Login Token)
- **Sinh token ngẫu nhiên:** Sử dụng `SecureRandom` sinh chuỗi định danh 256-bit an toàn mật mã học, mã hóa Base64 URL-safe.
- **Bảo vệ phía Database:** Tuyệt đối không lưu plaintext token hoặc Base64(userId). Database chỉ lưu hàm băm **SHA-256** của token.
- **Bảo vệ phía Client:** Token thật chỉ được gửi về trình duyệt qua Cookie có cờ `HttpOnly` (chống XSS đánh cắp), `SameSite=Lax` (giảm thiểu CSRF), `Path=/` và `Secure` khi chạy HTTPS.
- **Token Rotation:** Mỗi lần tự động đăng nhập thành công bằng token hợp lệ, hệ thống tự động thu hồi token cũ và phát hành token mới, triệt tiêu nguy cơ Replay Attack.
- **Thu hồi (Revocation):** Thu hồi server-side ngay khi người dùng bấm Đăng xuất hoặc khi tài khoản bị khóa (`enabled = false`).

### 2. Luồng Xác Thực Email OTP Bảo Mật
- **Tra cứu an toàn:** Tìm bản ghi OTP mới nhất của user theo `userId`, không tìm kiếm trực tiếp bằng cặp `(userId, otpCode)` để đảm bảo luôn kiểm tra trạng thái trước.
- **Kiểm tra đa tầng trước khi so khớp:** Kiểm tra OTP đã hết hạn chưa, đã được sử dụng chưa, và số lần thử (`attempts`) có vượt quá 3 lần hay không.
- **Chống Brute-force & Khóa tự động:** So sánh OTP bằng thuật toán constant-time (`MessageDigest.isEqual`) chống Timing Attack. Nếu nhập sai, tăng `attempts`; khi chạm ngưỡng 3 lần sai, tự động khóa vĩnh viễn mã OTP đó.
- **Chống Race Condition & Chống Tái Sử Dụng:** Tiêu thụ mã OTP nguyên tử (`consumeOtp`) với điều kiện `WHERE is_used = 0 AND attempts < 3`. Một mã OTP không thể được sử dụng lại lần thứ hai.

### 3. Phân Quyền Vai Trò & Phân Biệt Super Admin Minh Bạch (RBAC)
- **Dựa trên dữ liệu chuẩn trong DB:** Bổ sung cột `IsSuperAdmin BIT` trong SQL Server và trường `superAdmin` trong User entity. Tuyệt đối không kiểm tra quyền dựa trên chuỗi username/id (như `startsWith("super")`).
- **Chặn leo thang đặc quyền (Privilege Escalation):** Chỉ Super Admin mới có quyền quản trị, sửa đổi hoặc xóa tài khoản Quản trị viên khác. Admin thường không thể tự phong quyền hoặc can thiệp tài khoản Super Admin.
- **Cô lập tài nguyên phóng viên:** Phóng viên chỉ được xem, chỉnh sửa và xóa các bài viết do chính mình xuất bản (`SecurityHelper.canEditNews`, `canDeleteNews`). Không thể can thiệp bài viết của phóng viên khác.
- **Kiểm duyệt bình luận:** Chỉ Quản trị viên mới có quyền duyệt hoặc xóa bình luận của độc giả.

### 4. Phòng Chống CSRF (Cross-Site Request Forgery)
- **Chuyển đổi toàn diện Method:** Toàn bộ các thao tác thay đổi dữ liệu (tạo mới, cập nhật, xóa tin, xóa danh mục, xóa newsletter, duyệt bình luận, khóa tài khoản) đều được chuyển sang phương thức **POST**. Không dùng GET cho hành động làm biến đổi dữ liệu.
- **CsrfFilter & CsrfUtil:** Sinh token ngẫu nhiên và lưu trữ trong `HttpSession`. Bắt buộc kiểm tra token qua form parameter `_csrf` hoặc HTTP header `X-CSRF-TOKEN` đối với mọi request POST/PUT/DELETE trong khu vực quản trị `/admin/*`.
- **Miễn trừ hợp lý:** Bỏ qua kiểm tra CSRF session token cho các public callback như Google OAuth Sign-in.

### 5. Quản Lý Cấu Hình & Zero Hardcoded Secrets
- **Ưu tiên cấu hình:** `ConfigHelper` nạp cấu hình theo thứ tự ưu tiên: `Biến môi trường hệ điều hành (Environment Variables)` → `System Properties` → `app.properties` → `Safe Fallback`.
- **Bảo vệ Secret:** Không lưu mật khẩu database hay email SMTP thật trong mã nguồn hoặc git. Hỗ trợ file mẫu `app.properties.example`.
- **Google Client ID động:** Nạp động từ cấu hình hệ thống truyền xuống JSP, không hard-code trong mã HTML.

### 6. Xử Lý Lỗi & Phòng Chống Rò Rỉ Thông Tin (Error Leakage)
- Không dùng `e.printStackTrace()` hoặc trả về `e.getMessage()` ra màn hình người dùng.
- Ghi log lỗi có kiểm soát trên console server; người dùng chỉ nhận thông báo lỗi chung thân thiện, ngăn ngừa lộ cấu trúc SQL, đường dẫn file hệ thống hay tên bảng.

---

## Cấu Hình Hệ Thống (Configuration)

Sao chép file mẫu `app.properties.example` thành `app.properties`:

```properties
# 1. Cơ sở dữ liệu SQL Server
db.host=localhost
db.port=1433
db.name=ABCNews
db.user=sa
db.password=your_database_password_here

# 2. Email SMTP (Gmail App Password)
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
mail.smtp.user=your_email@gmail.com
mail.smtp.password=your_gmail_app_password_here

# 3. Google OAuth 2.0 Client ID
google.client.id=your_google_client_id_here
```

---

## Kiểm Thử Tự Động (Testing)

Dự án áp dụng kiểm thử tự động với JUnit 5 và Mockito, bao phủ toàn bộ các luồng bảo mật trọng yếu:
- **Kiểm thử Xác thực (Auth):** Đăng nhập thành công/thất bại, tài khoản bị vô hiệu hóa, cấp phát token Remember-Me, tự động đăng nhập và Token Rotation.
- **Kiểm thử OTP:** Nhập đúng OTP, sai lần 1, sai lần 2, sai lần 3 dẫn đến khóa OTP, từ chối OTP hết hạn, từ chối OTP đã dùng, chống tiêu thụ trùng lặp.
- **Kiểm thử Phân quyền (Authorization):** Phóng viên không vào được chức năng admin, phóng viên không sửa/xóa bài của phóng viên khác, admin thường không xóa được Super Admin, chặn tự xóa chính mình.
- **Kiểm thử CSRF:** GET request tự khởi tạo token, POST thiếu token bị từ chối HTTP 403, token sai bị từ chối, token hợp lệ (form / header) được chấp thuận, callback công khai được miễn trừ.
- **Kiểm thử Chuyên mục & Slug (SEO URLs):** Chuyển đổi slug tiếng Việt không dấu chuẩn SEO, xử lý xung đột trùng lặp slug tự động, tra cứu danh mục theo slug, kiểm tra tính duy nhất và đếm bài viết theo chuyên mục.

Chạy toàn bộ test suite:
```bash
mvn clean test
```
*Kết quả:* **75/75 tests passed (100% SUCCESS)**.

---

## Hướng dẫn Cài đặt & Khởi chạy (How to Run)

### Cách 1: Khởi chạy bằng Docker Compose (Khuyên dùng)

Yêu cầu: Máy tính đã cài sẵn [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
# 1. Clone mã nguồn
git clone https://github.com/nkhanhduy/ABCNews.git
cd ABCNews

# 2. Khởi chạy toàn bộ hệ thống (App + SQL Server)
docker compose up -d

# 3. Mở trình duyệt truy cập:
# - Trang chủ: http://localhost:8088/home
# - Đăng nhập: http://localhost:8088/login
```

Để kiểm tra trạng thái và dừng hệ thống:
```bash
docker compose ps
docker compose down
```

### Cách 2: Khởi chạy Cục bộ

Yêu cầu: JDK 17, Apache Maven 3.8+, SQL Server 2019+ và Apache Tomcat 10.1.

1. Tạo database `ABCNews` trong SQL Server, thực thi kịch bản `schema/ABCNews.sql` rồi nạp dữ liệu mẫu `schema/seed_data.sql`.
2. Chạy các bản cập nhật migration:
   - `schema/migrations/001_security_refactor.sql` (Bảo mật OTP, Remember-Me, RBAC)
   - `schema/migrations/002_category_slug.sql` (Cột Slug cho chuyên mục chuẩn SEO)
3. Tạo file `src/main/resources/app.properties` và điền thông tin đăng nhập database.
4. Chạy kiểm thử và đóng gói file WAR:
   ```bash
   mvn clean test
   mvn clean package -DskipTests
   ```
5. Deploy file `target/ABCNews.war` lên Apache Tomcat 10.1.

---

## Thông tin Tác giả

- **Họ và tên:** Nguyễn Khánh Duy
- **Chuyên ngành:** Phát triển Phần mềm — Trường Cao đẳng FPT Polytechnic TP. Hồ Chí Minh
- **GitHub:** [github.com/nkhanhduy](https://github.com/nkhanhduy)
- **Email:** [khanhndts02168@gmail.com](mailto:khanhndts02168@gmail.com)
- **Năm thực hiện:** 2025
