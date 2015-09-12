package org.oruko.dictionary.model;

import sun.misc.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * Represents the text to speech rendition of {@link org.oruko.dictionary.model.NameEntry}
 * Created by dadepo on 2/11/15.
 */
@Entity
public class Diction {

    @Id
    private String name;

    @Column
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] audioStream;

    /**
     * Public constructor for {@link Diction}
     */
    public Diction() {
    }

    /**
     * Public constructor
     * @param name the name
     * @param audioStream the {@link AudioInputStream}
     * @throws IOException if cannot transform the audio stream into byte array
     */
    public Diction(String name, AudioInputStream audioStream) throws IOException {
        this.name = name;
        this.audioStream = IOUtils.readFully(audioStream, -1, true);
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Audio represented as {@link AudioInputStream}
     * @return the audio for the diction
     */
    public AudioInputStream getAudioStream() {
        AudioFormat format = new AudioFormat(44100.0f,16,1,true,false);
        InputStream inputstream = new ByteArrayInputStream(audioStream);
        return new AudioInputStream(inputstream, format, audioStream.length);
    }

    /**
     * Returns the tonal mark as a string. i.e d-r-m
     * @return the tonal mark as a string
     */
    public String toString() {
        // TODO implement
        throw new UnsupportedOperationException();
    }
}
