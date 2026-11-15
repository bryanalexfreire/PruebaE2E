package com.screenplay.project.unit;

import com.screenplay.project.model.Credentials;
import com.screenplay.project.util.config.TestAccount;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;

public class TestAccountTest {

    private static Properties file(String... keyValues) {
        Properties properties = new Properties();
        for (int i = 0; i < keyValues.length; i += 2) {
            properties.setProperty(keyValues[i], keyValues[i + 1]);
        }
        return properties;
    }

    @Test
    public void prefersTheAccountOfTheBrowser() {
        Map<String, String> env = Map.of(
                "DEMOBLAZE_USERNAME", "shared", "DEMOBLAZE_PASSWORD", "p1",
                "DEMOBLAZE_USERNAME_CHROME", "chrome-user", "DEMOBLAZE_PASSWORD_CHROME", "p2");
        assertThat(TestAccount.resolve("chrome", env, file()), is(new Credentials("chrome-user", "p2")));
    }

    @Test
    public void fallsBackToTheSharedAccount() {
        Map<String, String> env = Map.of("DEMOBLAZE_USERNAME", "shared", "DEMOBLAZE_PASSWORD", "p1");
        assertThat(TestAccount.resolve("firefox", env, file()), is(new Credentials("shared", "p1")));
    }

    @Test
    public void environmentWinsOverTheLocalFile() {
        Map<String, String> env = Map.of("DEMOBLAZE_USERNAME", "from-env", "DEMOBLAZE_PASSWORD", "p1");
        Properties local = file("DEMOBLAZE_USERNAME", "from-file", "DEMOBLAZE_PASSWORD", "p2");
        assertThat(TestAccount.resolve("", env, local).username(), is("from-env"));
    }

    @Test
    public void usesTheLocalFileWhenTheEnvironmentHasNothing() {
        Properties local = file("DEMOBLAZE_USERNAME_EDGE", "edge-user", "DEMOBLAZE_PASSWORD_EDGE", "p3");
        assertThat(TestAccount.resolve("edge", Map.of(), local).username(), is("edge-user"));
    }

    @Test
    public void ignoresAnIncompletePair() {
        Map<String, String> env = Map.of("DEMOBLAZE_USERNAME_CHROME", "only-user",
                "DEMOBLAZE_USERNAME", "shared", "DEMOBLAZE_PASSWORD", "p1");
        assertThat(TestAccount.resolve("chrome", env, file()).username(), is("shared"));
    }

    @Test
    public void explainsHowToConfigureAMissingAccount() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> TestAccount.resolve("chrome", Map.of(), file()));
        assertThat(error.getMessage(), containsString("DEMOBLAZE_USERNAME"));
        assertThat(error.getMessage(), containsString(TestAccount.LOCAL_FILE));
    }

    @Test
    public void neverPrintsThePassword() {
        assertThat(new Credentials("user", "s3cret").toString(), not(containsString("s3cret")));
    }
}
