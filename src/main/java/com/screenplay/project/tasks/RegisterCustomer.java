package com.screenplay.project.tasks;

import com.screenplay.project.model.ModelCustomer;
import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;

/**
 * Opens the "Place Order" modal, fills in the customer details, and submits the purchase.
 * Expects a fully populated {@link ModelCustomer} object.
 */
public final class RegisterCustomer {
    private RegisterCustomer() {
    }

    /**
     * Returns a task that completes the purchase form with the given customer data.
     *
     * @param customer the customer details (name, country, city, card, month, year)
     */
    public static Performable withData(ModelCustomer customer) {
        return Task.where("Register customer and complete purchase",
                Click.on(PageCar.BTN_PLACE_ORDER),
                Enter.theValue(customer.getName()).into(PageCar.INPUT_NAME),
                Enter.theValue(customer.getCountry()).into(PageCar.INPUT_COUNTRY),
                Enter.theValue(customer.getCity()).into(PageCar.INPUT_CITY),
                Enter.theValue(customer.getCard()).into(PageCar.INPUT_CARD),
                Enter.theValue(customer.getMonth()).into(PageCar.INPUT_MONTH),
                Enter.theValue(customer.getYear()).into(PageCar.INPUT_YEAR),
                Click.on(PageCar.BTN_PURCHASE)
        );
    }
}
