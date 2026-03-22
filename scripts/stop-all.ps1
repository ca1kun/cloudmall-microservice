param(
    [string[]]$Services
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$pidDir = Join-Path $root "runtime\pids"

$serviceNames = @("mis-auth", "mis-api", "mis-marketing", "mis-cart", "mis-order", "mis-pay", "mis-ai", "mis-gateway")
if ($Services -and $Services.Count -gt 0) {
    $serviceNames = $serviceNames | Where-Object { $Services -contains $_ }
}

if (-not (Test-Path $pidDir)) {
    Write-Host "No PID directory found: runtime/pids"
    exit 0
}

foreach ($name in $serviceNames) {
    $pidFile = Join-Path $pidDir ("{0}.pid" -f $name)
    if (-not (Test-Path $pidFile)) {
        Write-Host ("[SKIP] {0} not running (no pid file)." -f $name)
        continue
    }

    $processId = Get-Content $pidFile -Raw
    $proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
    if ($proc) {
        Stop-Process -Id $processId -Force
        Write-Host ("[STOP] {0} stopped (PID: {1})" -f $name, $processId)
    } else {
        Write-Host ("[CLEAN] {0} pid file existed but process already exited." -f $name)
    }

    Remove-Item $pidFile -Force
}

Write-Host "Done."
