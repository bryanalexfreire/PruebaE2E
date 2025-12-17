package pruebaE2E.runners;


import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"pruebaE2E.glue", "pruebaE2E.hooks"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "@BuyDevicesHP"
)
public class RunnerTest {
}

