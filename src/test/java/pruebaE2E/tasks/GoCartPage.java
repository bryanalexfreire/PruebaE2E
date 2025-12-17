package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;
import pruebaE2E.interactions.SafeClick;
import pruebaE2E.userInterface.CartUI;
import pruebaE2E.userInterface.HomePageUI;

public class GoCartPage {

    public static Performable goCartPage(){
        return Task.where("Go to cart page",
                WaitUntil.the(HomePageUI.CART_LINK, WebElementStateMatchers.isEnabled()).forNoMoreThan(2).seconds(),
                SafeClick.on(CartUI.CART_LINK)
        );
    }
}
