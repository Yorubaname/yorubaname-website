package org.oruko.dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;

/**
 * @author Dadepo Aderemi.
 */
@SpringBootApplication
@EnableSwagger2
@EnableWebSecurity //switches off auto configuration for spring security
public class DictionaryApplication {

    /**
     * Main method used to kick start and run the application
     * @param args arguments supplied to the application
     */
    public static void main(String[] args) {
        File pid = new File("app.pid");
        SpringApplication app = new SpringApplication(DictionaryApplication.class);
        app.addListeners(new ApplicationPidFileWriter(pid));
        app.run(args);
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
