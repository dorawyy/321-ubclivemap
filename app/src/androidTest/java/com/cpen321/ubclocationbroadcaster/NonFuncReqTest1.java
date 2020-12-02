package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NonFuncReqTest1 {
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<MainActivity>(MainActivity.class);

    private String entered_username = "test_user1";
    private String entered_password = "password";

    @Before
    public void setUp() throws Exception {
        //unused
    }

    /** testing if sign in is done in less than 10 seconds**/
    @Test
    public void testSignInTimed() {
        onView(withId(R.id.username_button)).perform(typeText(entered_username));
        onView(withId(R.id.password_button)).perform(typeText(entered_password));
        closeSoftKeyboard();

        long startTime = System.nanoTime();
        onView(withId(R.id.sign_in_button)).perform(click());

        long currentTime = System.nanoTime();
        assertEquals((currentTime - startTime)/1_000_000_000 < 10, true);
    }

    @After
    public void tearDown() throws Exception {
        //unused
    }
}