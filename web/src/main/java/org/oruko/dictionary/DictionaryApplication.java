package org.oruko.dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class DictionaryApplication {

    /**
     * Main method used to kickstart and run the application
     * @param args arguments supplied to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(DictionaryApplication.class, args);
    }
}
