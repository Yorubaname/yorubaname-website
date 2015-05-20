package org.oruko.dictionary.tts;

import java.util.List;

/**
 * A {@link org.oruko.dictionary.tts.Tokenizer} breaks up a name into tonal segments
 * @author Dadepo Aderemi.
 */
public interface Tokenizer {

    /**
     * Split a name into the various tonal segments
     * @param word the works to tokenize.
     * @return returns a list of string which constituent the tonal segments
     */
    public List<String> split(String word);

}
