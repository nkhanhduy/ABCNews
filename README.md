# ABCNews

Ứng dụng web tin tức (Java Servlet/JSP) với khu vực public để đọc tin và khu vực `/admin/*` để quản trị nội dung.

## Chức năng chính

- Tin tức
  - Public: xem danh sách tin, xem chi tiết, tăng lượt xem, tin liên quan, tin “Trang nhất”.
  - Admin/Reporter: CRUD tin tức, upload ảnh, tìm kiếm/lọc/sắp xếp.
  - Newsletter: khi tạo tin có thể gửi email cho subscriber (tuỳ chọn trong form).
- Newsletter (đăng ký nhận tin)
  - Public: người dùng nhập email để đăng ký nhận bản tin từ sidebar (`POST /newsletter`).
  - Admin: quản lý danh sách email đăng ký, bật/tắt subscription, xoá, tìm kiếm/lọc (`/admin/newsletters`).
- Xác thực & tài khoản
  - Đăng nhập thường + remember-me cookie (`/login`).
  - Đăng nhập Google (verify ID token) và link với account có sẵn (`POST /auth/google/verify`).
  - Quên mật khẩu OTP qua email + reset password (BCrypt) (`/forgot-password` → `/verify-otp`).
  - Profile: xem thông tin tài khoản (`/admin/profile`).
- Quản trị
  - Dashboard phân quyền: Admin (tổng quan toàn hệ thống) và Reporter (chỉ số theo tin của mình).
  - Quản lý Users (`/admin/users`), Categories (`/admin/categories`).
- Activity Logs (Audit)
  - Ghi nhận các hành động quan trọng (login/logout, CRUD, lock/unlock, export…).
  - Có filter, phân trang (`/admin/activity-logs`).
- Export dữ liệu
  - Export News/Users/Categories/Newsletters theo CSV/Excel/PDF (`/admin/export`).
  - Reporter chỉ export News của chính mình; Admin export được tất cả module.

## Roles & Permissions

Ghi chú:

- Hệ thống có 4 nhóm quyền chính: Guest, Reporter, Admin, Super Admin.
- Khu vực quản trị nằm dưới `/admin/*` và yêu cầu đăng nhập.
- Super Admin có toàn bộ quyền của Admin và thêm quyền quản trị tài khoản cấp cao.

### Guest (chưa đăng nhập)

- **Public pages**: đọc tin, xem chi tiết tin.
- **Newsletter subscribe**: `POST /newsletter`.
- **Authentication**:
  - Đăng nhập thường: `/login`
  - Google verify: `POST /auth/google/verify`
  - Quên mật khẩu: `/forgot-password` → `/verify-otp`

### Reporter (đã đăng nhập, `role=false`)

- **Dashboard**: xem thống kê theo tin của chính mình: `GET /admin/dashboard`.
- **Quản lý tin tức**: `GET/POST /admin/news`
  - Xem danh sách tin của mình.
  - Tạo/Sửa/Xóa tin của mình.
  - Tìm kiếm/lọc/sắp xếp (áp trên scope tin của mình).
- **Export**:
  - Có thể export **News của chính mình**: `GET /admin/export?type=news...`.
  - Không được export `users/categories/newsletters` (controller trả `403`).
- **Profile**:
  - Xem profile của chính mình: `GET /admin/profile`.
  - Nếu truyền `?id=...` vẫn chỉ xem được chính mình (controller tự fallback).

### Admin (đã đăng nhập, `role=true`)

- **Dashboard**: xem tổng quan toàn hệ thống: `GET /admin/dashboard`.
- **Quản lý tin tức**: `GET/POST /admin/news`
  - Xem tất cả tin.
  - Có thể filter theo tác giả (`filterAuthor`).
- **Quản lý người dùng**: `GET/POST /admin/users`.
  - CRUD user.
  - Khóa/mở khóa user.
  - Xóa user: Admin thường **không** xóa/khóa được Admin khác.
  - Không được tạo Super Admin.
- **Quản lý loại tin**: `GET/POST /admin/categories`.
- **Quản lý newsletter (subscribers)**: `GET /admin/newsletters` (delete/toggle/search).
- **Activity logs**: `GET /admin/activity-logs`.
- **Export**:
  - Export News/Users/Categories/Newsletters: `GET /admin/export?...`.
  - Export nhiều module gộp 1 file (multi-select `type`).

### Super Admin (Admin đặc biệt, `isSuperAdmin=true`)

- **Toàn bộ quyền của Admin**.
- **Quản lý Admin/Super Admin (trong Users module)**:
  - Có thể tạo Super Admin (`role=super`).
  - Có thể khóa/xóa Admin khác (trừ chính mình).
  - Admin thường bị chặn khi sửa user là Super Admin.

## Kiến trúc (tổng quan)

Kiểu kiến trúc gần với **MVC/3-layer**:

- Tầng điều khiển (Servlet): nhận request, kiểm tra dữ liệu đầu vào, gọi tầng xử lý dữ liệu, rồi render JSP.
- Tầng truy cập dữ liệu (DAO): làm việc với SQL Server (JDBC).
- Tầng mô hình (Entity): ánh xạ dữ liệu theo bảng.
- Tầng dịch vụ/tiện ích: các phần dùng chung (ghi log, export, email, OTP, mã hoá mật khẩu, upload…).
- Tầng giao diện (JSP): public/admin + layout.

Khu vực `/admin/*` yêu cầu đăng nhập.

## Luồng xử lý (request → database → response)

Ví dụ điển hình:

- `GET /admin/news`
  - Hệ thống kiểm tra bạn đã đăng nhập chưa.
  - Lấy danh sách tin theo quyền:
    - Admin: xem tất cả tin.
    - Reporter: chỉ xem tin do mình đăng.
  - Render trang quản trị tin tức.

- `POST /forgot-password` → `POST /verify-otp`
  - Người dùng nhập email để nhận mã OTP.
  - Hệ thống gửi OTP qua email và lưu OTP để đối chiếu.
  - Người dùng nhập OTP + mật khẩu mới.
  - Hệ thống kiểm tra OTP (hợp lệ/hết hạn/số lần thử) và cập nhật mật khẩu mới.

- `POST /admin/news?action=create&sendNewsletter=true`
  - Lưu tin vào DB.
  - Nếu bật tuỳ chọn gửi newsletter: hệ thống gửi email thông báo tới danh sách người đã đăng ký nhận tin.
  - Ghi lại lịch sử thao tác (audit log).

## Công nghệ sử dụng

- Java 17
- Jakarta Servlet/JSP/JSTL (WAR)
- Maven
- SQL Server (JDBC Driver)
- Apache Commons BeanUtils
- Jakarta Mail (Angus)
- Apache POI (Excel)
- iText7 (PDF)
- BCrypt (jbcrypt)
- Google OAuth token verify (Google API Client)

## Yêu cầu môi trường

- JDK 17+
- Maven 3.8+
- Apache Tomcat **10.1+** (Jakarta EE 10 / Servlet 6)
- SQL Server

## Cách sử dụng khi tải về (Quick start)

### Bước 1: Chuẩn bị database

- Tạo database trên SQL Server (mặc định tên là `ABCNews`).
- Mở file kịch bản CSDL tại `schema/ABCNews.sql` và chạy trong SQL Server Management Studio (SSMS) để tự động khởi tạo bảng và dữ liệu mẫu.

### Bước 2: Cấu hình kết nối & Credentials

- Sao chép file cấu hình mẫu `src/main/resources/app.properties.example` thành:
  ```
  src/main/resources/app.properties
  ```
- Cập nhật các thông số `db.user`, `db.password` và email SMTP nếu muốn dùng tính năng gửi OTP/Newsletter.
- File `app.properties` đã được bảo vệ trong `.gitignore` để không bị lộ mật khẩu lên Git.

### Bước 3: (Tuỳ chọn) cấu hình Email & Google

- Nếu muốn dùng **Quên mật khẩu (OTP)** hoặc **Newsletter**, cấu hình tài khoản Gmail và Gmail App Password trong `src/main/resources/app.properties`.
- Nếu muốn dùng **Đăng nhập Google**, cấu hình `CLIENT_ID` trong `GoogleVerifyController.java`.

### Bước 4: Build & chạy trên Tomcat

Cách A (Maven build WAR):

```bash
mvn clean package
```

- Deploy file `target/ABCNews.war` lên Apache Tomcat 10.1+.
- Start Tomcat và truy cập ứng dụng.

Cách B (Eclipse / IDE):

- Import project Maven vào Eclipse hoặc IntelliJ IDEA.
- Add project vào Tomcat Server (Jakarta EE 10 / Servlet 6.0) và chạy.

## Cấu hình

Hệ thống sử dụng cơ chế nạp cấu hình linh hoạt thông qua lớp `ConfigHelper`:

- **Ưu tiên 1**: Biến môi trường hệ thống (`DB_HOST`, `DB_PASSWORD`, `MAIL_SMTP_PASSWORD`...).
- **Ưu tiên 2**: File cấu hình `src/main/resources/app.properties`.
- Có sẵn file mẫu `src/main/resources/app.properties.example` để tham khảo.

### 2) Email SMTP (Forgot password + Newsletter)

Cấu hình đang được hardcode trong source (tiện ích gửi email):

- `SMTP_USER`
- `SMTP_PASSWORD` (Gmail App Password)

Khuyến nghị chuyển sang biến môi trường hoặc file config và **không commit credential lên Git**.

### 3) Google OAuth

Endpoint: `POST /auth/google/verify`

Cần cấu hình `CLIENT_ID` theo Google Console.

## Build & Deploy

- Build WAR:

```bash
mvn clean package
```

- Deploy:
  - Deploy file `target/ABCNews.war` lên Tomcat.
  - Hoặc add project vào Eclipse + Tomcat Server.




