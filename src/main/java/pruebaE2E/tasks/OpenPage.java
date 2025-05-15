package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

public class OpenPage implements Task {

    private final String url;

    public OpenPage(String url)
    {
        this.url = url;
    }

    public static Task loadPage() {
        return new OpenPage("https://www.demoblaze.com/");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(Open.url(url));
    }
}
