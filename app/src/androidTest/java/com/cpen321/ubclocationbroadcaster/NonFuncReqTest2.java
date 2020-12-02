package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NonFuncReqTest2 {
    @Rule
    public IntentsTestRule<MenuActivities> mActivityTestRule = new IntentsTestRule<MenuActivities>(MenuActivities.class);

    @Before
    public void setUp() throws Exception {
        //unused
    }

    /** testing if display of map is done in lss than 5 seconds **/

    @Test
    public void testActivitesDisplay() {
        onView(withId(R.id.ViewActivitiesOnMap)).perform(click());
        try {
            intended(hasComponent(MainMapsActivity.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void testActivitesDisplayTimed() {
        long startTime = System.nanoTime();
        onView(withId(R.id.ViewActivitiesOnMap)).perform(click());
        long currentTime = System.nanoTime();
        assertEquals((currentTime - startTime)/1_000_000_000 < 10, true);
    }

    @After
    public void tearDown() throws Exception {
        //unused
    }
}