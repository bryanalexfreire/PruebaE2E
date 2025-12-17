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

        // esperar a que la tabla del carrito esté visible antes de leer los productos
        theActorInTheSpotlight().attemptsTo(
                WaitFor.visibilityOf(CartUI.CART_TABLE)
        );

        // esperar a que haya al menos 2 elementos en la tabla del carrito
        theActorInTheSpotlight().attemptsTo(
                WaitForElementsCount.atLeast(CartUI.CART_PRODUCT_NAMES, 2)
        );

        List<String> productNames = CartQuestions.productNames().answeredBy(theActorInTheSpotlight());
        assertTrue("El carrito debe contener al menos 2 productos", productNames.size() >= 2);
    }

    @When("he enters user data {string} {string} {string} {string} {string} {string}")
    public void heEntersUserData(String name, String country, String city, String card, String month, String year) {
        theActorInTheSpotlight().attemptsTo(
                PurchaseFormPage.withData(name, country,city,card,month,year)
        );

        // afirmar que la confirmación contiene el texto esperado
        String confirmation = Text.of(CartUI.PURCHASE_CONFIRMATION).answeredBy(theActorInTheSpotlight());
        assertTrue("La confirmación debe contener el texto esperado", confirmation.contains("Thank you for your purchase"));

    }

    @Then("he closes the session")
    public void heReturnsMainPage() {
        theActorInTheSpotlight().attemptsTo(
                CloseSesionPage.closeAndLogout()
        );

    }
}
