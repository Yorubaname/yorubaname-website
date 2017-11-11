package org.oruko.dictionary;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Predicates;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
//@EnableWebSecurity //switches off auto configuration for spring security
public class DictionaryApplication extends WebMvcConfigurerAdapter {

    private final String LANG = "lang";

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
                .apis(Predicates.or(RequestHandlerSelectors.basePackage("org.oruko.dictionary.web.rest"),
                                    RequestHandlerSelectors.basePackage("org.oruko.dictionary.auth.rest")))
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
        cookieLocaleResolver.setCookieName(LANG);
        return cookieLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName(LANG);
        return lci;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.setBasename("classpath:/messages");
        return source;
    }


    @Bean
    public net.sf.ehcache.CacheManager ecacheManager() {
        CacheConfiguration allNames = new CacheConfiguration();
        allNames.setName("allNames");
        allNames.setMaxEntriesLocalHeap(0);
        allNames.setEternal(false);
        allNames.setTimeToIdleSeconds(1800);

        CacheConfiguration querySearchResult = new CacheConfiguration();
        querySearchResult.setName("querySearchResult");
        querySearchResult.setMaxEntriesLocalHeap(0);
        querySearchResult.setEternal(false);
        querySearchResult.setTimeToIdleSeconds(1800);

        CacheConfiguration names = new CacheConfiguration();
        names.setName("names");
        names.setMaxEntriesLocalHeap(0);
        names.setEternal(false);
        names.setTimeToIdleSeconds(1800);

//        CacheConfiguration nameCount = new CacheConfiguration();
//        nameCount.setName("nameCount");
//        nameCount.setMaxEntriesLocalHeap(0);
//        nameCount.setEternal(false);
//        nameCount.setTimeToIdleSeconds(1800);

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(allNames);
        config.addCache(querySearchResult);
        config.addCache(names);
        //config.addCache(nameCount);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ecacheManager());
    }


    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("/resources/")
                .setCachePeriod(86400);
    }

}
