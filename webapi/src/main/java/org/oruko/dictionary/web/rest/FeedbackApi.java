package org.oruko.dictionary.web.rest;

import org.oruko.dictionary.model.NameEntryFeedback;
import org.oruko.dictionary.model.repository.NameEntryFeedbackRepository;
import org.oruko.dictionary.web.NameEntryService;
import org.oruko.dictionary.web.exception.GenericApiCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoint for interacting with feedback
 *
 * @author Dadepo Aderemi.
 */
@RestController
@RequestMapping("/v1/feedbacks")
public class FeedbackApi {

    private NameEntryService entryService;
    private NameEntryFeedbackRepository feedbackRepository;


    @Autowired
    public FeedbackApi(NameEntryService entryService,
                       NameEntryFeedbackRepository feedbackRepository) {
        this.entryService = entryService;
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Endpoint for getting all feedback within the system
     *
     * @return returns a list of all feedback
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NameEntryFeedback>> getFeedbacks() {
        final Sort sort = new Sort(Sort.Direction.DESC, "submittedAt");
        return new ResponseEntity<>(feedbackRepository.findAll(sort), HttpStatus.OK);
    }


    /**
     * Endpoint for getting all feedback within the system for a given name
     *
     * @param name the name to get all feedback for
     *
     * @return a list of all feedback for given name
     */
    @RequestMapping(params = "name", method = RequestMethod.GET)
    public ResponseEntity<List<NameEntryFeedback>> getFeedbacksForName(@RequestParam("name") String name) {
        final Sort sort = new Sort(Sort.Direction.DESC, "submittedAt");
        return new ResponseEntity<>(feedbackRepository.findByName(name, sort), HttpStatus.OK);
    }

    /**
     * Endpoint for deleting all feedback for a name
     *
     * @return {@link org.springframework.http.ResponseEntity} with string containing outcome of action
     */
    @RequestMapping(params = "name", method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> deleteAllFeedbackForName(@RequestParam("name") String name) {

        if (entryService.loadName(name) == null) {
            throw new GenericApiCallException(name + " does not exist. Cannot delete all feedback");
        }
        final Sort sort = new Sort(Sort.Direction.DESC, "submittedAt");
        List<NameEntryFeedback> feedbacks = feedbackRepository.findByName(name, sort);
        feedbacks.stream().forEach(feedback -> {
            feedbackRepository.delete(feedback);
        });

        return new ResponseEntity<>(response("All Feedback messages deleted for "+ name), HttpStatus.OK);
    }

    /**
     * Endpoint for deleting all feedback in the system
     *
     * @return {@link org.springframework.http.ResponseEntity} with string containing outcome of action
     */
    @RequestMapping(method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> deleteAllFeedBack() {
        feedbackRepository.deleteAll();
        return new ResponseEntity<>(response("All Feedback messages deleted"), HttpStatus.OK);
    }

    /**
     *  Endpoint for deleting a feedback by Id, for a name
     *
     * @param feedbackId the feedback to delete
     * @return the status of the delete operation
     */
    @RequestMapping(params = "id", method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> deleteAFeedback(@RequestParam("id") String feedbackId) {
        feedbackRepository.delete(Long.valueOf(feedbackId));
        return new ResponseEntity<>(response("Feedback message deleted"), HttpStatus.OK);
    }

    /**
     * Endpoint for adding a feedback
     *
     * @param postFeedback a map with key of "feedback" for the feedback
     * @return {@link org.springframework.http.ResponseEntity} with string containing outcome of action
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> addFeedback(@RequestBody Map<String, String> postFeedback) {
        String feedback = postFeedback.get("feedback");
        String name = postFeedback.get("name");

        if (feedback.isEmpty()) {
            throw new GenericApiCallException("Cannot give an empty feedback");
        }

        if (entryService.loadName(name) == null) {
            throw new GenericApiCallException(name + " does not exist. Cannot add feedback");
        }

        feedbackRepository.save(new NameEntryFeedback(name, feedback));
        return new ResponseEntity<>(response("Feedback added"), HttpStatus.CREATED);
    }

    //=====================================Helpers=========================================================//

    private HashMap<String, String> response(String value) {
        HashMap<String, String> response = new HashMap<>();
        response.put("message", value);
        return response;
    }
}
