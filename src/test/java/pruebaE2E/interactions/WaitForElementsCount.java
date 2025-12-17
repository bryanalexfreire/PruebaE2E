package pruebaE2E.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitForElementsCount implements Interaction {

    private final Target target;
    private final int minCount;
    private final Duration timeout;

    private WaitForElementsCount(Target target, int minCount, Duration timeout) {
        this.target = target;
        this.minCount = minCount;
        this.timeout = timeout;
    }

    public static WaitForElementsCount atLeast(Target target, int minCount) {
        return new WaitForElementsCount(target, minCount, Duration.ofSeconds(30));
    }

    public static WaitForElementsCount atLeast(Target target, int minCount, Duration timeout) {
        return new WaitForElementsCount(target, minCount, timeout);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until((ExpectedCondition<Boolean>) d -> {
            List<WebElementFacade> elements = target.resolveAllFor(actor);
            return elements != null && elements.size() >= minCount;
        });
    }
}
