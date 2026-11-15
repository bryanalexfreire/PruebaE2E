package com.screenplay.project.util.evidence;

import io.cucumber.java.Scenario;

import java.nio.charset.StandardCharsets;

/** Attaches evidence as text to the current Cucumber scenario (cucumber.json / cucumber.html). */
final class CucumberSink implements EvidenceSink {

    private final Scenario scenario;

    CucumberSink(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public void text(String title, String content) {
        scenario.attach(content.getBytes(StandardCharsets.UTF_8), "text/plain", title);
    }
}
