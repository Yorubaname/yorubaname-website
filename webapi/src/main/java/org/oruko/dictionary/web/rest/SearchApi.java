package org.oruko.dictionary.web.rest;

import org.oruko.dictionary.elasticsearch.ElasticSearchService;
import org.oruko.dictionary.elasticsearch.IndexOperationStatus;
import org.oruko.dictionary.events.EventPubService;
import org.oruko.dictionary.events.NameIndexedEvent;
import org.oruko.dictionary.events.NameSearchedEvent;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.State;
import org.oruko.dictionary.web.NameEntryService;
import org.oruko.dictionary.web.event.RecentIndexes;
import org.oruko.dictionary.web.event.RecentSearches;
import org.oruko.dictionary.web.exception.GenericApiCallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handler for search functionality
 *
 * @author Dadepo Aderemi.
 */
@RestController
@RequestMapping("/v1/search")
public class SearchApi {

    private Logger logger = LoggerFactory.getLogger(SearchApi.class);

    private NameEntryService entryService;
    private ElasticSearchService elasticSearchService;
    private RecentSearches recentSearches;
    private RecentIndexes recentIndexes;
    private EventPubService eventPubService;

    /**
     * Public constructor for {@link SearchApi}
     *
     * @param entryService         service layer for interacting with name entries
     * @param elasticSearchService service layer for elastic search functions
     * @param recentSearches       object holding the recent searches in memory
     * @param recentIndexes        object holding the recent index names in memory
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


    /**
     * Endpoint for retrieving metadata information
     *
     * @return a {@link ResponseEntity} with the response message
     */
    @RequestMapping(value = "/meta", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getMetaData() {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("totalPublishedNames", elasticSearchService.getCount());
        return new ResponseEntity<>(metaData, HttpStatus.OK);
    }

    /**
     * Doea a full text search for name
     * @param searchTerm the name to search
     * @return the set of names found. If only one name is found then {@link NameSearchedEvent} is published
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Map<String, Object>> search(@RequestParam(value = "q", required = true) String searchTerm,
                                           HttpServletRequest request) {

        Set<Map<String, Object>> name = elasticSearchService.search(searchTerm);
        if (name != null
                && name.size() == 1
                && name.stream().allMatch(result -> result.get("name").equals(searchTerm))) {
            eventPubService.publish(new NameSearchedEvent(searchTerm, request.getRemoteAddr()));
        }
        return name;
    }

    @RequestMapping(value = "/autocomplete", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAutocomplete(@RequestParam(value = "q") Optional<String> searchQuery) {
        if (!searchQuery.isPresent()) {
            return Collections.EMPTY_LIST;
        }

        String query = searchQuery.get();
        return elasticSearchService.autocomplete(query);
    }


    @RequestMapping(value = "/alphabet/{alphabet}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Map<String, Object>> getByAlphabet(@PathVariable Optional<String> alphabet) {
        if (!alphabet.isPresent()) {
            return Collections.EMPTY_SET;
        }


        return elasticSearchService.listByAlphabet(alphabet.get());
    }

    @RequestMapping(value = "/{searchTerm}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findByName(@PathVariable String searchTerm, HttpServletRequest request) {

        Map<String, Object> name = elasticSearchService.getByName(searchTerm);

        if (name != null) {
            eventPubService.publish(new NameSearchedEvent(searchTerm, request.getRemoteAddr()));
        }
        return name;
    }

    @RequestMapping(value = "/activity", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String[] recentSearches(@RequestParam(value = "q", required = false) String activityType,
                                   HttpServletResponse response)
            throws IOException {
        if (activityType == null || activityType.isEmpty()) {
            response.sendRedirect("/v1/search/activity/all");
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
        Map<String, Object> response = new HashMap<>();
        NameEntry nameEntry = entryService.loadName(entry.getName());
        if (nameEntry == null) {
            response.put("message", "Cannot index entry. Name " + entry.getName() + " not in the database");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        IndexOperationStatus indexOperationStatus = elasticSearchService.indexName(entry);
        boolean isIndexed = indexOperationStatus.getStatus();
        String message = indexOperationStatus.getMessage();
        if (isIndexed) {
            publishNameIsIndexed(nameEntry);
            nameEntry.setIndexed(true);
            nameEntry.setState(State.PUBLISHED);
            entryService.saveName(nameEntry);
            response.put("message", message);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private void publishNameIsIndexed(NameEntry nameEntry) {
        eventPubService.publish(new NameIndexedEvent(nameEntry.getName()));
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
            response.put("message", name + " not found in the repository so not indexed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        IndexOperationStatus indexOperationStatus = elasticSearchService.indexName(nameEntry);
        boolean isIndexed = indexOperationStatus.getStatus();

        response.put("message", indexOperationStatus.getMessage());

        if (isIndexed) {
            publishNameIsIndexed(nameEntry);
            nameEntry.setIndexed(true);
            nameEntry.setState(State.PUBLISHED);
            entryService.saveName(nameEntry);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Endpoint that takes an array of names, look them up in the repository and index the entries found
     * <p>
     * It allows for batch indexing of names
     *
     * @param names the array of names
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation
     */
    @RequestMapping(value = "/indexes/batch", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> batchIndexEntriesByName(@RequestBody String[] names) {
        Map<String, Object> response = new HashMap<>();
        List<NameEntry> nameEntries = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        Arrays.stream(names).forEach(name -> {
            NameEntry entry = entryService.loadName(name);
            if (entry == null) {
                notFound.add(name);
            } else {
                nameEntries.add(entry);
            }
        });

        if (nameEntries.size() == 0) {
            // none of the names requested to be indexed in the database
            response.put("message", "none of the names was found in the repository so not indexed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        IndexOperationStatus indexOperationStatus = elasticSearchService.bulkIndexName(nameEntries);
        updateIsIndexFlag(nameEntries, true, indexOperationStatus);
        for (NameEntry nameEntry : nameEntries) {
            publishNameIsIndexed(nameEntry);
            nameEntry.setState(State.PUBLISHED);
            entryService.saveName(nameEntry);
        }
        return returnStatusMessage(notFound, indexOperationStatus);
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
                nameEntry.setIndexed(false);
                nameEntry.setState(State.NEW);
                entryService.saveName(nameEntry);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Endpoint used to remove a list of names from the index.
     *
     * @param names the names to remove from the index.
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation.
     */
    @RequestMapping(value = "/indexes/batch", method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> batchDeleteFromIndex(@RequestBody String[] names) {
        Map<String, Object> response = new HashMap<>();
        List<String> found = new ArrayList<>();
        List<NameEntry> nameEntries = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        Arrays.stream(names).forEach(name -> {
            NameEntry entry = entryService.loadName(name);
            if (entry == null) {
                notFound.add(name);
            } else {
                found.add(name);
                nameEntries.add(entry);
            }
        });

        if (found.size() == 0) {
            response.put("message", "none of the names was found in the repository so not attempting to remove");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        IndexOperationStatus indexOperationStatus = elasticSearchService.bulkDeleteFromIndex(found);
        updateIsIndexFlag(nameEntries, false, indexOperationStatus);
        return returnStatusMessage(notFound, indexOperationStatus);
    }

    private void updateIsIndexFlag(List<NameEntry> nameEntries, boolean flag,
                                   IndexOperationStatus indexOperationStatus) {
        if (indexOperationStatus.getStatus()) {
            List<NameEntry> updatedNames = nameEntries.stream().map(entry -> {
                entry.setIndexed(flag);
                return entry;
            }).collect(Collectors.toList());

            entryService.saveNames(updatedNames);
        }
    }

    private ResponseEntity<Map<String, Object>> returnStatusMessage(List<String> notFound,
                                                                    IndexOperationStatus indexOperationStatus) {
        Map<String, Object> response = new HashMap<>();
        boolean isIndexed = indexOperationStatus.getStatus();
        String responseMessage = indexOperationStatus.getMessage();

        if (notFound.size() != 0) {
            responseMessage += " following names ignored as they were not found in the database: "
                    + String.join(",", notFound);
        }

        response.put("message", responseMessage);
        if (isIndexed) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
