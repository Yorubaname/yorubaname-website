package org.oruko.dictionary.website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for submit new name page
 * Created by Dadepo Aderemi.
 */
@Controller
public class SubmitNewNameController {

    private ApiService apiService;

    @Autowired
    public SubmitNewNameController(ApiService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping("/submitname")
    public String submitNameIndexPage(Model map) {
        map.addAttribute("geoLocations", apiService.getGeoLocations());
        map.addAttribute("title", "Submit Name");
        return "submitname";
    }
}
