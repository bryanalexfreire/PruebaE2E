<#
.SYNOPSIS
    Runs the Serenity BDD E2E tests against the specified browser.
.PARAMETER Browser
    Browser to use: firefox | chrome | edge  (default: firefox)
.EXAMPLE
    .\run-browser.ps1 -Browser chrome
#>
param(
    [ValidateSet('firefox','chrome','edge')]
    [string]$Browser = 'firefox'
)

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Set-Location $repoRoot

if (-not (Test-Path (Join-Path $repoRoot 'gradlew.bat'))) {
    Write-Error "gradlew.bat not found at $repoRoot"
    exit 1
}

Write-Host "===== Running tests on $Browser =====" -ForegroundColor Cyan
& .\gradlew.bat clean test "-Pbrowser=$Browser"

exit $LASTEXITCODE
