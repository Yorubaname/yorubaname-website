package org.oruko.dictionary.website;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with the NameAPI for the website
 *
 * Created by Dadepo Aderemi.
 */
@Service
public class ApiService {

    private static RestTemplate restTemplate = new RestTemplate();

    // TODO move to environment dependent configuration file
    public static String APIPATH = "http://127.0.0.1:8081/v1";

    @Cacheable("allNames")
    public List<Map<String, Object>> getAllNames() {
        return Arrays.asList(restTemplate.getForObject(APIPATH + "/names", Map[].class));
    }

    @Cacheable("querySearchResult")
    public Map<String, Object> getName(String nameQuery) {
        return restTemplate.getForObject(APIPATH  + "/search/" + nameQuery, Map.class);
    }

    @Cacheable("names")
    public List<Map<String, Object>> searchName(String nameQuery) {
        return Arrays.asList(restTemplate.getForObject(APIPATH + "/search/?q=" + nameQuery, Map[].class));
    }

    public Map<String, String[]> getSearchActivity() {
        return restTemplate.getForObject(APIPATH + "/search/activity/all", Map.class);
    }

    public List<Map<String, Object>> getGeoLocations() {
        return Arrays.asList(restTemplate.getForObject(APIPATH + "/admin/geolocations", Map[].class));
    }
}
