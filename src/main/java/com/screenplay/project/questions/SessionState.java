package com.screenplay.project.questions;

import com.screenplay.project.ui.PageMain;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.questions.Visibility;

/** Session state shown in the navigation bar. */
public final class SessionState {

    private SessionState() {
    }

    public static Question<String> welcomeMessage() {
        return Question.about("the welcome message in the navigation bar")
                .answeredBy(actor -> Text.of(PageMain.WELCOME_USER).answeredBy(actor).trim());
    }

    public static Question<Boolean> isSignedOut() {
        return Question.about("whether the 'Log in' link is visible")
                .answeredBy(actor -> Visibility.of(PageMain.BTN_LOGIN).answeredBy(actor));
    }
}
