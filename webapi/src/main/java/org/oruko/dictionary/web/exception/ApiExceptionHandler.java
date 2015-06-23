package org.oruko.dictionary.web.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for exception in API calls
 *
 * @author Dadepo Aderemi.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    private ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(GenericApiCallException.class)
    public ResponseEntity<String> handlerException(GenericApiCallException ex) throws JsonProcessingException {
        Map<String, Object> errMap = new HashMap<>();
        errMap.put("error", true);
        errMap.put("message", ex.getErrorMessage());
        return new ResponseEntity<>(mapper.writeValueAsString(errMap), ex.getStatusResponse());
    }
}
