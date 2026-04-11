@echo off
chcp 65001 >nul
echo ========================================
echo 律法先锋 - Docker一键启动（推荐）
echo ========================================
echo.

REM 检查Docker是否安装
echo [1/4] 检查Docker...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Docker未安装，请先安装Docker Desktop
    echo 下载地址: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)
echo ✓ Docker已安装

echo.
echo [2/4] 启动PostgreSQL数据库...
docker run -d --name xiaoli-postgres ^
  -e POSTGRES_DB=xiaoli_legal ^
  -e POSTGRES_USER=xiaoli ^
  -e POSTGRES_PASSWORD=Xiaoli@2024 ^
  -p 5432:5432 ^
  postgres:15

if %errorlevel% equ 0 (
    echo ✓ PostgreSQL容器已启动
) else (
    echo PostgreSQL容器可能已存在，正在重启...
    docker restart xiaoli-postgres
)

echo.
echo [3/4] 启动Redis缓存...
docker run -d --name xiaoli-redis ^
  -e REDIS_PASSWORD=Xiaoli@2024 ^
  -p 6379:6379 ^
  redis:7-alpine redis-server --requirepass Xiaoli@2024

if %errorlevel% equ 0 (
    echo ✓ Redis容器已启动
) else (
    echo Redis容器可能已存在，正在重启...
    docker restart xiaoli-redis
)

echo.
echo [4/4] 初始化数据库...
timeout /t 5 /nobreak >nul
docker exec -i xiaoli-postgres psql -U xiaoli -d xiaoli_legal < backend\init-database.sql

if %errorlevel% equ 0 (
    echo ✓ 数据库初始化成功
) else (
    echo ⚠ 数据库初始化失败，可能已经初始化过
)

echo.
echo ========================================
echo 数据库服务启动完成！
echo ========================================
echo.
echo 服务列表:
echo   - PostgreSQL: localhost:5432
echo   - Redis:      localhost:6379
echo.
echo 下一步:
echo   1. 运行 start-all-services.bat 启动后端服务
echo   2. 在 frontend/web 目录运行 npm run dev 启动前端
echo.
pause
