@echo off
chcp 65001 >nul
echo ==========================================
echo   测试 SSH 连接
echo ==========================================
echo.

ssh -i "C:\Users\Public\id_rsa" -o StrictHostKeyChecking=no -o ConnectTimeout=15 root@101.42.47.231 "echo === 连接成功 === && whoami && cat /etc/os-release | head -3 && free -h"

pause
