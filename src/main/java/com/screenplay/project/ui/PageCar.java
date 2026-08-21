package com.screenplay.project.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/**
 * UI locators for the shopping cart page and the "Place Order" modal on Demoblaze.
 * Covers everything from the cart table and the purchase form fields
 * to the confirmation dialog and the logout button.
 */
public final class PageCar {
    // Cart navigation and display
    public static final Target BTN_CART = Target.the("cart button").located(By.id("cartur"));
    public static final Target CART_TABLE = Target.the("cart table").located(By.id("tbodyid"));
    public static final Target CART_PRODUCT_NAMES = Target.the("cart product names").locatedBy("#tbodyid tr td:nth-child(2)");
    public static final Target CART_PRODUCT_PRICES = Target.the("cart product prices").locatedBy("#tbodyid tr td:nth-child(3)");
    public static final Target CART_TOTAL_PRICE = Target.the("cart total price").located(By.id("totalp"));
    public static final Target CART_PRODUCT_COUNT = Target.the("cart product count").locatedBy("#tbodyid tr");
    public static final Target BTN_DELETE_PRODUCT = Target.the("delete product button").located(By.xpath("//a[text()='Delete']"));

    // Checkout form fields
    public static final Target BTN_PLACE_ORDER = Target.the("place order button").located(By.xpath("//button[text()='Place Order']"));
    public static final Target INPUT_NAME = Target.the("name input").located(By.id("name"));
    public static final Target INPUT_COUNTRY = Target.the("country input").located(By.id("country"));
    public static final Target INPUT_CITY = Target.the("city input").located(By.id("city"));
    public static final Target INPUT_CARD = Target.the("card input").located(By.id("card"));
    public static final Target INPUT_MONTH = Target.the("month input").located(By.id("month"));
    public static final Target INPUT_YEAR = Target.the("year input").located(By.id("year"));
    public static final Target BTN_PURCHASE = Target.the("purchase button").located(By.xpath("//button[text()='Purchase']"));

    // Confirmation and logout
    public static final Target PURCHASE_CONFIRMATION = Target.the("purchase confirmation").locatedBy("//h2[contains(text(), 'Thank you for your purchase')]");
    public static final Target BTN_OK = Target.the("ok button").located(By.xpath("//button[text()='OK']"));
    public static final Target BTN_LOGOUT = Target.the("logout button").located(By.id("logout2"));

    private PageCar() {
    }
}
