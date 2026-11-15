package com.screenplay.project.tasks;

import com.screenplay.project.util.Timeouts;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;

/**
 * Waits until a Bootstrap modal has finished opening (fade and slide animations done).
 * Typing while the modal is still animating can be lost: a run left the "Name" field
 * empty and the purchase was never submitted.
 */
public final class WaitForModal implements Interaction {

    /** Computed transform of the dialog once the slide-in animation has ended. */
    private static final Set<String> RESTING_TRANSFORMS = Set.of("none", "matrix(1, 0, 0, 1, 0, 0)");

    private final String modalId;

    private WaitForModal(String modalId) {
        this.modalId = modalId;
    }

    public static WaitForModal withId(String modalId) {
        return new WaitForModal(modalId);
    }

    @Override
    @Step("{0} waits until the modal '#modalId' is fully open")
    public <T extends Actor> void performAs(T actor) {
        new WebDriverWait(BrowseTheWeb.as(actor).getDriver(), Timeouts.UI)
                .withMessage("the modal #" + modalId + " did not finish opening")
                .until(driver -> {
                    List<WebElement> modal = driver.findElements(By.id(modalId));
                    if (modal.isEmpty() || !modal.get(0).isDisplayed()) {
                        return false;
                    }
                    WebElement dialog = modal.get(0).findElement(By.cssSelector(".modal-dialog"));
                    return "1".equals(modal.get(0).getCssValue("opacity"))
                            && RESTING_TRANSFORMS.contains(dialog.getCssValue("transform"));
                });
    }
}
