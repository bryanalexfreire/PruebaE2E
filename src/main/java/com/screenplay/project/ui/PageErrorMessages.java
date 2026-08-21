package com.screenplay.project.ui;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
public final class PageErrorMessages {
    public static final Target LOGIN_ERROR_MODAL = Target.the("login error modal")
        .located(By.xpath("//div[@class='alert alert-danger']"));
    public static final Target ERROR_MESSAGE = Target.the("error message")
        .located(By.xpath("//div[@class='alert']/text()"));
    public static final Target FIELD_REQUIRED_ERROR = Target.the("field required error")
        .located(By.xpath("//span[@class='error-message']"));
    public static final Target INVALID_CARD_ERROR = Target.the("invalid card error")
        .located(By.xpath("//div[contains(text(), 'invalid')]"));
    private PageErrorMessages() {}
}