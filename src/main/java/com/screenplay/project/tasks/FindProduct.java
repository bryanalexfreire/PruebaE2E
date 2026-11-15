package com.screenplay.project.tasks;

import com.screenplay.project.ui.PageMain;
import com.screenplay.project.ui.PageProducts;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

/**
 * Adds two products to the shopping cart from the Demoblaze home page.
 * Each product addition triggers a browser alert which is accepted automatically.
 * After both items are added the actor navigates back to the home page.
 */
public final class FindProduct {
    private FindProduct() {
    }

    /**
     * Returns a task that clicks the first two available products and adds them to the cart.
     */
    public static Performable andAddToCart() {
        return Task.where("Add two products to cart",
                ClickWhenReady.on(PageMain.FIRST_PRODUCT),
                ClickWhenReady.on(PageProducts.ADD_TO_CART),
                AcceptBrowserAlert.now(),
                ClickWhenReady.on(PageMain.LOGO),
                ClickWhenReady.on(PageMain.SECOND_PRODUCT),
                ClickWhenReady.on(PageProducts.ADD_TO_CART),
                AcceptBrowserAlert.now(),
                ClickWhenReady.on(PageMain.LOGO)
        );
    }
}
