<#
.SYNOPSIS
    Runs the E2E tests and automatically opens the Serenity report when done.
.PARAMETER Browser
    Browser to use: firefox | chrome | edge  (default: firefox)
.EXAMPLE
    .\run-and-report.ps1 -Browser chrome
#>
param(
    [ValidateSet('firefox','chrome','edge')]
    [string]$Browser = 'firefox'
)

$repoRoot   = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Set-Location $repoRoot

$runScript  = Join-Path $PSScriptRoot 'run-browser.ps1'
$reportPath = Join-Path $repoRoot 'target\site\serenity\index.html'

Write-Host "Running tests with $Browser..." -ForegroundColor Cyan
& $runScript -Browser $Browser
$exitCode = $LASTEXITCODE

if (Test-Path $reportPath) {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "  Serenity report generated successfully"   -ForegroundColor Green
    Write-Host "  $reportPath"                              -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Start-Process $reportPath
} else {
    Write-Warning "Report not found at: $reportPath"
}

exit $exitCode
