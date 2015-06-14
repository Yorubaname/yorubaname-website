package org.oruko.dictionary.web.rest;

import com.google.common.eventbus.EventBus;
import org.oruko.dictionary.elasticsearch.ElasticSearchService;
import org.oruko.dictionary.elasticsearch.IndexOperationStatus;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.web.NameEntryService;
import org.oruko.dictionary.web.events.EventBusFactory;
import org.oruko.dictionary.web.events.NameIndexedEvent;
import org.oruko.dictionary.web.events.NameSearchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

// TODO all endpoints should be authorized
/**
 * Handler for search functionality
 *
 * @author Dadepo Aderemi.
 */
@RestController
@RequestMapping("/v1/search")
public class SearchApi {

    private Logger logger = LoggerFactory.getLogger(SearchApi.class);

    @Autowired
    private EventBusFactory eventBusFactory;

    @Autowired
    private NameEntryService entryService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @RequestMapping(value = "/{searchTerm}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(String searchTerm, HttpServletRequest request) {

        EventBus eventBus = eventBusFactory.getEventBus();
        eventBus.post(new NameSearchedEvent(searchTerm, request.getRemoteAddr().toString()));
        eventBus.post(new NameIndexedEvent(searchTerm));

        return null;
    }

    /**
     * Endpoint to index a NameEntry sent in as JSON string.
     *
     * @param entry the {@link org.oruko.dictionary.model.NameEntry} representation of the JSON String.
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation.
     */
    @RequestMapping(value = "/indexes", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> indexEntry(@Valid NameEntry entry) {
        IndexOperationStatus indexOperationStatus = elasticSearchService.indexName(entry);
        boolean isIndexed = indexOperationStatus.getStatus();
        String message = indexOperationStatus.getMessage();
        if (isIndexed) {
            return new ResponseEntity<String>(message, HttpStatus.CREATED);
        }
        return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<String> indexEntryByName(@PathVariable String name) {
        String message;
        NameEntry nameEntry = entryService.loadName(name);
        if (nameEntry == null) {
            // name requested to be indexed not in the database
            message = new StringBuilder(name).append(" not found in the repository so not indexed").toString();
            return new ResponseEntity<String>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        IndexOperationStatus indexOperationStatus = elasticSearchService.indexName(nameEntry);
        boolean isIndexed = indexOperationStatus.getStatus();
        message = indexOperationStatus.getMessage();

        if (isIndexed) {
            nameEntry.isIndexed(true);
            entryService.saveName(nameEntry);
            return new ResponseEntity<String>(message, HttpStatus.CREATED);
        }

        return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<String> deleteFromIndex(@PathVariable String name) {
        IndexOperationStatus indexOperationStatus = elasticSearchService.deleteFromIndex(name);
        boolean deleted = indexOperationStatus.getStatus();
        String message = indexOperationStatus.getMessage();
        if (deleted) {
            NameEntry nameEntry = entryService.loadName(name);
            if (nameEntry != null) {
                nameEntry.isIndexed(false);
                entryService.saveName(nameEntry);
            }
            return new ResponseEntity<String>(message, HttpStatus.OK);
        }
        return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
    }
}
