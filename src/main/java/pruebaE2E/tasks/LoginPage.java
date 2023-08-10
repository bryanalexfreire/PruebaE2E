package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.waits.WaitUntil;
import pruebaE2E.userInterfaces.LoginPageInterface;
import pruebaE2E.userInterfaces.SignupPageInterface;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class LoginPage implements Task{

    public final String username;
    public final String password;

    public LoginPage(String username, String password) {

        this.username = username;
        this.password = password;

    }

    public static Task loginPage(String username, String password) {

        return instrumented(LoginPage.class, username, password);

    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(

                WaitUntil.the(LoginPageInterface.LNK_LOGIN, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(LoginPageInterface.LNK_LOGIN),

                WaitUntil.the(LoginPageInterface.INP_USERNAME, isVisible()).forNoMoreThan(30).seconds(),
                Enter.theValue(username).into(LoginPageInterface.INP_USERNAME),

                WaitUntil.the(LoginPageInterface.INP_PASSWORD, isVisible()).forNoMoreThan(30).seconds(),
                Enter.theValue(password).into(LoginPageInterface.INP_PASSWORD),

                WaitUntil.the(LoginPageInterface.BTN_LOGIN, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(LoginPageInterface.BTN_LOGIN)

        );
    }
}
