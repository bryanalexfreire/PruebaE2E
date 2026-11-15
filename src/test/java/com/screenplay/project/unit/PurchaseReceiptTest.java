package com.screenplay.project.unit;

import com.screenplay.project.questions.PurchaseReceipt;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PurchaseReceiptTest {

    private static final String RECEIPT = String.join("\n",
            "Id: 6650932",
            "Amount: 1180 USD",
            "Card Number: 4111111111111111",
            "Name: Ana Test",
            "Date: 14/10/2026");

    @Test
    public void readsEachField() {
        assertThat(PurchaseReceipt.fieldOf(RECEIPT, "Amount"), is("1180 USD"));
        assertThat(PurchaseReceipt.fieldOf(RECEIPT, "Card Number"), is("4111111111111111"));
        assertThat(PurchaseReceipt.fieldOf(RECEIPT, "Name"), is("Ana Test"));
        assertThat(PurchaseReceipt.fieldOf(RECEIPT, "Date"), is("14/10/2026"));
    }

    @Test
    public void returnsEmptyTextForAMissingField() {
        assertThat(PurchaseReceipt.fieldOf(RECEIPT, "Country"), is(""));
    }

    @Test
    public void toleratesSpacesAndWindowsLineEnds() {
        assertThat(PurchaseReceipt.fieldOf("  Name:   Ana  \r\nDate: 1/2/2026", "Name"), is("Ana"));
    }

    @Test
    public void doesNotMatchAFieldWhoseNameOnlyEndsTheSame() {
        assertThat(PurchaseReceipt.fieldOf("Card Number: 1\nNumber: 2", "Number"), is("2"));
    }
}
