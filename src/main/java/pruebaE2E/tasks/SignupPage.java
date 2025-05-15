package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.waits.WaitUntil;
import pruebaE2E.userInterfaces.PurchasePageInterface;
import pruebaE2E.userInterfaces.ShopPageInterface;
import pruebaE2E.userInterfaces.SignupPageInterface;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class SignupPage implements Task{

    public final String username;
    public final String password;

    public SignupPage(String username, String password) {

        this.username = username;
        this.password = password;

    }

    public static Task signupPage(String username, String password){

        return instrumented(SignupPage.class, username, password);

    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(

                WaitUntil.the(SignupPageInterface.LNK_SIGNUP, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(SignupPageInterface.LNK_SIGNUP),

                WaitUntil.the(SignupPageInterface.INP_USERNAME, isVisible()).forNoMoreThan(30).seconds(),
                Enter.theValue(username).into(SignupPageInterface.INP_USERNAME),

                WaitUntil.the(SignupPageInterface.INP_PASSWORD, isVisible()).forNoMoreThan(30).seconds(),
                Enter.theValue(password).into(SignupPageInterface.INP_PASSWORD),

                WaitUntil.the(SignupPageInterface.BTN_SIGNUP, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(SignupPageInterface.BTN_SIGNUP),

                ReloadPage.withUrl("https://www.demoblaze.com")

        );
    }
}
