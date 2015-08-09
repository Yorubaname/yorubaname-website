package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for submit new name page
 * Created by Dadepo Aderemi.
 */
@Controller
public class SubmitNewNameController {
    @RequestMapping("/submitname")
    public String indexPage(Model map) {
        map.addAttribute("geoLocations", ApiService.getGeoLocations());
        map.addAttribute("title", "Submit name");
        return "submitname";
    }
}
