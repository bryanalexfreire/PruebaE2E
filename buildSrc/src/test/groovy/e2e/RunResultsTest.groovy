package e2e

import groovy.json.JsonSlurper
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

class RunResultsTest {

    private static String b64(String text) { text.bytes.encodeBase64().toString() }

    private static Object cucumber(String json) { new JsonSlurper().parseText(json) }

    private static final String RUN = """[{"elements": [
      {"type": "scenario", "name": "Buy", "line": 15, "tags": [{"name": "@TC-01"}],
       "steps": [
         {"keyword": "Given ", "name": "opens the store", "result": {"status": "passed", "duration": 1500000000}},
         {"keyword": "Then ", "name": "the cart lists both products", "result": {"status": "failed", "duration": 500000000,
          "error_message": "java.lang.AssertionError: \\nExpected: <2>\\n     but: was <3>\\n\\tat x.y(Z.java:1)"},
          "embeddings": [{"mime_type": "text/plain", "name": "Cart contents", "data": "${b64('A | 360')}"},
                         {"mime_type": "image/png", "name": "ignored", "data": "AAAA"}],
          "after": [{"embeddings": [{"mime_type": "text/plain", "name": "Page at the failure", "data": "${b64('https://x/cart')}"}]}]}
       ]},
      {"type": "scenario", "name": "Receipt date", "line": 30, "tags": [{"name": "@bug"}],
       "steps": [{"keyword": "Then ", "name": "shows today", "result": {"status": "failed", "error_message": "AssertionError: wrong date"}}]},
      {"type": "scenario", "name": "Sign out", "line": 40, "tags": [],
       "steps": [{"keyword": "Then ", "name": "signs out", "result": {"status": "passed"}}]},
      {"type": "background", "name": "ignored"}
    ]}]"""

    @Test
    void readsOneEntryPerScenarioWithItsStatus() {
        def scenarios = RunResults.scenarios(cucumber(RUN))
        assertEquals(['failed', 'failed', 'passed'], scenarios*.status)
        assertEquals('2.0', scenarios[0].seconds)
    }

    @Test
    void keepsTheFailedStepAndTheErrorWithoutStackTrace() {
        def first = RunResults.scenarios(cucumber(RUN))[0]
        assertEquals('Then the cart lists both products', first.failedStep)
        assertEquals('java.lang.AssertionError: Expected: <2> but: was <3>', first.error)
    }

    @Test
    void collectsTextAttachmentsOfStepsAndStepHooksOnly() {
        def first = RunResults.scenarios(cucumber(RUN))[0]
        assertEquals([['Cart contents', 'A | 360'], ['Page at the failure', 'https://x/cart']], first.data)
    }

    @Test
    void knownBugsDoNotFailTheRun() {
        def scenarios = RunResults.scenarios(cucumber(RUN))
        def verdict = RunResults.verdict(scenarios)
        assertEquals(1, verdict.knownBugs)
        assertEquals(1, verdict.unexpected)
        assertEquals('FAILED', verdict.verdict)
        assertEquals('PASSED', RunResults.verdict(scenarios.findAll { it.name != 'Buy' }).verdict)
    }

    @Test
    void undefinedStepsAreUnexpectedFailures() {
        assertEquals('undefined', RunResults.status(['passed', 'undefined', 'skipped']))
        assertEquals('failed', RunResults.status(['undefined', 'failed']))
        assertEquals('skipped', RunResults.status(['passed', 'skipped']))
    }

    @Test
    void takesTheScreenshotOfTheFailingStepNotALaterOne() {
        def steps = new JsonSlurper().parseText('''[
          {"result": "SUCCESS", "screenshots": [{"screenshot": "a.png"}]},
          {"result": "FAILURE", "exception": {}, "screenshots": [{"screenshot": "b.png", "htmlSource": "b.html"}]},
          {"result": "SKIPPED", "screenshots": [{"screenshot": "c.png"}]}]''')
        assertEquals([screenshot: 'b.png', html: 'b.html'], RunResults.failingStepEvidence(steps))
    }

    @Test
    void matchesSerenityOutcomesToFailedOutlineExamplesInOrder() {
        def scenarios = [[name: 'Buy', status: 'failed'], [name: 'Buy', status: 'passed'], [name: 'Buy', status: 'failed']]
        def outcome = new JsonSlurper().parseText('''{"title": "Buy", "dataTable": {}, "testSteps": [
          {"result": "FAILURE", "children": [{"result": "FAILURE", "exception": {}, "screenshots": [{"screenshot": "1.png"}]}]},
          {"result": "SUCCESS", "children": []},
          {"result": "FAILURE", "children": [{"result": "FAILURE", "exception": {}, "screenshots": [{"screenshot": "3.png"}]}]}]}''')
        RunResults.attachSerenityEvidence(scenarios, [[page: 'buy.html', json: outcome]])
        assertEquals('1.png', scenarios[0].serenity.screenshot)
        assertNull(scenarios[1].serenity)
        assertEquals('3.png', scenarios[2].serenity.screenshot)
        assertEquals('buy.html', scenarios[2].serenity.page)
    }

    @Test
    void summaryExplainsEachFailure() {
        def scenarios = RunResults.scenarios(cucumber(RUN))
        scenarios[0].serenity = [page: 'p.html', screenshot: 's.png', html: 'h.html']
        def text = RunResults.summary('run-1', 'chrome', scenarios, RunResults.verdict(scenarios)).join('\n')
        assertTrue(text.contains('**Result:** FAILED · Browser chrome'))
        assertTrue(text.contains('### ❌ Unexpected failure: Buy (line 15)'))
        assertTrue(text.contains('### 🐞 Known bug (fails until fixed): Receipt date (line 30)'))
        assertTrue(text.contains('[page HTML at the failure](serenity/h.html)'))
        assertTrue(text.contains('![Screenshot at the failure](serenity/s.png)'))
        assertTrue(!text.contains('### ❌ Unexpected failure: Sign out'))
    }

    @Test
    void indexKeepsOneRowPerRunNewestFirst() {
        def verdict = [verdict: 'PASSED', passed: 2, knownBugs: 1, unexpected: 0]
        def previous = ['# Test reports', '| [old](old/serenity/index.html) | PASSED |', '| [run-2](x) | FAILED |']
        def rows = RunResults.index('run-2', verdict, previous).readLines().findAll { it.startsWith('| [') }
        assertEquals(2, rows.size())
        assertTrue(rows[0].startsWith('| [run-2]') && rows[0].contains('PASSED'))
        assertTrue(rows[1].startsWith('| [old]'))
    }
}
