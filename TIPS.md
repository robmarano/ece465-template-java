# Tips

## Windows
Tail a log file
```powershell
Get-Content -Path .\node1.log -wait
```

Query if any java processes are running
```powershell
get-process | where {$_.ProcessName -Like "java*"}
```

