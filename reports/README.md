# reports/

Versioned test runs (Serenity report as evidence plus a Markdown summary), committed with the code so reviewers can open them without running anything.

```bash
gradlew test -Pbrowser=chrome -PignoreFailures=true
gradlew publishReport -Plabel=<name>
```

| Path | Content |
|---|---|
| `index.md` | History of runs, newest first |
| `LATEST.md` | Pointer to the newest run |
| `<yyyy-MM-dd_HHmm>-<label>/serenity/` | Serenity report: steps, a screenshot after each step, data recorded, page HTML at failures |
| `<…>/summary.md` | Verdict, results, known bugs (`@bug`) and failure details (step, error, data, screenshot, links into the Serenity report) |
| `<…>/cucumber.json`, `junit/` | Raw results for CI tools |

Scenarios carry `@TC-XX` tags, so each result points to its flow diagram in `qa/flows/`.
Keep only the runs that matter (deliveries, release candidates); delete old folders in a normal commit.
