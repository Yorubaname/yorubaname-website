package org.oruko.dictionary.website.staticpages;

import org.oruko.dictionary.website.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for about us page
 */

@Controller
public class AboutUsController {
    private ApiService apiService;

    @Autowired
    public AboutUsController(ApiService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping("/about-us")
    public String aboutUsIndexPage(Model map) {
        map.addAttribute("geoLocations", apiService.getGeoLocations());
        map.addAttribute("nameCount", apiService.getIndexedNameCount());
        map.addAttribute("title", "About Us");
        return "aboutus";
    }
}
