package com.screenplay.project.tasks;

import com.screenplay.project.model.Customer;
import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;

/**
 * Opens the "Place Order" modal, waits until it is fully open (typing during the animation
 * can be lost), fills in the customer details and submits the purchase.
 */
public final class RegisterCustomer {

    private RegisterCustomer() {
    }

    public static Performable withData(Customer customer) {
        return Task.where("{0} places the order for " + customer.name(),
                ClickWhenReady.on(PageCar.BTN_PLACE_ORDER),
                WaitForModal.withId(PageCar.ORDER_MODAL_ID),
                Enter.theValue(customer.name()).into(PageCar.INPUT_NAME),
                Enter.theValue(customer.country()).into(PageCar.INPUT_COUNTRY),
                Enter.theValue(customer.city()).into(PageCar.INPUT_CITY),
                Enter.theValue(customer.card()).into(PageCar.INPUT_CARD),
                Enter.theValue(customer.month()).into(PageCar.INPUT_MONTH),
                Enter.theValue(customer.year()).into(PageCar.INPUT_YEAR),
                ClickWhenReady.on(PageCar.BTN_PURCHASE)
        );
    }
}
