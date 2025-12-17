package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;
import pruebaE2E.userInterface.CartUI;
import pruebaE2E.model.PurchaseData;
import pruebaE2E.interactions.SafeClick;

public class PurchaseFormPage {

    public static Performable withData(String name, String country, String city, String card, String month, String year){
        return withData(new PurchaseData(name,country,city,card,month,year));
    }

    public static Performable withData(PurchaseData data){
        return Task.where("Fill purchase form and complete purchase",
                WaitUntil.the(CartUI.PLACE_ORDER, WebElementStateMatchers.isEnabled()).forNoMoreThan(10).seconds(),
                SafeClick.on(CartUI.PLACE_ORDER),
                Enter.theValue(data.getName()).into(CartUI.NAME),
                Enter.theValue(data.getCountry()).into(CartUI.COUNTRY),
                Enter.theValue(data.getCity()).into(CartUI.CITY),
                Enter.theValue(data.getCard()).into(CartUI.CARD),
                Enter.theValue(data.getMonth()).into(CartUI.MONTH),
                Enter.theValue(data.getYear()).into(CartUI.YEAR),
                SafeClick.on(CartUI.PURCHASE_BUTTON),
                // validar que la confirmación de compra contiene el texto esperado y cerrar rápido
                WaitUntil.the(CartUI.PURCHASE_CONFIRMATION, WebElementStateMatchers.containsText("Thank you for your purchase")).forNoMoreThan(2).seconds(),
                SafeClick.on(CartUI.CLOSE_BUTTON)
        );
    }
}
