package org.oruko.dictionary.website.handlebarshelpers;

import pl.allegro.tech.boot.autoconfigure.handlebars.HandlebarsHelper;

import java.text.Normalizer;

@HandlebarsHelper
public class UnicodeNormalizer {
    public UnicodeNormalizer() {
    }

    public static CharSequence normalize(String word) {
        return Normalizer.normalize(word, Normalizer.Form.NFD);
    }
}
