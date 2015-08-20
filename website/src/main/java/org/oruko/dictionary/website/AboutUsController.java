package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for about us page
 */

@Controller
public class AboutUsController {
    @RequestMapping("/about-us")
    public String indexPage(Model map) {
        map.addAttribute("geoLocations", ApiService.getGeoLocations());
        map.addAttribute("title", "About Us");
        return "aboutus";
    }
}
