package org.oruko.dictionary.tts;

import org.junit.Before;
import org.junit.Test;

public class TextToSpeechServiceTest {

    TextToSpeechService service;

    @Before
    public void setUp() {
        service = new TextToSpeechService(new SimpleYorubaNameTokenizer(), new SimpleFileSystemAudioSegmentMapper());
    }

    @Test
    //TODO Implement
    public void testConvertToSpeech() throws Exception {

    }
}