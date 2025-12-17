package pruebaE2E.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
        // reducir timeout para acelerar fallos y reintentos tras la confirmación de compra
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // clear any existing alert before interacting
        try {
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException ignored) {
        }

        WebElement element = null;
        // try resolving the element, accepting alerts if they appear, retry a couple times
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                element = target.resolveFor(actor);
                element = wait.until(ExpectedConditions.elementToBeClickable(element));
                break;
            } catch (UnhandledAlertException uae) {
                try { driver.switchTo().alert().accept(); } catch (Exception ignore) {}
            } catch (WebDriverException wde) {
                // if resolving fails for other reasons, retry after short pause
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }
        }

        if (element == null) {
            throw new WebDriverException("Unable to resolve clickable element for target: " + target.getName());
        }

        // Intentar el click con reintentos para manejar StaleElementReferenceException
        int clickAttempts = 0;
        while (clickAttempts < 3) {
            try {
                element.click();
                return; // éxito
            } catch (StaleElementReferenceException sere) {
                // re-resolver y reintentar
                clickAttempts++;
                try { Thread.sleep(150); } catch (InterruptedException ignored) {}
                try {
                    element = target.resolveFor(actor);
                    element = wait.until(ExpectedConditions.elementToBeClickable(element));
                } catch (Exception e) {
                    // continuar reintentos
                }
            } catch (WebDriverException e) {
                // try to accept any alert that may have appeared and retry with JS click
                try {
                    driver.switchTo().alert().accept();
                } catch (NoAlertPresentException ignored) {
                }
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    return; // éxito con JS
                } catch (Exception ex) {
                    // último intento: reintentar click directo o re-resolver
                    clickAttempts++;
                    try { Thread.sleep(150); } catch (InterruptedException ignored) {}
                    try {
                        element = target.resolveFor(actor);
                        element.click();
                        return;
                    } catch (Exception ignore) {}
                }
            }
        }

        throw new WebDriverException("Unable to click element for target after retries: " + target.getName());
    }
}
