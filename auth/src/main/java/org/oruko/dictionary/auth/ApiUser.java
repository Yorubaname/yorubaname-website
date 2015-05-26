package org.oruko.dictionary.auth;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Representing the API user
 * Created by Dadepo Aderemi.
 */
@Entity
public class ApiUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
    private String password;
    private String roles;

    public ApiUser() {}

    public ApiUser(ApiUser user) {
        id = user.getId();
        email = user.getEmail();
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
        this.roles = String.join(",", roles);
    }


    public enum ROLE {
        ADMIN("ROLE_ADMIN"),DASHBOARD("ROLE_DASHBOARD"), BASIC("ROLE_BASIC");

        private final String role;

        private ROLE(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }
}
