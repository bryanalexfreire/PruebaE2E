package pruebaE2E.userInterfaces;


import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ShopPageInterface {

    public static final Target LNK_SAMSUNG_MOBILE = Target.the("Link Samsung Mobile").located(By.xpath("//*[@id=\"tbodyid\"]/div[1]/div/a"));
    public static final Target BTN_ADD_CART = Target.the("Boton Add Cart").located(By.xpath("//*[@id=\"tbodyid\"]/div[2]/div/a"));
    public static final Target LNK_SONY_Laptop = Target.the("Link Sony Laptop").located(By.xpath("//a[@href=\"prod.html?idp_=9\"]/img"));
    public static final Target LNK_CART = Target.the("Link Cart").located(By.id("cartur"));
    public static final Target BTN_PLACE_ORDER = Target.the("Link Place Order").located(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/button"));

}
