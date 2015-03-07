package org.oruko.dictionary.dashboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles all data request by the dashboard client app
 * @author Dadepo Aderemi.
 */
@RestController
@RequestMapping("/dsh")
public class DashboardController {

    @RequestMapping(value = "/entry/columns", method = RequestMethod.GET)
    public String getNameEntryColumns() {
        //TODO Implement
        return "";
    }


}
