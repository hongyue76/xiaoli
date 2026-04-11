# 编译脚本
$ErrorActionPreference = 'Stop'
$OutputEncoding = [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Set-Location 'd:/me/project/xiaoli/backend'
try {
    mvn clean compile -DskipTests
    Write-Host "编译成功"
    exit 0
} catch {
    Write-Host "编译失败: $_"
    exit 1
}