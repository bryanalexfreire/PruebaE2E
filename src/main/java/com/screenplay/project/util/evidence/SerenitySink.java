package com.screenplay.project.util.evidence;

import net.serenitybdd.core.Serenity;

/** Writes evidence into the Serenity report, under the step that is running. */
final class SerenitySink implements EvidenceSink {

    @Override
    public void text(String title, String content) {
        Serenity.recordReportData().withTitle(title).andContents(content);
    }
}
