package com.screenplay.project.tasks;

import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

/**
 * Accepts the purchase confirmation and signs out.
 * "OK" on the confirmation navigates to the home page while the order modal is still shown;
 * signing out only after the home page has loaded avoids clicking through that modal.
 */
public final class CloseSession {

    private static final String HOME_PAGE = "index.html";

    private CloseSession() {
    }

    public static Performable now() {
        return Task.where("{0} accepts the confirmation and signs out",
                ClickWhenReady.on(PageCar.BTN_OK),
                WaitForPageLoad.of(HOME_PAGE),
                ClickWhenReady.on(PageCar.BTN_LOGOUT)
        );
    }
}
