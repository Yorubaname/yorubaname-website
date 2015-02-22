package org.oruko.dictionary.dashboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dadepo Aderemi.
 */
@RestController
public class DashboardController {

    @RequestMapping(value = {"/upload", "/"})
    public String uploadFile() {
        return "TODO";
    }

}
