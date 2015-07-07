package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Controller for homepage
 * @author Dadepo Aderemi.
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String indexPage(Model map) {
        map.addAttribute("title", "Home");
        Map<String, String[]> searchActivity = ApiService.getSearchActivity();

        map.addAttribute("latestSearches", searchActivity.get("search"));
        map.addAttribute("latestAdditions", searchActivity.get("index"));
        map.addAttribute("mostPopular", searchActivity.get("popular"));

        return "home";
    }
}
