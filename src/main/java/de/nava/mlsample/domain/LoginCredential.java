package de.nava.mlsample.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Niko Schmuck
 */
public class LoginCredential {

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String csrfToken;

    // To make Jackson Mapping Happy
    public LoginCredential() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    @JsonProperty("csrf_token")
    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }
}
