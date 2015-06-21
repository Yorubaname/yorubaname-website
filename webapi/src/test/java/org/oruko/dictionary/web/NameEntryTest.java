package org.oruko.dictionary.web;

import org.junit.*;
import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.NameEntry;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class NameEntryTest {

    @Test
    public void testUpdate() throws Exception {
        GeoLocation geoLocation = mock(GeoLocation.class);
        NameEntry nameEntry = new NameEntry("Ajani");
        nameEntry.setGeoLocation(geoLocation);
        nameEntry.setMeaning("meaning");
        nameEntry.setMorphology("Morphology");
        nameEntry.setSubmittedBy("submittedBy");
        nameEntry.setTonalMark(new char[]{'A'});

        NameEntry newEntry = new NameEntry("Ajani");
        newEntry.setGeoLocation(geoLocation);
        newEntry.setMeaning("meaning1");
        newEntry.setMorphology("morphology1");
        newEntry.setSubmittedBy("submittedBy1");
        char[] tonalMark = {'B'};
        newEntry.setTonalMark(tonalMark);

        // System under test
        nameEntry.update(newEntry);

        assertEquals("Ajani", nameEntry.getName());
        assertEquals(geoLocation, nameEntry.getGeoLocation());
        assertEquals("morphology1", nameEntry.getMorphology());
        assertEquals("submittedBy1", nameEntry.getSubmittedBy());
        assertEquals(tonalMark, nameEntry.getTonalMark());
    }
}