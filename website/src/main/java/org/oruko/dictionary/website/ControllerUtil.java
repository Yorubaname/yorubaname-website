package org.oruko.dictionary.website;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for all controllers
 *
 * @author Dadepo Aderemi.
 */
public class ControllerUtil {

    public static List<String> getYorubaAlphabets() {
        final List<String> alphabets = new ArrayList<>(Arrays.asList("abdefghijklmnoprstuwy".split("")));
        alphabets.add(4, "ẹ");
        alphabets.add(7, "gb");
        alphabets.add(16, "ọ");
        alphabets.add(20, "ṣ");
        return alphabets;
    }
}
