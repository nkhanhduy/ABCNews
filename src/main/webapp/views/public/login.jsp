<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Tòa soạn ABC News</title>
    
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome Icons (chỉ dùng cho icon chức năng: mắt ẩn hiện mật khẩu, dark mode) --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    
    <%-- Design System CSS chính (kèm query param phá cache trình duyệt) --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2026.2">
    <!-- Framework CSS chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/framework.css?v=2026.2">
    <!-- Dark Mode CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dark-mode.css?v=2026.2">
    <!-- Theme Toggle JS -->
    <script src="${pageContext.request.contextPath}/assets/js/theme-toggle.js?v=2026.2"></script>

    <style>
        /* CSS nhúng trực tiếp phòng ngừa tình trạng trình duyệt lưu cache style.css cũ */
        .auth-wrapper {
            min-height: calc(100vh - 160px);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 36px 15px;
        }
        .auth-card {
            background-color: var(--brand-card-bg, #ffffff);
            border-radius: 14px;
            border: 1px solid var(--brand-border, #e2e8f0);
            box-shadow: 0 10px 25px -5px rgba(15, 23, 42, 0.08), 0 8px 10px -6px rgba(15, 23, 42, 0.06);
            width: 100%;
            max-width: 440px;
            padding: 36px 30px;
            margin: 0 auto;
            transition: all 0.25s ease;
        }
        .auth-header {
            text-align: center;
            margin-bottom: 24px;
        }
        .auth-title {
            font-size: 1.65rem;
            font-weight: 800;
            color: var(--brand-text, #0f172a);
            margin-bottom: 6px;
            letter-spacing: -0.5px;
        }
        .auth-subtitle {
            color: var(--brand-text-muted, #64748b);
            font-size: 0.9rem;
            margin-bottom: 0;
        }
        .auth-form .form-label {
            font-weight: 600;
            font-size: 0.88rem;
            color: var(--brand-text, #0f172a);
            margin-bottom: 6px;
            display: block;
        }
        .auth-form .form-control {
            border: 1px solid var(--brand-input-border, #cbd5e1);
            border-radius: 8px;
            padding: 10px 14px;
            font-size: 0.95rem;
            color: var(--brand-text, #0f172a);
            background-color: var(--brand-input-bg, #ffffff);
            transition: all 0.2s ease;
        }
        .auth-form .form-control:focus {
            border-color: var(--brand-accent, #2563eb);
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.2);
            background-color: var(--brand-input-bg, #ffffff);
            color: var(--brand-text, #0f172a);
            outline: none;
        }
        .password-field-wrapper {
            position: relative;
        }
        .password-toggle-btn {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            background: transparent;
            border: none;
            color: var(--brand-text-muted, #64748b);
            cursor: pointer;
            padding: 4px;
        }
        .password-toggle-btn:hover {
            color: var(--brand-text, #0f172a);
        }
        .btn-auth-submit {
            background: linear-gradient(135deg, #16a34a, #15803d);
            color: #ffffff !important;
            border: none;
            font-weight: 700;
            font-size: 0.98rem;
            padding: 11px 20px;
            border-radius: 8px;
            width: 100%;
            cursor: pointer;
            transition: all 0.2s ease;
            margin-top: 8px;
            box-shadow: 0 4px 12px rgba(22, 163, 74, 0.25);
        }
        .btn-auth-submit:hover {
            background: linear-gradient(135deg, #15803d, #14532d);
            transform: translateY(-1px);
            box-shadow: 0 6px 16px rgba(22, 163, 74, 0.35);
        }
        .auth-divider {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 22px 0 18px;
            color: var(--brand-text-muted, #64748b);
            font-size: 0.82rem;
        }
        .auth-divider::before, .auth-divider::after {
            content: '';
            flex: 1;
            border-bottom: 1px solid var(--brand-border, #e2e8f0);
        }
        .auth-divider span {
            padding: 0 10px;
        }
        /* Box tài khoản trải nghiệm tối giản, hiện đại, không dùng icon */
        .demo-accounts-card {
            background-color: var(--brand-card-bg-subtle, #f8fafc);
            border: 1px solid var(--brand-border, #e2e8f0);
            border-radius: 8px;
            padding: 10px 12px;
            margin-top: 18px;
        }
        .demo-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            padding-bottom: 6px;
            border-bottom: 1px solid var(--brand-border, #e2e8f0);
        }
        .demo-card-title {
            font-size: 0.74rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: var(--brand-text-muted, #64748b);
        }
        .demo-card-pass {
            font-size: 0.76rem;
            color: var(--brand-text-muted, #64748b);
        }
        .demo-card-pass code {
            font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
            font-weight: 700;
            color: var(--brand-text, #0f172a);
            background: transparent;
            padding: 0;
            font-size: 0.8rem;
        }
        .demo-accounts-list {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        .demo-account-btn {
            display: flex;
            justify-content: space-between;
            align-items: center;
            width: 100%;
            padding: 5px 9px;
            background-color: var(--brand-card-bg, #ffffff);
            border: 1px solid var(--brand-border, #e2e8f0);
            border-radius: 6px;
            font-size: 0.8rem;
            color: var(--brand-text, #0f172a);
            cursor: pointer;
            transition: all 0.15s ease;
            text-align: left;
        }
        .demo-account-btn:hover {
            background-color: #f1f5f9;
            border-color: #cbd5e1;
        }
        .demo-account-btn:active {
            transform: scale(0.99);
        }
        .demo-role-name {
            font-weight: 600;
            color: var(--brand-text, #0f172a);
            font-size: 0.8rem;
        }
        .demo-account-email {
            font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
            font-size: 0.76rem;
            color: var(--brand-text-muted, #64748b);
        }

        /* Dark mode overrides cho box tài khoản trải nghiệm */
        [data-theme="dark"] .demo-accounts-card {
            background-color: #1e293b;
            border-color: #334155;
        }
        [data-theme="dark"] .demo-card-header {
            border-bottom-color: #334155;
        }
        [data-theme="dark"] .demo-card-title,
        [data-theme="dark"] .demo-card-pass {
            color: #94a3b8;
        }
        [data-theme="dark"] .demo-card-pass code {
            color: #f1f5f9;
        }
        [data-theme="dark"] .demo-account-btn {
            background-color: #0f172a;
            border-color: #334155;
            color: #f8fafc;
        }
        [data-theme="dark"] .demo-account-btn:hover {
            background-color: #1e293b;
            border-color: #475569;
        }
        [data-theme="dark"] .demo-role-name {
            color: #f8fafc;
        }
        [data-theme="dark"] .demo-account-email {
            color: #94a3b8;
        }
    </style>
</head>
<body>
    
    <%-- Header chuyên dụng cho trang Auth (không kèm menu tin tức rỗng) --%>
    <jsp:include page="/views/common/_header.jsp" />

    <div class="auth-wrapper">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-sm-10 col-md-8 col-lg-5 col-xl-4" style="max-width: 460px;">
                    <div class="auth-card">
                        <div class="auth-header">
                            <h1 class="auth-title">Đăng nhập</h1>
                            <p class="auth-subtitle">Chào mừng bạn quay trở lại với ABC News</p>
                        </div>
                        
                        <%-- Hiển thị thông báo khi reset password thành công --%>
                        <c:if test="${param.resetSuccess eq 'true'}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                Đã đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới.
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
                            </div>
                        </c:if>
                        
                        <%-- Hiển thị lỗi xác thực nếu có --%>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm" class="auth-form">
                            <div class="mb-3">
                                <label for="email" class="form-label">Địa chỉ Email</label>
                                <input type="email" class="form-control" id="email" name="email" value="${param.email}" placeholder="name@example.com" required autocomplete="email">
                            </div>
                            
                            <div class="mb-3">
                                <label for="password" class="form-label">Mật khẩu</label>
                                <div class="password-field-wrapper">
                                    <input type="password" class="form-control" id="password" name="password" placeholder="Nhập mật khẩu của bạn" required autocomplete="current-password">
                                    <button type="button" class="password-toggle-btn" id="togglePasswordBtn" aria-label="Hiện mật khẩu">
                                        <i class="fas fa-eye" id="passwordToggleIcon"></i>
                                    </button>
                                </div>
                            </div>
                            
                            <div class="mb-4 d-flex justify-content-between align-items-center">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" id="remember" name="remember" value="on">
                                    <label class="form-check-label text-muted" for="remember" style="font-size: 0.88rem;">
                                        Ghi nhớ đăng nhập
                                    </label>
                                </div>
                                <a href="${pageContext.request.contextPath}/forgot-password" style="font-size: 0.88rem; font-weight: 500;">
                                    Quên mật khẩu?
                                </a>
                            </div>
                            
                            <button type="submit" class="btn-auth-submit" id="submitBtn">
                                Đăng nhập
                            </button>
                        </form>
                        
                        <%-- Google Sign-In Divider --%>
                        <div class="auth-divider">
                            <span>Hoặc đăng nhập bằng</span>
                        </div>
                        
                        <div class="d-flex justify-content-center">
                            <div id="g_id_onload"
                                 data-client_id="${not empty googleClientId ? googleClientId : applicationScope.googleClientId}"
                                 data-callback="handleGoogleSignIn"
                                 data-auto_prompt="false">
                            </div>
                            <div class="g_id_signin"
                                 data-type="standard"
                                 data-size="large"
                                 data-theme="outline"
                                 data-text="signin_with"
                                 data-shape="rectangular"
                                 data-logo_alignment="left">
                            </div>
                        </div>

                        <%-- Thẻ tài khoản trải nghiệm nhanh (Thiết kế tối giản, chuyên nghiệp, không icon) --%>
                        <div class="demo-accounts-card">
                            <div class="demo-card-header">
                                <span class="demo-card-title">Tài khoản trải nghiệm</span>
                                <span class="demo-card-pass">Mật khẩu: <code>123456</code></span>
                            </div>
                            <div class="demo-accounts-list">
                                <button type="button" class="demo-account-btn" onclick="fillAccount('superadmin@abcnews.com', '123456')" title="Chọn tài khoản Super Admin">
                                    <span class="demo-role-name">Super Admin</span>
                                    <span class="demo-account-email">superadmin@abcnews.com</span>
                                </button>
                                <button type="button" class="demo-account-btn" onclick="fillAccount('admin@abcnews.com', '123456')" title="Chọn tài khoản Quản trị viên">
                                    <span class="demo-role-name">Quản trị viên</span>
                                    <span class="demo-account-email">admin@abcnews.com</span>
                                </button>
                                <button type="button" class="demo-account-btn" onclick="fillAccount('reporter1@abcnews.com', '123456')" title="Chọn tài khoản Phóng viên">
                                    <span class="demo-role-name">Phóng viên</span>
                                    <span class="demo-account-email">reporter1@abcnews.com</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Footer chuẩn tòa soạn --%>
    <jsp:include page="/views/common/_footer.jsp" />

    <%-- Bootstrap 5 JS Bundle --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    
    <%-- Google Sign-In API --%>
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    
    <script>
        // Xử lý Google Sign-in
        function handleGoogleSignIn(response) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/auth/google/verify';
            
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'credential';
            input.value = response.credential;
            form.appendChild(input);
            
            document.body.appendChild(form);
            form.submit();
        }

        // Bật / Tắt hiển thị mật khẩu
        const toggleBtn = document.getElementById('togglePasswordBtn');
        const passwordInput = document.getElementById('password');
        const toggleIcon = document.getElementById('passwordToggleIcon');
        
        if (toggleBtn && passwordInput && toggleIcon) {
            toggleBtn.addEventListener('click', function() {
                const isPassword = passwordInput.getAttribute('type') === 'password';
                passwordInput.setAttribute('type', isPassword ? 'text' : 'password');
                toggleIcon.classList.toggle('fa-eye', !isPassword);
                toggleIcon.classList.toggle('fa-eye-slash', isPassword);
            });
        }

        // Tiện ích tự điền tài khoản mẫu cho nhà tuyển dụng
        function fillAccount(email, password) {
            document.getElementById('email').value = email;
            document.getElementById('password').value = password;
            document.getElementById('email').focus();
        }

        // Hiệu ứng nút khi bấm submit
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', function() {
                const btn = document.getElementById('submitBtn');
                if (btn) {
                    btn.disabled = true;
                    btn.innerText = 'Đang xác thực...';
                }
            });
        }
    </script>
</body>
</html>