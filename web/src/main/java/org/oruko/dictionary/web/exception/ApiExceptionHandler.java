package org.oruko.dictionary.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for exception in API calls
 *
 * @author Dadepo Aderemi.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(GenericApiCallException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, String> handlerException(GenericApiCallException ex) {
        Map<String, String> errMap = new HashMap<>();
        errMap.put("error", "true");
        errMap.put("errorMessage", ex.getErrorMessage());
        return errMap;
    }
}
