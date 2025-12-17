package pruebaE2E.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitFor implements Interaction {

    private final Target target;
    private final Duration timeout;
    private final boolean waitForVisibility;

    private WaitFor(Target target, Duration timeout, boolean waitForVisibility) {
        this.target = target;
        this.timeout = timeout;
        this.waitForVisibility = waitForVisibility;
    }

    public static WaitFor visibilityOf(Target target) {
        return new WaitFor(target, Duration.ofSeconds(2), true);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        WebElement element = target.resolveFor(actor);
        if (waitForVisibility) {
            wait.until(ExpectedConditions.visibilityOf(element));
        } else {
            wait.until(ExpectedConditions.invisibilityOf(element));
        }
    }
}

