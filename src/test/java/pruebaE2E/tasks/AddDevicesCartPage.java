package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;
import pruebaE2E.interactions.AcceptAlert;
import pruebaE2E.interactions.SafeClick;
import pruebaE2E.userInterface.HomePageUI;
import pruebaE2E.userInterface.LoginUI;
import pruebaE2E.userInterface.ProductUI;

public class AddDevicesCartPage {

    public static Performable addDevicesCartPage(){
        return Task.where("Add two products to cart",
                WaitUntil.the(HomePageUI.FIRST_PRODUCT, WebElementStateMatchers.isEnabled()).forNoMoreThan(10).seconds(),
                SafeClick.on(HomePageUI.FIRST_PRODUCT),
                SafeClick.on(ProductUI.ADD_TO_CART),
                AcceptAlert.now(),
                SafeClick.on(HomePageUI.LOGO),
                SafeClick.on(HomePageUI.SECOND_PRODUCT),
                SafeClick.on(ProductUI.ADD_TO_CART),
                AcceptAlert.now(),
                SafeClick.on(HomePageUI.LOGO)
        );
    }
}
