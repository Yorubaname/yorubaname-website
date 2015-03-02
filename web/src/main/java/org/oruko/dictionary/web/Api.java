package org.oruko.dictionary.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.oruko.dictionary.importer.ImportStatus;
import org.oruko.dictionary.importer.ImporterInterface;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.Name;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.oruko.dictionary.model.NameEntryService;
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
import java.util.UUID;
import javax.validation.Valid;

/**
 * End point for inserting and retrieving Name Entries
 * This would be the end point the clients would interact with to get names in and out of the dictionary
 * TODO Consider moving this as a stand alone service
 * Created by dadepo on 2/12/15.
 */
@RestController
public class Api {

    Logger logger = LoggerFactory.getLogger(Api.class);

    @Autowired
    NameEntryRepository nameEntryRepository;

    @Autowired
    DuplicateNameEntryRepository duplicateEntryRepository;

    @Autowired
    ImporterInterface importerInterface;

    @Autowired
    NameEntryService entryService;

    @RequestMapping(value = "/v1/name", method = RequestMethod.POST)
    public ResponseEntity<String> addName(@Valid NameEntry entry, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            entry.setName(entry.getName().toLowerCase());
            entryService.insertTakingCareOfDuplicates(entry);
            return new ResponseEntity<String>("success", HttpStatus.CREATED);
        }
        return new ResponseEntity<String>(formatErrorMessage(bindingResult), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/v1/names", method = RequestMethod.GET)
    public String getAllNames() throws JsonProcessingException {
        List<Name> names = new ArrayList<>();
        Iterable<NameEntry> allNameEntries = nameEntryRepository.findAll();

        allNameEntries.forEach(nameEntry -> {
            names.add(nameEntry.toName());
        });

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(names);
    }

    @RequestMapping(value = "/v1/names/{name}", method = RequestMethod.GET)
    public String getName(@RequestParam(value = "duplicates", required = false) boolean withDuplicates,
                          @PathVariable String name) throws JsonProcessingException {
        NameEntry nameEntry = nameEntryRepository.findByName(name);
        ObjectMapper mapper = new ObjectMapper();
        if (withDuplicates) {
            List<DuplicateNameEntry> duplicates = duplicateEntryRepository.findByName(name);
            HashMap<String, Object> duplicateEntries = new HashMap<String, Object>();

            duplicateEntries.put("mainEntry", nameEntry.toName());
            duplicateEntries.put("duplicates", duplicates);

            return mapper.writeValueAsString(duplicateEntries);
        }
        return mapper.writeValueAsString(nameEntry.toName());
    }

    @RequestMapping(value = "/v1/names/upload", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportStatus upload(@RequestParam("nameFiles") MultipartFile multipartFile) {
        //TODO Remove
        nameEntryRepository.deleteAll();
        Assert.state(!multipartFile.isEmpty(), "You can't upload an empty content package");
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
        return status;
    }

    // TODO add method authorization for methods like this
    @RequestMapping(value = "/v1/names/delete")
    public String deleteNames() {
        nameEntryRepository.deleteAll();
        return "Names Deleted";
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
