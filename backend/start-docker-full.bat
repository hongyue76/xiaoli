@echo off
chcp 65001 >nul
title 律法先锋 - Docker启动脚本
cd /d %~dp0
echo ========================================
echo 律法先锋 - 完整启动（Docker方式）
echo ========================================
echo.

REM 检查Docker是否运行
echo [检查] Docker状态...
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] Docker未运行，请先启动Docker Desktop
    echo.
    echo 提示: 在开始菜单中找到Docker Desktop并启动
    pause
    exit /b 1
)
echo [OK] Docker运行正常
echo.

REM 启动PostgreSQL
echo [1/3] 启动PostgreSQL数据库...
docker ps -a | findstr xiaoli-postgres >nul 2>&1
if %errorlevel% equ 0 (
    echo PostgreSQL容器已存在
    docker ps | findstr xiaoli-postgres >nul 2>&1
    if %errorlevel% equ 0 (
        echo 正在重启...
        docker restart xiaoli-postgres
    ) else (
        echo 正在启动...
        docker start xiaoli-postgres
    )
) else (
    echo 正在创建新容器...
    docker run -d --name xiaoli-postgres -e POSTGRES_DB=xiaoli_legal -e POSTGRES_USER=xiaoli -e POSTGRES_PASSWORD=Xiaoli@2024 -p 5432:5432 postgres:15
)
timeout /t 3 /nobreak >nul
echo [OK] PostgreSQL已启动
echo.

REM 启动Redis
echo [2/3] 启动Redis缓存...
docker ps -a | findstr xiaoli-redis >nul 2>&1
if %errorlevel% equ 0 (
    echo Redis容器已存在
    docker ps | findstr xiaoli-redis >nul 2>&1
    if %errorlevel% equ 0 (
        echo 正在重启...
        docker restart xiaoli-redis
    ) else (
        echo 正在启动...
        docker start xiaoli-redis
    )
) else (
    echo 正在创建新容器...
    docker run -d --name xiaoli-redis -e REDIS_PASSWORD=Xiaoli@2024 -p 6379:6379 redis:7-alpine redis-server --requirepass Xiaoli@2024
)
timeout /t 3 /nobreak >nul
echo [OK] Redis已启动
echo.

REM 等待数据库就绪
echo 等待数据库完全启动...
timeout /t 10 /nobreak >nul

REM 初始化数据库
echo [3/3] 初始化数据库...
docker exec -i xiaoli-postgres psql -U xiaoli -d xiaoli_legal < init-database.sql 2>nul
if %errorlevel% equ 0 (
    echo [OK] 数据库初始化成功
) else (
    echo [提示] 数据库可能已初始化过
)

echo.
echo ========================================
echo 数据库服务已全部启动！
echo ========================================
echo.
echo 服务列表:
echo   - PostgreSQL: localhost:5432
echo   - Redis:      localhost:6379
echo   - 数据库:     xiaoli_legal
echo.
echo 下一步:
echo   1. 按任意键启动后端服务...
pause >nul
start-all-services.bat
