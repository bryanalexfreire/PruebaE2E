package pruebaE2E.userInterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LoginPageInterface {

    public static final Target LNK_LOGIN = Target.the("Link Login").located(By.id("login2"));
    public static final Target INP_USERNAME = Target.the("Input Username").located(By.id("loginusername"));
    public static final Target INP_PASSWORD = Target.the("Input Password").located(By.id("loginpassword"));
    public static final Target BTN_LOGIN = Target.the("Button Login").located(By.xpath("/html/body/div[3]/div/div/div[3]/button[2]"));

}
