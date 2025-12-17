package pruebaE2E.userInterface;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class CartUI {

    public static final Target CART_LINK = Target.the("cart link").located(By.id("cartur"));
    public static final Target PLACE_ORDER = Target.the("place order button").located(By.xpath("//button[text()='Place Order']"));
    public static final Target NAME = Target.the("name input").located(By.id("name"));
    public static final Target COUNTRY = Target.the("country input").located(By.id("country"));
    public static final Target CITY = Target.the("city input").located(By.id("city"));
    public static final Target CARD = Target.the("card input").located(By.id("card"));
    public static final Target MONTH = Target.the("month input").located(By.id("month"));
    public static final Target YEAR = Target.the("year input").located(By.id("year"));
    public static final Target PURCHASE_BUTTON = Target.the("purchase button").located(By.xpath("//button[text()='Purchase']"));
    public static final Target CLOSE_BUTTON = Target.the("close button").located(By.xpath("//button[text()='Close']"));

    // Products rows in cart table
    public static final Target CART_PRODUCT_NAMES = Target.the("cart product names").locatedBy("#tbodyid tr td:nth-child(2)");
    public static final Target CART_TABLE = Target.the("cart table").located(By.id("tbodyid"));

    // Confirmation modal text after purchase (short, exact text present in pagesource)
    public static final Target PURCHASE_CONFIRMATION = Target.the("purchase confirmation").locatedBy("//h2[contains(text(), 'Thank you for your purchase')]");
    public static final Target Ok_BUTTON = Target.the("ok button").located(By.xpath("//button[text()='OK']"));
    public static final Target LOGOUT_BUTTON = Target.the("logout button").located(By.id("logout2"));
}
