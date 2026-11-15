package com.screenplay.project.stepdefinition;

import com.screenplay.project.model.Customer;
import com.screenplay.project.questions.CartPrices;
import com.screenplay.project.questions.CartTotal;
import com.screenplay.project.questions.PurchaseReceipt;
import com.screenplay.project.questions.SessionState;
import com.screenplay.project.tasks.ClickWhenReady;
import com.screenplay.project.tasks.CloseSession;
import com.screenplay.project.tasks.EmptyCart;
import com.screenplay.project.tasks.FindProduct;
import com.screenplay.project.tasks.MakeLogin;
import com.screenplay.project.tasks.OpenWeb;
import com.screenplay.project.tasks.RegisterCustomer;
import com.screenplay.project.tasks.ViewCart;
import com.screenplay.project.ui.PageCar;
import com.screenplay.project.ui.PageMain;
import com.screenplay.project.util.Timeouts;
import com.screenplay.project.util.config.TestAccount;
import com.screenplay.project.util.evidence.Evidence;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.questions.TextContent;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.containsText;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Step definitions of the purchase flow (PUR-01..PUR-06). Steps stay thin: a Screenplay Task
 * does the work, the real values the app showed are recorded once for both reports
 * (Serenity adds a screenshot after each step) and a named Question checks the result.
 */
public class DemoblazeGlue {

    private static final int EXPECTED_PRODUCTS = 2;
    private static final DateTimeFormatter RECEIPT_DATE = DateTimeFormatter.ofPattern("d/M/yyyy");

    @Given("{string} opens the store")
    public void opensTheStore(String actorName) {
        theActorCalled(actorName).attemptsTo(OpenWeb.onDemoblaze());
    }

    /** TC-01 / PUR-01: the navigation bar greets the signed-in customer. */
    @When("the customer signs in with the test account")
    public void signsIn() {
        Actor actor = theActorInTheSpotlight();
        var account = TestAccount.forCurrentBrowser();
        actor.attemptsTo(
                MakeLogin.withCredentials(account),
                WaitUntil.the(PageMain.WELCOME_USER, containsText(account.username()))
                        .forNoMoreThan(Timeouts.SERVER.toSeconds()).seconds()
        );
        Evidence.record("Signed in", "Navigation bar: " + SessionState.welcomeMessage().answeredBy(actor));
        actor.should(seeThat("Welcome message after sign-in", SessionState.welcomeMessage(),
                equalTo("Welcome " + account.username())));
        // precondition for the cart checks: the account may keep products from earlier sessions
        actor.attemptsTo(EmptyCart.first(), ClickWhenReady.on(PageMain.LOGO));
    }

    /** TC-02 / PUR-02: two products added, each "Product added" alert accepted (texts recorded). */
    @When("the customer adds two products to the cart")
    public void addsTwoProducts() {
        theActorInTheSpotlight().attemptsTo(FindProduct.andAddToCart());
    }

    /** TC-03 / PUR-03: the cart lists both products and the total is the sum of their prices. */
    @Then("the cart lists both products and the total is the sum of their prices")
    public void cartListsProductsAndTotal() {
        Actor actor = openCart();
        List<String> names = List.copyOf(TextContent.ofEach(PageCar.CART_PRODUCT_NAMES).answeredBy(actor));
        List<String> prices = List.copyOf(TextContent.ofEach(PageCar.CART_PRODUCT_PRICES).answeredBy(actor));
        Integer sum = CartPrices.sum().answeredBy(actor);
        Evidence.table("Cart contents", new String[]{"Product", "Price"}, cartRows(names, prices, sum,
                CartTotal.displayed().answeredBy(actor)));
        actor.should(
                seeThat("Number of products in the cart", CartPrices.count(), equalTo(EXPECTED_PRODUCTS)),
                seeThat("Cart total equals the sum of the prices", CartTotal.displayed(), equalTo(sum))
        );
    }

    /** TC-04 / PUR-04: the purchase form is completed and submitted. */
    @When("the customer places the order with {string} {string} {string} {string} {string} {string}")
    public void placesTheOrder(String name, String country, String city, String card, String month, String year) {
        Actor actor = openCart();
        actor.attemptsTo(
                RegisterCustomer.withData(new Customer(name, country, city, card, month, year)),
                WaitUntil.the(PageCar.PURCHASE_CONFIRMATION, containsText("Thank you for your purchase"))
                        .forNoMoreThan(Timeouts.SERVER.toSeconds()).seconds()
        );
        Evidence.record("Purchase receipt", PurchaseReceipt.text().answeredBy(actor));
    }

    /** TC-04 / PUR-05: the receipt is for the customer who placed the order. */
    @Then("the purchase is confirmed for {string}")
    public void purchaseConfirmedFor(String name) {
        theActorInTheSpotlight().should(
                seeThat("Customer name on the receipt", PurchaseReceipt.field("Name"), equalTo(name)));
    }

    /** TC-06 / PUR-05: the receipt shows the date of the purchase (today). */
    @Then("the purchase receipt shows today's date")
    public void receiptShowsToday() {
        Actor actor = theActorInTheSpotlight();
        String today = LocalDate.now().format(RECEIPT_DATE);
        Evidence.record("Receipt date", "Expected (today): " + today
                + "\nShown on the receipt: " + PurchaseReceipt.field("Date").answeredBy(actor));
        actor.should(seeThat("Receipt date is the purchase date (today)", PurchaseReceipt.field("Date"), equalTo(today)));
    }

    /** TC-05 / PUR-06: after signing out the "Log in" link is shown again. */
    @Then("the customer can sign out")
    public void canSignOut() {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(
                CloseSession.now(),
                WaitUntil.the(PageMain.BTN_LOGIN, isVisible()).forNoMoreThan(Timeouts.SERVER.toSeconds()).seconds()
        );
        actor.should(seeThat("'Log in' link visible after sign-out", SessionState.isSignedOut(), is(true)));
    }

    /** Opens the cart and waits until its rows and total are loaded (they come from a server call). */
    private static Actor openCart() {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(
                ViewCart.details(),
                WaitUntil.the(PageCar.CART_PRODUCT_NAMES, isVisible()).forNoMoreThan(Timeouts.SERVER.toSeconds()).seconds(),
                WaitUntil.the(PageCar.CART_TOTAL_PRICE, isVisible()).forNoMoreThan(Timeouts.SERVER.toSeconds()).seconds()
        );
        return actor;
    }

    private static List<String[]> cartRows(List<String> names, List<String> prices, Integer sum, Integer total) {
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < Math.max(names.size(), prices.size()); i++) {
            rows.add(new String[]{i < names.size() ? names.get(i).trim() : "?", i < prices.size() ? prices.get(i).trim() : "?"});
        }
        rows.add(new String[]{"Sum of prices", String.valueOf(sum)});
        rows.add(new String[]{"Total shown", String.valueOf(total)});
        return rows;
    }
}
