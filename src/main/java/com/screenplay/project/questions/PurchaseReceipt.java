package com.screenplay.project.questions;

import com.screenplay.project.ui.PageCar;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Details shown in the purchase confirmation dialog, one per line:
 * "Id: 123", "Amount: 1180 USD", "Card Number: ...", "Name: ...", "Date: 14/11/2026".
 */
public final class PurchaseReceipt {

    private PurchaseReceipt() {
    }

    /** Full receipt text, one detail per line. */
    public static Question<String> text() {
        return Question.about("the purchase receipt").answeredBy(PurchaseReceipt::read);
    }

    /** Value of one receipt field ("Amount", "Name", "Date"...). */
    public static Question<String> field(String name) {
        return Question.about("the '" + name + "' on the purchase receipt")
                .answeredBy(actor -> fieldOf(read(actor), name));
    }

    /**
     * Value of a field in a receipt text, or an empty text if the field is missing.
     * Pure function, covered by unit tests.
     */
    public static String fieldOf(String receipt, String name) {
        Matcher m = Pattern.compile("(?m)^\\s*" + Pattern.quote(name) + ":\\s*(.*?)\\s*$").matcher(receipt);
        return m.find() ? m.group(1) : "";
    }

    private static String read(Actor actor) {
        return Text.of(PageCar.PURCHASE_RECEIPT).answeredBy(actor).trim();
    }
}
