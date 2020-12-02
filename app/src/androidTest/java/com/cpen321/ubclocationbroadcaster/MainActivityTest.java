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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<MainActivity>(MainActivity.class);
    private String unentered_username = "unentered_username";
    private String unentered_password = "unentered_password";

    @Before
    public void setUp() throws Exception {
        //unused
    }

    /** SIGN IN TESTS (no account) **/

    /** testing sign in with no username, expecting to get an ERROR message **/
    @Test
    public void testSignInWithNoUsername() {
        onView(withId(R.id.password_button)).perform(typeText(unentered_password));
        closeSoftKeyboard();
        onView(withId(R.id.sign_in_button)).perform(click());

        try {
        onView(withText("Enter a username")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    /** testing sign in with no password entered, expecting to get the "Enter a password" message **/
    @Test
    public void testSignInWithNoPassword() {
        onView(withId(R.id.username_button)).perform(typeText(unentered_username));
        closeSoftKeyboard();
        onView(withId(R.id.sign_in_button)).perform(click());

        try {
        onView(withText("Enter a password")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    /** testing sign in with no password entered, expecting to get the "Enter a username" message **/
    @Test
    public void testSignInWithNoPassNoUsername() {
        closeSoftKeyboard();
        onView(withId(R.id.sign_in_button)).perform(click());

        try {
            onView(withText("Enter a username")).inRoot(withDecorView(not(is(mActivityTestRule
                    .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    /** testing the sign up button, expecting to open up the sign up intent **/
    @Test
    public void testSignUp(){
        onView(withId(R.id.sign_up_button)).perform(click());
        try {
            intended(hasComponent(SignUpActivity.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @After
    public void tearDown() throws Exception {
        //unused
    }
}