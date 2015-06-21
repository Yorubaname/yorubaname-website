package org.oruko.dictionary.website;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with the NameAPI for the website
 *
 * Created by Dadepo Aderemi.
 */
public class ApiService {

    private static RestTemplate restTemplate = new RestTemplate();

    public static String APIPATH = "http://127.0.0.1:8081/v1";

    public static List<Map<String, Object>> getAllNames() {
        return Arrays.asList(restTemplate.getForObject(APIPATH + "/names", Map[].class));
    }

    public static Map<String, Object> getName(String nameQuery) {
        return restTemplate.getForObject(APIPATH  + "/search/" + nameQuery, Map.class);
    }

    public static List<Map<String, Object>> searchName(String nameQuery) {
        return Arrays.asList(restTemplate.getForObject(APIPATH + "/search/?q=" + nameQuery, Map[].class));
    }

    public static Map<String, String[]> getSearchActivity() {
        return restTemplate.getForObject(APIPATH + "/search/activity/all", Map.class);
    }
}
