<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác thực mã OTP - Tòa soạn ABC News</title>
    
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome Icons (chỉ dùng cho nút chức năng) --%>
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
        .otp-inputs {
            display: flex;
            justify-content: center;
            gap: 10px;
            margin: 20px 0;
        }
        .otp-box {
            width: 52px;
            height: 56px;
            font-size: 24px;
            font-weight: 700;
            text-align: center;
            border: 1.5px solid var(--brand-input-border, #cbd5e1);
            border-radius: 8px;
            background: var(--brand-input-bg, #ffffff);
            color: var(--brand-text, #0f172a);
            transition: all 0.2s ease;
        }
        .otp-box:focus {
            border-color: var(--brand-accent, #2563eb);
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.2);
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
        @media (max-width: 576px) {
            .otp-box {
                width: 42px;
                height: 48px;
                font-size: 20px;
            }
        }
    </style>
</head>
<body>
    
    <%-- Header chuyên dụng cho trang Auth --%>
    <jsp:include page="/views/common/_header.jsp" />

    <div class="auth-wrapper">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-sm-10 col-md-8 col-lg-6 col-xl-5" style="max-width: 500px;">
                    <div class="auth-card">
            <div class="auth-header">
                <h1 class="auth-title">Xác thực mã OTP</h1>
                <p class="auth-subtitle">
                    Mã xác nhận 6 chữ số đã được gửi tới email:<br>
                    <strong class="text-primary">${sessionScope.resetUserEmail}</strong>
                </p>
            </div>
            
            <%-- Thông báo gửi OTP thành công --%>
            <c:if test="${param.success eq 'sent'}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    Mã OTP mới đã được gửi thành công. Vui lòng kiểm tra hộp thư.
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
                </div>
            </c:if>
            
            <%-- Hiển thị thông báo lỗi nếu có --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/verify-otp" method="post" id="verifyForm" class="auth-form">
                
                <%-- 6 ô nhập mã OTP --%>
                <div class="mb-3">
                    <label class="form-label text-center d-block">Nhập mã OTP gồm 6 chữ số</label>
                    <div class="otp-inputs">
                        <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control" id="otp1" autocomplete="off">
                        <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control" id="otp2" autocomplete="off">
                        <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control" id="otp3" autocomplete="off">
                        <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control" id="otp4" autocomplete="off">
                        <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control" id="otp5" autocomplete="off">
                        <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control" id="otp6" autocomplete="off">
                    </div>
                    <input type="hidden" name="otpCode" id="otpCode">
                    
                    <div class="d-flex justify-content-between align-items-center text-muted small mt-2">
                        <span>Thời gian hiệu lực: <strong id="countdown" class="text-danger">05:00</strong></span>
                        <c:if test="${not expired}">
                            <button type="button" class="btn btn-link p-0 text-decoration-none small" id="resendBtn" onclick="resendOtp()" disabled>
                                Gửi lại mã (<span id="resendCountdown">60</span>s)
                            </button>
                        </c:if>
                    </div>
                </div>
                
                <%-- Mật khẩu mới --%>
                <div class="mb-3">
                    <label for="newPassword" class="form-label">Mật khẩu mới</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" placeholder="Tối thiểu 8 ký tự" required minlength="8">
                </div>
                
                <div class="mb-4">
                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" placeholder="Nhập lại mật khẩu mới" required minlength="8">
                </div>
                
                <button type="submit" class="btn-auth-submit" id="submitBtn">
                    Xác nhận đổi mật khẩu
                </button>
            </form>
            
            <div class="text-center mt-4">
                <a href="${pageContext.request.contextPath}/forgot-password" style="font-size: 0.9rem; font-weight: 500;">
                    &larr; Thay đổi địa chỉ email khác
                </a>
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
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const otpBoxes = document.querySelectorAll('.otp-box');
            const otpCodeInput = document.getElementById('otpCode');
            const form = document.getElementById('verifyForm');
            
            otpBoxes[0].focus();
            
            otpBoxes.forEach((box, index) => {
                box.addEventListener('input', function() {
                    const value = this.value;
                    if (!/^\d*$/.test(value)) {
                        this.value = '';
                        return;
                    }
                    if (value && index < otpBoxes.length - 1) {
                        otpBoxes[index + 1].focus();
                    }
                    updateOtpCode();
                });
                
                box.addEventListener('keydown', function(e) {
                    if (e.key === 'Backspace' && !this.value && index > 0) {
                        otpBoxes[index - 1].focus();
                    }
                });
                
                box.addEventListener('paste', function(e) {
                    e.preventDefault();
                    const pastedData = e.clipboardData.getData('text');
                    const digits = pastedData.match(/\d/g);
                    if (digits) {
                        digits.slice(0, 6).forEach((digit, i) => {
                            if (otpBoxes[i]) {
                                otpBoxes[i].value = digit;
                            }
                        });
                        updateOtpCode();
                        if (digits.length >= 6) {
                            otpBoxes[5].focus();
                        }
                    }
                });
            });
            
            function updateOtpCode() {
                const otp = Array.from(otpBoxes).map(box => box.value).join('');
                otpCodeInput.value = otp;
            }
            
            form.addEventListener('submit', function(e) {
                updateOtpCode();
                const otp = otpCodeInput.value;
                if (otp.length !== 6) {
                    e.preventDefault();
                    alert('Vui lòng nhập đủ 6 chữ số OTP');
                    otpBoxes[0].focus();
                    return;
                }
                
                const newPassword = document.getElementById('newPassword').value;
                const confirmPassword = document.getElementById('confirmPassword').value;
                if (newPassword !== confirmPassword) {
                    e.preventDefault();
                    alert('Mật khẩu xác nhận không khớp');
                    document.getElementById('confirmPassword').focus();
                    return;
                }
                
                const submitBtn = document.getElementById('submitBtn');
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerText = 'Đang xác thực...';
                }
            });
            
            // Đếm ngược 5 phút
            let timeLeft = 5 * 60;
            const countdownElement = document.getElementById('countdown');
            const submitBtn = document.getElementById('submitBtn');
            
            const timerInterval = setInterval(function() {
                const minutes = Math.floor(timeLeft / 60);
                const seconds = timeLeft % 60;
                countdownElement.textContent = 
                    String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
                
                if (timeLeft <= 0) {
                    countdownElement.textContent = 'Đã hết hạn';
                    submitBtn.disabled = true;
                    clearInterval(timerInterval);
                }
                timeLeft--;
            }, 1000);
            
            // Đếm ngược gửi lại 60s
            const resendBtn = document.getElementById('resendBtn');
            if (resendBtn) {
                let resendCooldown = 60;
                const resendCountdownSpan = document.getElementById('resendCountdown');
                const cooldownInterval = setInterval(function() {
                    resendCooldown--;
                    if (resendCountdownSpan) resendCountdownSpan.textContent = resendCooldown;
                    if (resendCooldown <= 0) {
                        clearInterval(cooldownInterval);
                        resendBtn.disabled = false;
                        resendBtn.innerText = 'Gửi lại mã OTP';
                    }
                }, 1000);
            }
        });
        
        function resendOtp() {
            const resendBtn = document.getElementById('resendBtn');
            if (resendBtn) {
                resendBtn.disabled = true;
                resendBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Đang gửi lại...';
            }
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/forgot-password';
            const emailInput = document.createElement('input');
            emailInput.type = 'hidden';
            emailInput.name = 'email';
            emailInput.value = '${sessionScope.resetUserEmail}';
            form.appendChild(emailInput);
            document.body.appendChild(form);
            form.submit();
        }
    </script>
</body>
</html>
