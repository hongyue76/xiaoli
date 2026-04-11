#!/usr/bin/env pwsh
# DeepSeek Intent Router 自动化测试脚本
# 用于验证意图路由服务的所有功能

$ErrorActionPreference = "Stop"
$Host.UI.RawUI.WindowTitle = "DeepSeek Intent Router - Automated Test"

# 颜色输出函数
function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Write-Success {
    param([string]$Message)
    Write-ColorOutput "✅ $Message" "Green"
}

function Write-Error {
    param([string]$Message)
    Write-ColorOutput "❌ $Message" "Red"
}

function Write-Info {
    param([string]$Message)
    Write-ColorOutput "ℹ️  $Message" "Cyan"
}

function Write-Warning {
    param([string]$Message)
    Write-ColorOutput "⚠️  $Message" "Yellow"
}

# 配置
$baseUrl = "http://localhost:8087"
$testResults = @()
$totalTests = 0
$passedTests = 0
$failedTests = 0

# 测试 API 端点
function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Path,
        [hashtable]$Body = $null
    )

    $script:totalTests++
    $url = "$baseUrl$Path"
    
    Write-Info "Testing: $Name"
    Write-Info "  URL: $url"
    
    try {
        $headers = @{
            "Content-Type" = "application/json"
        }
        
        if ($Method -eq "GET") {
            $response = Invoke-RestMethod -Uri $url -Method Get -Headers $headers -TimeoutSec 30
        } elseif ($Method -eq "POST") {
            $bodyJson = $Body | ConvertTo-Json -Depth 10
            $response = Invoke-RestMethod -Uri $url -Method Post -Headers $headers -Body $bodyJson -TimeoutSec 30
        }
        
        $script:passedTests++
        Write-Success "$Name - PASSED"
        
        return @{
            Success = $true
            Response = $response
        }
    } catch {
        $script:failedTests++
        Write-Error "$Name - FAILED"
        Write-Error "  Error: $($_.Exception.Message)"
        
        return @{
            Success = $false
            Error = $_.Exception.Message
        }
    }
}

# 等待服务启动
function Wait-ForService {
    param(
        [int]$MaxSeconds = 30
    )
    
    Write-Info "等待服务启动..."
    
    for ($i = 1; $i -le $MaxSeconds; $i++) {
        try {
            $response = Invoke-WebRequest -Uri "$baseUrl/intent-router/health" -UseBasicParsing -TimeoutSec 2
            if ($response.StatusCode -eq 200) {
                Write-Success "服务已启动！"
                return $true
            }
        } catch {
            Write-Host "." -NoNewline
            Start-Sleep -Seconds 1
        }
    }
    
    Write-Host ""
    Write-Error "服务启动超时"
    return $false
}

# 主测试流程
function Start-Tests {
    Write-ColorOutput "`n========================================" "Yellow"
    Write-ColorOutput "  DeepSeek Intent Router 测试套件" "Yellow"
    Write-ColorOutput "========================================`n" "Yellow"
    
    # 检查服务是否运行
    Write-Info "检查服务状态..."
    try {
        $healthResponse = Invoke-RestMethod -Uri "$baseUrl/intent-router/health" -Method Get -TimeoutSec 5
        Write-Success "服务正在运行"
    } catch {
        Write-Warning "服务未运行，请先启动服务："
        Write-Info "  Windows: .\start-intent-router.bat"
        Write-Info "  Linux/Mac: ./start-intent-router.sh"
        return $false
    }
    
    Write-Host ""
    
    # 测试 1: 健康检查
    Write-ColorOutput "`n【测试 1】健康检查" "Magenta"
    $result = Test-Endpoint -Name "健康检查" -Method "GET" -Path "/intent-router/health"
    if ($result.Success) {
        Write-Info "  状态: $($result.Response.status)"
    }
    
    # 测试 2: 意图分析 - 专业法律问题
    Write-ColorOutput "`n【测试 2】意图分析 - 专业法律问题" "Magenta"
    $result = Test-Endpoint -Name "专业法律问题意图分析" -Method "POST" -Path "/intent-router/analyze" -Body @{
        question = "劳动合同试用期的法律规定是什么？"
    }
    if ($result.Success -and $result.Response.intent) {
        Write-Info "  意图类型: $($result.Response.intent.type)"
        Write-Info "  需要检索: $($result.Response.intent.needSearch)"
        Write-Info "  置信度: $($result.Response.intent.confidence)"
    }
    
    # 测试 3: 意图分析 - 通用问题
    Write-ColorOutput "`n【测试 3】意图分析 - 通用问题" "Magenta"
    $result = Test-Endpoint -Name "通用问题意图分析" -Method "POST" -Path "/intent-router/analyze" -Body @{
        question = "你好，请介绍一下你自己"
    }
    if ($result.Success -and $result.Response.intent) {
        Write-Info "  意图类型: $($result.Response.intent.type)"
        Write-Info "  需要检索: $($result.Response.intent.needSearch)"
        Write-Info "  置信度: $($result.Response.intent.confidence)"
    }
    
    # 测试 4: 智能问答 - 专业问题
    Write-ColorOutput "`n【测试 4】智能问答 - 专业法律问题" "Magenta"
    $result = Test-Endpoint -Name "专业问题智能问答" -Method "POST" -Path "/intent-router/chat" -Body @{
        question = "试用期工资低于最低工资合法吗？"
    }
    if ($result.Success) {
        Write-Info "  处理类型: $($result.Response.processType)"
        Write-Info "  意图类型: $($result.Response.intentType)"
        Write-Info "  耗时: $($result.Response.duration)ms"
        Write-Info "  回答长度: $($result.Response.answer.Length) 字符"
        if ($result.Response.searchResults) {
            Write-Info "  包含检索结果: 是"
        }
    }
    
    # 测试 5: 智能问答 - 通用问题
    Write-ColorOutput "`n【测试 5】智能问答 - 通用问题" "Magenta"
    $result = Test-Endpoint -Name "通用问题智能问答" -Method "POST" -Path "/intent-router/chat" -Body @{
        question = "今天天气怎么样？"
    }
    if ($result.Success) {
        Write-Info "  处理类型: $($result.Response.processType)"
        Write-Info "  意图类型: $($result.Response.intentType)"
        Write-Info "  耗时: $($result.Response.duration)ms"
        Write-Info "  回答长度: $($result.Response.answer.Length) 字符"
    }
    
    # 测试 6: 智能问答 - 复杂问题
    Write-ColorOutput "`n【测试 6】智能问答 - 复杂法律问题" "Magenta"
    $result = Test-Endpoint -Name "复杂问题智能问答" -Method "POST" -Path "/intent-router/chat" -Body @{
        question = "我在公司工作了3年，公司没有给我签劳动合同，也没有缴纳社保，我应该怎么办？"
    }
    if ($result.Success) {
        Write-Info "  处理类型: $($result.Response.processType)"
        Write-Info "  意图类型: $($result.Response.intentType)"
        Write-Info "  耗时: $($result.Response.duration)ms"
        Write-Info "  回答长度: $($result.Response.answer.Length) 字符"
        if ($result.Response.searchResults) {
            Write-Info "  包含检索结果: 是"
        }
    }
    
    # 输出测试总结
    Write-ColorOutput "`n========================================" "Yellow"
    Write-ColorOutput "  测试总结" "Yellow"
    Write-ColorOutput "========================================`n" "Yellow"
    Write-Info "总测试数: $totalTests"
    Write-Success "通过: $passedTests"
    if ($failedTests -gt 0) {
        Write-Error "失败: $failedTests"
    } else {
        Write-Success "失败: $failedTests"
    }
    
    $passRate = [math]::Round(($passedTests / $totalTests) * 100, 2)
    Write-Info "通过率: $passRate%"
    
    Write-Host ""
    
    if ($failedTests -eq 0) {
        Write-Success "🎉 所有测试通过！"
    } else {
        Write-Warning "⚠️  有 $failedTests 个测试失败"
    }
    
    return $failedTests -eq 0
}

# 主入口
try {
    Start-Tests
    exit 0
} catch {
    Write-Error "测试执行出错: $($_.Exception.Message)"
    exit 1
}
