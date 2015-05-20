package org.oruko.dictionary.tts;

import java.util.List;
import javax.sound.sampled.AudioInputStream;

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

    public AudioInputStream convertToSpeech(String nameInput) {
        List<String> wordSegments = tokenizer.split(nameInput);
        return audioMapper.getAudio(wordSegments);
    }

}
