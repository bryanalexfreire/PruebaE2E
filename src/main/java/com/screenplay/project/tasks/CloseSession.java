package com.screenplay.project.tasks;

import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

/**
 * Closes the purchase confirmation dialog and logs out of Demoblaze.
 * This should be the last step in any scenario that completes a purchase.
 */
public final class CloseSession {
    private CloseSession() {
    }

    /**
     * Returns a task that clicks "OK" on the confirmation dialog and then logs out.
     */
    public static Performable now() {
        return Task.where("Close purchase modal and logout",
                SafeClick.on(PageCar.BTN_OK),
                SafeClick.on(PageCar.BTN_LOGOUT)
        );
    }
}

