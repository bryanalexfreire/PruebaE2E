package com.screenplay.project.questions;

import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;

/** Total shown by the cart (the "Total" value under the product table). */
public final class CartTotal {

    private CartTotal() {
    }

    public static Question<Integer> displayed() {
        return Question.about("the cart total").answeredBy(actor -> {
            String digits = Text.of(PageCar.CART_TOTAL_PRICE).answeredBy(actor).replaceAll("[^0-9]", "");
            return digits.isEmpty() ? 0 : Integer.parseInt(digits);
        });
    }
}
