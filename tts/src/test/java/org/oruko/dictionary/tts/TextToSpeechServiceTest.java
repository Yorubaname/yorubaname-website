package org.oruko.dictionary.tts;

import org.junit.*;

public class TextToSpeechServiceTest {

    TextToSpeechService service;

    @Before
    public void setUp() {
        service = new TextToSpeechService(new SimpleYorubaNameTokenizer(), new SimpleFileSystemAudioSegmentMapper());
    }

    @Test
    public void testConvertToSpeech() throws Exception {
//        AudioInputStream tanimomo = service.convertToSpeech("kekẹ́kẹ̀kọ́kọ̀kọ");
//        AudioInputStream tanimomo = service.convertToSpeech("dadepọ");
//        Path path = Paths.get("/home/dadepo/Desktop/kola/test.wav");
//
//        AudioSystem.write(tanimomo,
//                          AudioFileFormat.Type.WAVE,
//                          path.toFile());
//
//        System.out.println(tanimomo);
    }
}