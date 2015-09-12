package org.oruko.dictionary.web.rest;

import org.apache.commons.io.IOUtils;
import org.oruko.dictionary.model.Diction;
import org.oruko.dictionary.model.repository.DictionRepository;
import org.oruko.dictionary.tts.SimpleFileSystemAudioSegmentMapper;
import org.oruko.dictionary.tts.SimpleYorubaNameTokenizer;
import org.oruko.dictionary.tts.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Handler for TTS endpoints
 *
 * @author Dadepo Aderemi.
 */
@RestController
@RequestMapping("/v1/tts")
public class TtsApi {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TtsApi.class);
    private DictionRepository dictionRepository;

    @Autowired
    public TtsApi(DictionRepository dictionRepository) {
        this.dictionRepository = dictionRepository;
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public void doTTS(@PathVariable String name, HttpServletResponse response) {

        AudioInputStream audioInputStream = getDiction(name);

        if (audioInputStream == null) {
            return;
        }

        try {
            response.setContentType("audio/wav");
            AudioSystem.write(audioInputStream,
                              AudioFileFormat.Type.WAVE, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(audioInputStream);
        }
    }

    private AudioInputStream getDiction(String name) {
        Diction diction = dictionRepository.findByName(name);
        if (diction != null) {
            return diction.getAudioStream();
        }
        TextToSpeechService ttsService = new TextToSpeechService(new SimpleYorubaNameTokenizer(),
                                                                 new SimpleFileSystemAudioSegmentMapper());
        AudioInputStream audioInputStream = ttsService.convertToSpeech(name);

        if (audioInputStream == null) {
            return null;
        }

        try {
            Diction savedDiction = dictionRepository.save(new Diction(name, audioInputStream));
            return savedDiction.getAudioStream();
        } catch (IOException e) {
            logger.error("Error saving tts generated for {}", name);
            return null;
        }
    }
}
