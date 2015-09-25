package org.oruko.dictionary.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;

/**
 * Represents User creation request
 *
 * Created by Dadepo Aderemi.
 */
public class CreateUserRequest {

    @NotEmpty(message = "Username is empty")
    private final String username;
    @NotEmpty(message = "Password is empty")
    private final String password;
    @NotEmpty(message = "Email is empty")
    private final String email;
    @NotEmpty(message = "No role is selected")
    private final ArrayList<String> roles;

    @JsonCreator
    public CreateUserRequest(@JsonProperty("username") String username,
                             @JsonProperty("password") String password,
                             @JsonProperty("email") String email,
                             @JsonProperty("roles") ArrayList<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }
}
