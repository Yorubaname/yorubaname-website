package org.oruko.dictionary.tts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A naive and storage-less implementation that splits yoruba names into parts based on pronunciation partition.
 *
 * @author Dadepo Aderemi.
 */
public class SimpleNameTokenizer implements Tokenizer {

    private String unaccentedVowels = "aieẹuọo";
    private ArrayList<String> result = new ArrayList<>();

    @Override
    public List<String> split(String word) {
        performSplit(word.toLowerCase());
        return result;
    }


    private void performSplit(String word) {

        // This way leads out from the never ending recursive hole
        if (word.length() == 0) {
            return;
        } else if (word.length() == 1) {
            result.add(word);
            return;
        }

        char[] chars = word.toCharArray();


        char firstLetter = chars[0];

        if (isVowel(firstLetter)) {
            char[] split = Arrays.copyOfRange(chars, 0, 1);
            char[] rest = Arrays.copyOfRange(chars, 1, chars.length);
            result.add(String.valueOf(new String(split)));
            performSplit(new String(rest));
        }

        if (nextIsConsonantThenVowel(chars)) {
            char[] split = Arrays.copyOfRange(chars, 0, 2);
            char[] rest = Arrays.copyOfRange(chars, 2, chars.length);
            result.add(String.valueOf(new String(split)));
            performSplit(new String(rest));
        }
    }

    private boolean nextIsConsonantThenVowel(char[] characters) {
        char firstLetter = characters[0];
        char secondLetter = characters[1];

        if (isConsonant(firstLetter)
                && isVowel(secondLetter)) {
            return true;
        }
        return false;
    }


    boolean isVowel(char character) {
        return unaccentedVowels.indexOf(character) != -1;
    }

    boolean isConsonant(char character) {
        return !isVowel(character);
    }
}