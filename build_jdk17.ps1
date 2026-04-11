$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "C:\Program Files\Java\jdk-17\bin;$env:PATH"
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8 -Xms512m -Xmx1024m"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "Java version:"
& java -version 2>&1

Write-Host "`nMaven version:"
& mvn -version 2>&1

Write-Host "`nStarting build..."
Set-Location d:/me/project/xiaoli/backend
& mvn clean compile -DskipTests 2>&1