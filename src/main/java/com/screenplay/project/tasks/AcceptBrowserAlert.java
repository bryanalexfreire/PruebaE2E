package com.screenplay.project.tasks;

import com.screenplay.project.util.Timeouts;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Waits for a native browser alert to appear and then accepts it.
 * Demoblaze triggers a JavaScript alert after adding a product to the cart,
 * so this interaction is called right after each "Add to cart" click.
 */
public class AcceptBrowserAlert implements Interaction {

    public static AcceptBrowserAlert now() {
        return new AcceptBrowserAlert();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Timeouts.SHORT);
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }
}

