# 手动编译脚本
$ProjectDir = "d:/me/project/xiaoli/backend"
$Module = "common/common-core"
$LombokVersion = "1.18.30"
$OutputDir = "$ProjectDir/$Module/target/classes"

Write-Host "开始编译"

# 获取依赖
Set-Location $ProjectDir
mvn dependency:copy-dependencies -B -q -DoutputDirectory="$ProjectDir/$Module/target/deps" -pl $Module

# Lombok JAR
$env:LOMBOK = "$env:USERPROFILE\.m2\repository\org\projectlombok\lombok\$LombokVersion\lombok-$LombokVersion.jar"

# 收集依赖jar
$depDir = "$ProjectDir/$Module/target/deps"
$depJars = Get-ChildItem $depDir -Filter "*.jar" | ForEach-Object { $_.FullName }
$depPath = $depJars -join ";"

# 完整classpath
$fullCp = "$env:LOMBOK;$depPath"

# 创建输出目录
if (!(Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
}

# Java源文件
$srcDir = "$ProjectDir/$Module/src/main/java"
$javaFiles = Get-ChildItem -Path $srcDir -Recurse -Filter "*.java"
Write-Host "Found $($javaFiles.Count) Java files"

# 编译
$files = $javaFiles.FullName -join " "
$cmd = "javac -d `"$OutputDir`" -cp `"$fullCp`" -processorpath `"$env:LOMBOK`" -encoding UTF-8 $files"

cmd /c $cmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "SUCCESS"
} else {
    Write-Host "FAILED"
}