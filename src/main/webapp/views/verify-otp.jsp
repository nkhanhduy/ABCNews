<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác thực OTP - ABC News</title>
    <%-- Bootstrap 5 CSS --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Font Awesome Icons --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <%-- Custom CSS --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/framework.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public-style.css">
</head>
<body>
    
    <%-- Header và Menu --%>
    <jsp:include page="/views/common/_header.jsp" />
    <jsp:include page="/views/common/_menu.jsp" />

    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-lg border-0 login-card">
                    <div class="card-body p-5">
                        <div class="text-center mb-4">
                            <div class="login-icon-wrapper mb-3">
                                <i class="fas fa-shield-alt fa-3x text-success"></i>
                            </div>
                            <h2 class="fw-bold">Xác Thực OTP</h2>
                            <p class="text-muted">Mã OTP đã được gửi đến <strong class="text-success">${sessionScope.resetUserEmail}</strong></p>
                        </div>
                        
                        <%-- Success message --%>
                        <c:if test="${param.success eq 'sent'}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>Đã gửi mã OTP qua email của bạn!
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

                        <form action="${pageContext.request.contextPath}/verify-otp" method="post" id="verifyForm">
                            
                            <%-- OTP Input --%>
                            <div class="mb-4">
                                <label class="form-label fw-semibold">
                                    <i class="fas fa-lock me-1"></i>Nhập mã OTP (6 số)
                                </label>
                                <div class="otp-inputs d-flex justify-content-center gap-2 mb-3">
                                    <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control form-control-lg text-center" id="otp1" autocomplete="off">
                                    <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control form-control-lg text-center" id="otp2" autocomplete="off">
                                    <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control form-control-lg text-center" id="otp3" autocomplete="off">
                                    <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control form-control-lg text-center" id="otp4" autocomplete="off">
                                    <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control form-control-lg text-center" id="otp5" autocomplete="off">
                                    <input type="text" maxlength="1" pattern="[0-9]" class="otp-box form-control form-control-lg text-center" id="otp6" autocomplete="off">
                                </div>
                                <input type="hidden" name="otpCode" id="otpCode">
                                
                                <div class="alert alert-warning d-flex align-items-center" id="otpTimer">
                                    <i class="fas fa-clock me-2"></i>
                                    <span>Mã OTP có hiệu lực: <strong id="countdown">05:00</strong></span>
                                </div>
                            </div>
                            
                            <%-- Password Input --%>
                            <div class="mb-3">
                                <label for="newPassword" class="form-label">
                                    <i class="fas fa-key me-1"></i>Mật khẩu mới
                                </label>
                                <input type="password" class="form-control form-control-lg" id="newPassword" name="newPassword" placeholder="Ít nhất 8 ký tự" required minlength="8">
                            </div>
                            
                            <div class="mb-4">
                                <label for="confirmPassword" class="form-label">
                                    <i class="fas fa-key me-1"></i>Xác nhận mật khẩu
                                </label>
                                <input type="password" class="form-control form-control-lg" id="confirmPassword" name="confirmPassword" placeholder="Nhập lại mật khẩu" required minlength="8">
                            </div>
                            
                            <button type="submit" class="btn btn-success btn-lg w-100 login-submit-btn mb-3" id="submitBtn">
                                <i class="fas fa-check-circle me-2"></i>Xác Nhận
                            </button>
                            
                            <c:if test="${not expired}">
                                <button type="button" class="btn btn-outline-secondary w-100 mb-2" id="resendBtn" onclick="resendOtp()" disabled>
                                    <i class="fas fa-redo me-1"></i>Gửi lại OTP (<span id="resendCountdown">60</span>s)
                                </button>
                            </c:if>
                        </form>
                        
                        <div class="text-center mt-3">
                            <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password-link">
                                <i class="fas fa-arrow-left me-1"></i>Thay đổi email
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
    <script src="${pageContext.request.contextPath}/assets/js/public-ux.js"></script>
    
    <script>
        // Auto-focus và di chuyển giữa các ô OTP
        document.addEventListener('DOMContentLoaded', function() {
            const otpBoxes = document.querySelectorAll('.otp-box');
            const otpCodeInput = document.getElementById('otpCode');
            const form = document.getElementById('verifyForm');
            
            // Auto-focus vào ô đầu tiên
            otpBoxes[0].focus();
            
            // Xử lý input event
            otpBoxes.forEach((box, index) => {
                box.addEventListener('input', function(e) {
                    const value = this.value;
                    
                    // Chỉ cho phép số
                    if (!/^\d*$/.test(value)) {
                        this.value = '';
                        return;
                    }
                    
                    // Auto-focus sang ô tiếp theo
                    if (value && index < otpBoxes.length - 1) {
                        otpBoxes[index + 1].focus();
                    }
                    
                    // Update hidden input
                    updateOtpCode();
                });
                
                // Xử lý backspace
                box.addEventListener('keydown', function(e) {
                    if (e.key === 'Backspace' && !this.value && index > 0) {
                        otpBoxes[index - 1].focus();
                    }
                });
                
                // Xử lý paste
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
            
            // Update hidden input với OTP code đầy đủ
            function updateOtpCode() {
                const otp = Array.from(otpBoxes).map(box => box.value).join('');
                otpCodeInput.value = otp;
            }
            
            // Validate form trước khi submit
            form.addEventListener('submit', function(e) {
                updateOtpCode();
                
                const otp = otpCodeInput.value;
                if (otp.length !== 6) {
                    e.preventDefault();
                    alert('Vui lòng nhập đủ 6 số OTP');
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
                
                // Show loading state
                const submitBtn = document.getElementById('submitBtn');
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang xử lý...';
                }
            });
            
            // Countdown timer (5 phút)
            let timeLeft = 5 * 60;
            const countdownElement = document.getElementById('countdown');
            const timerElement = document.getElementById('otpTimer');
            const submitBtn = document.getElementById('submitBtn');
            
            function updateCountdown() {
                const minutes = Math.floor(timeLeft / 60);
                const seconds = timeLeft % 60;
                countdownElement.textContent = 
                    String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
                
                if (timeLeft === 0) {
                    timerElement.className = 'alert alert-danger d-flex align-items-center';
                    timerElement.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i><span>Mã OTP đã hết hạn</span>';
                    submitBtn.disabled = true;
                    clearInterval(timerInterval);
                }
                
                timeLeft--;
            }
            
            const timerInterval = setInterval(updateCountdown, 1000);
            
            // Resend OTP cooldown (60 giây)
            const resendBtn = document.getElementById('resendBtn');
            if (resendBtn) {
                let resendCooldown = 60;
                const resendCountdownSpan = document.getElementById('resendCountdown');
                
                const cooldownInterval = setInterval(() => {
                    resendCooldown--;
                    resendCountdownSpan.textContent = resendCooldown;
                    
                    if (resendCooldown === 0) {
                        clearInterval(cooldownInterval);
                        resendBtn.disabled = false;
                        resendBtn.innerHTML = '<i class="fas fa-redo me-1"></i>Gửi lại OTP';
                    }
                }, 1000);
            }
        });
        
        // Function gửi lại OTP
        function resendOtp() {
            window.location.href = '${pageContext.request.contextPath}/forgot-password?email=${sessionScope.resetUserEmail}';
        }
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
        
        .otp-box {
            width: 60px;
            height: 60px;
            font-size: 24px;
            font-weight: bold;
            color: #28a745;
        }
        
        .otp-box:focus {
            border-color: #28a745;
            box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.25);
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
            
            .otp-box {
                width: 45px;
                height: 50px;
                font-size: 20px;
            }
        }
    </style>
</body>
</html>
