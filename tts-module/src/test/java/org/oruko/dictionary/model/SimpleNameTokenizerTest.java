package org.oruko.dictionary.model;

import org.junit.*;
import org.oruko.dictionary.tts.SimpleNameTokenizer;
import org.oruko.dictionary.tts.Tokenizer;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleNameTokenizerTest {

    Tokenizer tokenizer;

    @Before
    public void setUp() {
        tokenizer = new SimpleNameTokenizer();
    }

    @Test
    public void testSplit() throws Exception {
        List<String> result = tokenizer.split("tanimomo");
        assertThat(result.get(0), is("ta"));
        assertThat(result.get(1), is("ni"));
        assertThat(result.get(2), is("mo"));
        assertThat(result.get(3), is("mo"));
    }
}