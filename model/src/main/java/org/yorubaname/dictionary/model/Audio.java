package org.yorubaname.dictionary.model;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for all Audio resources
 * Created by dadepo on 2/11/15.
 */
public interface Audio {
    /**
     * The name of the audio content (typically the original filename).
     *
     * @return the name of the Audio resource
     */
    String fileName();

    /**
     * The content type of the audio file that has been saved
     *
     * @return a string with the content type
     */
    String contentType();

    /**
     * Get the audio file as InputStream
     *
     * !!The inputStream needs to be closed manually!!
     *
     * @return returns an inputStream with the content
     * @throws java.io.IOException is thrown when the inputStream on the content cannot be opened.
     */
    InputStream content() throws IOException;

    /**
     * The length of the Audio resource
     *
     * @return the length of the resource in bytes
     * @throws IOException is thrown when the inputStream on the content cannot be opened.
     */
    long length() throws IOException;

}
