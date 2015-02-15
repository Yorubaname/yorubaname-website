package org.yorubaname.dictionary.model;

/**
 * Interface for storing and retrieving Audio contents
 * Created by dadepo on 2/11/15.
 */
public interface AudioRepository {

    /**
     * Loads the Audio content
     * @param audioId the id of the audio file
     * @return the {@link org.yorubaname.dictionary.model.Audio}
     */
    public Audio loadAudio(String audioId);

    /**
     * Saves the Audio content
     *
     * @param audio the audio file
     */
    public void saveAudio(Audio audio);

}
