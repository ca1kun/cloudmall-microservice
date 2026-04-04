param(
    [string[]]$Services
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$pidDir = Join-Path $root "runtime\pids"

function Stop-ProcessTree {
    param(
        [Parameter(Mandatory = $true)]
        [int]$ProcessId
    )

    # Use taskkill /T to terminate the full process tree (maven wrapper + child JVM).
    & taskkill /PID $ProcessId /T /F 2>$null | Out-Null
    Start-Sleep -Milliseconds 300
    return -not (Get-Process -Id $ProcessId -ErrorAction SilentlyContinue)
}

function Resolve-ServicePidByPort {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name,
        [Parameter(Mandatory = $true)]
        [int]$Port
    )

    $pids = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -ExpandProperty OwningProcess -Unique

    foreach ($candidatePid in $pids) {
        $proc = Get-Process -Id $candidatePid -ErrorAction SilentlyContinue
        if ($proc) {
            return [int]$candidatePid
        }
    }

    return $null
}

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

foreach ($service in $serviceCatalog) {
    $name = $service.Name
    $port = $service.Port
    $pidFile = Join-Path $pidDir ("{0}.pid" -f $name)

    $processId = $null

    if (Test-Path $pidFile) {
        $processIdRaw = (Get-Content $pidFile -Raw).Trim()
        $parsedProcessId = 0
        if ([int]::TryParse($processIdRaw, [ref]$parsedProcessId)) {
            if (Get-Process -Id $parsedProcessId -ErrorAction SilentlyContinue) {
                $processId = $parsedProcessId
            } else {
                Write-Host ("[CLEAN] {0} pid file existed but process already exited." -f $name)
                Remove-Item $pidFile -Force
            }
        } else {
            Write-Warning ("[CLEAN] {0} pid file is invalid: '{1}'" -f $name, $processIdRaw)
            Remove-Item $pidFile -Force
        }
    }

    $usedPortFallback = $false
    if (-not $processId) {
        $processId = Resolve-ServicePidByPort -Name $name -Port $port
        if ($processId) {
            $usedPortFallback = $true
        }
    }

    if (-not $processId) {
        Write-Host ("[SKIP] {0} not running (no pid file and port {1} not listening)." -f $name, $port)
        continue
    }

    $stopped = Stop-ProcessTree -ProcessId $processId
    if ($stopped) {
        if (Test-Path $pidFile) {
            Remove-Item $pidFile -Force
        }

        if ($usedPortFallback) {
            Write-Host ("[STOP] {0} stopped by port fallback (Port: {1}, PID: {2})" -f $name, $port, $processId)
        } else {
            Write-Host ("[STOP] {0} stopped (PID: {1})" -f $name, $processId)
        }
    } else {
        Write-Warning ("[FAIL] {0} could not be stopped (PID: {1}). Try running terminal as Administrator." -f $name, $processId)
    }
}

Write-Host "Done."
