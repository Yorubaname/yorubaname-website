package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Dadepo Aderemi.
 */
@Controller
//TODO remove?
public class ImproveEntryController {

    @RequestMapping("/improve-entry")
    public String improveEntryIndexPage(Model map) {
        map.addAttribute("title", "Improve Entry");
        return "improveentry";
    }
}
