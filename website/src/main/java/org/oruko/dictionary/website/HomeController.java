package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author Dadepo Aderemi.
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String indexPage(Map<String, String> map) {
        map.put("appName", "Yoruba Dictionary Application");
        return "website";
    }

}
