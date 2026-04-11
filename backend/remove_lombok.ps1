# Remove Lombok from Java files
# This script handles @RequiredArgsConstructor, @Slf4j, and simple @Data classes

param(
    [string]$RootPath = "d:\me\project\xiaoli\backend"
)

$ErrorActionPreference = "Continue"

# Files that still need manual work (complex classes with nested Builder patterns)
$skipFiles = @(
    "common\common-ai\src\main\java\com\xiaoli\ai\common\core\model\DeepSeekRequest.java",
    "common\common-ai\src\main\java\com\xiaoli\ai\common\core\model\DeepSeekResponse.java",
    "common\common-ai\src\main\java\com\xiaoli\ai\common\core\model\Intent.java",
    "common\common-ai\src\main\java\com\xiaoli\ai\common\core\service\IntentRouterService.java",
    "common\common-ai\src\main\java\com\xiaoli\ai\common\core\service\DeepSeekService.java"
)

Write-Host "Starting Lombok removal process..." -ForegroundColor Cyan

# Process Service Impl files (remove @RequiredArgsConstructor and @Slf4j)
$serviceImplFiles = Get-ChildItem -Path "$RootPath" -Recurse -Filter "*ServiceImpl.java" | Where-Object { $_.FullName -notlike "*target*" }
foreach ($file in $serviceImplFiles) {
    $content = Get-Content $file.FullName -Raw
    
    # Skip if already processed
    if ($content -notmatch "import lombok") { continue }
    
    # Remove import statements
    $content = $content -replace "import lombok\.AllArgsConstructor;\s*`n?", ""
    $content = $content -replace "import lombok\.Builder;\s*`n?", ""
    $content = $content -replace "import lombok\.Data;\s*`n?", ""
    $content = $content -replace "import lombok\.NoArgsConstructor;\s*`n?", ""
    $content = $content -replace "import lombok\.RequiredArgsConstructor;\s*`n?", ""
    $content = $content -replace "import lombok\.extern\.slf4j\.Slf4j;\s*`n?", ""
    
    # Remove @RequiredArgsConstructor annotation
    $content = $content -replace "@RequiredArgsConstructor`n", ""
    $content = $content -replace "@RequiredArgsConstructor\s*`n", ""
    
    # Remove @Slf4j annotation
    $content = $content -replace "@Slf4j`n", ""
    $content = $content -replace "@Slf4j\s*`n", ""
    
    Set-Content -Path $file.FullName -Value $content -NoNewline
    Write-Host "Processed: $($file.Name)" -ForegroundColor Green
}

Write-Host "`nLombok imports and annotations removed from ServiceImpl files." -ForegroundColor Cyan
Write-Host "Note: Some files may still have @Data, @Builder annotations that need manual removal." -ForegroundColor Yellow
