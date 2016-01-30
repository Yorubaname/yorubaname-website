package org.oruko.dictionary.web.event;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link RecentIndexes}
 * Created by Dadepo Aderemi.
 */
public class RecentIndexesTest {

    RecentIndexes recentIndexes = new RecentIndexes();

    @Before
    public void setUp() {
        recentIndexes.setLimit(5);
    }

    @Test
    public void test_recent_indexes_is_always_the_limit_Stack() throws Exception {
        recentIndexes.stack("1down");
        recentIndexes.stack("2down");
        recentIndexes.stack("3down");
        recentIndexes.stack("4down");
        recentIndexes.stack("5down");
        recentIndexes.stack("6down");
        recentIndexes.stack("7down");

        assertEquals(5, recentIndexes.get().length);
    }

    @Test
    public void test_first_in_is_last_out() throws Exception {
        recentIndexes.stack("1down");
        recentIndexes.stack("2down");
        recentIndexes.stack("3down");
        recentIndexes.stack("4down");
        recentIndexes.stack("5down");
        recentIndexes.stack("6down");
        recentIndexes.stack("7down");

        assertEquals(5, recentIndexes.get().length);
        assertEquals("7down", recentIndexes.get()[0]);
        assertEquals("6down", recentIndexes.get()[1]);
        assertEquals("5down", recentIndexes.get()[2]);
        assertEquals("4down", recentIndexes.get()[3]);
        assertEquals("3down", recentIndexes.get()[4]);
    }
}