package org.oruko.dictionary.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by Dadepo Aderemi.
 */
@Controller
public class SubmitNewNameController {
    @RequestMapping("/submitname")
    public String indexPage(Map<String, String> map) {
        map.put("title", "Submit name");
        return "submitname";
    }
}
