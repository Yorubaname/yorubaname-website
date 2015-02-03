package org.yorubaname.dictionary.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dadepo on 2/3/15.
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String indexPage() {
        return "index";
    }

}
