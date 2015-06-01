package org.oruko.dictionary.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for Authentication related endpoints
 * Created by Dadepo Aderemi.
 */
@RestController
public class AuthController {


    @RequestMapping("/dashboard/login")
    public Map<String, Object> login(Principal principal) {
        Map<String, Object> userDetails = new HashMap<>();
        Collection<GrantedAuthority> authorities = ((UsernamePasswordAuthenticationToken) principal).getAuthorities();

        ArrayList<String> roles = authorities.stream().map(auth -> {
            return auth.getAuthority();
        }).collect(Collectors.toCollection(ArrayList::new));

        userDetails.put("roles", roles);
        userDetails.put("username", principal.getName());
        return userDetails;
    }

}
