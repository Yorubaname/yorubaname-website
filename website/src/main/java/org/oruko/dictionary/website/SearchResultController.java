package org.oruko.dictionary.website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Controller for the search result pages
 * Created by Dadepo Aderemi.
 */
@Controller
public class SearchResultController {

    private ApiService apiService;

    @Autowired
    public SearchResultController(ApiService apiService) {
        this.apiService = apiService;
    }

    @Value("${app.host}")
    private String host;
    /**
     * Displays the result for a single entry
     * @param nameEntry the {@link org.oruko.dictionary.model.NameEntry}
     * @param map the map
     * @return the view name
     */
    @RequestMapping("/entries/{nameEntry}")
    public String showEntry(@PathVariable String nameEntry, Model map) {
        map.addAttribute("title", "Name Entry");
        map.addAttribute("host", host);
        if (!map.containsAttribute("name")) {
            final Map<String, Object> name = apiService.getName(nameEntry);
            if (name == null) {
                return "redirect:/entries?q=" + nameEntry;
            }
            map.addAttribute("name", name);
        }
        return "singleresult";
    }

    /**
     * Controller for page that displays multiple result for a search. i.e. ambiguous page
     * @param map model the model
     * @return returns the view name
     */
    @RequestMapping("/entries")
    public String searchNameQuery(@RequestParam(value = "q",required = false) String nameQuery,
                                  Model map,
                                  RedirectAttributes redirectAttributes)
            throws UnsupportedEncodingException {
        if (nameQuery == null || nameQuery.isEmpty()) {
            return "redirect:/entries/all";
        }

        map.addAttribute("title", "Search results for query");
        List<Map<String, Object>> names = apiService.searchName(nameQuery);

        if (names.size() == 1 && names.get(0).get("name").equals(nameQuery)) {
            nameQuery = URLEncoder.encode(nameQuery, "UTF-8");
            redirectAttributes.addFlashAttribute("name", names.get(0));
            return "redirect:/entries/"+nameQuery;
        }

        map.addAttribute("query", nameQuery);
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

}
