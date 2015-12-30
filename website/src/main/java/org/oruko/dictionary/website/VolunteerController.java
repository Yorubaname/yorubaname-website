package org.oruko.dictionary.website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Dadepo Aderemi.
 */
@Controller
public class VolunteerController {
    private ApiService apiService;

    @Autowired
    public VolunteerController(ApiService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping("/volunteer")
    public String volunteerIndexPage(ModelMap map) {
        map.addAttribute("title", "Volunteer");
        map.addAttribute("nameCount", apiService.getIndexedNameCount());
        return "volunteer";
    }
}
