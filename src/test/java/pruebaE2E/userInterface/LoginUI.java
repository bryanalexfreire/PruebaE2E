package pruebaE2E.userInterface;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LoginUI {

    public static final Target LOGIN_BUTTON = Target.the("login button").located(By.id("login2"));
    public static final Target LOGIN_USERNAME = Target.the("login username").located(By.id("loginusername"));
    public static final Target LOGIN_PASSWORD = Target.the("login password").located(By.id("loginpassword"));
    public static final Target LOGIN_SUBMIT = Target.the("login submit").located(By.xpath("//button[text()='Log in']"));

}
