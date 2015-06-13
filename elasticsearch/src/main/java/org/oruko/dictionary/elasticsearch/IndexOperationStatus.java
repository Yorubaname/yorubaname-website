package org.oruko.dictionary.elasticsearch;

/**
 * For communicating the status of operation on the index
 * Created by Dadepo Aderemi.
 */
public class IndexOperationStatus {

    private final boolean status;
    private final String message;

    public IndexOperationStatus(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
