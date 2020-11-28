package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.fail;

public class MenuActivityTest {
    @Rule
    public IntentsTestRule<MenuActivity> mActivityTestRule = new IntentsTestRule<MenuActivity>(MenuActivity.class);

    @Before
    public void setUp() throws Exception {
        // unused
    }

    /** Testing if selecting each button leads us to the corresponding intent **/
    @Test
    public void buttonSelectionTest1() {
        Espresso.onView(withId(R.id.ActivityMenu)).perform(click());

        try {
        intended(hasComponent(MenuActivities.class.getName()));
    } catch(Exception e) {
        fail("Exception thrown.");
        }
    }

    @Test
    public void buttonSelectionTest2() {
        Espresso.onView(withId(R.id.ProfileMenu)).perform(click());

        try {
        intended(hasComponent(MenuProfiles.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void buttonSelectionTest3() {
        Espresso.onView(withId(R.id.SignOut)).perform(click());

        try {
        intended(hasComponent(MainActivity.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void buttonSelectionTest4() {
        Espresso.onView(withId(R.id.Settings)).perform(click());

        try {
        intended(hasComponent(Settings.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @After
    public void tearDown() throws Exception {
        // unused
    }
}