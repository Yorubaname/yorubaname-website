package org.oruko.dictionary.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dadepo Aderemi.
 */
@Controller
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    DictionaryUserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DictionaryUserService userService;

    @RequestMapping("/")
    @ResponseBody
    public String indexPage() {
        return "Index Page";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE   )
    @ResponseBody
    //TODO
    public ResponseEntity<String> createUser(SignUpCredential credentials) {
        UserEntity user = new UserEntity();

        String username = credentials.getUserName();
        String email = credentials.getEmail();
        String password = credentials.getPassword();
        String role = credentials.getRole() != null ? credentials.getRole() : "lexicographer";

        List<Role> roles = new ArrayList<>();


        if (role.equalsIgnoreCase("admin")) {
            roles.addAll(Arrays.asList(Role.ADMIN, Role.LEXICOGRAPHER));
        } else if (role.equalsIgnoreCase("lexicographer")) {
            roles.add(Role.LEXICOGRAPHER);
        } else {
            roles.addAll(Arrays.asList(Role.LEXICOGRAPHER));
        }

        user.setRole(roles);
        user.setEmail(email);
        user.setUserName(username);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));

        try {
            userService.create(user);
            return new ResponseEntity<String>("success", HttpStatus.CREATED);
        } catch (Exception exception) {
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = "/createAdmin", method = RequestMethod.GET,
            produces = "text/plain")
    @ResponseBody
    public String createTestAdminData() {
        UserEntity user = new UserEntity();

        user.setRole(Arrays.asList(Role.ADMIN, Role.LEXICOGRAPHER));
        user.setEmail("admin@yorubaname.com");
        user.setUserName("admin");
        user.setPasswordHash(new BCryptPasswordEncoder().encode("admin"));
        userService.create(user);
        return "success";
    }


    @RequestMapping("/users")
    @ResponseBody
    public List<UserEntity> users() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public UserDetails login(@RequestBody Map<String, String> credentials) {

        String password = credentials.get("password");
        String email = credentials.get("email");
        UserEntity user = userRepository.findByEmail(email);

        if (new BCryptPasswordEncoder().matches(password, user.getPasswordHash())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            return userDetails;
        } else {
            throw new BadCredentialsException("Can not login with the given credentials");
        }

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
