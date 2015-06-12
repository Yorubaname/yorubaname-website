package org.oruko.dictionary.website;

import org.oruko.dictionary.model.NameDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for homepage
 * @author Dadepo Aderemi.
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String indexPage(Model map) {
        map.addAttribute("title", "Welcome - Yoruba Dictionary Application");
        List<NameDto> allNames = ApiService.getAllNames();

        map.addAttribute("latestSearches", allNames);
        map.addAttribute("latestAdditions", allNames);
        map.addAttribute("mostPopular", allNames);

        return "home";
    }

}
