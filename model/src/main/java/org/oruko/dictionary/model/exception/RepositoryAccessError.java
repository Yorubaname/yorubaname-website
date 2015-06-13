package org.oruko.dictionary.model.exception;

/**
 * Class capturing exception that occurs during interacting with the repositories
 *
 * Created by Dadepo Aderemi.
 */
public class RepositoryAccessError extends RuntimeException {

    public RepositoryAccessError(String message) {
        super(message);
    }

}
