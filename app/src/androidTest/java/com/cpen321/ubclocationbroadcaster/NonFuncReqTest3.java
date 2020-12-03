package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NonFuncReqTest3 {
    @Rule
    public IntentsTestRule<MenuActivity> mActivityTestRule = new IntentsTestRule<MenuActivity>(MenuActivity.class);

    @Before
    public void setUp() throws Exception {
        //unused
    }

    /** testing if display of map is done in less than 4 clicks from menu**/
    @Test
    public void testActivitesDisplayClicks() {
        int num_clicks = 0;
        onView(withId(R.id.ActivityMenu)).perform(click());
        num_clicks++;
        onView(withId(R.id.ViewActivitiesOnMap)).perform(click());
        num_clicks++;
        assertEquals(num_clicks < 4, true);
    }

    @After
    public void tearDown() throws Exception {
        //unused
    }
}