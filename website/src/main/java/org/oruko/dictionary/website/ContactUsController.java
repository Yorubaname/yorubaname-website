package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for contact us page
 */

@Controller
public class ContactUsController {

    @RequestMapping("/contact-us")
    public String contactUsIndexPage(Model map) {
        map.addAttribute("geoLocations", ApiService.getGeoLocations());
        map.addAttribute("title", "Contact Us");
        return "contactus";
    }
}
