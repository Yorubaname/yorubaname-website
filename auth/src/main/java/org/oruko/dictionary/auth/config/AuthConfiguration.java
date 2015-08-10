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
            .antMatchers(HttpMethod.DELETE, "/v1/names/delete").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/v1/suggest").permitAll()
            .antMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
            .antMatchers(HttpMethod.DELETE, "/v1/names/*").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/v1/**").hasAnyRole("ADMIN", "DASHBOARD")
            .antMatchers(HttpMethod.PUT, "/v1/**").hasAnyRole("ADMIN", "DASHBOARD")
            .antMatchers("/dashboardapp/**").permitAll()
            .antMatchers("/v1/**").permitAll()
            .and()
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                UserDetailsService userDetailsService) throws Exception {

        auth.userDetailsService(userDetailsService);
    }
}
