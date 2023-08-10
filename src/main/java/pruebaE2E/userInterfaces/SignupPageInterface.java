package pruebaE2E.userInterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class SignupPageInterface {

    public static final Target LNK_SIGNUP = Target.the("Link Login").located(By.id("signin2"));
    public static final Target INP_USERNAME = Target.the("Input Username").located(By.id("sign-username"));
    public static final Target INP_PASSWORD = Target.the("Input Password").located(By.id("sign-password"));
    public static final Target BTN_SIGNUP = Target.the("Button Login").located(By.xpath("/html/body/div[2]/div/div/div[3]/button[2]"));


}
