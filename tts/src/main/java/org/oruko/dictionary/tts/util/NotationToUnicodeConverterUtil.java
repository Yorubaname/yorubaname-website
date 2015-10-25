package org.oruko.dictionary.tts.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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


    private static class Converter {

        private Path sourcePath;
        private Path destPath;

        public Converter(Path source, Path dest) {
            sourcePath = source;
            destPath = dest;
        }

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

        private String toNameWithUnicode(String fullFileName) {
            String name = fullFileName.split("\\.")[0];
            String ext = fullFileName.split("\\.")[1];
            String risingOrFalling = "";
            String preMarker = "";
            int consonantUnicode;
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
                consonantUnicode = segment.charAt(0);
                vowel = segment.substring(1);
            }

            int vowelUnicode = getVowelUnicode(vowel, tone);

            if (vowelUnicode == -1) {
                return "";
            }

            // proceeding section gets the file name
            risingOrFalling = (tone.equals("r") || tone.equals("f")) ? tone : "";

            preMarker = preMarker != "" ? preMarker + "_" : "";
            risingOrFalling = risingOrFalling != "" ? "_" + risingOrFalling : "";

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

                    if (tone.equals("h") || tone.equals("r")) {
                        return 'á';
                    }

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
