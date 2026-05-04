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

---

## 7. Browser drivers

All drivers are bundled under `drivers/`. Gradle picks them up automatically.

| Browser | Driver | Path |
|---------|--------|------|
| Edge    | msedgedriver 147 | `drivers/edge/147/msedgedriver.exe` |
| Chrome  | chromedriver 147 | `drivers/chrome/147/extracted/chromedriver.exe` |
| Firefox | geckodriver 150  | `drivers/firefox/150/extracted/geckodriver.exe` |

---

## 8. Prerequisites

- **JDK 21** installed and available on `PATH`
- Gradle does **not** need to be installed — the project uses `gradlew.bat`
