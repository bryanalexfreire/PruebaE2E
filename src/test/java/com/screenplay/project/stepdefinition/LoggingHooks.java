package com.screenplay.project.stepdefinition;
import com.screenplay.project.util.logger.TestLogger;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
public class LoggingHooks {
    @Before
    public void logScenarioStart(Scenario scenario) {
        TestLogger.info("=====================================");
        TestLogger.info("▶▶ SCENARIO START: " + scenario.getName());
        TestLogger.info("Tags: " + scenario.getSourceTagNames());
        TestLogger.info("=====================================");
    }
    @After
    public void logScenarioEnd(Scenario scenario) {
        String status = scenario.isFailed() ? "FAILED ✗" : "PASSED ✓";
        TestLogger.info("=====================================");
        TestLogger.info("✓✓ SCENARIO END: " + scenario.getName());
        TestLogger.info("Status: " + status);
        TestLogger.info("=====================================");
    }
}