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
    public static final String CART_URL = PageMain.URL + "cart.html";
    /** By visible text: the link has id "cartur" on the home page but no id on cart.html. */
    public static final Target BTN_CART = Target.the("'Cart' link").located(By.xpath("//nav//a[normalize-space()='Cart']"));
    public static final Target CART_PRODUCT_NAMES = Target.the("cart product names").locatedBy("#tbodyid tr td:nth-child(2)");
    public static final Target CART_PRODUCT_PRICES = Target.the("cart product prices").locatedBy("#tbodyid tr td:nth-child(3)");
    public static final Target CART_TOTAL_PRICE = Target.the("cart total price").located(By.id("totalp"));
    public static final String CART_ROWS_CSS = "#tbodyid tr";
    public static final Target CART_ROWS = Target.the("cart rows").locatedBy(CART_ROWS_CSS);
    public static final Target BTN_DELETE_PRODUCT = Target.the("delete link of the first cart row")
            .located(By.xpath("(//tbody[@id='tbodyid']//a[text()='Delete'])[1]"));

    // Checkout form fields (inside the Bootstrap modal #orderModal)
    public static final String ORDER_MODAL_ID = "orderModal";
    public static final Target BTN_PLACE_ORDER = Target.the("place order button").located(By.xpath("//button[text()='Place Order']"));
    public static final Target INPUT_NAME = Target.the("name input").located(By.id("name"));
    public static final Target INPUT_COUNTRY = Target.the("country input").located(By.id("country"));
    public static final Target INPUT_CITY = Target.the("city input").located(By.id("city"));
    public static final Target INPUT_CARD = Target.the("card input").located(By.id("card"));
    public static final Target INPUT_MONTH = Target.the("month input").located(By.id("month"));
    public static final Target INPUT_YEAR = Target.the("year input").located(By.id("year"));
    public static final Target BTN_CLOSE_ORDER = Target.the("'Close' button of the order modal")
            .located(By.xpath("//div[@id='orderModal']//button[text()='Close']"));
    public static final Target BTN_PURCHASE = Target.the("purchase button").located(By.xpath("//button[text()='Purchase']"));

    // Confirmation and logout
    public static final Target PURCHASE_CONFIRMATION = Target.the("purchase confirmation")
            .locatedBy("//h2[contains(text(), 'Thank you for your purchase')]");
    public static final Target PURCHASE_RECEIPT = Target.the("purchase receipt details").locatedBy(".sweet-alert p.lead");
    public static final Target BTN_OK = Target.the("ok button").located(By.xpath("//button[text()='OK']"));
    public static final Target BTN_LOGOUT = Target.the("logout button").located(By.id("logout2"));

    private PageCar() {
    }
}
