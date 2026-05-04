package com.screenplay.project.stepdefinition;

import com.screenplay.project.model.ModelCredentials;
import com.screenplay.project.model.ModelCustomer;
import com.screenplay.project.tasks.CloseSession;
import com.screenplay.project.tasks.FindProduct;
import com.screenplay.project.tasks.MakeLogin;
import com.screenplay.project.tasks.OpenWeb;
import com.screenplay.project.tasks.RegisterCustomer;
import com.screenplay.project.tasks.ViewCart;
import com.screenplay.project.ui.PageCar;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.junit.Assert.assertTrue;

/**
 * Cucumber step definitions for the Demoblaze purchase flow.
 * Each method maps to a Gherkin step and delegates the actual work
 * to a Screenplay task, keeping the glue layer thin and readable.
 */
public class DemoblazeGlue {

    @Given("{string} accesses the shopping portal")
    public void accessesPortal(String actor) {
        theActorCalled(actor).attemptsTo(OpenWeb.onDemoblaze());
    }

    @When("he login in portal {string} {string}")
    public void loginPortal(String username, String password) {
        theActorInTheSpotlight().attemptsTo(MakeLogin.withCredentials(new ModelCredentials(username, password)));
    }

    @And("he adds devices to the cart")
    public void addsProducts() {
        theActorInTheSpotlight().attemptsTo(FindProduct.andAddToCart());
    }

    @And("he sees the products in cart")
    public void viewsCart() {
        theActorInTheSpotlight().attemptsTo(ViewCart.details());
        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(PageCar.CART_TABLE, WebElementStateMatchers.isVisible())
                        .forNoMoreThan(20).seconds()
        );
        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(PageCar.CART_PRODUCT_NAMES, WebElementStateMatchers.isVisible())
                        .forNoMoreThan(20).seconds()
        );
    }

    @When("he enters user data {string} {string} {string} {string} {string} {string}")
    public void entersUserData(String name, String country, String city, String card, String month, String year) {
        theActorInTheSpotlight().attemptsTo(
                RegisterCustomer.withData(new ModelCustomer(name, country, city, card, month, year))
        );

        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(PageCar.PURCHASE_CONFIRMATION, WebElementStateMatchers.containsText("Thank you for your purchase"))
                        .forNoMoreThan(20).seconds()
        );

        String confirmation = Text.of(PageCar.PURCHASE_CONFIRMATION).answeredBy(theActorInTheSpotlight());
        assertTrue("The confirmation must contain the expected text", confirmation.contains("Thank you for your purchase"));
    }

    @Then("he closes the session")
    public void closesSession() {
        theActorInTheSpotlight().attemptsTo(CloseSession.now());
    }
}

