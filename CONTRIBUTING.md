# Contributing

## Branching model: Git flow

| Branch | Purpose | Created from | Merges into |
|---|---|---|---|
| `main` | Released, tagged versions only | — | — |
| `develop` | Integration of finished work | `main` (once) | `release/*` |
| `feature/<short-name>` | New scenarios, Screenplay code, data, docs | `develop` | `develop` (PR) |
| `release/<x.y.z>` | Stabilise a version: docs, version bump, fixes only | `develop` | `main` (PR + tag) and back into `develop` |
| `hotfix/<x.y.z>` | Urgent fix of a released version | `main` | `main` (PR + tag) and `develop` |

```bash
git checkout develop && git pull
git checkout -b feature/checkout-negative-cases
# … commits …
git push -u origin feature/checkout-negative-cases   # open a PR to develop
```

Rules: no direct pushes to `main` or `develop` (PRs with green CI); `release/*` and `hotfix/*` accept fixes only; delete feature branches after merge.

## Commits: Conventional Commits
`<type>(<scope>): <summary>` — `feat`, `fix`, `test`, `docs`, `refactor`, `ci`, `chore`.
Examples: `test(checkout): add missing-card scenario`, `fix(ui): stable locator for place order`.

## CI (GitHub Actions)
- Every run: unit tests (`unitTest`, `buildSrc`), Checkstyle and SpotBugs first; the E2E runs only if they pass.
- Pull requests: E2E on Chrome (headless).
- Push to `develop`, `release/*`, `hotfix/*`: E2E on Chrome, Firefox and Edge (one after another while they share the test account).
- Secrets: `DEMOBLAZE_USERNAME`, `DEMOBLAZE_PASSWORD` (optional per browser: `..._CHROME`, `..._FIREFOX`, `..._EDGE`).
- Each run writes a summary on the job page, JUnit results in the checks tab and uploads the Serenity report (a screenshot after each step) with the raw results as an artifact. Known defects (`@bug`) are excluded from the regular run; run them on request with tags `@bug` (reported as known bugs, they do not fail the build).

## Definition of done for a PR
- [ ] `gradlew unitTest checkstyleMain checkstyleTest spotbugsMain` passes (and `gradlew -p buildSrc test` if the report logic changed).
- [ ] The affected scenarios pass locally, several times in a row and on at least two browsers for new scenarios.
- [ ] Screenplay rules followed (Targets in `ui/`, Tasks, Questions for assertions, thin step definitions).
- [ ] Explicit waits only (`Timeouts`), clicks with `ClickWhenReady`, passwords with `EnterSecret`; no `Thread.sleep`, no JavaScript clicks.
- [ ] New support code has unit tests in `src/test/java/.../unit`.
- [ ] A known defect is a `@bug @BUG-XXX` scenario that asserts the correct behaviour (never the buggy one); remove `@bug` once it passes.
- [ ] Scenario tags `@REQ`/`@TC`, flow diagram in `qa/flows/`, plan updated in `qa/plans/`.
- [ ] No credentials or personal data committed.
