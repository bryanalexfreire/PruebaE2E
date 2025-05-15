package pruebaE2E.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

public class ReloadPage implements Task {

    private final String targetUrl;

    public ReloadPage(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public static ReloadPage withUrl(String targetUrl) {
        return new ReloadPage(targetUrl);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(targetUrl)
        );
    }
}
