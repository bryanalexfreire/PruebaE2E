package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;
import pruebaE2E.userInterface.LoginUI;
import pruebaE2E.interactions.SafeClick;

public class LoginPage {

    public static Performable loginPage(String username, String password){
        return Task.where("Log in user",
                SafeClick.on(LoginUI.LOGIN_BUTTON),
                WaitUntil.the(LoginUI.LOGIN_BUTTON, WebElementStateMatchers.isEnabled()).forNoMoreThan(10).seconds(),
                Enter.theValue(username).into(LoginUI.LOGIN_USERNAME),
                Enter.theValue(password).into(LoginUI.LOGIN_PASSWORD),
                SafeClick.on(LoginUI.LOGIN_SUBMIT)
        );
    }
}
