<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - Tòa soạn ABC News</title>
    
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome Icons (chỉ dùng cho nút chức năng dark mode) --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    
    <%-- Design System CSS chính --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2026.2">
    <!-- Framework CSS chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/framework.css?v=2026.2">
    <!-- Dark Mode CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dark-mode.css?v=2026.2">
    <!-- Theme Toggle JS -->
    <script src="${pageContext.request.contextPath}/assets/js/theme-toggle.js?v=2026.2"></script>

    <style>
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
    </style>
</head>
<body>
    
    <%-- Header chuyên dụng cho trang Auth --%>
    <jsp:include page="/views/common/_header.jsp" />

    <div class="auth-wrapper">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-sm-10 col-md-8 col-lg-5 col-xl-4" style="max-width: 460px;">
                    <div class="auth-card">
                        <div class="auth-header">
                            <h1 class="auth-title">Khôi phục mật khẩu</h1>
                            <p class="auth-subtitle">Nhập địa chỉ email đã đăng ký để nhận mã OTP xác thực</p>
                        </div>
                        
                        <%-- Hiển thị thông báo lỗi nếu có --%>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/forgot-password" method="post" id="forgotForm" class="auth-form">
                            <div class="mb-4">
                                <label for="email" class="form-label">Email tài khoản</label>
                                <input type="email" class="form-control" id="email" name="email" value="${param.email}" placeholder="name@example.com" required autocomplete="email">
                            </div>
                            
                            <button type="submit" class="btn-auth-submit" id="submitBtn">
                                Gửi mã OTP xác thực
                            </button>
                        </form>
                        
                        <div class="text-center mt-4">
                            <a href="${pageContext.request.contextPath}/login" style="font-size: 0.9rem; font-weight: 500;">
                                &larr; Quay lại Đăng nhập
                            </a>
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

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('forgotForm');
            const submitBtn = document.getElementById('submitBtn');
            
            // Xử lý Cooldown nếu Backend yêu cầu đợi
            <c:if test="${not empty cooldownSeconds}">
            let cooldown = ${cooldownSeconds};
            if (submitBtn && cooldown > 0) {
                submitBtn.disabled = true;
                submitBtn.style.opacity = '0.7';
                submitBtn.style.cursor = 'not-allowed';
                submitBtn.innerText = 'Vui lòng đợi (' + cooldown + 's)...';
                
                const timer = setInterval(function() {
                    cooldown--;
                    if (cooldown > 0) {
                        submitBtn.innerText = 'Vui lòng đợi (' + cooldown + 's)...';
                    } else {
                        clearInterval(timer);
                        submitBtn.disabled = false;
                        submitBtn.style.opacity = '1';
                        submitBtn.style.cursor = 'pointer';
                        submitBtn.innerText = 'Gửi mã OTP xác thực';
                    }
                }, 1000);
            }
            </c:if>

            // Chống click đúp khi submit form
            if (form && submitBtn) {
                form.addEventListener('submit', function() {
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang gửi mã OTP...';
                });
            }
        });
    </script>
</body>
</html>
