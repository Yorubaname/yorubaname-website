package org.oruko.dictionary.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dadepo Aderemi.
 */
@RestController
@RequestMapping("/auth")
public class MainController {

    @RequestMapping("/index")
    @ResponseBody
    public String toDo() {
        return "todo";
    }
}
