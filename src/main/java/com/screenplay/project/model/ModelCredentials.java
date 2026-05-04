package com.screenplay.project.model;

/**
 * Holds the login credentials for a Demoblaze user.
 * This acts as a simple data transfer object (DTO) that is passed
 * into the {@code MakeLogin} task.
 */
public class ModelCredentials {
    private final String username;
    private final String password;

    public ModelCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

