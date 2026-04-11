Set-Location 'd:/me/project/xiaoli/backend'
mvn clean compile -DskipTests --debug 2>&1 | Select-Object -Last 500