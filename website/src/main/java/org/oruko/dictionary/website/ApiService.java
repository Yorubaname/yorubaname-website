package org.oruko.dictionary.website;

import org.oruko.dictionary.model.NameDto;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Service for interacting with the NameAPI for the website
 *
 * Created by Dadepo Aderemi.
 */
public class ApiService {

    private static RestTemplate restTemplate = new RestTemplate();

    public static String APIPATH = "http://127.0.0.1:8081/v1";

    public static List<NameDto> getAllNames() {
        return Arrays.asList(restTemplate.getForObject(APIPATH + "/names", NameDto[].class));
    }
}
