package com.screenplay.project.runners;
import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;
/**
 * Universal test runner that executes scenarios based on Cucumber tags.
 * 
 * Tags can be specified via:
 * 1. System property: -Dtags="@smoke"
 * 2. Gradle property: -Ptags="@smoke"
 * 3. Default: runs all features
 * 
 * Examples:
 *   gradlew test -Ptags="@smoke"
 *   gradlew test -Ptags="@authentication"
 *   gradlew test -Ptags="@cart_management"
 *   gradlew test -Ptags="@checkout"
 *   gradlew test -Ptags="@positive" 
 *   gradlew test -Ptags="@negative"
 *   gradlew test -Ptags="@smoke,@positive"  (multiple tags with AND logic)
 *   gradlew test -Ptags="@smoke or @positive"  (multiple tags with OR logic)
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.screenplay.project.stepdefinition"},
    plugin = {
        "json:build/cucumber-reports/json/cucumber.json",
        "html:build/cucumber-reports/html/cucumber.html",
        "summary"
    },
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "@BuyDevicesHP_DDT"
)
public class RunnerTest {
}