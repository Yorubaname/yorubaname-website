package org.oruko.dictionary.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.oruko.dictionary.importer.ImportStatus;
import org.oruko.dictionary.importer.ImporterInterface;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.NameDto;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.NameEntryService;
import org.oruko.dictionary.model.repository.GeoLocationRepository;
import org.oruko.dictionary.web.GeoLocationTypeConverter;
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

    @Autowired
    private ImporterInterface importerInterface;

    @Autowired
    private NameEntryService entryService;

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(GeoLocation.class, new GeoLocationTypeConverter(geoLocationRepository));
    }

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
     * @return the list of {@link org.oruko.dictionary.model.NameDto}
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/v1/names", method = RequestMethod.GET)
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

            duplicateEntries.put("mainEntry", nameEntry.toNameDto());
            duplicateEntries.put("duplicates", duplicates);

            return duplicateEntries;
        }
        return nameEntry.toNameDto();
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
    @RequestMapping(value = "/v1/names/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAllNames() {
        entryService.deleteAllAndDuplicates();
        return new ResponseEntity<String>("Names Deleted", HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/names/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteName(@PathVariable String name) {
        entryService.deleteNameInEntryAndDuplicates(name);
        return new ResponseEntity<String>(name + "Deleted", HttpStatus.OK);

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
