package org.oruko.dictionary.website.staticpages;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Team page
 */

@Controller
public class TeamController {

    @RequestMapping("/team")
    public String aboutUsIndexPage(Model map) {
        map.addAttribute("title", "Team");
        return "team";
    }
}
