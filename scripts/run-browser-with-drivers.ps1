# run-browser-with-drivers.ps1
#
# Purpose: Runs tests with intelligent driver handling:
#   1. Try to download (Serenity autodownload)
#   2. Check system PATH
#   3. Fallback to project bundled drivers
#
# Usage: .\scripts\run-browser-with-drivers.ps1 -Browser firefox [options]
#

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet('chrome', 'firefox', 'edge')]
    [string]$Browser = 'firefox',

    [Parameter(Mandatory=$false)]
    [string]$Tags = '',

    [Parameter(Mandatory=$false)]
    [switch]$ShowReport = $false,

    [Parameter(Mandatory=$false)]
    [switch]$ValidateOnly = $false
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Path $PSScriptRoot -Parent

# ============================================================================
# FUNCIONES
# ============================================================================

function Write-Header {
    Write-Host ""
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
    Write-Host $args[0] -ForegroundColor Cyan
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
}

function Write-Success { Write-Host "✅ $($args[0])" -ForegroundColor Green }
function Write-Error-Msg { Write-Host "❌ $($args[0])" -ForegroundColor Red }
function Write-Warning { Write-Host "⚠️  $($args[0])" -ForegroundColor Yellow }
function Write-Info { Write-Host "ℹ️  $($args[0])" -ForegroundColor Cyan }

function Test-DriverAvailability {
    param([string]$Browser)

    Write-Header "🔍 Verificando disponibilidad de driver: $Browser"

    $driverInfo = @{
        'chrome' = @{ exe = 'chromedriver'; project = 'drivers\chrome\147\extracted\chromedriver.exe' }
        'firefox' = @{ exe = 'geckodriver'; project = 'drivers\firefox\150\extracted\geckodriver.exe' }
        'edge' = @{ exe = 'msedgedriver'; project = 'drivers\edge\147\msedgedriver.exe' }
    }

    $info = $driverInfo[$Browser]
    $serenityCache = "$env:LOCALAPPDATA\.serenity\drivers"

    # Level 1: Serenity autodownload
    Write-Info "Nivel 1️⃣ - Intentará descargar automáticamente (si tiene Internet)"
    Write-Host "   Ubicación: $serenityCache" -ForegroundColor Gray

    # Level 2: System PATH
    Write-Info "Nivel 2️⃣ - Buscando en PATH del sistema"
    $pathResult = Get-Command $info.exe -ErrorAction SilentlyContinue
    if ($pathResult) {
        Write-Success "Encontrado en PATH: $($pathResult.Source)"
        return $true
    }
    else {
        Write-Warning "No encontrado en PATH"
    }

    # Level 3: Project bundled
    Write-Info "Nivel 3️⃣ - Verificando drivers del proyecto"
    $projectPath = Join-Path -Path $projectRoot -ChildPath $info.project
    if (Test-Path $projectPath) {
        Write-Success "Encontrado en proyecto: $projectPath"
        return $true
    }
    else {
        Write-Error-Msg "No encontrado en: $projectPath"
    }

    # If no driver found, warn but continue (Serenity may download)
    Write-Warning "Driver no encontrado en el sistema, pero Serenity intentará descargar automáticamente..."
    return $false
}

function Get-EffectiveDriverPath {
    param([string]$Browser)

    $driverInfo = @{
        'chrome' = @{ exe = 'chromedriver'; project = 'drivers\chrome\147\extracted\chromedriver.exe' }
        'firefox' = @{ exe = 'geckodriver'; project = 'drivers\firefox\150\extracted\geckodriver.exe' }
        'edge' = @{ exe = 'msedgedriver'; project = 'drivers\edge\147\msedgedriver.exe' }
    }

    $info = $driverInfo[$Browser]

    # Try system PATH
    $pathResult = Get-Command $info.exe -ErrorAction SilentlyContinue
    if ($pathResult) {
        return $pathResult.Source
    }

    # Try project bundled
    $projectPath = Join-Path -Path $projectRoot -ChildPath $info.project
    if (Test-Path $projectPath) {
        return Resolve-Path $projectPath
    }

    return $null
}

function Run-Tests {
    param(
        [string]$Browser,
        [string]$Tags,
        [string]$DriverPath
    )

    Write-Header "▶️  Ejecutando tests en $Browser"

    Set-Location -Path $projectRoot

    $gradleCmd = ".\gradlew.bat clean test -Pbrowser=$Browser"

    if ($Tags) {
        $gradleCmd += " -Ptags=`"$Tags`""
        Write-Info "Filtro de tags: $Tags"
    }

    if ($DriverPath) {
        # Set as environment variable for Gradle to pick up
        $env:DRIVER_PATH = $DriverPath
        Write-Info "Driver especificado: $DriverPath"
    }

    Write-Info "Comando: $gradleCmd"
    Write-Info "Directorio: $projectRoot"
    Write-Host ""

    # Run Gradle
    Invoke-Expression $gradleCmd

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Success "✅ Tests ejecutados exitosamente"
        return $true
    }
    else {
        Write-Host ""
        Write-Error-Msg "❌ Tests fallaron con código: $LASTEXITCODE"
        return $false
    }
}

function Show-Report {
    $reportPath = Join-Path -Path $projectRoot -ChildPath "target\site\serenity\index.html"

    if (Test-Path $reportPath) {
        Write-Header "📊 Abriendo reporte Serenity"
        Write-Success "Reporte: $reportPath"
        Start-Process $reportPath
    }
    else {
        Write-Warning "Reporte no encontrado en: $reportPath"
    }
}

function Print-BrowserInfo {
    param([string]$Browser)

    Write-Host ""
    Write-Host "Browser: " -NoNewline -ForegroundColor Gray
    Write-Host $Browser -ForegroundColor Yellow

    $browserInfo = @{
        'chrome' = 'Chrome v147 (Chromium-based)'
        'firefox' = 'Firefox v150 (Gecko)'
        'edge' = 'Microsoft Edge v147 (Chromium-based)'
    }

    Write-Host "Versión: " -NoNewline -ForegroundColor Gray
    Write-Host $browserInfo[$Browser] -ForegroundColor Cyan
}

# ============================================================================
# MAIN EXECUTION
# ============================================================================

Write-Host ""
Write-Host "🚀 Test Execution with Intelligent Driver Management" -ForegroundColor Green -BackgroundColor DarkGreen
Write-Host ""
Write-Host "   Proyecto: $projectRoot" -ForegroundColor Gray
Write-Host "   Fecha: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray

Print-BrowserInfo -Browser $Browser

# Validate driver availability
$driverExists = Test-DriverAvailability -Browser $Browser

if ($ValidateOnly) {
    Write-Header "✅ Validación completada"
    if ($driverExists) {
        Write-Success "Driver disponible - Puedes ejecutar los tests"
        exit 0
    }
    else {
        Write-Warning "Driver no encontrado localmente, pero será descargado automáticamente..."
        exit 1
    }
}

# Get effective driver path (if exists)
$driverPath = Get-EffectiveDriverPath -Browser $Browser

# Run tests
$success = Run-Tests -Browser $Browser -Tags $Tags -DriverPath $driverPath

# Show report if requested and tests passed
if ($ShowReport -and $success) {
    Show-Report
}

# Exit with appropriate code
exit ($success ? 0 : 1)


