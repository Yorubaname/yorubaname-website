package org.oruko.dictionary;

/**
 * @author Dadepo Aderemi.
 */
public class ImportStatus {

    private Boolean hasErrors;
    private String errorMessages;

    public Boolean getHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(Boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessages = errorMessages;
    }
}
