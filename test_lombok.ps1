# 测试Lombok是否正常工作
$ErrorActionPreference = "Continue"

# 检查Lombok JAR
$lombokJar = "C:\Users\25912\.m2\repository\org\projectlombok\lombok\1.18.30\lombok-1.18.30.jar"
if (Test-Path $lombokJar) {
    Write-Host "Lombok JAR exists: $lombokJar"
    $fileInfo = Get-Item $lombokJar
    Write-Host "Size: $($fileInfo.Length) bytes"
} else {
    Write-Host "Lombok JAR not found!"
}

# 运行delombok查看生成的代码
Write-Host "`n=== Running delombok ==="
$testFile = "d:/me/project/xiaoli/backend/common/common-core/src/main/java/com/xiaoli/legal/common/core/cache/UserCacheService.java"
java -jar $lombokJar delombok -v $testFile 2>&1