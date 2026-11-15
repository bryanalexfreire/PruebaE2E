<#
.SYNOPSIS
    Runs the E2E tests with the driver chosen by the resolver (gradlew resolveDriver):
      1. Selenium Manager downloads the driver that matches the installed browser (or takes it from its cache);
      2. if the download fails or does not match, a matching bundled driver from drivers\<browser>\<version>\;
      3. otherwise the run stops with a message that says what is installed and what is missing.
.PARAMETER Browser
    chrome | firefox | edge (default: firefox)
.PARAMETER Tags
    Cucumber tags (default of the build: "not @wip and not @bug")
.PARAMETER Offline
    Skip the download and use only the bundled drivers
.PARAMETER ValidateOnly
    Only resolve and print the driver; do not run the tests
.PARAMETER ShowReport
    Open the Serenity report at the end
.EXAMPLE
    .\scripts\run-browser-with-drivers.ps1 -Browser edge -ValidateOnly
    .\scripts\run-browser-with-drivers.ps1 -Browser chrome -Offline -ShowReport
#>
param(
    [ValidateSet('chrome', 'firefox', 'edge')]
    [string]$Browser = 'firefox',
    [string]$Tags = '',
    [switch]$Offline,
    [switch]$ValidateOnly,
    [switch]$ShowReport
)

$projectRoot = Split-Path -Path $PSScriptRoot -Parent
Set-Location -Path $projectRoot

$gradleArgs = @("-Pbrowser=$Browser")
if ($Offline) { $gradleArgs += '-PdriverDownload=off' }

if ($ValidateOnly) {
    & .\gradlew.bat resolveDriver @gradleArgs
    if ($LASTEXITCODE -eq 0) { Get-Content (Join-Path $projectRoot 'build\driver.properties') | Where-Object { $_ -notmatch '^#' } }
    exit $LASTEXITCODE
}

if ($Tags) { $gradleArgs += "-Ptags=$Tags" }
& .\gradlew.bat test @gradleArgs
$exitCode = $LASTEXITCODE

$report = Join-Path $projectRoot 'target\site\serenity\index.html'
if ($ShowReport -and (Test-Path $report)) { Start-Process $report }
exit $exitCode
