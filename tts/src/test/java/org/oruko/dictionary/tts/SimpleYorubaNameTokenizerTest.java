package org.oruko.dictionary.tts;

import org.junit.*;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SimpleYorubaNameTokenizerTest {

    Tokenizer tokenizer;

    @Before
    public void setUp() {
        tokenizer = new SimpleYorubaNameTokenizer();
    }

    @Test
    public void testSplit() throws Exception {
        List<String> result = tokenizer.split("tanimomo");
        assertThat(result.get(0), is("ta"));
        assertThat(result.get(1), is("ni"));
        assertThat(result.get(2), is("mo"));
        assertThat(result.get(3), is("mo"));
    }

    @Test
    public void testSplitWithAccentedVowels() throws Exception {
        List<String> result1 = tokenizer.split("dẹ̀");
        assertThat(result1.size(), is(1));
        assertThat(result1.get(0), is("dẹ̀"));
    }

    @Test
    public void testSplitWithManyAccentedVowels() throws Exception {
        List<String> result2 = tokenizer.split("kẹ́kẹ̀kọ́kọ̀");
        assertThat(result2.get(0), is("kẹ́"));
        assertThat(result2.get(1), is("kẹ̀"));
        assertThat(result2.get(2), is("kọ́"));
        assertThat(result2.get(3), is("kọ̀"));
    }
}