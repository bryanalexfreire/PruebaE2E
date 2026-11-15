package com.screenplay.project.tasks;

import com.screenplay.project.ui.PageCar;
import com.screenplay.project.util.Timeouts;
import com.screenplay.project.util.evidence.Evidence;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.function.Function;

/**
 * Accepts the purchase confirmation ("OK") and leaves the page ready for the next action.
 *
 * <p>"OK" is meant to navigate to the home page, but the order modal stays open until the new
 * page loads, and in some runs (CI) the confirmation closes without navigating at all. Both cases
 * are handled as a user would: wait for the home page, or close the order modal. The case that
 * happened is recorded as evidence.</p>
 *
 * <p>Not final: it runs other performables, so Serenity instruments it with a subclass.</p>
 */
public class AcceptPurchaseConfirmation implements Interaction {

    private static final By CONFIRMATION = By.cssSelector(".sweet-alert");
    private static final String HOME_PAGE = "index.html";

    public static AcceptPurchaseConfirmation now() {
        return new AcceptPurchaseConfirmation();
    }

    @Override
    @Step("{0} accepts the purchase confirmation")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        // clicking while the dialog is still animating in can be lost
        waitFor(driver, "the purchase confirmation to be fully shown", d -> isShown(d, CONFIRMATION));
        actor.attemptsTo(ClickWhenReady.on(PageCar.BTN_OK));
        waitFor(driver, "the purchase confirmation to close or the home page to open",
                d -> String.valueOf(d.getCurrentUrl()).contains(HOME_PAGE) || !isDisplayed(d, CONFIRMATION));

        if (String.valueOf(driver.getCurrentUrl()).contains(HOME_PAGE)) {
            actor.attemptsTo(WaitForPageLoad.of(HOME_PAGE));
            Evidence.record("After the confirmation", "The home page was opened");
            return;
        }
        Evidence.record("After the confirmation", "The confirmation closed but the page stayed on "
                + driver.getCurrentUrl() + " (no navigation to the home page); the order modal was closed");
        By orderModal = By.id(PageCar.ORDER_MODAL_ID);
        if (isDisplayed(driver, orderModal)) {
            actor.attemptsTo(ClickWhenReady.on(PageCar.BTN_CLOSE_ORDER));
            waitFor(driver, "the order modal to close", d -> !isDisplayed(d, orderModal));
        }
    }

    private static boolean isShown(WebDriver driver, By locator) {
        List<WebElement> found = driver.findElements(locator);
        return !found.isEmpty() && found.get(0).isDisplayed() && "1".equals(found.get(0).getCssValue("opacity"));
    }

    private static boolean isDisplayed(WebDriver driver, By locator) {
        List<WebElement> found = driver.findElements(locator);
        return !found.isEmpty() && found.get(0).isDisplayed();
    }

    private static void waitFor(WebDriver driver, String condition, Function<WebDriver, Boolean> check) {
        new WebDriverWait(driver, Timeouts.SERVER).withMessage(condition).until(check::apply);
    }
}
