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
        //TODO add an initializing step for ElasticSearch
        // 1. Get the ElasticSearch Service from the controller
        // 2. Use it to start the ElasticSearch instance
        // 3. If ElasticSearch cannot be started then mark ES as unavailable
        SpringApplication.run(DictionaryApplication.class, args);
    }
}
