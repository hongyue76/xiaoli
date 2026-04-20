@echo off
chcp 65001 >nul
echo ==========================================
echo   连接服务器 (使用SSH密钥)
echo ==========================================
echo.

echo 测试 lighthouse 用户...
ssh -i "C:\Users\Public\id_rsa" -o StrictHostKeyChecking=no lighthouse@101.42.47.231

echo.
echo 连接已断开。
pause
