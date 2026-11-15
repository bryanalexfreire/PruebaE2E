package com.screenplay.project.stepdefinition;

import com.screenplay.project.util.logger.TestLogger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/** Logs the start and the result of each scenario, to follow a run in the console or the CI log. */
public class LoggingHooks {

    @Before(order = 5)
    public void logStart(Scenario scenario) {
        TestLogger.info("SCENARIO START: " + scenario.getName() + " " + scenario.getSourceTagNames());
    }

    @After(order = 30000)
    public void logEnd(Scenario scenario) {
        TestLogger.info("SCENARIO END: " + scenario.getName() + " -> " + scenario.getStatus());
    }
}
