package org.oruko.dictionary.tts;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A simple implementation of {@link org.oruko.dictionary.tts.AudioSegmentMapper}
 * which does not persist generated audio but generates it on each request.
 * <p/>
 * It generates it from audio segments persisted on the file system
 *
 * @author Dadepo Aderemi.
 */
public class SimpleFileSystemAudioSegmentMapper implements AudioSegmentMapper {

    @Override
    public AudioInputStream getAudio(List<String> tokenizedName) {
        AudioInputStream concatenated = null;
        List<InputStream> fileNamesForSegments = getInputStreamForSegments(tokenizedName);

        for (InputStream input : fileNamesForSegments) {
            try {
                if (concatenated == null) {
                    concatenated  = AudioSystem.getAudioInputStream(input);
                    continue;
                }

                AudioInputStream toAddInputStream = AudioSystem.getAudioInputStream(input);

                concatenated =
                        new AudioInputStream(
                                new SequenceInputStream(concatenated, toAddInputStream),
                                concatenated.getFormat(),
                                concatenated.getFrameLength() + toAddInputStream.getFrameLength());


            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return concatenated;
    }

    @Override
    public List<InputStream> getAudioFiles(List<String> tokenizedName) {
        return getInputStreamForSegments(tokenizedName);
    }


    private List<InputStream> getInputStreamForSegments(List<String> tokenizedName) {
        List<InputStream> fileAsInputStream = new ArrayList<>();
        List<String> fileNames = tokenizedName.stream().map(this::mapToFile).collect(Collectors.toList());

        for (String fileName: fileNames) {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/segments/translated/" + fileName);
            fileAsInputStream.add(resourceAsStream);
        }

        return fileAsInputStream;
    }

    private String mapToFile(String token) {
        String audioFileName = getAudioFileName(token);
        return audioFileName;
    }

    private String getAudioFileName(String token) {
        String audioFile = "";
        char[] chars = token.toCharArray();

        if (chars.length == 3) {
            audioFile = String.valueOf((int) chars[0]) + "_" + String.valueOf((int) chars[1] + chars[2]);
            return audioFile + ".wav";
        }

        for (char character : chars) {
            audioFile += String.valueOf((int) character) + "_";
        }
        audioFile = audioFile.substring(0,audioFile.length()-1);
        return audioFile + ".wav";
    }
}
