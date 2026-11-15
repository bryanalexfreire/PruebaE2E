package com.screenplay.project.model;

import java.util.Objects;

/**
 * Sign-in data of a test account. The password never appears in logs or reports:
 * {@link #toString()} masks it.
 *
 * @param username account name
 * @param password account password (secret)
 */
public record Credentials(String username, String password) {

    public Credentials {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");
    }

    @Override
    public String toString() {
        return "Credentials[username=" + username + ", password=****]";
    }
}
