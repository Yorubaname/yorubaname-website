package org.oruko.dictionary.web.rest;

import org.oruko.dictionary.elasticsearch.ElasticSearchService;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.NameEntryService;
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

import javax.servlet.ServletContext;
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
    ServletContext servletContext;

    @Autowired
    private NameEntryService entryService;

    @Autowired
    private ElasticSearchService elasticSearchService;

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
        boolean isIndexed = elasticSearchService.indexName(entry.toIndexEntry());
        String message;
        if (isIndexed) {
            message = new StringBuilder(entry.getName()).append(" successfully indexed").toString();
            return new ResponseEntity<String>(message, HttpStatus.CREATED);
        }
        message = new StringBuilder(entry.getName()).append(" could not be indexed").toString();
        return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
    }


    /**
     * Endpoint that takes a name, looks it up in the repository and index the entry found
     *
     * @param name the name
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation
     */
    @RequestMapping(value = "/indexes/{name}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> indexEntryByName(@PathVariable String name) {
        String message;
        NameEntry nameEntry = entryService.loadName(name);
        if (nameEntry == null) {
            // name requested to be indexed not in the database
            message = new StringBuilder(name).append(" not found in the repository so not indexed").toString();
            return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
        }

        // TODO now cause of index failure cannot be propagated to client. Fix this. One way is to return
        // TODO a status object from the indexName method?
        boolean isIndexed = elasticSearchService.indexName(nameEntry.toIndexEntry());

        if (isIndexed) {
            nameEntry.isIndexed(true);
            entryService.saveName(nameEntry);
            message = new StringBuilder(name).append(" successfully indexed").toString();
            return new ResponseEntity<String>(message, HttpStatus.CREATED);
        }

        message = new StringBuilder(name).append(" could not be indexed").toString();
        return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint used to remove a name from the index.
     *
     * @param name the name to remove from the index.
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation.
     */
    @RequestMapping(value = "/indexes/{name}", method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deleteFromIndex(@PathVariable String name) {
        String message;
        boolean deleted = elasticSearchService.deleteFromIndex(name);
        if (deleted) {
            NameEntry nameEntry = entryService.loadName(name);
            if (nameEntry != null) {
                nameEntry.isIndexed(false);
                entryService.saveName(nameEntry);
            }
            message = new StringBuilder(name).append(" successfully removed from index").toString();
            return new ResponseEntity<String>(message, HttpStatus.OK);
        }
        message = new StringBuilder(name).append(" not found for deletion").toString();
        return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
    }
}
