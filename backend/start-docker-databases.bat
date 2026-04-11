@echo off
chcp 65001 >nul
echo ========================================
echo 律法先锋 - Docker启动脚本（需要管理员权限）
echo ========================================
echo.

REM 检查Docker状态
echo 检查Docker状态...
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ⚠ Docker未运行或需要管理员权限
    echo.
    echo 请按以下步骤操作：
    echo 1. 右键点击此脚本
    echo 2. 选择"以管理员身份运行"
    echo 3. 或确保Docker Desktop已启动
    echo.
    pause
    exit /b 1
)

echo ✓ Docker运行正常
echo.

REM 启动PostgreSQL
echo [1/3] 启动PostgreSQL数据库...
docker ps | findstr "xiaoli-postgres" >nul 2>&1
if %errorlevel% equ 0 (
    echo PostgreSQL容器已存在，正在重启...
    docker restart xiaoli-postgres
) else (
    docker run -d --name xiaoli-postgres ^
        -e POSTGRES_DB=xiaoli_legal ^
        -e POSTGRES_USER=xiaoli ^
        -e POSTGRES_PASSWORD=Xiaoli@2024 ^
        -p 5432:5432 ^
        postgres:15
)

if %errorlevel% equ 0 (
    echo ✓ PostgreSQL启动成功
) else (
    echo ✗ PostgreSQL启动失败
    pause
    exit /b 1
)

echo.
echo [2/3] 启动Redis缓存...
docker ps | findstr "xiaoli-redis" >nul 2>&1
if %errorlevel% equ 0 (
    echo Redis容器已存在，正在重启...
    docker restart xiaoli-redis
) else (
    docker run -d --name xiaoli-redis ^
        -e REDIS_PASSWORD=Xiaoli@2024 ^
        -p 6379:6379 ^
        redis:7-alpine redis-server --requirepass Xiaoli@2024
)

if %errorlevel% equ 0 (
    echo ✓ Redis启动成功
) else (
    echo ✗ Redis启动失败
    pause
    exit /b 1
)

echo.
echo 等待数据库完全启动...
timeout /t 10 /nobreak >nul

echo.
echo [3/3] 初始化数据库...
docker exec -i xiaoli-postgres psql -U xiaoli -d xiaoli_legal < %~dp0init-database.sql

if %errorlevel% equ 0 (
    echo ✓ 数据库初始化成功
) else (
    echo ⚠ 数据库初始化失败（可能已初始化）
)

echo.
echo ========================================
echo 基础设施启动完成！
echo ========================================
echo.
echo 服务列表:
echo   - PostgreSQL: localhost:5432
echo   - Redis:      localhost:6379
echo   - Nacos:      localhost:8848 (控制台: http://localhost:8848/nacos)
echo   - 数据库:     xiaoli_legal
echo.
echo 下一步:
echo   1. 在 backend 目录运行: start-all-services.bat
echo   2. 在 frontend/web 目录运行: npm run dev
echo   3. 访问 Nacos 控制台查看服务注册状态
echo.
pause
