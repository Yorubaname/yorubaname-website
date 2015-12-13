package org.oruko.dictionary.website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller page to be dispalyed when entry was not found (disambiguous page)
 */

@Controller
public class NoEntryFoundController {
    private ApiService apiService;

    @Autowired
    public NoEntryFoundController(ApiService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping("/entry-not-found")
    public String noEntryFoundIndexPage(Model map) {
        map.addAttribute("geoLocations", apiService.getGeoLocations());
        map.addAttribute("title", "Entry Not Found");
        return "entrynotfound";
    }
}