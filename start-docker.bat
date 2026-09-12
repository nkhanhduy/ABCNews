@echo off
chcp 65001 > nul
echo ==========================================================
echo   KHOI CHAY ABCNEWS ENTERPRISE CMS TREN DOCKER
echo ==========================================================
echo.
echo [1/3] Kiem tra Docker Engine...
docker info > nul 2>&1
if %errorlevel% neq 0 (
    echo [LOI] Docker Desktop chua bat. Vui long khoi dong Docker Desktop truoc!
    pause
    exit /b 1
)

echo [2/3] Dang khoi tao va build cac container (SQL Server 2022 + Tomcat 10.1)...
docker compose up -d --build

echo.
echo [3/3] Kiem tra trang thai cac container:
docker compose ps

echo.
echo ==========================================================
echo   HE THONG ABCNEWS DA KHOI CHAY THANH CONG!
echo   Dia chi trang chu: http://localhost:8088/home
echo   Quan tri vien:     http://localhost:8088/admin/dashboard
echo ==========================================================
echo.
start http://localhost:8088/home
pause
