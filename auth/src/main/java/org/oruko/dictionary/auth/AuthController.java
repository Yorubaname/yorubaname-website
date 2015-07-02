package org.oruko.dictionary.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for Authentication related endpoints
 * Created by Dadepo Aderemi.
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private ApiUserRepository userRepository;

    /**
     * Public constructor for {@link AuthController}
     * @param userRepository repository for persisting {@link ApiUser}
     */
    @Autowired
    public AuthController(ApiUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Login endpoint serves as the endpoint to validate a user details is valid and to get any associated
     * needed properties of the user
     * @param principal the {@link java.security.Principal}
     * @return a map of principal properties
     */
    @RequestMapping("/login")
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


    /**
     * //TODO look at how to inject the values as an object instead of a map
     * Endpoint to create user
     */
    @RequestMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity<String> create(@RequestBody Map<String, Object> createUserRequest) {
        String username = (String) createUserRequest.get("username");
        String email = (String) createUserRequest.get("email");
        String password = (String) createUserRequest.get("password");
        ArrayList<String> roles = (ArrayList<String>) createUserRequest.get("roles");
        roles = roles.stream().map(role -> role.toUpperCase()).collect(Collectors.toCollection(ArrayList::new));

        ApiUser apiUser = new ApiUser();
        apiUser.setEmail(email);
        apiUser.setPassword(password);
        apiUser.setUsername(username);
        apiUser.setRoles(roles.toArray(new String[roles.size()]));
        ApiUser savedUser = userRepository.save(apiUser);

        if (savedUser == null) {
            return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    /**
     * End point listing all users
     * @return list of all {@link org.oruko.dictionary.auth.ApiUser}
     */
    @RequestMapping(value = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public List<ApiUser> getUsers() {
        List<ApiUser> all = userRepository.findAll();
        all.forEach(user -> user.setPassword("xxx"));
        return all;
    }
}
