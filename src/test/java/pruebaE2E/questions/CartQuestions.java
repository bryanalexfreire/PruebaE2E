package pruebaE2E.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;
import net.serenitybdd.screenplay.Actor;

public class CartQuestions {

    public static Question<List<String>> productNames(){
        return actor -> {
            WebDriver driver = BrowseTheWeb.as(actor).getDriver();
            List<WebElement> elements = driver.findElements(By.cssSelector("#tbodyid tr td:nth-child(2)"));
            return elements.stream().map(WebElement::getText).collect(Collectors.toList());
        };
    }
}
