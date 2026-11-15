package com.screenplay.project.tasks;

import com.screenplay.project.model.Credentials;
import com.screenplay.project.ui.PageMain;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;

/**
 * Opens the login modal and signs in. The password is typed with {@link EnterSecret},
 * so it never appears in the report.
 */
public final class MakeLogin {

    private MakeLogin() {
    }

    public static Performable withCredentials(Credentials credentials) {
        return Task.where("{0} signs in as " + credentials.username(),
                ClickWhenReady.on(PageMain.BTN_LOGIN),
                WaitForModal.withId(PageMain.LOGIN_MODAL_ID),
                Enter.theValue(credentials.username()).into(PageMain.INPUT_USERNAME),
                EnterSecret.into(PageMain.INPUT_PASSWORD, credentials.password()),
                ClickWhenReady.on(PageMain.BTN_LOGIN_SUBMIT)
        );
    }
}
