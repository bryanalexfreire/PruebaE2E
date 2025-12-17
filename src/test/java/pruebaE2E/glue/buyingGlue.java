package pruebaE2E.glue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

import pruebaE2E.tasks.*;
import pruebaE2E.questions.CartQuestions;
import pruebaE2E.interactions.WaitFor;
import pruebaE2E.interactions.WaitForElementsCount;
import pruebaE2E.userInterface.CartUI;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.util.List;
import static org.junit.Assert.assertTrue;

public class buyingGlue {

    @Given("{string} accesses the shopping portal")
    public void thatActorAccessesTheShoppingPortal(String actor) {
        theActorCalled(actor).attemptsTo(OpenPage.loadPage());
    }

    @When("he login in portal {string} {string}")
    public void heLoginInPortal(String username, String password) {
        theActorInTheSpotlight().attemptsTo(
                LoginPage.loginPage(username, password)
        );
    }

    @And("he adds devices to the cart")
    public void heAddsDevicesToTheCart() {
        theActorInTheSpotlight().attemptsTo(
                AddDevicesCartPage.addDevicesCartPage()
        );
    }

    @And("he sees the products in cart")
    public void heSeesTheProductsInCart() {
        theActorInTheSpotlight().attemptsTo(
                GoCartPage.goCartPage()
        );

        // wait for cart table to be visible before reading products
        theActorInTheSpotlight().attemptsTo(
                WaitFor.visibilityOf(CartUI.CART_TABLE)
        );

        // wait until there are at least 2 items in the cart table
        theActorInTheSpotlight().attemptsTo(
                WaitForElementsCount.atLeast(CartUI.CART_PRODUCT_NAMES, 2)
        );

        List<String> productNames = CartQuestions.productNames().answeredBy(theActorInTheSpotlight());
        assertTrue("The cart must contain at least 2 products", productNames.size() >= 2);
    }

    @When("he enters user data {string} {string} {string} {string} {string} {string}")
    public void heEntersUserData(String name, String country, String city, String card, String month, String year) {
        theActorInTheSpotlight().attemptsTo(
                PurchaseFormPage.withData(name, country,city,card,month,year)
        );

        // wait up to 2 seconds for the confirmation text to appear
        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(CartUI.PURCHASE_CONFIRMATION, WebElementStateMatchers.containsText("Thank you for your purchase")).forNoMoreThan(2).seconds()
        );

        // assert that the confirmation contains the expected text
        String confirmation = Text.of(CartUI.PURCHASE_CONFIRMATION).answeredBy(theActorInTheSpotlight());
        assertTrue("The confirmation must contain the expected text", confirmation.contains("Thank you for your purchase"));

    }

    @Then("he closes the session")
    public void heReturnsMainPage() {
        theActorInTheSpotlight().attemptsTo(
                CloseSessionPage.closeAndLogout()
        );

    }
}
