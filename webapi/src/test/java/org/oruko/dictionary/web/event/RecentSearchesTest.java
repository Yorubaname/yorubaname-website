package org.oruko.dictionary.web.event;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link RecentSearches}
 * Created by Dadepo Aderemi.
 */
public class RecentSearchesTest {

    RecentSearches recentSearches = new RecentSearches();
    private int recentLimit = 5;
    private int recentPopularListLimit = 5;

    @Before
    public void setUp() {
        recentSearches.setRecencyLimit(recentLimit);
        recentSearches.setPopularListLimit(recentPopularListLimit);
    }

    @Test
    public void test_recent_search_is_always_the_limit_Stack() throws Exception {
        recentSearches.stack("1down");
        recentSearches.stack("2down");
        recentSearches.stack("3down");
        recentSearches.stack("4down");
        recentSearches.stack("5down");
        recentSearches.stack("6down");
        recentSearches.stack("7down");

        assertEquals(recentLimit, recentSearches.get().length);
    }

    @Test
    public void test_most_popular_is_always_the_limit_set() throws Exception {
        recentSearches.stack("1down");
        recentSearches.stack("2down");
        recentSearches.stack("2down");
        recentSearches.stack("3down");
        recentSearches.stack("3down");
        recentSearches.stack("3down");
        recentSearches.stack("4down");
        recentSearches.stack("4down");
        recentSearches.stack("5down");
        recentSearches.stack("5down");
        recentSearches.stack("5down");
        recentSearches.stack("5down");
        recentSearches.stack("6down");
        recentSearches.stack("7down");
        recentSearches.stack("8down");

        assertEquals(recentPopularListLimit, recentSearches.getMostPopular().length);
    }


    @Test
    public void test_first_in_is_last_out() throws Exception {
        recentSearches.stack("1down");
        recentSearches.stack("2down");
        recentSearches.stack("3down");
        recentSearches.stack("4down");
        recentSearches.stack("5down");
        recentSearches.stack("6down");
        recentSearches.stack("7down");

        assertEquals(5, recentSearches.get().length);
        assertEquals("7down", recentSearches.get()[0]);
        assertEquals("6down", recentSearches.get()[1]);
        assertEquals("5down", recentSearches.get()[2]);
        assertEquals("4down", recentSearches.get()[3]);
        assertEquals("3down", recentSearches.get()[4]);
    }

    @Test
    public void test_stacked_entries_are_unique() throws Exception {
        recentSearches.stack("1down");
        recentSearches.stack("1down");
        recentSearches.stack("1down");
        recentSearches.stack("1down");
        recentSearches.stack("1down");

        assertEquals(1, recentSearches.get().length);
        assertEquals("1down", recentSearches.get()[0]);
    }


    @Test
    public void test_duplicate_entry_is_pushed_up_the_stack() throws Exception {
        recentSearches.stack("1down"); // first entry
        recentSearches.stack("2down");
        recentSearches.stack("3down");
        recentSearches.stack("4down");
        recentSearches.stack("1down"); // last entry is same as first entry

        assertEquals(4, recentSearches.get().length);
        assertEquals("1down", recentSearches.get()[0]);
    }

    @Test
    public void test_most_popular() throws Exception {
        recentSearches.stack("1down");
        recentSearches.stack("2down");
        recentSearches.stack("2down");
        recentSearches.stack("3down");
        recentSearches.stack("3down");
        recentSearches.stack("3down");

        assertEquals("3down", recentSearches.getMostPopular()[0]);
        assertEquals("2down", recentSearches.getMostPopular()[1]);
        assertEquals("1down", recentSearches.getMostPopular()[2]);
    }



}