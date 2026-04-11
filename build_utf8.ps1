Set-Location 'd:/me/project/xiaoli/backend'
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8"
mvn clean compile -DskipTests -pl common/common-core -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 -Dmaven.compiler.encoding=UTF-8 2>&1