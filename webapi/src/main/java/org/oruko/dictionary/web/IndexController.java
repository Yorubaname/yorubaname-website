package org.oruko.dictionary.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dadepo on 2/4/15.
 */
@Controller
public class IndexController {

    @RequestMapping("/dashboard")
    public String showDashBoard() {
        return "redirect:http://localhost/dashboardapp";
    }

}
