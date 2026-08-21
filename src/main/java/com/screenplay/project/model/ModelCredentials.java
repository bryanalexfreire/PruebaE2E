package com.screenplay.project.model;
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