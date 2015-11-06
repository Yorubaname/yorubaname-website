package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller page to be dispalyed when entry was not found (disambiguous page)
 */

@Controller
public class NoEntryFoundController {
    @RequestMapping("/entry-not-found")
    public String noEntryFoundIndexPage(Model map) {
        map.addAttribute("geoLocations", ApiService.getGeoLocations());
        map.addAttribute("title", "Entry Not Found");
        return "entrynotfound";
    }
}