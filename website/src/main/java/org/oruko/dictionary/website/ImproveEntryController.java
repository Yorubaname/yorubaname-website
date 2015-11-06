package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by Dadepo Aderemi.
 */
@Controller
public class ImproveEntryController {

    @RequestMapping("/improve-entry")
    public String improveEntryIndexPage(Map<String, String> map) {
        map.put("title", "Improve Entry");
        return "improveentry";
    }
}
