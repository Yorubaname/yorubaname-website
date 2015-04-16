package org.oruko.dictionary.tts;

import java.util.List;

/**
 * @author Dadepo Aderemi.
 */
public interface Tokenizer {

    public List<String> split(String word);

}
