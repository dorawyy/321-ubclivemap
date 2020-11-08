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

public class MenuActivityTest {
    @Rule
    public IntentsTestRule<MenuActivity> mActivityTestRule = new IntentsTestRule<MenuActivity>(MenuActivity.class);

    @Before
    public void setUp() throws Exception {
        // unused
    }

    @After
    public void tearDown() throws Exception {
        // unused
    }

    @Test
    public void buttonSelectionTest1() {
        Espresso.onView(withId(R.id.view_map_btn)).perform(click());
        intended(hasComponent(MainMapsActivity.class.getName()));
    }

    @Test
    public void buttonSelectionTest2() {
        Espresso.onView(withId(R.id.list_activites_btn)).perform(click());
        intended(hasComponent(ListScrollingActivity.class.getName()));
    }

    @Test
    public void buttonSelectionTest3() {
        Espresso.onView(withId(R.id.my_activity_btn)).perform(click());
        intended(hasComponent(ActivityStuffMenu.class.getName()));
    }

    @Test
    public void buttonSelectionTest4() {
        Espresso.onView(withId(R.id.edit_profile_btn)).perform(click());
        intended(hasComponent(ProfileActivity.class.getName()));
    }

    @Test
    public void buttonSelectionTest5() {
        Espresso.onView(withId(R.id.sign_out_btn)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void buttonSelectionTest6(){
        UserDetails.inactivity = false;
        UserDetails.courseRegistered = new String[]{"cpen321", "cpen 331"};
        Espresso.onView(withId(R.id.create_activity_button)).perform(click());
        intended(hasComponent(CreateActivity.class.getName()));
    }
}