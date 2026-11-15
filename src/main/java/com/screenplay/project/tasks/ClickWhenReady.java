package com.screenplay.project.tasks;

import com.screenplay.project.util.Timeouts;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

/**
 * Clicks an element the way a user would: it waits until the element is visible and enabled,
 * and retries only while it is not rendered yet, another element covers it for a moment or
 * the page re-renders it.
 * It never falls back to a JavaScript click, which would hide real problems (a button that
 * is covered, off-screen or disabled for the user would still "work" in the test).
 */
public final class ClickWhenReady implements Interaction {

    private static final Duration POLLING = Duration.ofMillis(250);

    private final Target target;

    private ClickWhenReady(Target target) {
        this.target = target;
    }

    public static ClickWhenReady on(Target target) {
        return new ClickWhenReady(target);
    }

    @Override
    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T actor) {
        new FluentWait<>(BrowseTheWeb.as(actor).getDriver())
                .withTimeout(Timeouts.UI)
                .pollingEvery(POLLING)
                .ignoring(ElementClickInterceptedException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NotFoundException.class)
                .withMessage(target.getName() + " was not clickable")
                .until(driver -> {
                    WebElement element = ExpectedConditions.elementToBeClickable(target.resolveFor(actor)).apply(driver);
                    if (element == null) {
                        return false;
                    }
                    element.click();
                    return true;
                });
    }
}
