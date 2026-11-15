package com.screenplay.project.tasks;

import com.screenplay.project.util.Timeouts;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Waits until the browser shows a page (its URL contains the given part) and that page has
 * finished loading: document complete and no jQuery request in progress (Demoblaze makes every
 * call with jQuery). Use it after an action that navigates, before acting on the next page:
 * acting during the navigation hits elements of the page that is going away.
 */
public final class WaitForPageLoad implements Interaction {

    /** True once the page is loaded and jQuery has no request in progress. */
    private static final String PAGE_IDLE = "return document.readyState === 'complete'"
            + " && (!window.jQuery || window.jQuery.active === 0);";

    private final String urlPart;

    private WaitForPageLoad(String urlPart) {
        this.urlPart = urlPart;
    }

    /** Waits for the page whose URL contains {@code urlPart} (for example "index.html"). */
    public static WaitForPageLoad of(String urlPart) {
        return new WaitForPageLoad(urlPart);
    }

    /** True when the current page is loaded and has no AJAX call in progress. */
    public static boolean isIdle(WebDriver driver) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(PAGE_IDLE));
    }

    @Override
    @Step("{0} waits until the page '#urlPart' has finished loading")
    public <T extends Actor> void performAs(T actor) {
        new WebDriverWait(BrowseTheWeb.as(actor).getDriver(), Timeouts.SERVER)
                .withMessage("the page " + urlPart + " to finish loading")
                .until(driver -> String.valueOf(driver.getCurrentUrl()).contains(urlPart) && isIdle(driver));
    }
}
