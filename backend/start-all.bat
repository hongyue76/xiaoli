@echo off
chcp 65001 >nul
echo ========================================
echo 律法先锋后端服务 - 一键启动
echo ========================================
echo.

:: 启动 ms-document (文书生成 - 18082)
echo [1/8] 启动 ms-document 服务 (端口 18082)...
cd /d %~dp0ms-document
start "ms-document" cmd /k "mvn spring-boot:run > ..\logs\ms-document.log 2>&1"

:: 启动 ms-case (案例检索 - 18083)
echo [2/8] 启动 ms-case 服务 (端口 18083)...
cd /d %~dp0ms-case
start "ms-case" cmd /k "mvn spring-boot:run > ..\logs\ms-case.log 2>&1"

:: 启动 ms-contract (合同审查 - 18084)
echo [3/8] 启动 ms-contract 服务 (端口 18084)...
cd /d %~dp0ms-contract
start "ms-contract" cmd /k "mvn spring-boot:run > ..\logs\ms-contract.log 2>&1"

:: 启动 analysis (案件分析 - 18085)
echo [4/8] 启动 analysis 服务 (端口 18085)...
cd /d %~dp0analysis
start "analysis" cmd /k "mvn spring-boot:run > ..\logs\analysis.log 2>&1"

:: 启动 ms-decision (司法决策 - 18086)
echo [5/8] 启动 ms-decision 服务 (端口 18086)...
cd /d %~dp0ms-decision
start "ms-decision" cmd /k "mvn spring-boot:run > ..\logs\ms-decision.log 2>&1"

:: 启动 ms-compliance (企业合规 - 18087)
echo [6/8] 启动 ms-compliance 服务 (端口 18087)...
cd /d %~dp0ms-compliance
start "ms-compliance" cmd /k "mvn spring-boot:run > ..\logs\ms-compliance.log 2>&1"

:: 启动 ms-evidence (证据分析 - 18088)
echo [7/8] 启动 ms-evidence 服务 (端口 18088)...
cd /d %~dp0ms-evidence
start "ms-evidence" cmd /k "mvn spring-boot:run > ..\logs\ms-evidence.log 2>&1"

:: 启动 ms-speech (语音对话 - 18089)
echo [8/8] 启动 ms-speech 服务 (端口 18089)...
cd /d %~dp0ms-speech
start "ms-speech" cmd /k "mvn spring-boot:run > ..\logs\ms-speech.log 2>&1"

echo.
echo ========================================
echo 所有服务启动命令已发送
echo ========================================
echo.
echo 服务列表:
echo   - ms-consult    (法律咨询):    http://localhost:18081
echo   - ms-document   (文书生成):    http://localhost:18082
echo   - ms-case       (案例检索):    http://localhost:18083
echo   - ms-contract   (合同审查):    http://localhost:18084
echo   - analysis      (案件分析):    http://localhost:18085
echo   - ms-decision   (司法决策):    http://localhost:18086
echo   - ms-compliance (企业合规):    http://localhost:18087
echo   - ms-evidence   (证据分析):    http://localhost:18088
echo   - ms-speech     (语音对话):    http://localhost:18089
echo.
echo 请等待约 60 秒让服务完全启动
echo 查看各个窗口的输出确认服务状态
echo.
pause
