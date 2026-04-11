@echo off
chcp 65001 >nul
title 律法先锋 - 启动完整服务
echo ========================================
echo 律法先锋 - 快速启动（前端+后端）
echo ========================================
echo.

REM 启动后端服务（静态数据模式，无需数据库）
echo [1/2] 启动后端服务（使用内置静态数据）...
echo 注意: 后端服务使用静态数据，无需数据库
echo.

REM 检查Java是否安装
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] Java未安装，后端服务无法启动
    echo 前端将使用内置的静态数据运行
    echo.
    goto START_FRONTEND
)

echo 检测到Java已安装
echo.
echo 后端服务依赖数据库，您有两个选择：
echo.
echo [选项1] 完整模式（需要PostgreSQL + Redis）
echo   - 真实的AI对话能力
echo   - 数据持久化
echo   - 需要配置数据库
echo.
echo [选项2] 静态数据模式（当前推荐）
echo   - 使用前端内置的静态数据
echo   - 无需配置数据库
echo   - 立即可用
echo.
set /p choice=请选择模式 (1 或 2):

if "%choice%"=="1" (
    echo.
    echo 您选择了完整模式
    echo 需要先启动数据库（PostgreSQL + Redis）
    echo 请确保数据库已配置并启动
    echo.
    echo 启动后端服务...
    cd /d %~dp0ms-consult
    start "ms-consult" cmd /k "mvn spring-boot:run"
    timeout /t 5 /nobreak >nul
    cd /d %~dp0ms-caseinfo
    start "ms-caseinfo" cmd /k "mvn spring-boot:run"
    cd /d %~dp0ms-document
    start "ms-document" cmd /k "mvn spring-boot:run"
) else (
    echo.
    echo 使用静态数据模式
    echo 前端将使用内置的静态数据运行
    echo 无需启动后端服务
)

:START_FRONTEND
echo.
echo [2/2] 启动前端服务...
cd /d %~dp0..\frontend\web
echo 正在启动Vite开发服务器...
echo 访问地址: http://localhost:3000
echo.
echo 按 Ctrl+C 停止前端服务
echo.
npm run dev

pause
