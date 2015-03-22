package org.oruko.dictionary.auth;

import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;

/**
 * @author Dadepo Aderemi.
 */
public class LoggedInUser extends org.springframework.security.core.userdetails.User {

    private UserEntity user;

    public LoggedInUser(UserEntity user) {
        super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public List<Role> getRole() {
        return user.getRole();
    }

    public Boolean isAdmin() {
        return user.getRole().contains(Role.ADMIN);
    }
}