package org.oruko.dictionary.web.exception;

import org.springframework.http.HttpStatus;

/**
 * A generic class used to capture error messages during execution of the API methods
 * Contains just two fields. a redundant (boolean) error field which can be easily use on the client
 * side to check of returns JSON is a n error object and a errorMessage fields that contains the error message
 *
 * @author Dadepo Aderemi.
 */
public class GenericApiCallException extends RuntimeException {

    private boolean error;
    private String errorMessage;
    private HttpStatus statusResponse = HttpStatus.BAD_REQUEST;


    /**
     * Public controller use for creating an instance of
     * {@link org.oruko.dictionary.web.exception.GenericApiCallException} it expects the error message to be
     * supplied as the constructor argument
     * @param errorMessage the error message.
     *
     */
    public GenericApiCallException(String errorMessage) {
        this.error = true;
        this.errorMessage = errorMessage;
    }


    /**
     * Public controller use for creating an instance of
     * {@link org.oruko.dictionary.web.exception.GenericApiCallException} it expects the error message to be
     * supplied as the constructor argument
     * @param errorMessage the error message.
     * @param statusResponse the http status response
     *
     */
    public GenericApiCallException(String errorMessage, HttpStatus statusResponse) {
        this.error = true;
        this.errorMessage = errorMessage;
        this.statusResponse = statusResponse;
    }

    /**
     * Gets the error message
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public HttpStatus getStatusResponse() {
        return statusResponse;
    }

}
