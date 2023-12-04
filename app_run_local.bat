REM @ECHO OFF
REM
REM app_run_local.bat
REM

REM kill old app processes
REM Get-WmiObject Win32_Process -Filter "name = 'java.exe'" | where {$_.CommandLine -like "*zkApp*"} |Select-Object ProcessId | ForEach-Object {taskkill.exe /F /PID $_.ProcessId}

SET APP_JAR=..\..\target\zkApp-1.0-SNAPSHOT.jar
SET SERVER_PORT=808
SET MGMT_PORT=909

RMDIR /S /Q .\nodes
MD .\nodes
MD .\nodes\node1
MD .\nodes\node2
MD .\nodes\node3

CD .\nodes
SET NODE=1
CD .\node%NODE%
REM @ECHO ON
START /B java -Dserver.port=%SERVER_PORT%%NODE% -Dmanagement.server.port=%MGMT_PORT%%NODE% -Dzk.url=localhost:218%NODE% -Dleader.algo=2 -jar %APP_JAR% 2>&1 > node%NODE%.log &
REM @ECHO ON
SET NODE=2
CD ..\node%NODE%
START /B java -Dserver.port=%SERVER_PORT%%NODE% -Dmanagement.server.port=%MGMT_PORT%%NODE% -Dzk.url=localhost:218%NODE% -Dleader.algo=2 -jar %APP_JAR% 2>&1 > node%NODE%.log &
SET NODE=3
CD ..\node%NODE%
START /B java -Dserver.port=%SERVER_PORT%%NODE% -Dmanagement.server.port=%MGMT_PORT%%NODE% -Dzk.url=localhost:218%NODE% -Dleader.algo=2 -jar %APP_JAR% 2>&1 > node%NODE%.log &

REM @ECHO OFF
CD ..\..

echo "Running."