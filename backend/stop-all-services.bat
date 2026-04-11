@echo off
chcp 65001 >nul
echo ========================================
echo 律法先锋后端服务 - 一键停止脚本
echo ========================================
echo.

echo 正在停止后端服务...
echo.

REM 查找并终止Java进程
taskkill /F /FI "WINDOWTITLE eq ms-consult*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq ms-caseinfo*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq ms-document*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq ms-contract*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq ms-compliance*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq ms-evidence*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq analysis*" >nul 2>&1

echo ✓ 停止命令已发送
echo.
echo 请手动关闭所有相关的命令行窗口
echo.
pause
