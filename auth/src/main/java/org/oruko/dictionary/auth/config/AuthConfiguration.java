package org.oruko.dictionary.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Context configuration
 * Created by Dadepo Aderemi.
 */
@Configuration
@EnableWebMvcSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                // dashboard
            .antMatchers("/dashboardapp/**").permitAll()
                // suggest auth
            .antMatchers(HttpMethod.POST, "/v1/suggest").permitAll()
            .antMatchers(HttpMethod.DELETE, "/v1/suggest/*").hasAnyRole("ADMIN", "LEXICOGRAPHER")
                // feedback auth
            .antMatchers(HttpMethod.POST, "/v1/*/feedback").permitAll()
            .antMatchers(HttpMethod.DELETE, "/v1/*/feedback").hasAnyRole("ADMIN")
                // auth auth
            .antMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                // names endpoint auth
            .antMatchers(HttpMethod.DELETE, "/v1/names/delete").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/v1/names/*").hasRole("ADMIN")
                // search endpoint auth
            .antMatchers(HttpMethod.POST, "/v1/search/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "/v1/search/*").hasRole("ADMIN")
                // if none of the pattern above matches then stick to the rule
                // that only Admin and Lexicographer can post and put
            .antMatchers(HttpMethod.POST, "/v1/**").hasAnyRole("ADMIN", "LEXICOGRAPHER")
            .antMatchers(HttpMethod.PUT, "/v1/**").hasAnyRole("ADMIN", "LEXICOGRAPHER")
                // Well get should be available to all
            .antMatchers(HttpMethod.GET, "/v1/**").permitAll()
            .antMatchers("/v1/**").permitAll() //TODO revisit. Might be redundant
            .and()
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                UserDetailsService userDetailsService) throws Exception {

        auth.userDetailsService(userDetailsService);
    }
}
