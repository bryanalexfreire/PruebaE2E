package com.screenplay.project.tasks;
import com.screenplay.project.ui.PageMain;
import com.screenplay.project.ui.PageProducts;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import java.util.List;
public final class AddProductsByIndex {
    private final List<Integer> productIndices;
    private AddProductsByIndex(List<Integer> indices) {
        this.productIndices = indices;
    }
    public static Performable forIndices(List<Integer> indices) {
        return new AddProductsByIndex(indices).asPerformable();
    }
    private Performable asPerformable() {
        return Task.where("Add products at specified indices",
            actor -> {
                for (int idx : productIndices) {
                    Target product = Target.the("product " + idx)
                        .located(By.xpath("(//a[@class='hrefch'])[" + idx + "]"));
                    actor.attemptsTo(SafeClick.on(product));
                    actor.attemptsTo(SafeClick.on(PageProducts.ADD_TO_CART));
                    actor.attemptsTo(AcceptBrowserAlert.now());
                    actor.attemptsTo(SafeClick.on(PageMain.LOGO));
                }
            }
        );
    }
}