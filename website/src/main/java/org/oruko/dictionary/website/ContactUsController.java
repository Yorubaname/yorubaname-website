package org.oruko.dictionary.website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for contact us page
 */

@Controller
public class ContactUsController {

    private ApiService apiService;

    @Autowired
    public ContactUsController(ApiService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping("/contact-us")
    public String contactUsIndexPage(Model map) {
        map.addAttribute("geoLocations", apiService.getGeoLocations());
        map.addAttribute("nameCount", apiService.getIndexedNameCount());
        map.addAttribute("title", "Contact Us");
        return "contactus";
    }
}
