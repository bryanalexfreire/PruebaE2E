package com.screenplay.project.tasks;

import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

/**
 * Navigates to the shopping cart page so the actor can review the items before purchasing.
 */
public final class ViewCart {
    private ViewCart() {
    }

    /**
     * Returns a task that clicks the cart button in the navigation bar.
     */
    public static Performable details() {
        return Task.where("Open shopping cart", Click.on(PageCar.BTN_CART));
    }
}
