package org.oruko.dictionary.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.oruko.dictionary.model.NameEntryRepository;

/**
 * Created by dadepo on 2/4/15.
 */
@Controller
public class IndexController {

    @Autowired
    NameEntryRepository repository;

    @RequestMapping("/")
    public String indexPage() {
        return "index";
    }

}
