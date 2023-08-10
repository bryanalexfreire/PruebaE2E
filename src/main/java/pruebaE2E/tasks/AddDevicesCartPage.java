package pruebaE2E.tasks;

import pruebaE2E.userInterfaces.ShopPageInterface;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;


import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class AddDevicesCartPage implements Task {



    public static Task addDevicesCartPage(){
        return instrumented(AddDevicesCartPage.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(

                WaitUntil.the(ShopPageInterface.LNK_SAMSUNG_MOBILE, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(ShopPageInterface.LNK_SAMSUNG_MOBILE),

                WaitUntil.the(ShopPageInterface.BTN_ADD_CART, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(ShopPageInterface.BTN_ADD_CART),

                ReloadPage.withUrl("https://www.demoblaze.com"),

                WaitUntil.the(ShopPageInterface.LNK_SONY_Laptop, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(ShopPageInterface.LNK_SONY_Laptop),

                WaitUntil.the(ShopPageInterface.BTN_ADD_CART, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(ShopPageInterface.BTN_ADD_CART)



        );
    }
}
