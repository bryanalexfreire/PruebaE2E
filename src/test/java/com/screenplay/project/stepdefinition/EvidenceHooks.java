package com.screenplay.project.stepdefinition;

import com.screenplay.project.util.evidence.Evidence;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnStage;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

/**
 * Scenario-level evidence that complements what Serenity captures by itself
 * (a screenshot per step and the page HTML at a failure):
 * the browser used and, when a step fails, the page URL and title, or the reason
 * the browser could not be read (for example a lost driver session).
 * Cucumber creates a new instance per scenario, so the fields hold per-scenario state.
 */
public class EvidenceHooks {

    private boolean browserRecorded;
    private boolean failureRecorded;

    @Before(order = 0)
    public void openEvidence(Scenario scenario) {
        Evidence.start(scenario);
    }

    @AfterStep
    public void stepEvidence(Scenario scenario) {
        if (!browserRecorded) {
            browserRecorded = driver().map(EvidenceHooks::recordBrowser).orElse(false);
        }
        if (scenario.isFailed() && !failureRecorded) {
            failureRecorded = true;
            recordFailureContext();
        }
    }

    /** Runs before the stage is closed (ActorHooks uses the default order). */
    @After(order = 20000)
    public void closeEvidence(Scenario scenario) {
        if (scenario.isFailed() && !failureRecorded) {
            recordFailureContext();
        }
        Evidence.finish();
    }

    private static boolean recordBrowser(WebDriver driver) {
        if (driver instanceof HasCapabilities withCapabilities) {
            Capabilities caps = withCapabilities.getCapabilities();
            Evidence.record("Browser", caps.getBrowserName() + " " + caps.getBrowserVersion()
                    + " · headless " + System.getProperty("headless.mode", "false"));
        }
        return true;
    }

    private static void recordFailureContext() {
        Optional<WebDriver> driver = driver();
        if (driver.isEmpty()) {
            Evidence.record("Page at the failure", "No browser was open for this scenario.");
            return;
        }
        try {
            Evidence.record("Page at the failure", driver.get().getCurrentUrl() + "\n" + driver.get().getTitle());
        } catch (RuntimeException e) {
            Evidence.record("Page at the failure (browser not available)", Evidence.firstLine(e));
        }
    }

    /** Browser of the actor in the spotlight, if one has been opened. */
    private static Optional<WebDriver> driver() {
        try {
            Actor actor = OnStage.theActorInTheSpotlight();
            return actor.abilityTo(BrowseTheWeb.class) == null
                    ? Optional.empty()
                    : Optional.of(BrowseTheWeb.as(actor).getDriver());
        } catch (RuntimeException noActorYet) {
            return Optional.empty();
        }
    }
}
