@echo off
chcp 65001 >nul
title 律法先锋 - 停止服务
echo ========================================
echo   律法先锋 - 停止所有服务
echo ========================================
echo.

REM 停止后端服务窗口
taskkill /FI "WINDOWTITLE eq ms-consult*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq ms-document*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq ms-case*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq ms-contract*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq ms-analysis*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq ms-decision*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq ms-compliance*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq ms-evidence*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq ms-speech*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq frontend*" /F >nul 2>&1

REM 停止后端 Java 进程
echo [停止] 后端 Java 服务...
taskkill /FI "WINDOWTITLE eq java*" /F >nul 2>&1
taskkill /IM java.exe /F >nul 2>&1

REM 停止前端 Node 进程
echo [停止] 前端服务...
taskkill /IM node.exe /F >nul 2>&1

REM 可选：停止数据库
set /p STOP_DB="是否停止数据库容器？(Y/N): "
if /i "%STOP_DB%"=="Y" (
    docker stop xiaoli-postgres xiaoli-redis >nul 2>&1
    echo [停止] 数据库容器已停止
)

echo.
echo ========================================
echo   所有服务已停止
echo ========================================
echo.
pause
