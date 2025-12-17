package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;
import pruebaE2E.interactions.SafeClick;
import pruebaE2E.userInterface.CartUI;

public class CloseSessionPage {

    public static Performable closeAndLogout() {
        return Task.where("Validate purchase confirmation, close modal and logout",
                // validate that the confirmation contains the expected text and close quickly
                WaitUntil.the(CartUI.PURCHASE_CONFIRMATION, WebElementStateMatchers.containsText("Thank you for your purchase")).forNoMoreThan(2).seconds(),
                SafeClick.on(CartUI.Ok_BUTTON),
                SafeClick.on(CartUI.LOGOUT_BUTTON)
        );
    }
}

