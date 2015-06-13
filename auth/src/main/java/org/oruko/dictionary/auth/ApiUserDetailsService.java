package org.oruko.dictionary.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service used by Spring security to load a user from repository
 *
 * Created by Dadepo Aderemi.
 */
@Service
public class ApiUserDetailsService implements UserDetailsService {

    @Autowired
    private ApiUserRepository apiUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApiUser apiUser = apiUserRepository.findByEmail(username);
        if (apiUser == null) {
            throw new UsernameNotFoundException("Not found");
        }
        return new CustomUserDetails(apiUser);
    }
}
