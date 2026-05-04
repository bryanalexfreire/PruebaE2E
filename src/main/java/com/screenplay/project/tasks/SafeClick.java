package com.screenplay.project.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * A resilient click interaction that retries up to three times before falling back
 * to a JavaScript click. This is useful for elements that may be briefly obscured
 * by overlays or animations.
 *
 * <p>Usage: {@code SafeClick.on(PageMain.BTN_LOGIN)}</p>
 */
public class SafeClick implements Interaction {
    private final Target target;

    private SafeClick(Target target) {
        this.target = target;
    }

    public static SafeClick on(Target target) {
        return new SafeClick(target);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(target.resolveFor(actor)));
                element.click();
                return;
            } catch (WebDriverException exception) {
                if (attempt == 2) {
                    WebElement element = target.resolveFor(actor);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    return;
                }
            }
        }
    }
}
