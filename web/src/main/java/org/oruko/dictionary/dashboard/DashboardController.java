package org.oruko.dictionary.dashboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles dashboard specific data request by the dashboard client app
 * @author Dadepo Aderemi.
 */
@RestController
@RequestMapping("/dApi")
public class DashboardController {

    @RequestMapping(method = RequestMethod.GET)
    public String getNameEntryColumns() {
        //TODO Implement
        return "";
    }
}
