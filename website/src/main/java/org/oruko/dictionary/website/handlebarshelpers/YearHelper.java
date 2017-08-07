package org.oruko.dictionary.website.handlebarshelpers;

import pl.allegro.tech.boot.autoconfigure.handlebars.HandlebarsHelper;

import java.time.Year;

@HandlebarsHelper
public class YearHelper {
    public YearHelper() {
    }

    public static CharSequence year() {
        return Year.now().toString();
    }
}
