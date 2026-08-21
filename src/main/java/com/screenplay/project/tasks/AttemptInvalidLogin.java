package com.screenplay.project.tasks;
import com.screenplay.project.model.ModelCredentials;
import com.screenplay.project.ui.PageMain;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
public final class AttemptInvalidLogin {
    private AttemptInvalidLogin() {}
    public static Performable with(ModelCredentials invalidCreds) {
        return Task.where("Attempt login with invalid credentials",
            actor -> {
                actor.attemptsTo(SafeClick.on(PageMain.BTN_LOGIN));
                actor.attemptsTo(Enter.theValue(invalidCreds.getUsername())
                    .into(PageMain.INPUT_USERNAME));
                actor.attemptsTo(Enter.theValue(invalidCreds.getPassword())
                    .into(PageMain.INPUT_PASSWORD));
            }
        );
    }
}