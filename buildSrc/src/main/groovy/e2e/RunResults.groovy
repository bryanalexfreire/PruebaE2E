package e2e

/**
 * Turns the raw results of a run (cucumber.json and the Serenity outcome files) into the
 * scenario list and the Markdown summary published by {@code gradlew publishReport}.
 * Pure functions over parsed JSON: no file access, so every rule is unit tested.
 */
class RunResults {

    static final List<String> SERENITY_FAILURES = ['FAILURE', 'ERROR', 'COMPROMISED']

    /** One map per scenario run (an outline example is one run): status, failed step, error, data. */
    static List<Map> scenarios(cucumberJson) {
        def scenarios = []
        cucumberJson.each { feature ->
            (feature.elements ?: []).findAll { it.type == 'scenario' }.each { sc ->
                def steps = sc.steps ?: []
                def all = (sc.before ?: []) + steps + (sc.after ?: [])
                def statuses = all.collect { it.result?.status }
                def failed = all.find { it.result?.status == 'failed' }
                def tags = (sc.tags ?: []).collect { it.name }
                long nanos = (steps.sum { (it.result?.duration ?: 0) as long } ?: 0) as long
                scenarios << [
                        name      : sc.name,
                        line      : sc.line,
                        tags      : tags,
                        knownBug  : tags.contains('@bug'),
                        status    : status(statuses),
                        seconds   : String.format(Locale.ROOT, '%.1f', nanos / 1_000_000_000.0),
                        failedStep: failed ? (failed.keyword ? "${failed.keyword.trim()} ${failed.name}".toString() : 'hook') : null,
                        error     : readableError(failed?.result?.error_message),
                        data      : textAttachments(all),
                ]
            }
        }
        scenarios
    }

    static String status(List statuses) {
        if (statuses.contains('failed')) { return 'failed' }
        if (statuses.any { it in ['undefined', 'pending'] }) { return 'undefined' }
        statuses.contains('skipped') ? 'skipped' : 'passed'
    }

    /** First lines of an error without the stack trace: {@code AssertionError: Expected: "x" but: was "y"}. */
    static String readableError(String message) {
        (message ?: '').readLines()*.trim().findAll { it && !it.startsWith('at ') }.take(3).join(' ')
    }

    /** Text attachments of the steps and their step hooks, in order, as [title, text]. */
    static List<List<String>> textAttachments(List steps) {
        steps.collectMany { st -> (st.before ?: []) + [st] + (st.after ?: []) }
                .collectMany { it.embeddings ?: [] }
                .findAll { it.mime_type == 'text/plain' }
                .collect { [(it.name ?: 'Data') as String, new String(it.data.toString().decodeBase64(), 'UTF-8').trim()] }
    }

    /**
     * Adds to each failed scenario the Serenity evidence of its failing step (screenshot, page HTML,
     * scenario page). Outcomes are matched by scenario name and, for outlines, by the order of the
     * failed examples.
     *
     * @param outcomes Serenity outcome files as [page: '&lt;id&gt;.html', json: parsed outcome], sorted by file name
     */
    static void attachSerenityEvidence(List<Map> scenarios, List<Map> outcomes) {
        Map<String, List<Map>> failuresByTitle = [:].withDefault { [] }
        outcomes.each { file ->
            def outcome = file.json
            if (!(outcome instanceof Map) || !outcome.testSteps) { return }
            def examples = outcome.dataTable != null ? outcome.testSteps : [[result: outcome.result, children: outcome.testSteps]]
            examples.findAll { it.result in SERENITY_FAILURES }.each { example ->
                def evidence = failingStepEvidence(example.children as List)
                evidence.page = file.page
                failuresByTitle[outcome.title.toString()] << evidence
            }
        }
        scenarios.findAll { it.status == 'failed' }.each { sc ->
            def candidates = failuresByTitle[sc.name.toString()]
            if (candidates) { sc.serenity = candidates.remove(0) }
        }
    }

    /** Screenshot and page HTML of the failing step (or the last ones taken before it). */
    static Map failingStepEvidence(List steps) {
        def evidence = [screenshot: null, html: null]
        Closure<Boolean> visit
        visit = { List level ->
            for (step in (level ?: [])) {
                (step.screenshots ?: []).each { shot ->
                    evidence.screenshot = shot.screenshot
                    if (shot.htmlSource) { evidence.html = shot.htmlSource }
                }
                if (visit(step.children as List)) { return true }
                if (step.result in SERENITY_FAILURES && step.exception != null) { return true }
            }
            false
        }
        visit(steps)
        evidence
    }

    /** Counts and verdict: a run fails only on unexpected failures (known bugs are tagged @bug). */
    static Map verdict(List<Map> scenarios) {
        def v = [
                passed    : scenarios.count { it.status == 'passed' },
                failed    : scenarios.count { it.status == 'failed' },
                knownBugs : scenarios.count { it.status == 'failed' && it.knownBug },
                unexpected: scenarios.count { it.status in ['failed', 'undefined'] && !it.knownBug },
                undefined : scenarios.count { it.status == 'undefined' },
                skipped   : scenarios.count { it.status == 'skipped' },
        ]
        v.verdict = v.unexpected ? 'FAILED' : 'PASSED'
        v
    }

    static List<String> summary(String runName, String browser, List<Map> scenarios, Map r) {
        def icon = [passed: '✅', failed: '❌', undefined: '❓', skipped: '⏭️']
        def lines = []
        lines << "# Test run ${runName}" << ''
        lines << "**Result:** ${r.verdict} · Browser ${browser} · Passed ${r.passed} · Failed ${r.failed} (known bugs ${r.knownBugs}) · Undefined ${r.undefined} · Skipped ${r.skipped}" << ''
        lines << 'Evidence (steps, a screenshot after each step, data recorded): [serenity/index.html](serenity/index.html)' << ''
        lines << '## Results' << '| Status | Scenario | Tags | Duration |' << '|---|---|---|---|'
        scenarios.each { sc ->
            def mark = sc.status == 'failed' && sc.knownBug ? '🐞' : icon[sc.status]
            lines << "| ${mark} ${sc.status} | ${sc.name} (line ${sc.line}) | ${sc.tags.join(' ')} | ${sc.seconds} s |"
        }
        lines << ''
        def failures = scenarios.findAll { it.status in ['failed', 'undefined'] }
        if (failures) {
            lines << '## Failures' << ''
            failures.each { sc -> lines.addAll(failure(sc)) }
        }
        lines*.toString()
    }

    static List<String> failure(Map sc) {
        def lines = []
        def kind = sc.knownBug ? '🐞 Known bug (fails until fixed)' : '❌ Unexpected failure'
        lines << "### ${kind}: ${sc.name} (line ${sc.line})" << ''
        lines << "- **Failed step:** ${sc.failedStep ?: sc.status}"
        if (sc.error) { lines << "- **Error:** `${sc.error.replace('`', '')}`" }
        if (sc.serenity) {
            lines << "- **Evidence:** [scenario in the Serenity report](serenity/${sc.serenity.page})" +
                    (sc.serenity.html ? " · [page HTML at the failure](serenity/${sc.serenity.html})" : '')
        }
        if (sc.data) {
            lines << '- **Data recorded:**' << '' << '```text'
            sc.data.each { title, text -> lines << "${title}:" << text << '' }
            lines << '```'
        }
        if (sc.serenity?.screenshot) { lines << '' << "![Screenshot at the failure](serenity/${sc.serenity.screenshot})" }
        lines << ''
        lines*.toString()
    }

    /** reports/index.md: header plus the new row on top of the previous rows (one row per run). */
    static String index(String runName, Map r, List<String> previousLines) {
        def header = "# Test reports\n\nNewest first. Each run keeps the Serenity report (evidence), the summary and the raw results (cucumber.json, JUnit).\n\n" +
                "| Run | Result | Passed | Known bugs | Unexpected | Summary |\n|---|---|---|---|---|---|\n"
        def previous = previousLines.findAll { it.startsWith('| [') && !it.contains("[${runName}]") }
        def row = "| [${runName}](${runName}/serenity/index.html) | ${r.verdict} | ${r.passed} | ${r.knownBugs} | ${r.unexpected} | [summary](${runName}/summary.md) |"
        header + ([row] + previous).join('\n') + '\n'
    }
}
