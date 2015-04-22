package org.oruko.dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableJpaRepositories
@EnableWebSecurity // Switches off the automatic spring boot configurations
public class DictionaryApplication {

    /**
     * Main method used to kickstart and run the application
     * @param args arguments supplied to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(DictionaryApplication.class, args);
    }
}
