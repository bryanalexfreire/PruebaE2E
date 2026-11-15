package com.screenplay.project.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Runs the Cucumber scenarios with Serenity.
 * <ul>
 *   <li>Gradle ({@code gradlew test}) runs the generated features (build/generated/features), which
 *       hold the Excel rows of the data-driven outlines, and builds the Serenity report.</li>
 *   <li>Default selection: {@code not @wip and not @bug}. Known defects ({@code @bug}) are documented as
 *       scenarios but run only on request: {@code gradlew test -Ptags="@bug"}.</li>
 *   <li>Other filters: {@code gradlew test -Ptags="@TC-03"}.</li>
 *   <li>From the IDE, run tests with Gradle so the data and the Serenity report are produced.</li>
 * </ul>
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.screenplay.project.stepdefinition",
        plugin = {
            "json:build/cucumber-reports/json/cucumber.json",
            "html:build/cucumber-reports/html/cucumber.html",
            "summary"
        },
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "not @wip and not @bug"
)
public class RunnerTest {
}
