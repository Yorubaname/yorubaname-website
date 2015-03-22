package org.oruko.dictionary.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author Dadepo Aderemi.
 */
@Component
public class DictionaryUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {


    @Autowired
    private DictionaryUserService userService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity dictionaryUser = userService.getUserByEmail(email)
                                                   .orElseThrow(() -> new UsernameNotFoundException(String.format(
                                                           "User with email=%s was not found",
                                                           email)));

        return new LoggedInUser(dictionaryUser);
    }
}
