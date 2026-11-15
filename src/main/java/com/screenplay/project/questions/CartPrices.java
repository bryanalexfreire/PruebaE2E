package com.screenplay.project.questions;

import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.TextContent;

import java.util.Collection;

/** Prices of every product row in the cart: how many rows there are and their sum. */
public final class CartPrices {

    private CartPrices() {
    }

    public static Question<Integer> count() {
        return Question.about("the number of products in the cart").answeredBy(actor -> prices(actor).size());
    }

    public static Question<Integer> sum() {
        return Question.about("the sum of the product prices in the cart").answeredBy(actor ->
                prices(actor).stream()
                        .map(price -> price.replaceAll("[^0-9]", ""))
                        .filter(price -> !price.isEmpty())
                        .mapToInt(Integer::parseInt)
                        .sum());
    }

    private static Collection<String> prices(Actor actor) {
        return TextContent.ofEach(PageCar.CART_PRODUCT_PRICES).answeredBy(actor);
    }
}
