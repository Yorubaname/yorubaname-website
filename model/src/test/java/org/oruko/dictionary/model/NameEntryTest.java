package org.oruko.dictionary.model;

import org.junit.*;

import static org.junit.Assert.*;

public class NameEntryTest {

    @Test
    public void testUpdate() throws Exception {
        NameEntry nameEntry = new NameEntry("Ajani");
        nameEntry.setGeoLocation("geolocation");
        nameEntry.setMeaning("meaning");
        nameEntry.setMorphology("Morphology");
        nameEntry.setSubmittedBy("submittedBy");
        nameEntry.setTonalMark(new char[]{'A'});

        NameEntry newEntry = new NameEntry("Ajani");
        newEntry.setGeoLocation("geolocation1");
        newEntry.setMeaning("meaning1");
        newEntry.setMorphology("morphology1");
        newEntry.setSubmittedBy("submittedBy1");
        char[] tonalMark = {'B'};
        newEntry.setTonalMark(tonalMark);

        // System under test
        nameEntry.update(newEntry);

        assertEquals("Ajani", nameEntry.getName());
        assertEquals("geolocation1", nameEntry.getGeoLocation());
        assertEquals("morphology1", nameEntry.getMorphology());
        assertEquals("submittedBy1", nameEntry.getSubmittedBy());
        assertEquals(tonalMark, nameEntry.getTonalMark());
    }
}