<#
.SYNOPSIS
    Runs the E2E tests across all three browsers (firefox, chrome, edge) in sequence
    and prints a summary with the exit code for each one.
.EXAMPLE
    .\run-all-browsers.ps1
#>
$ErrorActionPreference = 'Continue'
$runScript = Join-Path $PSScriptRoot 'run-browser.ps1'
$browsers  = @('firefox', 'chrome', 'edge')
$results   = @()

foreach ($browser in $browsers) {
    Write-Host "`n===== Running on $browser =====" -ForegroundColor Cyan
    & $runScript -Browser $browser
    $results += [PSCustomObject]@{
        Browser  = $browser
        ExitCode = $LASTEXITCODE
        Status   = if ($LASTEXITCODE -eq 0) { 'PASSED' } else { 'FAILED' }
    }
}

Write-Host "`n===== Execution summary =====" -ForegroundColor Yellow
$results | Format-Table Browser, Status, ExitCode -AutoSize | Out-String | Write-Host

$failed = $results | Where-Object { $_.ExitCode -ne 0 }
if ($failed) {
    Write-Warning "$($failed.Count) browser(s) failed: $($failed.Browser -join ', ')"
    exit 1
}

Write-Host "All browsers completed without errors." -ForegroundColor Green
exit 0
