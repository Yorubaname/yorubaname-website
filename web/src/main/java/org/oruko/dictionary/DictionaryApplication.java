package org.oruko.dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableJpaRepositories
@EnableSwagger2
@EnableWebSecurity // Switches off the automatic spring boot configurations
public class DictionaryApplication {

    /**
     * Main method used to kick start and run the application
     * @param args arguments supplied to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(DictionaryApplication.class, args);
    }

    @Bean
    public Docket dictionaryApi() {

        ApiInfo apiInfo = new ApiInfo("Dictionary Api Documentation",
                                      "Documentation of the rest endpoint for the dictionary application",
                                      "v1",
                                      "", // termsOfServiceUrl
                                      "", // contact
                                      "", // license
                                      ""); // licenseUrl
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .build();
    }


}
