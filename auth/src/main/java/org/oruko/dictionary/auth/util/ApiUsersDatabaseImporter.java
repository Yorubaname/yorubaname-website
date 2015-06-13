package org.oruko.dictionary.auth.util;

import org.oruko.dictionary.auth.ApiUser;
import org.oruko.dictionary.auth.ApiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Utility class for populating API user with mock users
 *
 * Created by Dadepo Aderemi.
 */
@Component
public class ApiUsersDatabaseImporter {

    @Autowired
    private ApiUserRepository userRepository;

    @PostConstruct
    public void initApiUsers() {
        // demo admin
        ApiUser admin = new ApiUser();
        admin.setEmail("admin@example.com");
        admin.setPassword("admin");
        admin.setRoles(ApiUser.ROLE.ADMIN.getRole(),
                       ApiUser.ROLE.DASHBOARD.getRole(),
                       ApiUser.ROLE.BASIC.getRole());
        userRepository.save(admin);

        // dashboard user
        ApiUser dashboard = new ApiUser();
        dashboard.setEmail("dashboard@example.com");
        dashboard.setPassword("dashboard");
        dashboard.setRoles(ApiUser.ROLE.DASHBOARD.getRole(),
                           ApiUser.ROLE.BASIC.getRole());
        userRepository.save(dashboard);

        // lexi user
        ApiUser lexicographer = new ApiUser();
        lexicographer.setEmail("lexicographer@example.com");
        lexicographer.setPassword("lexicographer");
        lexicographer.setRoles(ApiUser.ROLE.LEXICOGRAPHER.getRole());
        userRepository.save(lexicographer);

        // normal user
        ApiUser basic = new ApiUser();
        basic.setEmail("basic@example.com");
        basic.setPassword("basic");
        basic.setRoles(ApiUser.ROLE.BASIC.getRole());
        userRepository.save(basic);
    }
}
