package com.screenplay.project.tasks;

import com.screenplay.project.util.Timeouts;
import com.screenplay.project.util.evidence.Evidence;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Waits for a native browser alert, records its text in the report and accepts it.
 * Demoblaze triggers a JavaScript alert ("Product added.") after each "Add to cart" click,
 * so the report shows exactly what the app said.
 */
public class AcceptBrowserAlert implements Interaction {

    public static AcceptBrowserAlert now() {
        return new AcceptBrowserAlert();
    }

    @Override
    @Step("{0} accepts the browser alert")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        Alert alert = new WebDriverWait(driver, Timeouts.UI).until(ExpectedConditions.alertIsPresent());
        Evidence.record("Browser alert", alert.getText());
        alert.accept();
    }
}
