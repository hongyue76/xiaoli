@echo off
chcp 65001 >nul
title xiaoli - Legal AI Platform

cd /d %~dp0

echo ========================================
echo   xiaoli - Legal AI Platform
echo ========================================
echo.

REM Check Docker
docker ps >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Docker is running
) else (
    echo [Error] Docker is not running!
    echo Please start Docker Desktop first.
    pause
    exit
)

REM Start databases with docker-compose
echo.
echo [Step] Starting databases...
cd /d %~dp0deployment
echo [Warning] Removing old database volumes to ensure schema sync...
docker-compose -f docker-compose.local.yml down -v >nul 2>&1
docker-compose -f docker-compose.local.yml up -d

REM Wait for database to be ready
echo [Step] Waiting for database to be ready...
set /a count=0
:wait_db
if %count% gtr 60 (
    echo [Error] Database failed to start within 60 seconds!
    pause
    exit /b 1
)
docker exec xiaoli-postgres pg_isready -U xiaoli >nul 2>&1
if %errorlevel% neq 0 (
    set /a count+=1
    echo        Waiting... %count%/60
    ping -n 2 127.0.0.1 >nul 2>&1
    goto wait_db
)
echo [OK] Database is ready!

REM Verify database tables
echo [Step] Verifying database schema...
docker exec xiaoli-postgres psql -U xiaoli -d xiaoli_legal -c "\dt" >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Database tables not found!
    pause
    exit /b 1
)
echo [OK] Database schema verified!

REM Go back to project root
cd /d %~dp0

REM Kill existing ms-consult processes
echo.
echo [Step] Stopping existing services...
taskkill /F /IM java.exe >nul 2>&1
ping -n 2 127.0.0.1 >nul 2>&1

REM Check if jar exists, if not build the project
if not exist "backend\ms-consult\target\ms-consult-1.0.0.jar" (
    echo [Step] Building ms-consult...
    cd /d %~dp0backend
    call mvnw clean package -DskipTests >nul 2>&1
    if %errorlevel% neq 0 (
        echo [Error] Build failed!
        pause
        exit /b 1
    )
    cd /d %~dp0
)

REM Start ms-consult service
echo.
echo [Step] Starting ms-consult service...
start "ms-consult" cmd /k "cd /d %~dp0backend && set DB_HOST=localhost&& set DB_PORT=5433&& set DB_NAME=xiaoli_legal&& set DB_USERNAME=xiaoli&& set DB_PASSWORD=Xiaoli@2024&& set DEEPSEEK_API_KEY=sk-ab6f6d5baa9349c6a735df32f9cc4f16&& set DEEPSEEK_API_URL=https://api.deepseek.com/v1&& set DEEPSEEK_MODEL=deepseek-chat&& set DEEPSEEK_TIMEOUT=120&& set REDIS_HOST=localhost&& set REDIS_PORT=6379&& java -jar ms-consult\target\ms-consult-1.0.0.jar --server.port=18081"

REM Wait for ms-consult to be ready
echo [Step] Waiting for ms-consult to start...
set /a count=0
:wait_service
if %count% gtr 60 (
    echo [Error] ms-consult failed to start within 60 seconds!
    echo Please check the ms-consult window for errors.
    pause
    exit /b 1
)
netstat -ano | findstr ":18081" | findstr "LISTENING" >nul 2>&1
if %errorlevel% neq 0 (
    set /a count+=1
    echo        Waiting... %count%/60
    ping -n 2 127.0.0.1 >nul 2>&1
    goto wait_service
)
echo [OK] ms-consult is running!

REM Test the API
echo [Step] Testing API...
curl -s -X POST http://localhost:18081/api/consult/chat -H "Content-Type: application/json" -d "{\"message\":\"test\"}" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] API is responding!
) else (
    echo [Warning] API test failed, but service is running.
)

REM Start frontend
echo.
echo [Step] Starting frontend...
cd /d %~dp0frontend\web
if not exist "node_modules" (
    echo [Step] Installing dependencies...
    call npm install >nul 2>&1
)
start "frontend" cmd /k "npm run dev"

REM Open browser
echo.
echo [Info] Opening browser in 10 seconds...
timeout /t 10 >nul 2>&1
start http://localhost:3000

echo.
echo ========================================
echo   xiaoli - Legal AI Platform
echo   Started successfully!
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:18081
echo ========================================
echo.
echo Press any key to exit this window...
pause >nul
