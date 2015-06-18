package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Controller for the search result pages
 * Created by Dadepo Aderemi.
 */
@Controller
public class SearchResultController {

    /**
     * Controller for page that displays multiple result for a search. i.e. ambiguous page
     * @param map model
     * @return returns the view name
     */
    @RequestMapping("/entries")
    public String searchNameQuery(@RequestParam(value = "q",required = false) String nameQuery, Model map) {
        if (nameQuery == null || nameQuery.isEmpty()) {
            return "redirect:/entries/all";
        }

        map.addAttribute("title", "Search results for query");

        List<Map<String, Object>> names = ApiService.searchName(nameQuery);
        map.addAttribute("names", names);

        return "searchresults";
    }

    @RequestMapping("/entries/all")
    public String showAll(Model map) {
        map.addAttribute("title", "All name entries");
        map.addAttribute("names", "Shows all entries. Supports pagination");
        return "searchresults";
    }


    @RequestMapping("/entries/{nameEntry}")
    public String showEntry(@PathVariable String nameEntry, Model map) {
        Map<String, Object> name = ApiService.getName(nameEntry);
        map.addAttribute("name", name);
        return "singleresult";
    }
}
