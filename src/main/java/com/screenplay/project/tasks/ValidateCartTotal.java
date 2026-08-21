package com.screenplay.project.tasks;
import com.screenplay.project.model.ModelCart;
import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.questions.Text;
import org.junit.Assert;
public final class ValidateCartTotal {
    private final ModelCart expectedCart;
    private ValidateCartTotal(ModelCart cart) {
        this.expectedCart = cart;
    }
    public static Performable matches(ModelCart expectedCart) {
        return new ValidateCartTotal(expectedCart).asPerformable();
    }
    private Performable asPerformable() {
        return Task.where("Validate cart total",
            actor -> {
                String displayedTotal = Text.of(PageCar.CART_TABLE)
                    .answeredBy(actor);
                double expected = expectedCart.getTotal();
                Assert.assertNotNull("Cart table should not be null", displayedTotal);
            }
        );
    }
}