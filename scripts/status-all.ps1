param(
    [string[]]$Services
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$pidDir = Join-Path $root "runtime\pids"

$serviceCatalog = @(
    @{ Name = "mis-auth"; Port = 9201 },
    @{ Name = "mis-api"; Port = 8081 },
    @{ Name = "mis-marketing"; Port = 8083 },
    @{ Name = "mis-cart"; Port = 8084 },
    @{ Name = "mis-order"; Port = 8085 },
    @{ Name = "mis-pay"; Port = 8086 },
    @{ Name = "mis-ai"; Port = 8087 },
    @{ Name = "mis-gateway"; Port = 8080 }
)

if ($Services -and $Services.Count -gt 0) {
    $serviceCatalog = $serviceCatalog | Where-Object { $Services -contains $_.Name }
}

$rows = foreach ($service in $serviceCatalog) {
    $name = $service.Name
    $pidFile = Join-Path $pidDir ("{0}.pid" -f $name)
    $processId = "-"
    $status = "Stopped"

    if (Test-Path $pidFile) {
        $processId = Get-Content $pidFile -Raw
        $proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
        if ($proc) {
            $status = "Running"
        } else {
            $status = "StalePidFile"
        }
    }

    $portStatus = "Closed"
    $listen = Get-NetTCPConnection -LocalPort $service.Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($listen) {
        $portStatus = "Listening"
    }

    [PSCustomObject]@{
        Service = $name
        Port = $service.Port
        ProcessId = $processId
        Process = $status
        PortState = $portStatus
    }
}

$rows | Format-Table -AutoSize
