package org.oruko.dictionary.tts;

import java.io.InputStream;
import java.util.List;
import javax.sound.sampled.AudioInputStream;

/**
 * Class for mapping tokenized name to audio segments
 *
 * @author Dadepo Aderemi.
 */
public interface AudioSegmentMapper {

    /**
     * Returns the audio file generated from mapping the audio segments as an inputStream
     * @param tokenizedName An array of string containing the audio segments
     * @return Audio as InputStream
     */
    public AudioInputStream getAudio(List<String> tokenizedName);

    /**
     * Returns the audio files for each of the name segments. This would be useful in situations
     * where other operations needed to be peformed before stringing the files together eg. inserting pause etc
     * @param tokenizedName array of string containing the audio segments
     * @return an array of InputStream
     */
    public List<InputStream> getAudioFiles(List<String> tokenizedName);
}
