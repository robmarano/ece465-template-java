# @ECHO OFF
#
# app_kill_app_processes.ps1
#

# kill old app processes
Get-WmiObject Win32_Process -Filter "name = 'java.exe'" | where {$_.CommandLine -like "*zkApp*"} |Select-Object ProcessId | ForEach-Object {taskkill.exe /F /PID $_.ProcessId}
