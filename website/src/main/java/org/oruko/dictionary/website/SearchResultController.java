package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * @param map model the model
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

    /**
     * Displays all the names in the dictionary. Supports pagination
     * @param map model the model
     * @return returns the view name
     */
    @RequestMapping("/entries/all")
    public String showAll(Model map) {
        map.addAttribute("title", "All name entries");
        map.addAttribute("names", "Shows all entries. Supports pagination");
        return "searchresults";
    }


    /**
     * Displays the result for a single entry
     * @param nameEntry the {@link org.oruko.dictionary.model.NameEntry}
     * @param map the map
     * @return the view name
     */
    @RequestMapping("/entries/{nameEntry}")
    public String showEntry(@PathVariable String nameEntry, Model map) {
        Map<String, Object> name = ApiService.getName(nameEntry);
		map.addAttribute("title", "Name Entry");
        map.addAttribute("name", name);
        return "singleresult";
    }
}
