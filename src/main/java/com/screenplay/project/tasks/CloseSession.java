package com.screenplay.project.tasks;

import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

/**
 * Accepts the purchase confirmation and signs out. The confirmation is handled by
 * {@link AcceptPurchaseConfirmation}, so "Log out" is clicked only when no modal covers the page.
 */
public final class CloseSession {

    private CloseSession() {
    }

    public static Performable now() {
        return Task.where("{0} accepts the confirmation and signs out",
                AcceptPurchaseConfirmation.now(),
                ClickWhenReady.on(PageCar.BTN_LOGOUT)
        );
    }
}
