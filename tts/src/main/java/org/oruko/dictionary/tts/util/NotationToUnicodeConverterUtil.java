package org.oruko.dictionary.tts.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Utility class that converts from the audio segment notation to unicode
 *
 * @author Dadepo Aderemi.
 */
public class NotationToUnicodeConverterUtil {

    public static void main(String[] args) {
        String sourceDir = args[0];
        String destinationDir = args[1];
        Path sourcePath = Paths.get(sourceDir);
        Path destPath = Paths.get(destinationDir);


        Converter converter = new Converter(sourcePath, destPath);
        converter.performConversion();
    }


    private static Map<String, Long> groupBySegmentCount(Path sourcePath, Path destPath) throws IOException {
        return Files.walk(sourcePath)
                    .map(Path::getFileName)
                    .map(s -> s.toString())
                    .filter(s -> {
                        for (char c : s.toCharArray()) {
                            if (Character.isDigit(c)) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .map(s -> {
                        int endIndex = s.indexOf("_");
                        if (endIndex == -1) {
                            return s;
                        }
                        return s.substring(0, endIndex);
                    }).collect(groupingBy(s -> s, counting()));
    }

    private static class Converter {

        private Path sourcePath;
        private Path destPath;

        public Converter(Path source, Path dest) {
            sourcePath = source;
            destPath = dest;
        }

        /**
         * Files are saved in the following format: Consonant | Oral vowels  "_" intonation
         *                                      or  Consonant | Nasal vowels "_" intonation
         * For example da_h -> da (with M tonal mark)
         * intonation could be HIGH -> _h
         *                     LOW -> _l
         *                     MID -> _m
         *                     RISING -> _r
         *                     FALLING -> _f
         *
         * The conversation takes the consonant portion and find its unicode value
         * for example d ->
         *             gb -> g + b ->
         *
         * It then takes the vowel portion and gets its unicode value.
         *      for Unaccented (ie MID tone) the unicode is direct value of the vowel, for example
         *              a ->
         *      if accented, then the accented character is used to get the unicode so for example
         *              for a_h, 'á' will be used to get the unicode
         *
         *      vowels with sub dots are saved with double vowels. for example ee_m is ẹ
         *
         *      At this point we have the unicode for the consonant plus the consonant for the vowels (which takes into
         *      consideration the intonation)
         *
         *      The final step is stringing everything together in the format [premarker]_[consonant_in_unicode]_[vowel_in_unicode]_[r or f]
         *      where premarker_ is only appended with "gb"
         *
         *      and [r or f] is used to qualify the vowel if it is rising or falling
         *
         *      if its a vowel, then the format is [vowel_in_unicode] _[r or f]
         *
         */
        public void performConversion() {
            try (DirectoryStream<Path> dirContents = Files.newDirectoryStream(sourcePath, "*.wav")) {
                for (Path audioFile : dirContents) {
                    String oldFileName = audioFile.getFileName().toString();
                    String newFileName = toNameWithUnicode(oldFileName);
                    if (!newFileName.equals("")) {
                        Path newFile = destPath.resolve(newFileName);
                        Files.copy(audioFile, newFile);
                        System.out.format("Successfully converted %s to %s\n", oldFileName, newFileName);
                    } else {
                        System.out.println("Error converting " + oldFileName);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean isVowel(char character) {
            return  "aieẹuọoáàéèíìóòúù".indexOf(character) != -1;
        }

        private String toNameWithUnicode(String fullFileName) {
            String name = fullFileName.split("\\.")[0];
            String ext = fullFileName.split("\\.")[1];
            String risingOrFalling = "";
            String preMarker = "";
            int consonantUnicode = -1;
            String vowel;

            String segment = name.split("_")[0];
            String tone = name.split("_")[1];

            if (segment.startsWith("gb")) {
                consonantUnicode = 'g' + 'b';
                preMarker = "gb";
                vowel = segment.substring(2);
            } else if (segment.startsWith("sh")) {
                consonantUnicode = 'ṣ';
                vowel = segment.substring(2);
            } else if (segment.startsWith("kp")) {
                consonantUnicode = 'p';
                vowel = segment.substring(2);
            } else {
                if (!isVowel(segment.charAt(0))) {
                    consonantUnicode = segment.charAt(0);
                    vowel = segment.substring(1);
                } else {
                    vowel = segment;
                }
            }

            int vowelUnicode = getVowelUnicode(vowel, tone);

            if (vowelUnicode == -1) {
                return "";
            }

            // proceeding section gets the file name
            risingOrFalling = (tone.equals("r") || tone.equals("f")) ? tone : "";

            preMarker = preMarker != "" ? preMarker + "_" : "";
            risingOrFalling = risingOrFalling != "" ? "_" + risingOrFalling : "";

            if (consonantUnicode == -1) {
                return String.valueOf(vowelUnicode) + risingOrFalling + "." + ext;
            }

            return preMarker + String.valueOf(consonantUnicode) + "_" + String.valueOf(vowelUnicode)
                    + risingOrFalling + "." + ext;
        }

        private int getVowelUnicode(String vowel, String tone) {
            if (vowel.length() == 1) { // unaccented vowel
                /**
                 * a + low
                 * a + mid
                 * a + high
                 *
                 * e + low
                 * e + mid
                 * e + high
                 *
                 * i + low
                 * i + mid
                 * i + high
                 *
                 * o + low
                 * o + mid
                 * o + high
                 *
                 * u + low
                 * u + mid
                 * u + high
                 */

                if (vowel.equals("a")) {
                    if (tone.equals("m")) {
                        return vowel.charAt(0);
                    }

                    // TODO high and rising are currently returning same unicode, they should be different
                    if (tone.equals("h") || tone.equals("r")) {
                        return 'á';
                    }

                    // TODO low and falling are currently returning same unicode, they should be different
                    if (tone.equals("l") || tone.equals("f")) {
                        return 'à';
                    }
                }

                if (vowel.equals("e")) {
                    if (tone.equals("m")) {
                        return vowel.charAt(0);
                    }

                    if (tone.equals("h") || tone.equals("r")) {
                        return 'é';
                    }

                    if (tone.equals("l") || tone.equals("f")) {
                        return 'è';
                    }
                }

                if (vowel.equals("i")) {
                    if (tone.equals("m")) {
                        return vowel.charAt(0);
                    }

                    if (tone.equals("h") || tone.equals("r")) {
                        return 'í';
                    }

                    if (tone.equals("l") || tone.equals("f")) {
                        return 'ì';
                    }
                }

                if (vowel.equals("o")) {
                    if (tone.equals("m")) {
                        return vowel.charAt(0);
                    }

                    if (tone.equals("h") || tone.equals("r")) {
                        return 'ó';
                    }

                    if (tone.equals("l") || tone.equals("f")) {
                        return 'ò';
                    }
                }

                if (vowel.equals("u")) {
                    if (tone.equals("m")) {
                        return vowel.charAt(0);
                    }

                    if (tone.equals("h") || tone.equals("r")) {
                        return 'ú';
                    }

                    if (tone.equals("l") || tone.equals("f")) {
                        return 'ù';
                    }
                }
            }

            /*
             * e-bottom-accent + low
             * e-bottom-accent + mid
             * e-bottom-accent + high
             *
             *
             * o-bottom-accent + low
             * o-bottom-accent + mid
             * o-bottom-accent + high
             */

            if (vowel.length() == 2) { // accented vowel
                if (vowel.equals("ee")) {
                    if (tone.equals("m")) {
                        return 'ẹ';
                    }

                    if (tone.equals("h") || tone.equals("r")) {
                        return (int) 'ẹ' + (int) '́';
                    }

                    if (tone.equals("l") || tone.equals("f")) {
                        return (int) 'ẹ' + (int) '̀';
                    }
                }

                if (vowel.equals("oo")) {
                    if (tone.equals("m")) {
                        return 'ọ';
                    }

                    if (tone.equals("h") || tone.equals("r")) {
                        return (int) 'ọ' + (int) '́';
                    }

                    if (tone.equals("l") || tone.equals("f")) {
                        return (int) 'ọ' + (int) '̀';
                    }
                }
            }
            return -1;
        }
    }
}
