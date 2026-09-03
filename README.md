# PruebaE2E — E2E Automation for Demoblaze

End-to-end functional web automation project built with **Serenity BDD + Screenplay + Cucumber + JUnit 4**.

The test suite covers the full purchase flow on [https://www.demoblaze.com](https://www.demoblaze.com):
login → add products to the cart → review cart → fill in the purchase form → log out.

---

## 1. Technologies and versions

| Technology       | Version  |
|------------------|----------|
| Java             | JDK 21   |
| Gradle Wrapper   | 8.7      |
| Serenity BDD     | 4.1.14   |
| Cucumber (JUnit) | bundled with serenity-cucumber 4.1.14 |
| Apache POI       | 5.2.5    |
| Commons Lang3    | 3.12.0   |
| Validated OS     | Windows  |
| Browsers         | Firefox · Chrome · Edge |

---

## 2. Screenplay pattern

The project follows the Screenplay pattern, which keeps responsibilities clearly separated:

```text
Actor
 └── attemptsTo(Task / Interaction)
       └── Task  →  Interaction(s)
                      └── UI Target (locator)
```

- **Actor** — the person interacting with the system (e.g. a buyer).
- **Task** — a high-level business action (e.g. `MakeLogin`, `FindProduct`).
- **Interaction** — a low-level browser action (e.g. `SafeClick`, `AcceptBrowserAlert`).
- **UI Target** — a locator that maps to a specific element on the page.

---

## 3. Project structure

```text
PruebaE2E/
├── build.gradle                        ← Dependencies, Gradle tasks, browser configuration
├── serenity.properties                 ← Global Serenity settings (timeouts, screenshots, etc.)
├── settings.gradle
├── gradlew.bat
│
├── scripts/                            ← PowerShell execution scripts
│   ├── run-browser.ps1                 ← Core: runs the tests on a given browser
│   ├── run-and-report.ps1              ← Runs tests and opens the Serenity report automatically
│   └── run-all-browsers.ps1            ← Runs all 3 browsers in sequence and prints a summary
│
├── drivers/
│   ├── edge/147/msedgedriver.exe
│   ├── chrome/147/extracted/chromedriver.exe
│   └── firefox/150/extracted/geckodriver.exe
│
└── src/
    ├── main/java/co/com/screenplay/project/
    │   ├── model/
    │   │   ├── ModelCredentials.java   ← DTO: username + password
    │   │   └── ModelCustomer.java      ← DTO: purchase form fields
    │   ├── tasks/
    │   │   ├── OpenWeb.java            ← Opens the Demoblaze URL
    │   │   ├── MakeLogin.java          ← Logs in with the given credentials
    │   │   ├── FindProduct.java        ← Adds two products to the cart
    │   │   ├── ViewCart.java           ← Opens the shopping cart
    │   │   ├── RegisterCustomer.java   ← Fills in and submits the purchase form
    │   │   ├── CloseSession.java       ← Confirms the purchase and logs out
    │   │   ├── SafeClick.java          ← Click with retry logic and JS fallback
    │   │   └── AcceptBrowserAlert.java ← Accepts native browser alert dialogs
    │   ├── ui/
    │   │   ├── PageMain.java           ← Locators: home page, login modal, product links
    │   │   ├── PageProducts.java       ← Locator: "Add to cart" button
    │   │   └── PageCar.java            ← Locators: cart, purchase form, confirmation dialog
    │   └── util/
    │       ├── Timeouts.java           ← SHORT timeout constant (3 s)
    │       └── overwritedata/
    │           ├── FeatureOverwriteCli.java  ← CLI entry point (prepare / restore modes)
    │           ├── FeatureOverwrite.java     ← Materializes and restores the @externaldata descriptor
    │           ├── ExcelReader.java          ← Reads .xlsx files using Apache POI
    │           └── CSVReader.java            ← Reads .csv files
    │
    └── test/
        ├── java/co/com/screenplay/project/
        │   ├── runners/
        │   │   └── RunnerOpenWebTest.java    ← Cucumber/Serenity runner (tag: @BuyDevicesHP_DDT)
        │   └── stepdefinition/
        │       ├── DemoblazeGlue.java        ← Step definitions for the purchase flow
        │       ├── SetupStage.java           ← @Before: sets up the Screenplay stage
        │       └── ActorHooks.java           ← @After: tears down the stage
        └── resources/
            ├── serenity.conf                 ← Browser options and base URL
            ├── features/
            │   └── 01 - E-Commerce Platform/
            │       └── onlineShop.feature    ← DDT scenario using @externaldata
            └── data/purchase/
                └── dataDemoblaze.xlsx        ← Test data (sheet: purchase)
```

---

## 4. Data-Driven Testing (DDT) from Excel

The feature file uses the `@externaldata` descriptor inside the `Examples` block. This tells the framework to load the actual test rows from the referenced Excel sheet at runtime.

```gherkin
@completeFlow
Feature: End-to-end web automation tests with Serenity BDD - Demoblaze

  @completeFlowBP @BuyDevicesHP_DDT
  Scenario Outline: Full purchase flow using external Excel data
    Given "<name>" accesses the shopping portal
    When he login in portal "<username>" "<password>"
    And he adds devices to the cart
    And he sees the products in cart
    When he enters user data "<name>" "<country>" "<city>" "<card>" "<month>" "<year>"
    Then he closes the session

    Examples:
      | @externaldata@src/test/resources/data/purchase/dataDemoblaze.xlsx..purchase |
```

### Automatic pipeline (Gradle)

| Step | Gradle task | What it does |
|------|-------------|--------------|
| 1 | `prepareExternalDataFeatures` | Reads the Excel file and writes the real data rows into `Examples` |
| 2 | `test` | Runs the Cucumber/Serenity suite with the materialized data |
| 3 | `restoreExternalDataFeatures` | Restores the original `@externaldata` descriptor in the feature file |
| 4 | `aggregate` | Generates the full Serenity HTML report |

Steps 2–4 are wired together automatically in `build.gradle` using `dependsOn` / `finalizedBy`, so you only need to run `test`.

---

## 5. Running the tests

### Using the PowerShell scripts (recommended)

```powershell
# Run on a specific browser
.\scripts\run-browser.ps1 -Browser firefox
.\scripts\run-browser.ps1 -Browser chrome
.\scripts\run-browser.ps1 -Browser edge

# Run and open the Serenity report automatically when done
.\scripts\run-and-report.ps1 -Browser firefox

# Run all 3 browsers in sequence and get a summary table
.\scripts\run-all-browsers.ps1
```

### Using Gradle directly

```powershell
Set-Location "C:\Users\User\Desktop\DemonblazeE2E\PruebaE2E"

# Full test suite
.\gradlew.bat clean test -Pbrowser=firefox

# Filter by tag
.\gradlew.bat clean test -Pbrowser=chrome -Ptags="@BuyDevicesHP_DDT"

# Materialize or restore external data manually
.\gradlew.bat prepareExternalDataFeatures
.\gradlew.bat restoreExternalDataFeatures
```

> **Note:** The `aggregate` task (Serenity report) runs automatically after `test`
> thanks to the `finalizedBy` hook in `build.gradle`.

---

## 6. Reports

| Report | Path |
|--------|------|
| Serenity HTML (main) | `target/site/serenity/index.html` |
| JUnit / Gradle | `build/reports/tests/test/index.html` |
| Cucumber JSON | `build/cucumber-reports/json/cucumber.json` |
| Screenshots | `target/site/serenity/screenshots/` |

### How Reports are Generated

Every test execution automatically:
1. **Cleans old reports** — Removes outdated results
2. **Prepares data** — Loads Excel test data
3. **Executes tests** — Runs with Serenity + Cucumber
4. **Captures evidence** — Screenshots on every step
5. **Restores features** — Cleans up temporary data
6. **Aggregates results** — Generates HTML reports

**Key:** Old reports are deleted before each run to ensure fresh, up-to-date results.

### Screenshot Evidence

Every test step captures screenshots automatically:
- **Success Screenshots** — Shows what passed
- **Failure Screenshots** — Shows what failed and why
- **Location:** `target/site/serenity/screenshots/`

Screenshots are embedded in the Serenity HTML report. View them by opening:
```powershell
Start-Process "target\site\serenity\index.html"
```

**Configuration** (in `serenity.properties`):
```ini
serenity.take.screenshots=FOR_EACH_ACTION
serenity.screenshots.take.failure=true
serenity.screenshots.take.success=true
webdriver.screenshot.dir=target/site/serenity/screenshots
```

---

## 6.1 Quick Reference — Common Commands

### Execute Tests

```powershell
# Run on Firefox (default)
.\gradlew.bat clean test

# Run on specific browser
.\gradlew.bat clean test -Pbrowser=chrome
.\gradlew.bat clean test -Pbrowser=edge

# Run with tag filter
.\gradlew.bat clean test -Pbrowser=firefox -Ptags="@BuyDevicesHP_DDT"

# Run and open report
.\scripts\run-and-report.ps1 -Browser firefox

# Run all 3 browsers
.\scripts\run-all-browsers.ps1
```

### Troubleshooting

| Issue | Solution |
|-------|----------|
| **Driver not found** | Run `.\gradlew.bat test --info` to see logs; Serenity will auto-download |
| **Permission denied on drivers** | `Get-ChildItem drivers -Recurse -File \| % { Unblock-File $_.FullName }` |
| **Gradle not found** | Verify you're in project root: `cd C:\Users\User\Desktop\DemonblazeE2E\PruebaE2E` |
| **Java not found** | Verify JDK 21 PATH: `java -version` |
| **Tests timeout** | Increase gradle memory: `$env:GRADLE_OPTS="-Xmx2048m"` |
| **Port already in use** | Kill driver processes: `taskkill /F /IM chromedriver.exe` |

---

## 7. WebDriver Management — Runtime Resolution

The project now uses **runtime WebDriver resolution** so CI and local runs do not depend on fixed driver paths.

### How It Works

```
Serenity webdriver.autodownload=true
         ↓
Selenium Manager resolves the correct driver for the selected browser
         ↓
If resolution fails, the job surfaces the real environment error
```

### Browser Selection

| Browser | Execution value |
|---------|-----------------|
| Chrome  | `-Pbrowser=chrome` |
| Firefox | `-Pbrowser=firefox` |
| Edge    | `-Pbrowser=edge` |

### Configuration

**serenity.properties:**
```ini
webdriver.autodownload=true
webdriver.driver=firefox
```

**build.gradle:**
```groovy
test {
    def browser = (project.findProperty("browser") ?: 'firefox').toString().toLowerCase()
    systemProperty "webdriver.driver", browser
}
```

### Troubleshooting Drivers

| Issue | Solution |
|-------|----------|
| Driver not found | Run `.\gradlew.bat test --info` to see logs; Serenity auto-downloads |
| Permission denied | If you keep a local fallback driver folder, unblock the files first |
| Stale driver version | Remove old local overrides and let Selenium Manager resolve the binary |
| Port conflicts | `taskkill /F /IM chromedriver.exe; taskkill /F /IM geckodriver.exe` |

---

## 8. Prerequisites

- **JDK 21** installed and available on `PATH` — Verify with `java -version`
- **Gradle** — Not required, project uses `gradlew.bat`
- **Internet** (recommended for first run) — Serenity downloads drivers automatically
  - If the environment has no network access, the run will fail fast and the workflow will show the real driver error instead of masking it

### Quick Setup

```powershell
# Verify Java
java -version

# Inspect current browser setup
Get-ChildItem Env:BROWSER,Env:JAVA_HOME,Env:GRADLE_OPTS

# Run tests (auto-configures drivers)
.\gradlew.bat clean test -Pbrowser=firefox
```


---

## 9. Getting Started in 5 Minutes

1. **Navigate to project**
   ```powershell
   cd "C:\Users\User\Desktop\DemonblazeE2E\PruebaE2E"
   ```

2. **Verify Java**
   ```powershell
   java -version  # Should show JDK 21.x
   ```

3. **Run tests**
   ```powershell
   # Option A: Run on Firefox (default)
   .\gradlew.bat clean test

   # Option B: Run on Chrome or Edge
   .\gradlew.bat clean test -Pbrowser=chrome
   .\gradlew.bat clean test -Pbrowser=edge

   # Option C: Filter by tags
   .\gradlew.bat clean test -Pbrowser=firefox -Ptags="@BuyDevicesHP_DDT"

   # Option D: Use PowerShell script
   .\scripts\run-and-report.ps1 -Browser firefox
   ```

4. **View Results**
   - Serenity Report: `target/site/serenity/index.html` (includes all screenshots)
   - Gradle Report: `build/reports/tests/test/index.html`
   - Screenshots folder: `target/site/serenity/screenshots/` (PNG files)

5. **Open Report Automatically**
   ```powershell
   .\scripts\run-and-report.ps1 -Browser firefox  # Opens report after tests complete
   ```

**Evidence Captured:**
- ✅ Screenshots on every action (success and failure)
- ✅ Step-by-step visual evidence in HTML report
- ✅ Embedded in Serenity report for easy review

---

## 10. Screenshot Evidence & Reporting

### How Screenshots Work

The framework captures **visual evidence** automatically:

| When | What | Where |
|------|------|-------|
| **Each Step** | Screenshot of action result | Embedded in report |
| **On Failure** | Screenshot when test fails | Highlighted in report |
| **On Success** | Screenshot when test passes | Embedded in report |

### View Evidence

1. **Run tests**
   ```powershell
   .\gradlew.bat clean test -Pbrowser=firefox
   ```

2. **Open Serenity Report** (contains all screenshots)
   ```powershell
   Start-Process "target\site\serenity\index.html"
   ```

3. **Screenshots location** (individual PNG files)
   ```powershell
   Get-ChildItem "target\site\serenity\screenshots" -Filter "*.png"
   ```

### Configuration

**serenity.properties:**
```ini
serenity.take.screenshots=FOR_EACH_ACTION
serenity.screenshots.take.failure=true
serenity.screenshots.take.success=true
webdriver.screenshot.dir=target/site/serenity/screenshots
```

**serenity.conf:**
```
serenity {
  take.screenshots = "FOR_EACH_ACTION"
  screenshots.take.failure = true
  screenshots.take.success = true
}
```

### What You'll See in Report

✅ Each step shows:
- Step name (Given/When/Then)
- Step description
- Screenshot of the action
- Result (PASSED or FAILED)
- Duration

✅ Navigation panel shows:
- Test scenario overview
- Feature file reference
- Test statistics
- Screenshot gallery

---

## 11. CI/CD Integration

```yaml
# GitHub Actions example
jobs:
  e2e-tests:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - run: .\gradlew.bat clean test -Pbrowser=firefox

      # Upload screenshots and reports as artifacts
      - if: always()
        uses: actions/upload-artifact@v3
        with:
          name: serenity-report
          path: target/site/serenity/
```

The driver strategy is now intentionally simple: Serenity/Selenium Manager resolves the browser driver at runtime, so CI failures point to real environment or browser issues instead of stale binary paths.

---

## 12. GitHub Flow Integration

The project includes automated GitHub Actions workflows for continuous integration and testing.

### Automated Workflows

**File:** `.github/workflows/e2e-tests.yml`

#### Trigger Events
```yaml
- Push to main or develop branches
- Pull requests against main or develop
- Daily schedule (06:00 UTC)
- Manual trigger (workflow_dispatch)
```

#### What It Does

```
┌─────────────────────────────────────────────────┐
│ 1. Checkout code                                │
├─────────────────────────────────────────────────┤
│ 2. Setup Java 21 + Gradle                       │
├─────────────────────────────────────────────────┤
│ 3. Verify drivers availability                  │
├─────────────────────────────────────────────────┤
│ 4. Run E2E Tests (3 browsers in parallel)       │
│    ├─ Firefox                                   │
│    ├─ Chrome                                    │
│    └─ Edge                                      │
├─────────────────────────────────────────────────┤
│ 5. Generate Serenity Reports                    │
├─────────────────────────────────────────────────┤
│ 6. Upload Artifacts                             │
│    ├─ Serenity reports (HTML)                   │
│    ├─ Test results (XML)                        │
│    ├─ Screenshots (PNG)                         │
│    └─ Test logs                                 │
└─────────────────────────────────────────────────┘
```

### Branch Protection Setup

Configure in repository settings → Branches:

#### Main Branch (`main`)
```
✅ Require pull request reviews
✅ Require status checks to pass
   - All 3 browser tests must pass
✅ Require branches up to date
✅ Dismiss stale approvals
```

#### Develop Branch (`develop`)
```
✅ Require pull request reviews
✅ Require status checks to pass
✅ Allow optional force pushes
```

### GitHub Flow Workflow

```
1. Create feature branch
   git checkout -b feature/xyz

2. Make changes and commit
   git add .
   git commit -m "feat: xyz"

3. Push to GitHub
   git push origin feature/xyz

4. Create Pull Request
   - CI/CD pipeline runs automatically
   - 3 browsers execute tests in parallel
   - Reports generated and uploaded

5. Review and Approve
   - Check Serenity reports
   - Review screenshots
   - Approve changes

6. Merge to Develop
   - All checks must pass
   - Feature branch auto-deleted

7. Create Release PR
   - From develop → main
   - Final verification
   - Merge triggers release
```

### Pull Request Checklist

When creating a PR, ensure:

```markdown
## Description
Brief summary of changes

## Testing
- [ ] Tested on Firefox
- [ ] Tested on Chrome
- [ ] Tested on Edge

## Verification
- [ ] All E2E tests pass
- [ ] No new screenshots show errors
- [ ] Report generation successful
```

### Viewing Test Results

After PR checks complete:

1. **In GitHub**
   - Click "Details" on status check
   - View workflow summary

2. **Download Artifacts**
   - Go to Actions tab
   - Click completed workflow
   - Download Serenity reports

3. **View Report Locally**
   ```powershell
   # After downloading serenity-report-firefox.zip
   Expand-Archive serenity-report-firefox.zip
   Start-Process serenity-report-firefox\index.html
   ```

### Manual Workflow Trigger

Run tests manually for specific browser:

```powershell
# Via GitHub CLI
gh workflow run e2e-tests.yml -f browser=chrome

# Or use GitHub Actions UI
# Go to Actions → E2E Tests → Run workflow
```

### Status Badge

Add to README:

```markdown
[![E2E Tests](https://github.com/USER/REPO/actions/workflows/e2e-tests.yml/badge.svg?branch=main)](https://github.com/USER/REPO/actions)
```

### CI/CD Artifacts Retention

Reports and artifacts retained for:
- **Serenity Reports:** 30 days
- **Test Results:** 30 days
- **Screenshots:** 30 days
- **Logs:** 30 days

---

## 13. Summary

✅ **Cross-Browser Testing** — Chrome, Firefox, Edge
✅ **Data-Driven Testing** — Excel sheets with test data
✅ **Enhanced Screenshots** — Visual evidence on every step (success + failure)
✅ **3-Level Driver Strategy** — Autodownload → System PATH → Bundled
✅ **Rich Reporting** — Serenity HTML with dashboard, charts, and traceability
✅ **GitHub Flow Integration** — Automated CI/CD with GitHub Actions
✅ **Production Ready** — CI/CD enabled with artifact uploads

### Report Contents

- 📊 Dashboard with statistics and charts
- 📸 Screenshot gallery for every step
- 📋 Feature-to-test traceability
- 🏷️ Tag-based test breakdown
- ⏱️ Execution timeline and metrics
- 📁 Organized test artifacts

---

**Status:** Production Ready ✅
**Last Updated:** 20 Aug 2026
**Java:** JDK 21
**Screenshots:** Enabled (FOR_EACH_ACTION) with compression
**Reports:** Enhanced with dashboards and evidence
**CI/CD:** GitHub Actions integrated

---

## 14. Where to Find Screenshot Evidence

### After Running Tests

```
target/site/serenity/
├── index.html                    ← Main report (open in browser) ⭐
├── screenshots/
│   ├── screenshot-001.png        ← Screenshot of step 1
│   ├── screenshot-002.png        ← Screenshot of step 2
│   ├── screenshot-003.png        ← Screenshot of step 3
│   └── ... (more screenshots)
├── css/
├── js/
├── requirements.html             ← Feature requirements mapping
├── tagReport.html               ← Test tags breakdown
└── screenshots.json              ← Metadata about screenshots
```

### How to View Enhanced Evidence

**Option 1: Open Main Report (Recommended) ⭐**
```powershell
Start-Process "target\site\serenity\index.html"
```
**Features:**
- Dashboard with test statistics
- Pass/Fail charts and percentages
- Screenshots embedded for each step
- Timeline showing test execution
- Feature requirements traceability
- Link to detailed breakdown

**Option 2: View Individual Screenshots**
```powershell
Get-ChildItem "target\site\serenity\screenshots" -Filter "*.png" |
  ForEach-Object { Start-Process $_.FullName }
```

**Option 3: Automated Report Opening**
```powershell
.\scripts\run-and-report.ps1 -Browser firefox
# Automatically opens report after tests complete
```

**Option 4: View Requirements Mapping**
```powershell
Start-Process "target\site\serenity\requirements.html"
# Shows how tests map to features
```

### Enhanced Report Contents

The Serenity report now includes:

✅ **Dashboard**
- Total tests executed
- Pass/Fail rate (%)
- Duration summary
- Charts and statistics

✅ **Detailed Steps**
- Step name and description
- Before/After screenshots
- Execution time per step
- Pass/Fail indicator

✅ **Screenshots Gallery**
- All screenshots organized
- Click to view full size
- Linked to specific steps

✅ **Feature Mapping**
- Requirements traceability
- Feature file reference
- Scenario status
- Test coverage

✅ **Test Execution Timeline**
- When each test ran
- Duration breakdown
- Browser/OS information
- Execution history

### Report Configuration

**Enhanced Settings** (in `serenity.properties`):
```ini
# Screenshots with compression
serenity.take.screenshots=FOR_EACH_ACTION
serenity.screenshots.take.failure=true
serenity.screenshots.take.success=true
serenity.compress.screenshots=true

# Report details
serenity.report.should.include.source=true
serenity.consistency.level=RELAXED
serenity.report.encoding=UTF-8

# Display settings
serenity.display.width=1920
serenity.display.height=1080

# Context
serenity.milestone=Demoblaze E2E
```

### Exporting Report Evidence

**Option A: Zip Entire Report**
```powershell
Compress-Archive -Path "target\site\serenity\" -DestinationPath "serenity-report.zip"
```

**Option B: Copy to Shared Location**
```powershell
Copy-Item "target\site\serenity\" -Destination "\\shared\reports\$(Get-Date -Format 'yyyy-MM-dd')" -Recurse
```

**Option C: Upload to Artifact Storage**
```yaml
# GitHub Actions
- uses: actions/upload-artifact@v3
  if: always()
  with:
    name: serenity-report-${{ matrix.browser }}
    path: target/site/serenity/
    retention-days: 30
```

---

## 15. Troubleshooting Reports — Report Not Updated

### Problem: Old Report Still Shows

If the report shows old test results or screenshots, try these solutions:

**Solution 1: Clean and Rebuild (Recommended)**
```powershell
# Full clean — removes all cached reports
.\gradlew.bat clean test -Pbrowser=firefox

# This automatically:
# - Deletes old reports from target/ and build/
# - Cleans cached screenshots
# - Regenerates everything fresh
# - Opens new report when done
Start-Process "target\site\serenity\index.html"
```

**Solution 2: Manual Clean**
```powershell
# Delete old reports manually
Remove-Item -Recurse -Force "target\site\serenity"
Remove-Item -Recurse -Force "build\reports\tests"
Remove-Item -Recurse -Force "build\cucumber-reports"

# Then run tests
.\gradlew.bat test -Pbrowser=firefox
```

**Solution 3: Hard Reset (Nuclear Option)**
```powershell
# Complete Gradle cache clean
.\gradlew.bat clean

# Run fresh
.\gradlew.bat test -Pbrowser=firefox
```

### Why Reports Don't Update

| Cause | Symptom | Fix |
|-------|---------|-----|
| Gradle cache | Old dates in report | `.\gradlew.bat clean test` |
| Screenshots not regenerated | Missing recent screenshots | Check `serenity.take.screenshots=FOR_EACH_ACTION` |
| Browser crashed | Incomplete report | Check logs, retry test |
| Previous run still running | Port locked | `taskkill /F /IM java.exe` |
| Permissions issue | Report not written | Run PowerShell as Admin |

### Verify Fresh Report

After running tests, confirm the report is fresh:

```powershell
# Check report timestamp (should be recent)
(Get-Item "target\site\serenity\index.html").LastWriteTime

# Expected: Current date/time

# Check screenshot count
(Get-ChildItem "target\site\serenity\screenshots" -Filter "*.png").Count

# Expected: Should match number of test steps
```

### Report Generation Flow

```
1. .\gradlew.bat test -Pbrowser=firefox
              ↓
2. cleanReports task → Deletes old reports
              ↓
3. prepareExternalDataFeatures → Loads Excel data
              ↓
4. test task → Executes all scenarios
              ↓
5. Screenshots captured → FOR_EACH_ACTION
              ↓
6. restoreExternalDataFeatures → Cleanup
              ↓
7. aggregate → Generates fresh HTML report
              ↓
8. target/site/serenity/index.html ← FRESH REPORT ✅
```

### Best Practices

✅ Always use `clean test` (not just `test`)
✅ Check browser console for JavaScript errors
✅ Verify internet connection for downloads
✅ Use PowerShell Run as Administrator if permission issues
✅ Close browser before running new tests
