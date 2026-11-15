---
name: test-automation
description: Conventions for turning test conditions and confirmed bugs into Serenity BDD tests with the Screenplay pattern, Cucumber features, data-driven examples, tags and evidence, and for running, reporting and repairing them. Use when writing, reviewing or fixing a scenario, or when test-generator / test-healer work. Not for exploratory sessions.
metadata:
  version: 1.0.0
---

# Test automation (Serenity BDD + Screenplay + Cucumber)

## Critical
- One test condition → one scenario (or one Examples row). Assert only the expected result; never weaken an assertion.
- Every scenario carries its requirement and test-case tags (`@REQ-ID @TC-XX`) so reports trace back to `qa/plans/` and `qa/flows/`.
- No `Thread.sleep`, no implicit waits, no JavaScript clicks, no absolute XPath, no secrets in code, features or data.
- A scenario is done when it passes several runs in a row on at least two browsers and the unit tests and static analysis pass.

## Project layout
```
src/main/java/com/screenplay/project/
  ui/             Targets, one class per screen (PageMain, PageProducts, PageCar…)
  tasks/          Tasks (business actions) and Interactions (low-level browser actions)
  questions/      Questions: values read from the app for assertions
  model/          Records (Customer, Credentials with masked password)
  util/           Timeouts, config/TestAccount (credentials from env or git-ignored file),
                  data/ (Excel → generated features), evidence/, logger
src/test/java/com/screenplay/project/
  runners/        RunnerTest (tags from -Ptags / CUCUMBER_TAGS)
  stepdefinition/ thin glue + hooks (stage, evidence, logging)
  unit/           unit tests of the support code (no browser): gradlew unitTest
src/test/resources/
  features/<NN>-<Area>/   Gherkin features
  data/                   external test data (Excel)
  serenity.conf           environments, driver options
qa/                       requirements, plans per screen, flow diagrams, sessions
reports/                  published runs: Serenity report + summary (gradlew publishReport)
```

## Screenplay rules
- **Target**: `Target.the("<business name>").located(By.id(...))`; prefer id, name, visible text; one place per locator.
- **Task**: business verb (`MakeLogin`, `FindProduct`, `RegisterCustomer`), static factory, `@Step("{0} …")` for readable reports.
- **Interaction**: browser mechanics reused by Tasks: `ClickWhenReady` (waits until clickable, retries only covered or re-rendered elements, never clicks by JavaScript), `EnterSecret` (passwords, not shown in reports), `WaitForModal` (Bootstrap animations), `AcceptBrowserAlert` (records the alert text).
- **Question**: everything the scenario checks; assert in the step with `theActorInTheSpotlight().should(seeThat(question, matcher))`.
- **Step definitions**: one line per step when possible; no locators, no logic. Waits use `Timeouts.UI` (page reaction) or `Timeouts.SERVER` (server result) and always name the condition.
- **Assertions**: named checks, `seeThat("Cart total equals the sum of the prices", question, matcher)`.
- **Independence**: each scenario sets its own preconditions (for example `EmptyCart`); never rely on the order of scenarios.

## Features and data-driven tests
- Business language; Given (context) · When (action) · Then (expected result).
- Data-driven: `Scenario Outline` + `Examples`. External data: `| @externaldata@src/test/resources/data/<file>.xlsx..<sheet> |`; `generateFeatures` writes the rows into a generated copy (`build/generated/features`) before `gradlew test`; the source features are never modified. Test data holds fictitious customer data only; the test account comes from `TestAccount`.
- Tags: `@REQ-ID @TC-XX`, priority (`@smoke`, `@regression`, `@critical`), type (`@positive`, `@negative`), `@bug @BUG-XXX` for known defects (excluded from the regular run, run with `-Ptags="@bug"`; assert the correct behaviour, never the buggy one), `@wip` for work in progress (excluded in CI).

## Evidence and reports
- Reports have one role each: Serenity is the evidence (screenshot after each step, page HTML at failures; `aggregate` runs after `test`), Cucumber holds results and data as text, JUnit feeds CI. Record the real values read from the app once with `Evidence.record/table` (both reports receive them; never credentials). Do not add screenshots to the Cucumber report.
- After a run worth keeping: `gradlew publishReport -Plabel=<name>` → `reports/<date>-<label>/` (Serenity report, `summary.md`, cucumber.json, JUnit XML), `reports/LATEST.md` and `reports/index.md`. Commit it when it backs a delivery or a release.
- CI publishes the same summary on the job page, the JUnit results in the checks tab and the Serenity report as an artifact; known defects (`@bug`) are excluded by default and run on request; when run they are listed as known bugs and do not fail the build.

## Running
- One scenario while writing: `gradlew test -Ptags="@TC-XX" -Pbrowser=chrome -Pheadless=true`.
- Suite: `gradlew test -Pbrowser=chrome|firefox|edge [-Pheadless=true] [-Ptags="@smoke"]`.
- Windows scripts: `scripts/run-browser.ps1`, `scripts/run-all-browsers.ps1`, `scripts/run-and-report.ps1`.

## Triage of a failing scenario (test-healer)
Script broken → fix the Target/Task · Product defect → candidate for `bug-verifier`, keep the assertion · Environment → report · Flaky → find the cause, no blind retries.

## If something fails
- Driver problems: `gradlew resolveDriver -Pbrowser=<b>` shows the chosen driver and why. Download first, then a matching bundled driver in `drivers/<browser>/<version>/`, otherwise a clear error. Refresh the fallback with `gradlew refreshBundledDrivers -Pbrowser=<b>`; offline runs: `-PdriverDownload=off`.
- "No test account configured": set `DEMOBLAZE_USERNAME` / `DEMOBLAZE_PASSWORD` or create `test-accounts.properties` from the example file.
- "No data rows in ...": the Excel sheet is empty or the descriptor points to the wrong sheet.
