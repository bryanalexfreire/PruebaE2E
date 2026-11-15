package com.screenplay.project.util.evidence;

import io.cucumber.java.Scenario;

import java.util.List;
import java.util.Objects;

/**
 * Single entry point for test evidence: steps record the real values read from the app
 * (alert texts, cart rows, totals, receipt) once, and every report receives them.
 * Screenshots and page HTML are taken by Serenity only (serenity.properties), so they
 * are never duplicated in the Cucumber report.
 *
 * <p>The hooks open the evidence for each scenario ({@link #start}) and close it ({@link #finish}).
 * Outside a scenario only the Serenity report receives the evidence.</p>
 */
public final class Evidence {

    private static final EvidenceSink SERENITY = new SerenitySink();
    private static final ThreadLocal<EvidenceSink> CUCUMBER = new ThreadLocal<>();

    private Evidence() {
    }

    public static void start(Scenario scenario) {
        CUCUMBER.set(new CucumberSink(Objects.requireNonNull(scenario, "scenario")));
    }

    public static void finish() {
        CUCUMBER.remove();
    }

    /** Records a titled block of text in every report. */
    public static void record(String title, String content) {
        String text = content == null ? "" : content;
        SERENITY.text(title, text);
        EvidenceSink cucumber = CUCUMBER.get();
        if (cucumber != null) {
            cucumber.text(title, text);
        }
    }

    /** Records a table (header row plus rows, columns separated by " | "). */
    public static void table(String title, String[] header, List<String[]> rows) {
        StringBuilder text = new StringBuilder(String.join(" | ", header)).append('\n');
        rows.forEach(row -> text.append(String.join(" | ", row)).append('\n'));
        record(title, text.toString());
    }

    /** First line of an exception, readable in a report ("NoSuchSessionException: ..."). */
    public static String firstLine(Throwable error) {
        String message = String.valueOf(error.getMessage());
        int end = message.indexOf('\n');
        return error.getClass().getSimpleName() + ": " + (end > 0 ? message.substring(0, end) : message);
    }
}
