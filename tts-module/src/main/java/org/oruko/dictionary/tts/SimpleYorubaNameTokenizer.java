package org.oruko.dictionary.tts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A naive and storage-less implementation that splits yoruba names into parts based on pronunciation partition.
 *
 * @author Dadepo Aderemi.
 */
public class SimpleYorubaNameTokenizer implements Tokenizer {

    private String unaccentedVowels = "aieẹuọoáàéèíìóòúù";
    private String accents = "̀́";
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
            int cutOff = 1;
            if (accents.indexOf(chars[1]) != -1) { // then this is accented vowel
                cutOff = 2;
            }

            char[] split = Arrays.copyOfRange(chars, 0, cutOff);
            char[] rest = Arrays.copyOfRange(chars, cutOff, chars.length);
            result.add(String.valueOf(new String(split)));
            performSplit(new String(rest));
        }

        if (nextIsConsonantThenVowel(chars)) {
            int cutOff = 2;
            if (chars.length > 2 &&
                    accents.indexOf(chars[2]) != -1) { // then this is accented vowel
                cutOff = 3;
            }

            char[] split = Arrays.copyOfRange(chars, 0, cutOff);
            char[] rest = Arrays.copyOfRange(chars, cutOff, chars.length);
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

    private boolean isVowel(char character) {
        return unaccentedVowels.indexOf(character) != -1;
    }
    private boolean isConsonant(char character) {
        return !isVowel(character);
    }

}