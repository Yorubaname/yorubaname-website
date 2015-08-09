package org.oruko.dictionary.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.oruko.dictionary.events.NameUploadStatus;
import org.oruko.dictionary.importer.ImporterInterface;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.NameDto;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.SuggestedName;
import org.oruko.dictionary.model.repository.GeoLocationRepository;
import org.oruko.dictionary.web.GeoLocationTypeConverter;
import org.oruko.dictionary.web.NameEntryService;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * End point for inserting and retrieving NameDto Entries
 * This would be the end point the clients would interact with to get names in and out of the dictionary
 * Created by dadepo on 2/12/15.
 */
@RestController
public class NameApi {

    private Logger logger = LoggerFactory.getLogger(NameApi.class);

    private ImporterInterface importerInterface;
    private NameEntryService entryService;
    private GeoLocationRepository geoLocationRepository;
    private NameUploadStatus nameUploadStatus;


    /**
     * Public constructor for {@link NameApi}
     * @param importerInterface an implementation of {@link ImporterInterface} used for adding names in files
     * @param entryService an instance of {@link NameEntryService} representing the service layer
     * @param geoLocationRepository an instance of {@link GeoLocationRepository} for persiting {@link GeoLocation}
     */
    @Autowired
    public NameApi(ImporterInterface importerInterface, NameEntryService entryService,
                   GeoLocationRepository geoLocationRepository,
                   NameUploadStatus nameUploadStatus) {
        this.importerInterface = importerInterface;
        this.entryService = entryService;
        this.geoLocationRepository = geoLocationRepository;
        this.nameUploadStatus = nameUploadStatus;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(GeoLocation.class, new GeoLocationTypeConverter(geoLocationRepository));
    }

    /**
     * End point that is used to add a {@link org.oruko.dictionary.model.NameEntry}.
     * @param entry the {@link org.oruko.dictionary.model.NameEntry}
     * @param bindingResult {@link org.springframework.validation.BindingResult} used to capture result of validation
     * @return {@link org.springframework.http.ResponseEntity} with string containing error message.
     * "success" is returned if no error
     */
    @RequestMapping(value = "/v1/names", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> addName(@Valid @RequestBody NameEntry entry, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            entry.setName(entry.getName().trim().toLowerCase());
            entryService.insertTakingCareOfDuplicates(entry);

            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Name successfully added");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        throw new GenericApiCallException(formatErrorMessage(bindingResult));
    }

    /**
     * End point for receiving suggested names into the database. The names
     * suggested won't be added to the main database or search index until
     * approved by admin of the system.
     * @param suggestedName the name suggested
     * @param bindingResult the {@link BindingResult}
     * @return {@link org.springframework.http.ResponseEntity} with message if successful or not
     */
    @RequestMapping(value = "/v1/suggest/name", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> suggestName(@Valid @RequestBody SuggestedName suggestedName,
                                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GenericApiCallException(formatErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }

        entryService.addSuggestedName(suggestedName);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Suggested Name successfully added");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Returns all the suggested names
     * @return all suggested names
     */
    @RequestMapping(value = "/v1/suggest/name", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SuggestedName> getAllSuggestedNames() {
        return entryService.loadAllSuggestedNames();
    }

    /**
     * Get names that has been persisted. Supports ability to specify the count of names to return and the offset
     * @param pageParam a {@link Integer} representing the page (offset) to start the
     *                  result set from. 0 if none is given
     * @param countParam a {@link Integer} the number of names to return. 50 is none is given
     * @return the list of {@link org.oruko.dictionary.model.NameDto}
     * @throws JsonProcessingException JSON processing exception
     */
    @RequestMapping(value = "/v1/names", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NameDto> getAllNames(@RequestParam("page") Optional<Integer> pageParam,
                                  @RequestParam("count") Optional<Integer> countParam,
                                  @RequestParam("submittedBy") final Optional<String> submittedBy,
                                  @RequestParam(value = "indexed", required = false) final Optional<Boolean> indexed)
            throws JsonProcessingException {

        List<NameDto> names = new ArrayList<>();
        Iterable<NameEntry> allNameEntries = entryService.loadAllNames(pageParam, countParam);;

        allNameEntries.forEach(nameEntry -> {
            names.add(nameEntry.toNameDto());
        });

        // for filtering based on whether entry has been indexed
        Predicate<NameDto> filterBasedOnIndex = (name) -> {
            if (indexed.isPresent()) {
                return name.isIndexed().equals(indexed.get());
            } else {
                return true;
            }
        };

        // for filtering based on value of submitBy
        Predicate<NameDto> filterBasedOnSubmitBy = (name) -> {
            if (submittedBy.isPresent()) {
                return name.getSubmittedBy().trim().equalsIgnoreCase(submittedBy.get().toString().trim());
            } else {
                return true;
            }
        };

        return names.stream()
                    .filter(filterBasedOnIndex)
                    .filter(filterBasedOnSubmitBy)
                    .collect(Collectors.toCollection(ArrayList::new));

    }

    /**
     * Get the details of a name
     * @param withDuplicates flag whether to return duplicate entries for the name being retrieved
     * @param name the name whose details needs to be retrieved
     * @return a name serialized to a jason string
     * @throws JsonProcessingException json processing exception
     */
    @RequestMapping(value = "/v1/names/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getName(@RequestParam(value = "duplicates", required = false) boolean withDuplicates,
                          @PathVariable String name) throws JsonProcessingException {
        NameEntry nameEntry = entryService.loadName(name);
        if (nameEntry == null) {
            String errorMsg = "#NAME not found in the database".replace("#NAME", name);
            throw new GenericApiCallException(errorMsg);
        }

        if (withDuplicates) {
            List<DuplicateNameEntry> duplicates = entryService.loadNameDuplicates(name);
            HashMap<String, Object> duplicateEntries = new HashMap<>();

            duplicateEntries.put("mainEntry", nameEntry.toNameDto());
            duplicateEntries.put("duplicates", duplicates);

            return duplicateEntries;
        }
        return nameEntry.toNameDto();
    }


    /**
     * End point that is used to update a {@link org.oruko.dictionary.model.NameEntry}.
     * @param newNameEntry the {@link org.oruko.dictionary.model.NameEntry}
     * @param bindingResult {@link org.springframework.validation.BindingResult} used to capture result of validation
     * @return {@link org.springframework.http.ResponseEntity} with string containting error message.
     * "success" is returned if no error
     */
    @RequestMapping(value = "/v1/names/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PUT)
    public ResponseEntity<Map> updateName(@PathVariable String name,
                                             @Valid @RequestBody NameEntry newNameEntry,
                                             BindingResult bindingResult) {
        //TODO tonalMark is returning null on update. Fix
        if (!bindingResult.hasErrors()) {

            NameEntry oldNameEntry = entryService.loadName(name);

            if (oldNameEntry == null) {
                throw new GenericApiCallException(name + " not in database", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            entryService.updateName(oldNameEntry, newNameEntry);
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Name successfully updated");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        throw new GenericApiCallException(formatErrorMessage(bindingResult),
                                          HttpStatus.BAD_REQUEST);
    }


    /**
     * Endpoint for uploading names via spreadsheet
     *
     * @param multipartFile the spreadsheet file
     * @return the Import status
     * @throws JsonProcessingException Json processing exception
     */
    @RequestMapping(value = "/v1/names/upload", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("nameFiles") MultipartFile multipartFile)
            throws JsonProcessingException {
        Assert.state(!multipartFile.isEmpty(), "You can't upload an empty file");

        try {
            File file = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
            multipartFile.transferTo(file);

            // perform the importation of names in a seperate thread
            // client can poll /v1/names/uploading?q=progress for upload progress
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                importerInterface.importFile(file);
                file.delete();
            });

            Map<String, String> response = new HashMap<>();
            response.put("message", "File successfully imported");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (IOException e) {
            logger.warn("Failed to import File with error {}", e.getMessage());
            throw new GenericApiCallException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint that returns if a name uploading is ongoing and if so, provides
     * the number of total names to be uploaded and the numbers already uploaded.
     * @param parameter query parameter. Supports "progress"
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/v1/names/uploading", method = RequestMethod.GET)
    public ResponseEntity<NameUploadStatus> uploadProgress(@RequestParam("q") Optional<String> parameter)
            throws JsonProcessingException {
        if (parameter.isPresent()) {
            switch (parameter.get()) {
                case "progress":
                    return new ResponseEntity<>(nameUploadStatus, HttpStatus.OK);
                default:
                    throw new GenericApiCallException("query parameter [" + parameter.get() + "] not supported",
                                                      HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw  new GenericApiCallException("query parameter missing", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Endpoint for batch uploading of names. Names are sent as array of json from the client
     * @param nameEntries the array of {@link org.oruko.dictionary.model.NameEntry}
     * @param bindingResult {@link org.springframework.validation.BindingResult} used to capture result of validation
     * @return {@link org.springframework.http.ResponseEntity} with string containting error message.
     * "success" is returned if no error
     */
    @RequestMapping(value = "/v1/names/batch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< Map<String, String>> addName(@Valid @RequestBody NameEntry[] nameEntries, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && nameEntries.length != 0) {

            Arrays.stream(nameEntries).forEach(entry -> {
                entry.setName(entry.getName().trim().toLowerCase());
                entryService.insertTakingCareOfDuplicates(entry);
            });

            Map<String, String> response = new HashMap<>();
            response.put("message", "Names successfully imported");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        throw new GenericApiCallException(formatErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/v1/names",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> deleteAllNames() {
        entryService.deleteAllAndDuplicates();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Names deleted");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/names/{name}",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< Map<String, String>> deleteName(@PathVariable String name) {
        entryService.deleteNameEntryAndDuplicates(name);
        Map<String, String> response = new HashMap<>();
        response.put("message", name + "Deleted");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //=====================================Helpers=========================================================//

    private String formatErrorMessage(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            builder.append(error.getField() + " " + error.getDefaultMessage() + " ");
        }
        return builder.toString();
    }
}
