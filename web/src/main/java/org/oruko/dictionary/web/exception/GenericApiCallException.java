package org.oruko.dictionary.web.exception;

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

    /**
     * Public controller use for creating an instance of
     * {@link org.oruko.dictionary.web.exception.GenericApiCallException} it expects the error message to be
     * supplied as the constructor argument
     * @param errorMessage the error message
     */
    public GenericApiCallException(String errorMessage) {
        this.error = true;
        this.errorMessage = errorMessage;
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

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
