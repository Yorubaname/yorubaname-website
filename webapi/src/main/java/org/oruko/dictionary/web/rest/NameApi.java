package org.oruko.dictionary.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.oruko.dictionary.events.EventPubService;
import org.oruko.dictionary.events.NameDeletedEvent;
import org.oruko.dictionary.importer.ImporterInterface;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.State;
import org.oruko.dictionary.model.repository.GeoLocationRepository;
import org.oruko.dictionary.web.GeoLocationTypeConverter;
import org.oruko.dictionary.web.NameEntryService;
import org.oruko.dictionary.web.event.NameUploadStatus;
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
import java.util.stream.Stream;
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
    private EventPubService eventPubService;


    /**
     * Public constructor for {@link NameApi}
     * @param importerInterface an implementation of {@link ImporterInterface} used for adding names in files
     * @param entryService an instance of {@link NameEntryService} representing the service layer
     * @param geoLocationRepository an instance of {@link GeoLocationRepository} for persiting {@link GeoLocation}
     */
    @Autowired
    public NameApi(ImporterInterface importerInterface, NameEntryService entryService,
                   GeoLocationRepository geoLocationRepository,
                   NameUploadStatus nameUploadStatus,
                   EventPubService eventPubService) {
        this.importerInterface = importerInterface;
        this.entryService = entryService;
        this.geoLocationRepository = geoLocationRepository;
        this.nameUploadStatus = nameUploadStatus;
        this.eventPubService = eventPubService;
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
    public ResponseEntity<Map<String, String>> addName(@Valid @RequestBody NameEntry entry,
                                                       BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            if (entry.getState() == null) {
                entry.setState(State.NEW);
            }

            if (!State.NEW.equals(entry.getState())) {
                // You can only add a name to the system with its state NEW
                throw new GenericApiCallException("Invalid State: A new entry needs to have the NEW state");
            }
            entry.setName(entry.getName().trim().toLowerCase());
            entryService.insertTakingCareOfDuplicates(entry);
            return new ResponseEntity<>(response("Name successfully added"), HttpStatus.CREATED);
        }
        throw new GenericApiCallException(formatErrorMessage(bindingResult));
    }

    /**
     * Endpoint for retrieving metadata information
     *
     * @return a {@link ResponseEntity} with the response message
     */
    @RequestMapping(value = "/v1/names/meta", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getMetaData() {

        final List<NameEntry> nameEntries = entryService.loadAllNames();
        long totalNames = ((Integer) nameEntries.size()).longValue();

        final long totalModifiedNames = nameEntries.stream()
                                                   .filter(nameEntry -> State.MODIFIED.equals(nameEntry.getState()))
                                                   .count();
        final long totalNewNames = nameEntries.stream()
                                              .filter(nameEntry -> State.NEW.equals(nameEntry.getState()))
                                              .count();
        final long totalPublishedNames = nameEntries.stream()
                                                    .filter(nameEntry -> State.PUBLISHED.equals(nameEntry.getState()))
                                                    .count();
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("totalNames", totalNames);
        metaData.put("totalNewNames", totalNewNames);
        metaData.put("totalModifiedNames", totalModifiedNames);
        metaData.put("totalPublishedNames", totalPublishedNames);

        return new ResponseEntity<>(metaData, HttpStatus.OK);
    }

    /**
     * Get names that has been persisted. Supports ability to specify the count of names to return and the offset
     * @param pageParam a {@link Integer} representing the page (offset) to start the
     *                  result set from. 0 if none is given
     * @param countParam a {@link Integer} the number of names to return. 50 is none is given
     * @return the list of {@link org.oruko.dictionary.model.NameEntry}
     * @throws JsonProcessingException JSON processing exception
     */
    @RequestMapping(value = "/v1/names", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NameEntry> getAllNames(@RequestParam("page") final Optional<Integer> pageParam,
                                  @RequestParam("count") final Optional<Integer> countParam,
                                  @RequestParam("all") final Optional<Boolean> all,
                                  @RequestParam("submittedBy") final Optional<String> submittedBy,
                                  @RequestParam("state") final Optional<State> state,
                                  @RequestParam(value = "indexed", required = false) final Optional<Boolean> indexed)
            throws JsonProcessingException {

        List<NameEntry> names = new ArrayList<>();
        List<NameEntry> allNameEntries;

        if (all.isPresent() && all.get() == true) {
            if (state.isPresent()) {
                allNameEntries = entryService.loadAllByState(state);
            } else {
                allNameEntries = entryService.loadAllNames();
            }
        } else {
            allNameEntries = entryService.loadByState(state, pageParam, countParam);
        }

        allNameEntries.forEach(nameEntry -> {
            names.add(nameEntry);
        });

        // for filtering based on whether entry has been indexed
        Predicate<NameEntry> filterBasedOnIndex = (name) -> {
            if (indexed.isPresent()) {
                return name.getIndexed().equals(indexed.get());
            } else {
                return true;
            }
        };

        // for filtering based on value of submitBy
        Predicate<NameEntry> filterBasedOnSubmitBy = (name) -> {
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
    public Object getName(@RequestParam("duplicates") final Optional<Boolean> withDuplicates,
                          @RequestParam("feedback") final Optional<Boolean> feedback,
                          @PathVariable String name) throws JsonProcessingException {
        NameEntry nameEntry = entryService.loadName(name);
        if (nameEntry == null) {
            String errorMsg = "#NAME not found in the database".replace("#NAME", name);
            throw new GenericApiCallException(errorMsg);
        }

        HashMap<String, Object> nameEntries = new HashMap<>();
        nameEntries.put("mainEntry", nameEntry);

        if (withDuplicates.isPresent() && (withDuplicates.get() == true)) {
            List<DuplicateNameEntry> duplicates = entryService.loadNameDuplicates(name);
            nameEntries.put("duplicates", duplicates);
        }

        if (feedback.isPresent() && (feedback.get() == true)) {
            nameEntries.put("feedback", entryService.getFeedback(nameEntry));
        }

        if (nameEntries.size() == 1 && nameEntries.get("mainEntry") != null) {
            return nameEntries.get("mainEntry");
        }

        return nameEntries;
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
            return new ResponseEntity<>(response("Name successfully updated"), HttpStatus.CREATED);
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

            return new ResponseEntity<>(response("File successfully imported"), HttpStatus.ACCEPTED);
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
    public ResponseEntity< Map<String, String>> addName(@Valid @RequestBody NameEntry[] nameEntries,
                                                        BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && nameEntries.length != 0) {
            entryService.bulkInsertTakingCareOfDuplicates(Arrays.asList(nameEntries));
            return new ResponseEntity<>(response("Names successfully imported"), HttpStatus.CREATED);
        }
        throw new GenericApiCallException(formatErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
    }


    /**
     * Endpoint for batch updating  of names. Names are sent as array of json from the client
     * @param nameEntries the array of {@link org.oruko.dictionary.model.NameEntry}
     * @param bindingResult {@link org.springframework.validation.BindingResult} used to capture result of validation
     * @return {@link org.springframework.http.ResponseEntity} with string containing error message.
     * "success" is returned if no error
     */
    @RequestMapping(value = "/v1/names/batch", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< Map<String, String>> updateNames(@Valid @RequestBody NameEntry[] nameEntries,
                                                         BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && nameEntries.length != 0) {

            //TODO refactor into a method
            List<NameEntry> notFoundNames = Stream.of(nameEntries)
                                             .filter(entry -> entryService.loadName(entry.getName()) == null)
                                            .collect(Collectors.toList());

            List<NameEntry> foundNames = new ArrayList<>(Arrays.asList(nameEntries));
            foundNames.removeAll(notFoundNames);

            if (foundNames.size() == 0) {
                return new ResponseEntity<>(response("none of the names was found in the repository so not indexed"),
                                            HttpStatus.BAD_REQUEST);
            }
            entryService.bulkUpdateNames(foundNames);

            List<String> notFound = notFoundNames.stream()
                                                 .map(notFoundName -> notFoundName.getName())
                                                 .collect(Collectors.toList());
            List<String> found = foundNames.stream()
                                             .map(foundName -> foundName.getName())
                                             .collect(Collectors.toList());

            String responseMessage = String.join(",", found) + " updated. ";

            if (notFound.size() > 0) {
                responseMessage += String.join(",",notFound) + " not updated as they were not found in the database";
            }

            return new ResponseEntity<>(response(responseMessage), HttpStatus.CREATED);
        }

        throw new GenericApiCallException(formatErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
    }

    /**
     * End points for deleting ALL names (and their duplicates) from the database
     * @return {@link org.springframework.http.ResponseEntity} with string containing error message.
     * "success" is returned if no error
     */
    @RequestMapping(value = "/v1/names",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> deleteAllNames() {
        entryService.deleteAllAndDuplicates();
        return new ResponseEntity<>(response("Names deleted"), HttpStatus.OK);
    }

    /**
     * End point for deleting a name (and its duplicates) from the database.
     * @param name the name to delete
     * @return {@link org.springframework.http.ResponseEntity} with string containing status message
     */
    @RequestMapping(value = "/v1/names/{name}",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< Map<String, String>> deleteName(@PathVariable String name) {
        if (entryService.loadName(name) == null) {
            throw new GenericApiCallException(name + " not found in the system so cannot be deleted");
        }
        entryService.deleteNameEntryAndDuplicates(name);
        publishNamesDeletedEvent(Arrays.asList(name));
        return new ResponseEntity<>(response(name + " Deleted"), HttpStatus.OK);
    }

    /**
     * Endpoint for deleting a list of names
     *
     * @param names the list of names to delete
     * @return {@link org.springframework.http.ResponseEntity} with string containing status message
     */
    @RequestMapping(value = "/v1/names/batch",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< Map<String, String>> batchDeleteName(@RequestBody String[] names) {

        List<String> notFoundNames = Stream.of(names)
                                     .filter(entry -> entryService.loadName(entry) == null)
                                     .collect(Collectors.toCollection(ArrayList::new));

        List<String> foundNames = new ArrayList<>(Arrays.asList(names));
        foundNames.removeAll(notFoundNames);

        if (foundNames.size() == 0) {
            return new ResponseEntity<>(response("No deletion as none of the names were found in the database."),
                                        HttpStatus.BAD_REQUEST);
        }

        entryService.batchDeleteNameEntryAndDuplicates(foundNames);
        publishNamesDeletedEvent(foundNames);

        String responseMessage = String.join(",",foundNames) + " deleted. ";
        if (notFoundNames.size() > 0) {
            responseMessage += String.join(",",notFoundNames) + " not deleted as they were not found in the database";
        }
        return new ResponseEntity<>(response(responseMessage), HttpStatus.OK);
    }

    private void publishNamesDeletedEvent(List<String> foundNames) {
        for (String name: foundNames) {
            eventPubService.publish(new NameDeletedEvent(name));
        }
    }

    //=====================================Helpers=========================================================//

    private String formatErrorMessage(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            builder.append(error.getField() + " " + error.getDefaultMessage() + " ");
        }
        return builder.toString();
    }


    private HashMap<String, String> response(String value) {
        HashMap<String, String> response = new HashMap<>();
        response.put("message", value);
        return response;
    }
}
