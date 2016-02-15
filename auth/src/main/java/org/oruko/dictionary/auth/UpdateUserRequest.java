package org.oruko.dictionary.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Represents User update request
 *
 * Created by Dadepo Aderemi.
 */
public class UpdateUserRequest {

    private final String username;
    private final String password;
    private final ArrayList<String> roles;

    @JsonCreator
    public UpdateUserRequest(@JsonProperty("username") String username,
                             @JsonProperty("password") String password,
                             @JsonProperty("roles") ArrayList<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public ArrayList<String> getRoles() {
        return roles;
    }
}
