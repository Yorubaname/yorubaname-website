package org.oruko.dictionary.auth.config;

import org.oruko.dictionary.auth.ApiUser;
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
                // suggest auth
            .antMatchers(HttpMethod.POST, "/v1/suggest").permitAll()
            .antMatchers(HttpMethod.GET, "/v1/suggest/*").hasAnyRole(Role.ADMIN.toString(),
                                                                        Role.PRO_LEXICOGRAPHER.toString(),
                                                                        Role.BASIC_LEXICOGRAPHER.toString())
            .antMatchers(HttpMethod.DELETE, "/v1/suggest/*").hasAnyRole(Role.ADMIN.toString(),
                                                                        Role.PRO_LEXICOGRAPHER.toString())
                // feedback auth
            .antMatchers(HttpMethod.GET, "/v1/names/feedbacks").hasAnyRole(Role.ADMIN.toString(),
                                                                           Role.PRO_LEXICOGRAPHER.toString(),
                                                                           Role.BASIC_LEXICOGRAPHER.toString())
            .antMatchers(HttpMethod.POST, "/v1/*/feedback").permitAll()
            .antMatchers(HttpMethod.DELETE, "/v1/*/feedback").hasAnyRole(Role.ADMIN.toString(),
                                                                         Role.PRO_LEXICOGRAPHER.toString())
                // authentication auth
            .antMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
            .antMatchers(HttpMethod.POST, "/v1/auth/create").hasRole(Role.ADMIN.toString())
            .antMatchers(HttpMethod.GET, "/v1/auth/users").hasAnyRole(Role.ADMIN.toString(),
                                                                      Role.PRO_LEXICOGRAPHER.toString(),
                                                                      Role.BASIC_LEXICOGRAPHER.toString())
                // names endpoint auth
            .antMatchers(HttpMethod.DELETE, "/v1/names/delete").hasRole(Role.ADMIN.toString())
            .antMatchers(HttpMethod.PUT, "/v1/names/*").hasAnyRole(Role.ADMIN.toString(),
                                                                   Role.PRO_LEXICOGRAPHER.toString(),
                                                                   Role.BASIC_LEXICOGRAPHER.toString())
            .antMatchers(HttpMethod.POST, "/v1/names/*").hasAnyRole(Role.ADMIN.toString(),
                                                                 Role.PRO_LEXICOGRAPHER.toString(),
                                                                 Role.BASIC_LEXICOGRAPHER.toString())
            .antMatchers(HttpMethod.DELETE, "/v1/names/*").hasRole(Role.ADMIN.toString())
                // search endpoint auth
            .antMatchers(HttpMethod.POST, "/v1/search/*").hasAnyRole(Role.ADMIN.toString(),
                                                                  Role.PRO_LEXICOGRAPHER.toString())
            .antMatchers(HttpMethod.PUT, "/v1/search/*").hasAnyRole(Role.ADMIN.toString(),
                                                                    Role.PRO_LEXICOGRAPHER.toString())
            .antMatchers(HttpMethod.DELETE, "/v1/search/*").hasAnyRole(Role.ADMIN.toString(),
                                                                    Role.PRO_LEXICOGRAPHER.toString())

                // if none of the pattern above matches then stick to the rule
                // that only Admin and Lexicographer can post and put
            .antMatchers(HttpMethod.POST, "/v1/**").hasAnyRole(Role.ADMIN.toString(),
                                                               Role.PRO_LEXICOGRAPHER.toString())
            .antMatchers(HttpMethod.PUT, "/v1/**").hasAnyRole(Role.ADMIN.toString(),
                                                              Role.PRO_LEXICOGRAPHER.toString())
                // Well get should be available to all
            .antMatchers(HttpMethod.GET, "/v1/**").permitAll()
            .and()
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                UserDetailsService userDetailsService) throws Exception {

        auth.userDetailsService(userDetailsService);
    }
}
