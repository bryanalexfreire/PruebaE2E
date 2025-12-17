package pruebaE2E.userInterface;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ProductUI {

    public static final Target ADD_TO_CART = Target.the("add to cart button").located(By.xpath("//a[text()='Add to cart']"));

}

