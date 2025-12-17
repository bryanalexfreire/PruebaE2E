package pruebaE2E.hooks;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

public class Setup {

    @Before
    public void prepareStage() {
        OnStage.setTheStage(new OnlineCast());
    }
}

