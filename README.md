# PruebaE2E — E2E Automation for Demoblaze

End-to-end functional automation of the purchase flow on [https://www.demoblaze.com](https://www.demoblaze.com) with **Serenity BDD + Screenplay + Cucumber + JUnit 4**:
sign in → add two products → review the cart (products and total) → fill in the purchase form → confirm the order → sign out.

## 1. Technologies and versions

| Technology | Version |
|---|---|
| Java | JDK 21 |
| Gradle Wrapper | 8.7 |
| Serenity BDD (core, screenplay, cucumber, junit) | 4.1.14 |
| JUnit | 4.13.2 |
| Apache POI (Excel data) | 5.2.5 |
| SLF4J + Log4j2 | 2.0.7 / 2.20.0 |
| Browsers | Chrome · Firefox · Edge (driver resolved before each run, section 6) |

## 2. Screenplay pattern

```text
Actor ── attemptsTo(Task) ── Interaction(s) ── Target (locator)
      └─ should(seeThat(Question, matcher))
```

| Layer | Package | Examples |
|---|---|---|
| Targets | `ui/` | `PageMain`, `PageProducts`, `PageCar` |
| Tasks | `tasks/` | `OpenWeb`, `MakeLogin`, `EmptyCart`, `FindProduct`, `ViewCart`, `RegisterCustomer`, `CloseSession` |
| Interactions | `tasks/` | `ClickWhenReady` (waits, never clicks by JavaScript), `EnterSecret` (types a password without showing it), `WaitForModal`, `AcceptBrowserAlert` |
| Questions | `questions/` | `CartPrices` (count, sum), `CartTotal`, `SessionState` (welcome, signed out), `PurchaseReceipt` (receipt text and fields) |
| Models | `model/` | `Credentials` (password masked in `toString`), `Customer` (records) |
| Step definitions | `stepdefinition/` | `DemoblazeGlue` (thin: Tasks + Questions), `ActorHooks`, `EvidenceHooks`, `LoggingHooks` |
| Support | `util/` | `config/TestAccount` (credentials), `data/` (Excel → generated features), `evidence/`, `Timeouts` |

## 3. Project structure

```text
PruebaE2E/
├── AGENTS.md · .agents/ · link-agents.bat   testing agents and skills (see section 8)
├── qa/                                       requirements, plan, flow diagrams, sessions
├── reports/                                  published runs: Serenity report + summary (index, LATEST, one folder per run)
├── src/main/java/com/screenplay/project/     ui · tasks · questions · model · util (config, data, evidence, logger, timeouts)
├── src/test/java/com/screenplay/project/     runners/RunnerTest · stepdefinition/ · unit/ (unit tests, no browser)
├── buildSrc/                                 report logic (RunResults) with its unit tests
├── config/                                   Checkstyle and SpotBugs rules
├── src/test/resources/
│   ├── features/04-Full-Flow/onlineShop.feature
│   ├── data/purchase/dataDemoblaze.xlsx      external test data (DDT)
│   └── serenity.conf
├── scripts/                                  PowerShell helpers (run by browser, all browsers, suite by tags, drivers)
├── build.gradle · gradle/reports.gradle · serenity.properties
├── test-accounts.example.properties          template of the local, git-ignored test account file
└── .github/                                  CI (Git flow) and PR template
```

## 4. Data-driven testing from Excel

The scenario's `Examples` block holds `| @externaldata@src/test/resources/data/purchase/dataDemoblaze.xlsx..purchase |`. Around `gradlew test`:

| Step | Task | What it does |
|---|---|---|
| 1 | `generateFeatures` | copies the features to `build/generated/features`, writing the Excel rows into `Examples` |
| 2 | `test` | runs the generated features (one scenario per row) |
| 3 | `aggregate` | builds the Serenity HTML report |

The source features are never modified, so an interrupted run cannot leave them changed. An empty sheet or a missing file stops the build with a clear message instead of running no examples. The Excel holds customer data only (fictitious); no credentials.

## 5. Test account

Credentials never live in features, test data or the repository. `TestAccount` reads them from:

1. `DEMOBLAZE_USERNAME_<BROWSER>` / `DEMOBLAZE_PASSWORD_<BROWSER>` (one account per browser, e.g. `_CHROME`);
2. `DEMOBLAZE_USERNAME` / `DEMOBLAZE_PASSWORD`;
3. the local file `test-accounts.properties` (git-ignored): copy `test-accounts.example.properties` and fill it in.

In CI they are repository secrets with the same names. The password is typed with `EnterSecret`, so it never appears in the reports.

## 6. Running the tests

```bash
gradlew test -Pbrowser=chrome                      # chrome | firefox | edge (default firefox)
gradlew test -Pbrowser=chrome -Pheadless=true      # headless (as in CI)
gradlew test -Ptags="@TC-03"                       # filter by tag (default: not @wip and not @bug)
gradlew test -Ptags="@bug"                         # check whether the known defects are fixed
gradlew test -PignoreFailures=true                 # keep going to the report even if a scenario fails
```

### Browser drivers

Before every run, `resolveDriver` chooses the driver for `-Pbrowser` and passes it to Serenity:

| Order | Source | When |
|---|---|---|
| 1 | **Download** with Selenium Manager (cached in `~/.cache/selenium`) | normal case; it must match the installed browser |
| 2 | **Bundled** driver from `drivers/<browser>/<version>/` (optional folder) | the download fails (no Internet, blocked network) or returns another version |
| 3 | **Clear error** | nothing matches: it says the installed version, what was tried and how to fix it |

Chrome and Edge drivers must have the same major version as the browser; a driver of another version is never used (it would fail later with *session not created*). geckodriver supports a range of Firefox versions.

```bash
gradlew resolveDriver -Pbrowser=edge               # only show which driver would be used
gradlew test -Pbrowser=chrome -PdriverDownload=off # offline: bundled drivers only
gradlew refreshBundledDrivers -Pbrowser=chrome     # store the current driver in drivers/ as fallback
```

The `drivers/` folder is optional and not in the repository: the download covers the normal case. Create it only for offline runs with `gradlew refreshBundledDrivers -Pbrowser=<browser>` (drivers are per operating system and must match the installed browser version). Edge: Selenium 4.21 still points to Microsoft's retired driver site, so the resolver passes the current one (`msedgedriver.microsoft.com`) as mirror.

From the IDE, run tests with Gradle (IntelliJ: *Settings → Build Tools → Gradle → Run tests using: Gradle*), so the Excel data and the Serenity report are produced.

PowerShell helpers: `scripts\run-browser-with-drivers.ps1 -Browser edge [-Offline] [-ValidateOnly] [-ShowReport]`, `scripts\run-browser.ps1 -browser chrome`, `scripts\run-and-report.ps1`, `scripts\run-all-browsers.ps1`, `scripts\run-suite.ps1 -Tags "@critical"`.

## 7. Reports and evidence

Each report has one role, so nothing is duplicated:

| Report | Where | Role |
|---|---|---|
| **Serenity** (evidence) | `target/site/serenity/index.html` | Steps and Screenplay actions, a **full-resolution screenshot after each step** (1920x1080 window), the data recorded and the **page HTML at a failure** |
| **Cucumber** (results) | `build/cucumber-reports/html/cucumber.html`, `cucumber.json` | Results and the data as text, no screenshots. Written by the runner, so it is current also when you run `RunnerTest` from the IDE |
| **JUnit** | `build/test-results/test/*.xml` | CI checks |

- Steps record the **real values the app showed** once (`Evidence.record/table`) and both reports receive them: alert texts, the cart rows with the sum of prices and the total shown, the purchase receipt, the receipt date (expected vs shown).
- When a step fails, `EvidenceHooks` adds the page URL and title, or the reason the browser could not be read (for example a lost driver session).
- The Serenity HTML is built by `aggregate`, which `gradlew test` runs at the end. After a run from the IDE, run `gradlew aggregate` (or set the IDE to run tests with Gradle); `cleanReports` empties all reports before each Gradle run.
- Every step checks a real result with a named check: welcome message after sign-in, two products in the cart, **total = sum of the product prices**, customer name on the receipt, "Log in" visible after sign-out.
- Before adding products, the test empties the cart left by earlier sessions (Demoblaze keeps the cart of an account), so the count and total are reliable.
- `@bug @BUG-XXX` scenarios document known defects (TC-06: the receipt shows the previous month). They are **not part of the regular run** (default tags `not @wip and not @bug`), so the suite stays green with what the flow requires. Check whether a defect is fixed with `gradlew test -Ptags="@bug"`: it fails until the fix and is reported as a known bug, not a regression. When it passes, remove the `@bug` tag so it joins the regression suite.
- Publish a run so it can be committed:
  ```bash
  gradlew publishReport -Plabel=<name>
  ```
  → `reports/<date>-<label>/` with the Serenity report (`serenity/`), `summary.md`, `cucumber.json` and JUnit XML, plus `reports/index.md` and `reports/LATEST.md` (logic in `gradle/reports.gradle`).
  For each failure, `summary.md` shows the failed step, the error (expected vs actual), the data recorded, the screenshot at the failure and links to the scenario and the page HTML in the Serenity report.

## 8. Reliability and code quality

| Practice | How |
|---|---|
| Explicit waits only | implicit wait 0; every wait names its condition (`Timeouts.UI` 10 s for page reactions, `Timeouts.SERVER` 20 s for server results) |
| Real user clicks | `ClickWhenReady` waits until clickable and retries only covered/re-rendered elements; no JavaScript click that would hide a real problem |
| No animation races | `WaitForModal` waits until the Bootstrap modals finish opening before typing |
| Independent scenarios | new browser per scenario; the cart left by earlier sessions is emptied first; one account per browser supported |
| Named assertions | `seeThat("Cart total equals the sum of the prices", ...)`: a failure says what was checked, expected and actual |
| Known bugs apart | `@bug @BUG-XXX` scenarios document the defect; excluded from the regular run, run on request (`-Ptags="@bug"`), never asserting the wrong behaviour |
| Secrets | from environment or a git-ignored file; masked in reports and logs |
| Unit tests | `gradlew unitTest` (data generation, Excel reader, receipt parsing, account resolution) and `gradlew -p buildSrc test` (report summary) |
| Static analysis | `gradlew checkstyleMain checkstyleTest spotbugsMain` (rules in `config/`), zero findings allowed |

## 9. CI and Git flow

- Branches: `main` (releases, tagged), `develop` (integration), `feature/*`, `release/*`, `hotfix/*` — see [CONTRIBUTING.md](CONTRIBUTING.md).
- **Every run:** unit tests, Checkstyle and SpotBugs first; the E2E runs only if they pass.
- **Pull requests:** E2E on Chrome (headless). **Push to develop / release / hotfix:** Chrome, Firefox and Edge, one after another (they share the test account; set per-browser secrets to run them in parallel).
- Secrets: `DEMOBLAZE_USERNAME`, `DEMOBLAZE_PASSWORD` (optional per browser: `..._CHROME`, `..._FIREFOX`, `..._EDGE`).
- Each run: summary on the job page, JUnit results in the checks tab, Serenity report (evidence) and raw results as an artifact. Known defects (`@bug`) are excluded by default; run them with *Run workflow* and tags `@bug` (reported as known bugs, they do not fail the build).
- Manual run with custom tags: *Actions → CI → Run workflow*.

## 10. Testing agents, plan and flow diagrams

- `qa/requirements/` (PUR-01..06), `qa/plans/demoblaze-purchase.md` (test conditions) and `qa/flows/purchase/` (Mermaid diagram per test case, plus the journey). Scenario tags `@TC-XX` link each result to its diagram.
- `AGENTS.md` and `.agents/` hold vendor-neutral agents (test-planner, charter-explorer, bug-verifier, test-generator, test-healer, report-reviewer) and skills (risk analysis, exploratory testing, bug report, Serenity automation, summary report). Connect them to your agent client once per machine:
  ```bat
  link-agents.bat <client-folder> <client-rules-file>
  ```
  It creates local links and excludes them from Git (`.git/info/exclude`).

## 11. Prerequisites

- JDK 21 on `PATH` (`java -version`).
- Chrome, Firefox or Edge installed; the driver is downloaded or taken from `drivers/` (section 6).
- Internet access to https://www.demoblaze.com.
- A Demoblaze test account (section 5).
