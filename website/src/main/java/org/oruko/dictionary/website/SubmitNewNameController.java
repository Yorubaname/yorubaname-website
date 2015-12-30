package org.oruko.dictionary.website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String submitNameIndexPage(Model map, @RequestParam(value = "missing", required = false) String missingName) {
        map.addAttribute("geoLocations", apiService.getGeoLocations());
        map.addAttribute("title", "Submit Name");
        map.addAttribute("missingName", missingName);
        map.addAttribute("nameCount", apiService.getIndexedNameCount());
        return "submitname";
    }
}
