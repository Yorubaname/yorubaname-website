package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Dadepo Aderemi.
 */
@Controller
public class VolunteerController {

    @RequestMapping("/volunteer")
    public String volunteerIndexPage(ModelMap map) {
        map.addAttribute("title", "Volunteer");
        return "volunteer";
    }
}
