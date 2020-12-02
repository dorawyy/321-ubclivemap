package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

public class MenuProfilesTest {
    @Rule
    public IntentsTestRule<MenuProfiles> mActivityTestRule = new IntentsTestRule<MenuProfiles>(MenuProfiles.class);

    @Before
    public void setUp() throws Exception {
        // unused
    }

    /** Testing if selecting each button leads us to the corresponding intent **/
    @Test
    public void buttonSelectionTest1() {
        onView(withId(R.id.UpdateProfile)).perform(click());

        try {
            intended(hasComponent(UpdateProfile.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void buttonSelectionTest2() {
        onView(withId(R.id.ViewProfile)).perform(click());

        try {
            intended(hasComponent(ViewProfile.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void buttonSelectionTest3() {
        onView(withId(R.id.DeleteAccount)).perform(click());
        onView(withText("YES")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(isDisplayed())).perform(click());
        try {
            intended(hasComponent(MainActivity.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @After
    public void tearDown() throws Exception {
        // unused
    }
}