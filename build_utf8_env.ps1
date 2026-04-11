$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8 -Xms512m -Xmx1024m"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Set-Location d:/me/project/xiaoli/backend
& mvn clean compile -DskipTests 2>&1 | Tee-Object -FilePath d:/me/project/xiaoli/build_utf8_env.log