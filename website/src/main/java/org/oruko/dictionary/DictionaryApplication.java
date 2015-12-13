package org.oruko.dictionary;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;
import java.util.Locale;

/**
 * @author Dadepo Aderemi.
 */
@SpringBootApplication
@EnableCaching
@EnableSwagger2
@EnableWebSecurity //switches off auto configuration for spring security
public class DictionaryApplication extends WebMvcConfigurerAdapter {

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
                .apis(RequestHandlerSelectors.basePackage("org.oruko.dictionary.web.rest"))
                .build();
    }

    @Bean
    // This switches off the ContentNegotiatingViewResolver as instructed in this issue
    // https://github.com/spring-projects/spring-boot/issues/546. This is due to the issue
    // in spring-boot-starter-handlebars reported here
    // https://github.com/allegro/handlebars-spring-boot-starter/issues/7
    public ContentNegotiatingViewResolver viewResolver() {
        return null;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

}
