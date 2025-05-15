package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import pruebaE2E.userInterfaces.ShopPageInterface;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class GoCartPage implements Task {

    public static Task goCartPage(){
        return instrumented(GoCartPage.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(

                ReloadPage.withUrl("https://www.demoblaze.com"),

                WaitUntil.the(ShopPageInterface.LNK_CART, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(ShopPageInterface.LNK_CART),

                WaitUntil.the(ShopPageInterface.BTN_PLACE_ORDER, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(ShopPageInterface.BTN_PLACE_ORDER)

        );

    }
}
