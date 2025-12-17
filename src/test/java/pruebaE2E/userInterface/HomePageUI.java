package pruebaE2E.userInterface;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class HomePageUI {

    public static final String URL = "https://www.demoblaze.com";
    public static final Target CART_LINK = Target.the("cart link").located(By.id("cartur"));
    public static final Target FIRST_PRODUCT = Target.the("first product").located(By.xpath("(//a[@class='hrefch'])[1]"));
    public static final Target SECOND_PRODUCT = Target.the("second product").located(By.xpath("(//a[@class='hrefch'])[2]"));
    public static final Target LOGO = Target.the("logo link").located(By.id("nava"));

}

