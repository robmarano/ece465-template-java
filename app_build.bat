REM
REM app_build.bat
REM
REM For help see https://steve-jansen.github.io/guides/windows-batch-scripting/index.html

SET APP_JAR=zkApp-1.0-SNAPSHOT.jar
CLS
mvn clean package -Dmaven.test.skip=true