@echo off
chcp 65001 >nul
echo ==========================================
echo   连接 Ubuntu 服务器 (使用SSH密钥)
echo ==========================================
echo.

echo 测试 ubuntu 用户...
ssh -i "C:\Users\Public\id_rsa" -o StrictHostKeyChecking=no ubuntu@101.42.47.231

echo.
echo 连接已断开。
pause
