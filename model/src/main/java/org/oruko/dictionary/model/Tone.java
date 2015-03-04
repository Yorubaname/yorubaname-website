package org.oruko.dictionary.model;

import org.oruko.dictionary.model.repository.Audio;

import java.util.stream.Stream;

/**
 * Represents a tone on a {@link org.oruko.dictionary.model.NameEntry}
 * Created by dadepo on 2/11/15.
 */
public class Tone {

    private char[] toneMark;
    private Audio audio;

    /**
     * Public constructor for {@link Tone}
     * @param toneMark
     */
    public Tone(char[] toneMark) {
        this.toneMark = toneMark;
        // generates or retrieve the associated media file
    }

    /**
     * Get the {@link Audio} representation of the tone
     * @return the audio representation of the audio
     */
    public Audio getAudio() {
        return audio;
    }

    /**
     * Get the tonal mark as character array
     * @return
     */
    public char[] getToneMark() {
        return toneMark;
    }

    /**
     * Returns the tonal mark as a string
     * @return
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Stream.of(toneMark).forEach(tone -> builder.append(tone));
        return builder.toString();
    }
}
