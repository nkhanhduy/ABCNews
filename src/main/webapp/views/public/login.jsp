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
        .demo-accounts-card {
            background-color: var(--brand-demo-bg, #f8fafc);
            border: 1px dashed var(--brand-demo-border, #cbd5e1);
            border-radius: 8px;
            padding: 12px 14px;
            margin-top: 22px;
            font-size: 0.82rem;
            color: var(--brand-text-body, #334155);
        }
        .demo-accounts-card .demo-title {
            font-weight: 700;
            color: var(--brand-text, #0f172a);
            margin-bottom: 5px;
        }
        .demo-account-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 3px 0;
        }
        .demo-account-item span {
            color: var(--brand-text-muted, #64748b);
        }
        .demo-account-item strong {
            color: var(--brand-accent, #2563eb) !important;
            cursor: pointer;
        }
        .demo-account-item strong:hover {
            text-decoration: underline;
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
                                 data-client_id="248224711124-mr0usg1vgteil4fbo06hgrmshchtq4ca.apps.googleusercontent.com"
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

                        <%-- Thẻ tài khoản trải nghiệm nhanh --%>
                        <div class="demo-accounts-card">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="fw-bold text-dark small"><i class="fas fa-bolt text-warning me-1"></i>Tài khoản trải nghiệm nhanh</span>
                                <span class="text-muted small">Mật khẩu: <code class="fw-bold text-dark">123456</code></span>
                            </div>
                            <div class="d-grid gap-1">
                                <button type="button" class="btn btn-sm btn-outline-success text-start py-1 px-2 d-flex justify-content-between align-items-center" onclick="fillAccount('admin@abcnews.com', '123456')" title="Bấm để tự động điền tài khoản Tổng Biên Tập">
                                    <span><i class="fas fa-user-shield me-1"></i><strong>Tổng Biên Tập</strong></span>
                                    <span class="small font-monospace">admin@abcnews.com</span>
                                </button>
                                <button type="button" class="btn btn-sm btn-outline-primary text-start py-1 px-2 d-flex justify-content-between align-items-center" onclick="fillAccount('reporter1@abcnews.com', '123456')" title="Bấm để tự động điền tài khoản Phóng Viên">
                                    <span><i class="fas fa-feather-alt me-1"></i><strong>Phóng Viên</strong></span>
                                    <span class="small font-monospace">reporter1@abcnews.com</span>
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