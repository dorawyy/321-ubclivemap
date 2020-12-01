package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

public class SignUpActivityTest {
    @Rule
    public IntentsTestRule<SignUpActivity> mActivityTestRule = new IntentsTestRule<SignUpActivity>(SignUpActivity.class);
    private String username_t = "newUser";
    private String password_t = "password";
    private String username2_t = "user1";


    @Before
    public void setUp() throws Exception {
        //unused
    }

    @Test
    public void testSignUpNoUsername(){
        /** testing sign up with leaving username field blank, expecting to get an ERROR message **/
        Espresso.onView(withId(R.id.sign_up_password_button)).perform(typeText(password_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());

        try {
        Espresso.onView(withText("Enter a username")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        }catch (Exception e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testSignUpNoPassword(){
        /** testing sign up with leaving password field blank, expecting to get an ERROR message **/
        Espresso.onView(withId(R.id.sign_up_username_button)).perform(typeText(username_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());

        try {
        Espresso.onView(withText("Enter a password")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        }catch (Exception e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testSignUpNoUserNoPass(){
        /** testing sign up with leaving password and username fields blank, expecting to get an ERROR message **/
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());

        try {
        Espresso.onView(withText("Enter a username")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        }catch (Exception e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testSignUpExistedUser(){
        /** testing sign up with with an already existed username, expecting to get an ERROR message **/
        Espresso.onView(withId(R.id.sign_up_username_button)).perform(typeText(username2_t));
        Espresso.onView(withId(R.id.sign_up_password_button)).perform(typeText(password_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());

        try {
        Espresso.onView(withText("Username Already Exists")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        }catch (Exception e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testSignUp(){
        /** testing sign up by entering valid username and password, expected success message **/
        Espresso.onView(withId(R.id.sign_up_username_button)).perform(typeText(username_t));
        Espresso.onView(withId(R.id.sign_up_password_button)).perform(typeText(password_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());

        try {
        Espresso.onView(withText("Thanks for signing up!")).inRoot(withDecorView(not(is(mActivityTestRule
                .getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        }catch (Exception e) {
            fail("Exception thrown");
        }
    }

    @After
    public void tearDown() throws Exception {
        //unused
    }

}