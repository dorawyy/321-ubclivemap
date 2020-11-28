package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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

public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<MainActivity>(MainActivity.class);
    private String username_t = "user1";
    private String password1_t = "wrongPassword";
    private String password2_t = "1234";


    @Before
    public void setUp() throws Exception {
        //unused
    }


    @Test
    public void testSignInWithWrongPassword() throws Exception{
        /** testing sign in with wrong username and password, expecting to get an ERROR message **/
        Espresso.onView(withId(R.id.username_button)).perform(typeText(username_t));
        Espresso.onView(withId(R.id.password_button)).perform(typeText(password1_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());

        try {
            Espresso.onView(withText("ERROR: Invalid password.")).inRoot(withDecorView(not(is(mActivityTestRule
                    .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void testSignInWithNoUsername() {
        /** testing sign in with no username, expecting to get an ERROR message **/
        Espresso.onView(withId(R.id.password_button)).perform(typeText(password2_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());

        try {
        Espresso.onView(withText("Enter a username")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void testSignInWithNoPassword() {
        /** testing sign in with no password entered, expecting to get the "Enter a password" message **/
        Espresso.onView(withId(R.id.username_button)).perform(typeText(username_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());

        try {
        Espresso.onView(withText("Enter a password")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void testSignInWithNoPassNoUsername() {
        /** testing sign in with no password entered, expecting to get the "Enter a username" message **/
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());

        try {
        Espresso.onView(withText("Enter a username")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void testSignIn(){
        /** testing if correct username/password leads to the corresponding intent, expecting to get the "Login Succeeded" message **/
        Espresso.onView(withId(R.id.username_button)).perform(typeText(username_t));
        Espresso.onView(withId(R.id.password_button)).perform(typeText(password2_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());

        try {
        Espresso.onView(withText("Login Succeeded!")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }
    }

    @Test
    public void testSignUp(){
        /** testing the sign up button, expecting to open up the sign up intent **/
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());

        try {
        intended(hasComponent(SignUpActivity.class.getName()));
        } catch(Exception e) {
            fail("Exception thrown.");
        }    }


    @After
    public void tearDown() throws Exception {
        //unused
    }
}