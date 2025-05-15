package pruebaE2E.glue;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pruebaE2E.tasks.*;
import java.time.Duration;
import static net.serenitybdd.screenplay.GivenWhenThen.then;
import static net.serenitybdd.screenplay.GivenWhenThen.when;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;


public class buyingGlue {

    private WebDriver driver;
    private WebDriverWait wait;

    public void base(WebDriver driver) {

        this.driver =driver;

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(40)
        );

    }

    public void acceptAlert() {

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

    }

    private static final String ACTOR_NAME = "Bryan";

    @Before
    public void setTheStag(){

        OnStage.setTheStage(new OnlineCast());

    }

    @Given("^(.*) accesses the shopping portal$")
    public void thatBryanAccessesTheShoppingPortal(String actor) {
        
        theActorCalled(actor).attemptsTo(OpenPage.loadPage());

    }

    @When("^he signup in portal (.*) (.*)$")
    public void heSignupInPortal(String username, String password) {

        when(theActorInTheSpotlight()).attemptsTo(
                SignupPage.signupPage(username,password)
        );

    }

    @Then("^he login in portal (.*) (.*)$")
    public void heLoginInPortal(String username, String password) {

        then(theActorInTheSpotlight()).attemptsTo(
                LoginPage.loginPage(username,password)
        );

    }

    @And("he adds devices to the cart")
    public void heAddsDevicesToTheCart() {

        theActorInTheSpotlight().attemptsTo(
                AddDevicesCartPage.addDevicesCartPage()
        );

    }

    @And("^he sees the products in cart$")
    public void heSeesTheProductsInCart() {

        theActorInTheSpotlight().attemptsTo(
                GoCartPage.goCartPage()
        );

    }
    @When("^he enters user data (.*) (.*) (.*) (.*) (.*) (.*)$")
    public void heEntersUserDataBryanEcuadorQuitoAgosto(String name, String country, String city, String card, String month, String year) {

        theActorInTheSpotlight().attemptsTo(
                PurchaseFormPage.withData(name, country,city,card,month,year)
        );

    }
    @Then("^he returns main page$")
    public void heReturnsMainPage() {

    }
}
