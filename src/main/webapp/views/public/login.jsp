<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - ABC News</title>
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome Icons --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <%-- Custom CSS --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <!-- Framework CSS chung - Dropdown, Buttons, Cards -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/framework.css">
    <!-- Public Style Framework -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public-style.css">
</head>
<body>
    
    <%-- Header và Menu --%>
    <jsp:include page="/views/common/_header.jsp" />
    <jsp:include page="/views/common/_menu.jsp" />

    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-md-5">
                <div class="card shadow-lg border-0 login-card">
                    <div class="card-body p-5">
                        <div class="text-center mb-4">
                            <div class="login-icon-wrapper mb-3">
                                <i class="fas fa-user-circle fa-3x text-success"></i>
                            </div>
                            <h2 class="fw-bold">Đăng nhập</h2>
                            <p class="text-muted">Vui lòng đăng nhập để tiếp tục</p>
                        </div>
                        
                        <%-- Hiển thị success message khi reset password thành công --%>
                        <c:if test="${param.resetSuccess eq 'true'}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>Đã đặt lại mật khẩu thành công! Vui lòng đăng nhập.
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                        
                        <%-- Hiển thị lỗi (nếu có) --%>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-circle me-2"></i>${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
                            <div class="mb-3">
                                <label for="email" class="form-label">
                                    <i class="fas fa-envelope me-1"></i>Email
                                </label>
                                <input type="email" class="form-control form-control-lg" id="email" name="email" value="${param.email}" placeholder="Nhập email của bạn" required autocomplete="email">
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label">
                                    <i class="fas fa-lock me-1"></i>Mật khẩu
                                </label>
                                <div class="password-input-wrapper">
                                    <input type="password" class="form-control form-control-lg" id="password" name="password" placeholder="Nhập mật khẩu" required autocomplete="current-password">
                                    <button type="button" class="btn-toggle-password" aria-label="Show password">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="mb-4 d-flex justify-content-between align-items-center">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" id="remember" name="remember" value="on">
                                    <label class="form-check-label" for="remember">
                                        Remember me
                                    </label>
                                </div>
                                <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password-link">
                                    <i class="fas fa-question-circle me-1"></i>Quên mật khẩu?
                                </a>
                            </div>
                            <button type="submit" class="btn btn-success btn-lg w-100 login-submit-btn">
                                <i class="fas fa-sign-in-alt me-2"></i>Đăng nhập
                            </button>
                        </form>
                        
                        <%-- Google Sign-In --%>
                        <div class="text-center my-4">
                            <div class="position-relative">
                                <hr>
                                <span class="position-absolute top-50 start-50 translate-middle bg-white px-3 text-muted">
                                    or
                                </span>
                            </div>
                        </div>
                        
                        <div class="d-flex justify-content-center mb-3 google-signin-wrapper">
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
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Footer --%>
    <jsp:include page="/views/common/_footer.jsp" />

    <%-- Bootstrap 5 JS Bundle --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    
    <%-- Google Sign-In JavaScript API --%>
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <%-- Public UX Enhancements --%>
    <script src="${pageContext.request.contextPath}/assets/js/public-ux.js"></script>
    <script>
        function handleGoogleSignIn(response) {
            // Gửi credential về server để verify
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
        
        // Login form enhancements
        (function() {
            // Password toggle
            const toggleBtn = document.querySelector('.btn-toggle-password');
            const passwordInput = document.getElementById('password');
            
            if (toggleBtn && passwordInput) {
                toggleBtn.addEventListener('click', function() {
                    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                    passwordInput.setAttribute('type', type);
                    this.querySelector('i').classList.toggle('fa-eye');
                    this.querySelector('i').classList.toggle('fa-eye-slash');
                });
            }
            
            // Form validation
            const loginForm = document.getElementById('loginForm');
            if (loginForm) {
                loginForm.addEventListener('submit', function(e) {
                    const email = document.getElementById('email').value.trim();
                    const password = document.getElementById('password').value;
                    
                    if (!email || !password) {
                        e.preventDefault();
                        return false;
                    }
                    
                    // Show loading state
                    const submitBtn = this.querySelector('.login-submit-btn');
                    if (submitBtn) {
                        submitBtn.disabled = true;
                        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang đăng nhập...';
                    }
                });
            }
            
            // Input focus effects
            const inputs = document.querySelectorAll('#loginForm input');
            inputs.forEach(function(input) {
                input.addEventListener('focus', function() {
                    this.parentElement.classList.add('focused');
                });
                
                input.addEventListener('blur', function() {
                    if (!this.value) {
                        this.parentElement.classList.remove('focused');
                    }
                });
            });
        })();
    </script>
    <style>
        /* Login Page Specific Styles */
        .login-card {
            animation: fadeInUp 0.5s ease-out;
            border-radius: 15px;
            overflow: hidden;
        }
        
        .login-icon-wrapper {
            animation: bounceIn 0.6s ease-out;
        }
        
        .login-icon-wrapper i {
            transition: transform 0.3s ease;
        }
        
        .login-card:hover .login-icon-wrapper i {
            transform: scale(1.1);
        }
        
        .password-input-wrapper {
            position: relative;
        }
        
        .btn-toggle-password {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #6c757d;
            cursor: pointer;
            padding: 5px 10px;
            transition: color 0.3s ease;
        }
        
        .btn-toggle-password:hover {
            color: #28a745;
        }
        
        .form-control:focus {
            border-color: #28a745;
            box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.25);
        }
        
        .login-submit-btn {
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        
        .login-submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
        }
        
        .login-submit-btn:active {
            transform: translateY(0);
        }
        
        .login-submit-btn:disabled {
            opacity: 0.7;
            cursor: not-allowed;
        }
        
        .form-check-input:checked {
            background-color: #28a745;
            border-color: #28a745;
        }
        
        .form-check-input:focus {
            border-color: #28a745;
            box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.25);
        }
        
        .google-signin-wrapper {
            min-height: 50px;
        }
        
        .forgot-password-link {
            color: #667eea;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: color 0.3s ease;
        }
        
        .forgot-password-link:hover {
            color: #764ba2;
            text-decoration: underline;
        }
        
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        @keyframes bounceIn {
            0% {
                opacity: 0;
                transform: scale(0.3);
            }
            50% {
                transform: scale(1.05);
            }
            70% {
                transform: scale(0.9);
            }
            100% {
                opacity: 1;
                transform: scale(1);
            }
        }
        
        /* Responsive */
        @media (max-width: 768px) {
            .login-card .card-body {
                padding: 2rem !important;
            }
        }
    </style>
</body>
</html>