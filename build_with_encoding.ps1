# 设置环境变量并运行Maven编译
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8"

cd d:/me/project/xiaoli/backend

mvn clean compile -DskipTests -X 2>&1 | Select-String -Pattern 'Processing|annotation|processor|Error|error' | Select-Object -First 50