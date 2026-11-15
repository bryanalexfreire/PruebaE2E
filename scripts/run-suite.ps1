<#
.SYNOPSIS
    Runs tests based on Cucumber tags using a single unified runner.
.PARAMETER Tags
    Cucumber tags to filter tests (default: runs all features)
    Can use single tags or combinations with AND/OR logic
.PARAMETER Browser
    Browser to use: firefox, chrome, edge (default: firefox)
.EXAMPLE
    .\run-suite.ps1 -Tags "@BuyDevicesHP_DDT" -Browser firefox
    .\run-suite.ps1 -Tags "@TC-03" -Browser chrome
    .\run-suite.ps1 -Tags "@critical and not @bug"
#>
param(
    [string]$Tags = "",
    [ValidateSet('firefox','chrome','edge')]
    [string]$Browser = 'firefox'
)
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Set-Location $repoRoot
Write-Host "======================================" -ForegroundColor Cyan
if ($Tags) {
    Write-Host "Running tests with tags: $Tags on $Browser" -ForegroundColor Cyan
} else {
    Write-Host "Running ALL tests on $Browser" -ForegroundColor Cyan
}
Write-Host "======================================" -ForegroundColor Cyan
if ($Tags) {
    & .\gradlew.bat clean test "-Pbrowser=$Browser" "-Ptags=$Tags"
} else {
    & .\gradlew.bat clean test "-Pbrowser=$Browser"
}
exit $LASTEXITCODE
