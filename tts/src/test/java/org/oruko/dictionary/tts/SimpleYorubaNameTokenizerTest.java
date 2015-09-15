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

    @Test
    public void testSplitWordThatStartsWithAVowel() throws Exception {
        List<String> result = tokenizer.split("omowumi");
        assertThat(result.get(0), is("o"));
        assertThat(result.get(1), is("mo"));
        assertThat(result.get(2), is("wu"));
        assertThat(result.get(3), is("mi"));
    }


    @Test
    public void testSplitWordWithGb() throws Exception {
        List<String> result = tokenizer.split("gbemi");
        assertThat(result.get(0), is("gbe"));
        assertThat(result.get(1), is("mi"));
    }

    @Test
    public void testSplitWordWithGb_with_accented_vowels() throws Exception {
        List<String> result = tokenizer.split("gbemi");
        assertThat(result.get(0), is("gbe"));
        assertThat(result.get(1), is("mi"));
    }

    @Test
    public void testSplitWordWithNfollowedByConsonant() throws Exception {
        List<String> result = tokenizer.split("gbenga");
        assertThat(result.get(0), is("gbe"));
        assertThat(result.get(1), is("n"));
        assertThat(result.get(2), is("ga"));
    }

    @Test
    public void testSplitWordWithAccentedSthenVowel() throws Exception {
        List<String> result = tokenizer.split("ṣola");
        assertThat(result.get(0), is("ṣo"));
        assertThat(result.get(1), is("la"));
    }
}