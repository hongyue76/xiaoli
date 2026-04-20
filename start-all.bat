@echo off
chcp 65001 >nul
title 律法先锋 - 一键启动
cd /d %~dp0

echo ========================================
echo 律法先锋 - 完整启动（前端+后端+数据库）
echo ========================================
echo.

REM ===== 第1步：启动数据库 =====
echo [步骤1/4] 检查Docker...
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] Docker未运行
    echo 请先启动Docker Desktop，然后重新运行此脚本
    pause
    exit /b 1
)
echo [OK] Docker运行正常
echo.

echo [步骤2/4] 启动数据库服务...
echo   - PostgreSQL (端口 5432)
echo   - Redis (端口 6379)
echo.

REM 启动PostgreSQL
docker ps -a | findstr xiaoli-postgres >nul 2>&1
if %errorlevel% equ 0 (
    docker ps | findstr xiaoli-postgres >nul 2>&1
    if %errorlevel% neq 0 docker start xiaoli-postgres
) else (
    docker run -d --name xiaoli-postgres -e POSTGRES_DB=xiaoli_legal -e POSTGRES_USER=xiaoli -e POSTGRES_PASSWORD=Xiaoli@2024 -p 5432:5432 postgres:15
)
echo [OK] PostgreSQL已就绪

REM 启动Redis
docker ps -a | findstr xiaoli-redis >nul 2>&1
if %errorlevel% equ 0 (
    docker ps | findstr xiaoli-redis >nul 2>&1
    if %errorlevel% neq 0 docker start xiaoli-redis
) else (
    docker run -d --name xiaoli-redis -p 6379:6379 redis:7-alpine
)
echo [OK] Redis已就绪
timeout /t 5 /nobreak >nul
echo.

REM ===== 第2步：启动后端服务 =====
echo [步骤3/4] 启动后端微服务...
echo   - ms-consult   (端口 18081)
echo   - ms-document  (端口 18082)
echo   - ms-case      (端口 18083)
echo   - ms-contract  (端口 18084)
echo   - analysis     (端口 18085)
echo   - ms-decision  (端口 18086)
echo   - ms-compliance(端口 18087)
echo   - ms-evidence  (端口 18088)
echo   - ms-speech    (端口 18089)
echo.

REM 启动 ms-consult
echo [启动] ms-consult...
cd /d %~dp0ms-consult
start "ms-consult" cmd /k "mvn spring-boot:run"

REM 启动 ms-document
echo [启动] ms-document...
cd /d %~dp0ms-document
start "ms-document" cmd /k "mvn spring-boot:run"

REM 启动 ms-case
echo [启动] ms-case...
cd /d %~dp0ms-case
start "ms-case" cmd /k "mvn spring-boot:run"

REM 启动 ms-contract
echo [启动] ms-contract...
cd /d %~dp0ms-contract
start "ms-contract" cmd /k "mvn spring-boot:run"

REM 启动 analysis
echo [启动] analysis...
cd /d %~dp0analysis
start "analysis" cmd /k "mvn spring-boot:run"

REM 启动 ms-decision
echo [启动] ms-decision...
cd /d %~dp0ms-decision
start "ms-decision" cmd /k "mvn spring-boot:run"

REM 启动 ms-compliance
echo [启动] ms-compliance...
cd /d %~dp0ms-compliance
start "ms-compliance" cmd /k "mvn spring-boot:run"

REM 启动 ms-evidence
echo [启动] ms-evidence...
cd /d %~dp0ms-evidence
start "ms-evidence" cmd /k "mvn spring-boot:run"

REM 启动 ms-speech
echo [启动] ms-speech...
cd /d %~dp0ms-speech
start "ms-speech" cmd /k "mvn spring-boot:run"

echo.
echo 后端服务启动命令已发送
echo.

REM ===== 第3步：启动前端 =====
echo [步骤4/4] 启动前端服务...
cd /d %~dp0..\frontend\web
start "frontend" cmd /k "npm run dev"

REM 等待前端启动
echo.
echo 等待前端服务启动...
timeout /t 8 /nobreak >nul

REM ===== 第4步：打开浏览器 =====
echo.
echo [自动打开浏览器] http://localhost:3000
start http://localhost:3000

REM ===== 完成 =====
echo.
echo ========================================
echo 启动完成！
echo ========================================
echo.
echo 等待约 2-3 分钟让后端服务完全启动
echo.
echo 访问地址:
echo   - 前端: http://localhost:3000
echo   - 后端API: http://localhost:18081
echo.
echo 提示: 查看后端窗口确认服务启动状态
echo.
pause
