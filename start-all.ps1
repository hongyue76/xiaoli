# 律法先锋 - 一键启动脚本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "律法先锋 - 完整启动（前端+后端+数据库）" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Docker
Write-Host "[1/4] 检查Docker..." -ForegroundColor Yellow
try {
    $dockerStatus = docker ps 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[错误] Docker未运行" -ForegroundColor Red
        Write-Host "请先启动Docker Desktop，然后重新运行此脚本" -ForegroundColor Yellow
        Read-Host "按Enter退出"
        exit 1
    }
    Write-Host "[OK] Docker运行正常" -ForegroundColor Green
} catch {
    Write-Host "[错误] Docker未安装或未运行" -ForegroundColor Red
    exit 1
}

# 启动数据库
Write-Host ""
Write-Host "[2/4] 启动数据库..." -ForegroundColor Yellow

# PostgreSQL
Write-Host "  - 启动PostgreSQL (端口 5432)..." -ForegroundColor Cyan
$postgresRunning = docker ps --filter "name=xiaoli-postgres" --format "{{.Names}}" 2>$null
if ($postgresRunning -eq "xiaoli-postgres") {
    Write-Host "    PostgreSQL已在运行" -ForegroundColor Gray
} else {
    $postgresExists = docker ps -a --filter "name=xiaoli-postgres" --format "{{.Names}}" 2>$null
    if ($postgresExists -eq "xiaoli-postgres") {
        docker start xiaoli-postgres | Out-Null
        Write-Host "    PostgreSQL已启动" -ForegroundColor Green
    } else {
        docker run -d --name xiaoli-postgres -e POSTGRES_DB=xiaoli_legal -e POSTGRES_USER=xiaoli -e POSTGRES_PASSWORD=Xiaoli@2024 -p 5432:5432 postgres:15 | Out-Null
        Write-Host "    PostgreSQL容器已创建并启动" -ForegroundColor Green
    }
}

# Redis
Write-Host "  - 启动Redis (端口 6379)..." -ForegroundColor Cyan
$redisRunning = docker ps --filter "name=xiaoli-redis" --format "{{.Names}}" 2>$null
if ($redisRunning -eq "xiaoli-redis") {
    Write-Host "    Redis已在运行" -ForegroundColor Gray
} else {
    $redisExists = docker ps -a --filter "name=xiaoli-redis" --format "{{.Names}}" 2>$null
    if ($redisExists -eq "xiaoli-redis") {
        docker start xiaoli-redis | Out-Null
        Write-Host "    Redis已启动" -ForegroundColor Green
    } else {
        docker run -d --name xiaoli-redis -p 6379:6379 redis:7-alpine | Out-Null
        Write-Host "    Redis容器已创建并启动" -ForegroundColor Green
    }
}

Start-Sleep -Seconds 3

# 后端服务列表
$services = @(
    @{ Name="ms-consult"; Path="ms-consult"; Port="18081"; Desc="法律咨询" },
    @{ Name="ms-document"; Path="ms-document"; Port="18082"; Desc="文书生成" },
    @{ Name="ms-case"; Path="ms-case"; Port="18083"; Desc="案例检索" },
    @{ Name="ms-contract"; Path="ms-contract"; Port="18084"; Desc="合同审查" },
    @{ Name="analysis"; Path="analysis"; Port="18085"; Desc="案件分析" },
    @{ Name="ms-decision"; Path="ms-decision"; Port="18086"; Desc="司法决策" },
    @{ Name="ms-compliance"; Path="ms-compliance"; Port="18087"; Desc="企业合规" },
    @{ Name="ms-evidence"; Path="ms-evidence"; Port="18088"; Desc="证据分析" },
    @{ Name="ms-speech"; Path="ms-speech"; Port="18089"; Desc="语音对话" }
)

# 启动后端服务
Write-Host ""
Write-Host "[3/4] 启动后端服务..." -ForegroundColor Yellow

$backendPath = "D:\me\project\xiaoli\backend"

foreach ($svc in $services) {
    Write-Host "  - 启动 $($svc.Desc) ($($svc.Port))..." -ForegroundColor Cyan
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c cd /d $backendPath\$($svc.Path) && mvn spring-boot:run" -WindowStyle Minimized
    Start-Sleep -Milliseconds 500
}

# 启动前端
Write-Host ""
Write-Host "[4/4] 启动前端服务..." -ForegroundColor Yellow
Write-Host "  - 访问地址: http://localhost:3000" -ForegroundColor Cyan
Start-Process -FilePath "cmd.exe" -ArgumentList "/c cd /d D:\me\project\xiaoli\frontend\web && npm run dev" -WindowStyle Minimized

# 完成
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "启动完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "等待约 2-3 分钟让后端服务完全启动" -ForegroundColor Yellow
Write-Host ""
Write-Host "访问地址:" -ForegroundColor Cyan
Write-Host "  - 前端: http://localhost:3000" -ForegroundColor White
Write-Host "  - 后端API: http://localhost:18081" -ForegroundColor White
Write-Host ""
Write-Host "后端服务端口:" -ForegroundColor Cyan
foreach ($svc in $services) {
    Write-Host "  - $($svc.Name): $($svc.Port)" -ForegroundColor Gray
}
Write-Host ""
Read-Host "Press Enter to exit"
