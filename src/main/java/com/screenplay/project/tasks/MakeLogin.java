package com.screenplay.project.tasks;

import com.screenplay.project.model.ModelCredentials;
import com.screenplay.project.ui.PageMain;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;

/**
 * Opens the login modal and signs in using the provided credentials.
 * Expects a {@link ModelCredentials} object with a valid username and password.
 */
public final class MakeLogin {
    private MakeLogin() {
    }

    /**
     * Returns a task that fills in the login form and submits it.
     *
     * @param credentials the username and password to use
     */
    public static Performable withCredentials(ModelCredentials credentials) {
        return Task.where("Login with valid credentials",
                SafeClick.on(PageMain.BTN_LOGIN),
                Enter.theValue(credentials.getUsername()).into(PageMain.INPUT_USERNAME),
                Enter.theValue(credentials.getPassword()).into(PageMain.INPUT_PASSWORD),
                SafeClick.on(PageMain.BTN_LOGIN_SUBMIT)
        );
    }
}
