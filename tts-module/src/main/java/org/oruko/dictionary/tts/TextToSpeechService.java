package org.oruko.dictionary.tts;

import java.util.List;

/**
 * Text to speech service.
 *
 * @author Dadepo Aderemi.
 */
public class TextToSpeechService {

    private Tokenizer tokenizer;
    private AudioSegmentMapper audioMapper;

    public TextToSpeechService(Tokenizer tokenizer, AudioSegmentMapper mapper) {
        this.tokenizer = tokenizer;
        this.audioMapper = mapper;
    }

    public List<String> convertToSpeech(String nameInput) {
        return tokenizer.split(nameInput);
    }

}
