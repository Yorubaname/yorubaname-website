package org.oruko.dictionary.model;

/**
 * Enum representing the various states a {@link NameEntry}
 * can be within the system.
 *
 * @author Dadepo Aderemi.
 */
public enum State {
    /**
     * Entry just got added to the system. Not visible to users who search yet
     */
    NEW,
    /**
     * Name has been indexed in the search engine, thus publish and users can find it when they search for it
     */
    PUBLISHED,
    /**
     * A name that was initially published but has been removed from the index
     */
    UNPUBLISHED,
    /**
     * A name that has been published (indexed into the search engine) was modified, thus needs to be re-indexed
     */
    MODIFIED
}
