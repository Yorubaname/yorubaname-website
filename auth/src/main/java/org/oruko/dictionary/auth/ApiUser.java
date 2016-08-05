package org.oruko.dictionary.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.stream.Stream;

/**
 * Representing the API user
 * Created by Dadepo Aderemi.
 */
@Entity
public class ApiUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String email;
    private String username;
    private String password;
    private String roles;

    public ApiUser() {}

    public ApiUser(ApiUser user) {
        id = user.getId();
        email = user.getEmail();
        username = user.getUsername();
        password = user.getPassword();
        roles = user.getRoles();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String... roles) {
        final String[] withRoleAppended = Stream.of(roles)
                                                     .map(role -> "ROLE_" + role)
                                                     .toArray(size -> new String[size]);

        this.roles = String.join(",", withRoleAppended);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
