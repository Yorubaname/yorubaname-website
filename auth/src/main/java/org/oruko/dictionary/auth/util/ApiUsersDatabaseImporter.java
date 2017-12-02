package org.oruko.dictionary.auth.util;

import org.oruko.dictionary.auth.ApiUser;
import org.oruko.dictionary.auth.ApiUserRepository;
import org.oruko.dictionary.auth.config.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Utility class for populating API user with mock users
 *
 * Created by Dadepo Aderemi.
 */
@Component
public class ApiUsersDatabaseImporter {

    @Value("${app.host}")
    private String host;

    @Autowired
    private ApiUserRepository userRepository;

    @PostConstruct
    public void initApiUsers() {

        if (userRepository.count() == 0) {
            // demo admin
            ApiUser admin = new ApiUser();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
            admin.setRoles(Role.ADMIN.toString());
            userRepository.save(admin);
        }
        
        /**
         * Only initialize the database only when in dev
         */
        if (host.equalsIgnoreCase("localhost")) {

            // lexi user
            ApiUser lexicographer = new ApiUser();
            lexicographer.setUsername("lexicographer");
            lexicographer.setEmail("prolex@example.com");
            lexicographer.setPassword(new BCryptPasswordEncoder().encode("prolex"));
            lexicographer.setRoles(Role.PRO_LEXICOGRAPHER.toString());
            userRepository.save(lexicographer);

            // normal user
            ApiUser basic = new ApiUser();
            basic.setUsername("basic");
            basic.setEmail("basiclex@example.com");
            basic.setPassword(new BCryptPasswordEncoder().encode("basiclex"));
            basic.setRoles(Role.BASIC_LEXICOGRAPHER.toString());
            userRepository.save(basic);
        }
    }
}
