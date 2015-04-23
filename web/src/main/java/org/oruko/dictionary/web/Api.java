package org.oruko.dictionary.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.IOUtils;
import org.oruko.dictionary.elasticsearch.ElasticSearchService;
import org.oruko.dictionary.importer.ImportStatus;
import org.oruko.dictionary.importer.ImporterInterface;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.Name;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.NameEntryService;
import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.oruko.dictionary.tts.SimpleFileSystemAudioSegmentMapper;
import org.oruko.dictionary.tts.SimpleYorubaNameTokenizer;
import org.oruko.dictionary.tts.TextToSpeechService;
import org.oruko.dictionary.web.exception.GenericApiCallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.validation.Valid;

/**
 * End point for inserting and retrieving Name Entries
 * This would be the end point the clients would interact with to get names in and out of the dictionary
 * TODO Consider moving this as a stand alone service
 * Created by dadepo on 2/12/15.
 */
@RestController
public class Api {

    private Logger logger = LoggerFactory.getLogger(Api.class);

    @Autowired
    ServletContext servletContext;

    @Autowired
    private NameEntryRepository nameEntryRepository;

    @Autowired
    private DuplicateNameEntryRepository duplicateEntryRepository;

    @Autowired
    private ImporterInterface importerInterface;

    @Autowired
    private NameEntryService entryService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    /**
     * End point that is used to add a {@link org.oruko.dictionary.model.NameEntry}.
     * @param entry the {@link org.oruko.dictionary.model.NameEntry}
     * @param bindingResult {@link org.springframework.validation.BindingResult} used to capture result of validation
     * @return {@link org.springframework.http.ResponseEntity} with string containting error message.
     * "success" is returned if no error
     */
    @RequestMapping(value = "/v1/name", method = RequestMethod.POST, produces = "text/plain; charset=utf-8")
    public ResponseEntity<String> addName(@Valid NameEntry entry, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            entry.setName(entry.getName().toLowerCase());
            entryService.insertTakingCareOfDuplicates(entry);
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        }
        throw new GenericApiCallException(formatErrorMessage(bindingResult));
    }


    /**
     * End point that is used to update a {@link org.oruko.dictionary.model.NameEntry}.
     * @param entry the {@link org.oruko.dictionary.model.NameEntry}
     * @param bindingResult {@link org.springframework.validation.BindingResult} used to capture result of validation
     * @return {@link org.springframework.http.ResponseEntity} with string containting error message.
     * "success" is returned if no error
     */
    @RequestMapping(value = "/v1/name", method = RequestMethod.PUT, produces = "text/plain")
    public ResponseEntity<String> updateName(@Valid NameEntry entry, BindingResult bindingResult) {
        //TODO tonalMark is returning null on update. Fix
        if (!bindingResult.hasErrors()) {
            entryService.updateName(entry);
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        }
        return new ResponseEntity<>(formatErrorMessage(bindingResult), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Get names that has been persisted. Supports ability to specify the count of names to return and the offset
     * @param pageParam a {@link Integer} representing the page (offset) to start the
     *                  result set from. 0 if none is given
     * @param countParam a {@link Integer} the number of names to return. 50 is none is given
     * @return the list of {@link org.oruko.dictionary.model.Name}
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/v1/names", method = RequestMethod.GET)
    public List<Name> getAllNames(@RequestParam("page") Optional<Integer> pageParam,
                                  @RequestParam("count") Optional<Integer> countParam) throws JsonProcessingException {

        List<Name> names = new ArrayList<>();
        Iterable<NameEntry> allNameEntries = entryService.loadAllNames(pageParam, countParam);;

        allNameEntries.forEach(nameEntry -> {
            names.add(nameEntry.toName());
        });

        return names;
    }

    /**
     * Get the details of a name
     * @param withDuplicates flag whether to return duplicate entries for the name being retrieved
     * @param name the name whose details needs to be retrieved
     * @return a name serialized to a jason string
     * @throws JsonProcessingException json processing expectopm
     */
    @RequestMapping(value = "/v1/names/{name}", method = RequestMethod.GET)
    public Object getName(@RequestParam(value = "duplicates", required = false) boolean withDuplicates,
                          @PathVariable String name) throws JsonProcessingException {
        NameEntry nameEntry = entryService.loadName(name);
        if (nameEntry == null) {
            String errorMsg = "#NAME not found in the database".replace("#NAME", name);
            throw new GenericApiCallException(errorMsg);
        }

        if (withDuplicates) {
            List<DuplicateNameEntry> duplicates = entryService.loadNameDuplicates(name);
            HashMap<String, Object> duplicateEntries = new HashMap<String, Object>();

            duplicateEntries.put("mainEntry", nameEntry.toName());
            duplicateEntries.put("duplicates", duplicates);

            return duplicateEntries;
        }
        return nameEntry.toName();
    }

    @RequestMapping(value = "/v1/names/upload", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportStatus> upload(@RequestParam("nameFiles") MultipartFile multipartFile)
            throws JsonProcessingException {
        Assert.state(!multipartFile.isEmpty(), "You can't upload an empty file");

        ImportStatus status = new ImportStatus();
        File file = null;
        try {
            file = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
            multipartFile.transferTo(file);
            status = importerInterface.doImport(file);
        } catch (IOException e) {
            logger.warn("Failed to import File with error {}", e.getMessage());
            status.setErrorMessages(e.getMessage());
        } finally {
            file.delete();
        }

        if (status.hasErrors()) {
            return new ResponseEntity<ImportStatus>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<ImportStatus>(status, HttpStatus.CREATED);
    }

    // TODO add method authorization for methods like this
    @RequestMapping(value = "/v1/names/delete")
    public String deleteNames() {
        entryService.deleteAllAndDuplicates();
        return "Names Deleted";
    }

    // TODO this endpoint should be authorized
    /**
     * Endpoint to index a NameEntry sent in as JSON string.
     * @param entry the {@link org.oruko.dictionary.model.NameEntry} representation of the JSON String.
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation.
     */
    @RequestMapping(value = "/v1/search/indexes", method = RequestMethod.POST,
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

    // TODO this endpoint should be authorized
    /**
     * Endpoint that takes a name, looks it up in the repository and index the entry found
     * @param name the name
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation
     */
    @RequestMapping(value = "/v1/search/indexes/{name}", method = RequestMethod.PUT,
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

    // TODO this endpoint should be authorized
    /**
     * Endpoint used to remove a name from the index.
     * @param name the name to remove from the index.
     * @return a {@link org.springframework.http.ResponseEntity} representing the status of the operation.
     */
    @RequestMapping(value = "/v1/search/indexes/{name}", method = RequestMethod.DELETE,
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


    @RequestMapping(value = "v1/tts/{name}", method = RequestMethod.GET,
    produces = MediaType.TEXT_PLAIN_VALUE)
    public void doTTS(@PathVariable String name, HttpServletResponse response) {
        TextToSpeechService ttsService = new TextToSpeechService(new SimpleYorubaNameTokenizer(),
                                                                 new SimpleFileSystemAudioSegmentMapper());
        AudioInputStream audioInputStream = ttsService.convertToSpeech(name);

        try {
            response.setContentType("audio/wav");
            AudioSystem.write(audioInputStream,
                              AudioFileFormat.Type.WAVE, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(audioInputStream);
        }
    }

    //=====================================Helpers=========================================================//

    private String formatErrorMessage(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            builder.append(error.getField() + " " + error.getDefaultMessage());
        }
        return builder.toString();
    }
}
