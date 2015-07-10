package org.oruko.dictionary.web.rest;

import org.oruko.dictionary.elasticsearch.ElasticSearchService;
import org.oruko.dictionary.elasticsearch.IndexOperationStatus;
import org.oruko.dictionary.events.EventPubService;
import org.oruko.dictionary.events.NameSearchedEvent;
import org.oruko.dictionary.events.RecentIndexes;
import org.oruko.dictionary.events.RecentSearches;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.web.NameEntryService;
import org.oruko.dictionary.web.exception.GenericApiCallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Handler for search functionality
 *
 * @author Dadepo Aderemi.
 */
@RestController
@RequestMapping("/v1/search")
public class SearchApi {

    private Logger logger = LoggerFactory.getLogger(SearchApi.class);

    private EventPubService eventPubService;
    private NameEntryService entryService;
    private ElasticSearchService elasticSearchService;
    private RecentSearches recentSearches;
    private RecentIndexes recentIndexes;

    /**
     * Public constructor for {@link SearchApi}
     * @param eventPubService instance of {@link EventPubService} for publishing events
     * @param entryService service layer for interacting with name entries
     * @param elasticSearchService service layer for elastic search functions
     * @param recentSearches object holding the recent searches in memory
     * @param recentIndexes object holding the recent index names in memory
     */
    @Autowired
    public SearchApi(EventPubService eventPubService, NameEntryService entryService,
                     ElasticSearchService elasticSearchService, RecentSearches recentSearches,
                     RecentIndexes recentIndexes) {
        this.eventPubService = eventPubService;
        this.entryService = entryService;
        this.elasticSearchService = elasticSearchService;
        this.recentSearches = recentSearches;
        this.recentIndexes = recentIndexes;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> search(@RequestParam(value = "q", required = true) String searchTerm,
                                            HttpServletRequest request) {

        List<Map<String, Object>> name = elasticSearchService.search(searchTerm);
        return name;
    }


    @RequestMapping(value = "/autocomplete", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public  List<String>  getAutocomplete(@RequestParam(value = "q") Optional<String> searchQuery) {
        if (!searchQuery.isPresent()) {
            return new ArrayList<>();
        }

        String query = searchQuery.get();
        return elasticSearchService.autocomplete(query);
    }

    @RequestMapping(value = "/{searchTerm}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findByName(@PathVariable String searchTerm, HttpServletRequest request) {

        Map<String, Object> name = elasticSearchService.getByName(searchTerm);

        if (name != null) {
            eventPubService.publish(new NameSearchedEvent(searchTerm, request.getRemoteAddr().toString()));
            return name;
        }

        throw new GenericApiCallException(searchTerm + " not found");
    }

    @RequestMapping(value = "/activity", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String[] recentSearches(@RequestParam(value = "q", required = false) String activityType, HttpServletResponse response)
            throws IOException {
        if (activityType == null || activityType.isEmpty()) {
            response.sendRedirect("/v1//search/activity/all");
        }

        if ("search".equalsIgnoreCase(activityType)) {
            return recentSearches.get();
        }

        if ("index".equals(activityType)) {
            return recentIndexes.get();
        }

        if ("popular".equals(activityType)) {
            return recentSearches.getMostPopular();
        }

        throw new GenericApiCallException("Activity type not recognized");
    }

    @RequestMapping(value = "/activity/all", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String[]> allActivity() {
        Map<String, String[]> activities = new HashMap<>();
        activities.put("search", recentSearches.get());
        activities.put("popular", recentSearches.getMostPopular());
        activities.put("index", recentIndexes.get());
        return activities;
    }

    /**
     * Endpoint to index a NameEntry sent in as JSON string.
     *
     * @param entry the {@link NameEntry} representation of the JSON String.
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation.
     */
    @RequestMapping(value = "/indexes", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> indexEntry(@Valid NameEntry entry) {
        IndexOperationStatus indexOperationStatus = elasticSearchService.indexName(entry);
        boolean isIndexed = indexOperationStatus.getStatus();
        String message = indexOperationStatus.getMessage();
        Map<String, Object> response = new HashMap<>();
        if (isIndexed) {
            response.put("message", message);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Endpoint that takes a name, looks it up in the repository and index the entry found
     *
     * @param name the name
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation
     */
    @RequestMapping(value = "/indexes/{name}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> indexEntryByName(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        NameEntry nameEntry = entryService.loadName(name);
        if (nameEntry == null) {
            // name requested to be indexed not in the database
            response.put("message",
                         new StringBuilder(name).append(" not found in the repository so not indexed").toString());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        IndexOperationStatus indexOperationStatus = elasticSearchService.indexName(nameEntry);
        boolean isIndexed = indexOperationStatus.getStatus();

        response.put("message", indexOperationStatus.getMessage());

        if (isIndexed) {
            nameEntry.isIndexed(true);
            entryService.saveName(nameEntry);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint used to remove a name from the index.
     *
     * @param name the name to remove from the index.
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation.
     */
    @RequestMapping(value = "/indexes/{name}", method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> deleteFromIndex(@PathVariable String name) {
        IndexOperationStatus indexOperationStatus = elasticSearchService.deleteFromIndex(name);
        boolean deleted = indexOperationStatus.getStatus();
        String message = indexOperationStatus.getMessage();
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        if (deleted) {
            NameEntry nameEntry = entryService.loadName(name);
            if (nameEntry != null) {
                nameEntry.isIndexed(false);
                entryService.saveName(nameEntry);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
