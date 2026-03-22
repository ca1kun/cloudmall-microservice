param(
    [string[]]$Services,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$runtimeDir = Join-Path $root "runtime"
$pidDir = Join-Path $runtimeDir "pids"
$logDir = Join-Path $runtimeDir "logs"

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
    if (-not $serviceCatalog) {
        throw "No matching service found. Valid values: mis-auth, mis-api, mis-marketing, mis-cart, mis-order, mis-pay, mis-ai, mis-gateway"
    }
}

if (-not (Get-Command mvn.cmd -ErrorAction SilentlyContinue)) {
    throw "mvn.cmd not found in PATH. Please install Maven or open terminal from IDEA with Maven available."
}

New-Item -ItemType Directory -Path $pidDir -Force | Out-Null
New-Item -ItemType Directory -Path $logDir -Force | Out-Null

Write-Host "Starting services from: $root"

foreach ($service in $serviceCatalog) {
    $name = $service.Name
    $pidFile = Join-Path $pidDir ("{0}.pid" -f $name)
    $outLog = Join-Path $logDir ("{0}.out.log" -f $name)
    $errLog = Join-Path $logDir ("{0}.err.log" -f $name)
    $modulePom = Join-Path $root ("{0}\pom.xml" -f $name)

    if (-not (Test-Path $modulePom)) {
        Write-Warning ("Skip {0}: module pom not found at {1}" -f $name, $modulePom)
        continue
    }

    if (Test-Path $pidFile) {
        $existingPid = Get-Content $pidFile -Raw
        $existingProc = Get-Process -Id $existingPid -ErrorAction SilentlyContinue
        if ($existingProc) {
            Write-Host ("[SKIP] {0} is already running (PID: {1})" -f $name, $existingPid)
            continue
        }
        Remove-Item $pidFile -Force
    }

    # Run spring-boot:run against the module itself to avoid executing on the root aggregator pom.
    $args = @("-f", $modulePom, "spring-boot:run")

    if ($DryRun) {
        Write-Host ("[DRY-RUN] mvn.cmd {0}" -f ($args -join " "))
        continue
    }

    Write-Host ("[START] {0} ..." -f $name)
    # Keep service processes detached but without popping extra console windows per module.
    $proc = Start-Process -FilePath "mvn.cmd" -ArgumentList $args -WorkingDirectory $root -WindowStyle Hidden -RedirectStandardOutput $outLog -RedirectStandardError $errLog -PassThru
    Set-Content -Path $pidFile -Value $proc.Id -NoNewline

    Start-Sleep -Seconds 2
    if ($proc.HasExited) {
        Write-Warning ("{0} exited early. Check log: {1}" -f $name, $errLog)
    } else {
        Write-Host ("[OK] {0} started (PID: {1}, Port: {2})" -f $name, $proc.Id, $service.Port)
    }
}

Write-Host "Done. Use scripts/status-all.ps1 to check status."
Write-Host "Logs: runtime/logs"
