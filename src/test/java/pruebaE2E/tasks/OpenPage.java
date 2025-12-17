package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;
import pruebaE2E.userInterface.HomePageUI;

public class OpenPage {

    public static Performable loadPage() {
        return Task.where("Open the demoblaze home page",
                Open.url(HomePageUI.URL)
        );
    }
}

