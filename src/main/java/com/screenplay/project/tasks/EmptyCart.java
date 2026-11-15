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
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;

/**
 * Precondition: leaves the cart empty before a scenario adds its own products.
 * Demoblaze keeps the cart of a signed-in account between sessions, so products left
 * by an earlier run would change the count and the total checked later.
 *
 * <p>The cart table is loaded and redrawn by AJAX calls: after a delete it is cleared and
 * filled again. Counting rows during that redraw would see an empty cart that is not empty,
 * so every read waits until the page has no AJAX call in progress, and the result is
 * confirmed by loading the cart page again.</p>
 *
 * <p>Not final: it runs other performables ({@code actor.attemptsTo}), so Serenity instruments it
 * with a generated subclass to report the nested steps.</p>
 */
public class EmptyCart implements Interaction {

    private static final By ROWS = By.cssSelector(PageCar.CART_ROWS_CSS);
    /** Upper bound so a cart that never empties fails instead of looping forever. */
    private static final int MAX_DELETES = 50;

    public static EmptyCart first() {
        return new EmptyCart();
    }

    @Override
    @Step("{0} empties the cart left by earlier sessions")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        int removed = 0;
        int left = loadCart(driver);
        while (left > 0) {
            if (removed == MAX_DELETES) {
                throw new IllegalStateException("The cart still has " + left + " product(s) after " + MAX_DELETES + " deletes");
            }
            actor.attemptsTo(ClickWhenReady.on(PageCar.BTN_DELETE_PRODUCT));
            int expected = left - 1;
            waitFor(driver, "the cart to show " + expected + " product(s) after a delete",
                    d -> WaitForPageLoad.isIdle(d) && d.findElements(ROWS).size() == expected);
            removed++;
            left = expected == 0 ? loadCart(driver) : expected;
        }
        Evidence.record("Cart before the test", removed == 0
                ? "Already empty"
                : removed + " product(s) left by earlier sessions were removed");
    }

    /** Opens the cart page, waits until its AJAX calls finish and returns the number of rows. */
    private static int loadCart(WebDriver driver) {
        driver.navigate().to(PageCar.CART_URL);
        waitFor(driver, "the cart page to finish loading", WaitForPageLoad::isIdle);
        return driver.findElements(ROWS).size();
    }


    private static void waitFor(WebDriver driver, String condition, Function<WebDriver, Boolean> check) {
        new WebDriverWait(driver, Timeouts.SERVER).withMessage(condition).until(check::apply);
    }
}
