package pruebaE2E.userInterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class PurchasePageInterface {

    public static final Target BODY_WINDOWS = Target.the("Body Windows").located(By.xpath("/html/body"));
    public static final Target INP_NAME = Target.the("Input Name").located(By.id("name"));
    public static final Target INP_COUNTRY = Target.the("Input Country").located(By.id("country"));
    public static final Target INP_CITY = Target.the("Input City").located(By.id("city"));
    public static final Target INP_CARD = Target.the("Input Card").located(By.id("card"));
    public static final Target INP_MONTH = Target.the("Input Month").located(By.id("month"));
    public static final Target INP_YEAR = Target.the("Input Year").located(By.id("year"));
    public static final Target BTN_PURCHASE = Target.the("Button Purchase").located(By.xpath("/html/body/div[3]/div/div/div[3]/button[2]"));
    public static final Target BTN_Ok = Target.the("Button Ok").located(By.xpath("/html/body/div[10]/div[7]/div/button"));

}
