package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;


import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class SignUpActivityTest {
    @Rule
    public IntentsTestRule<SignUpActivity> mActivityTestRule = new IntentsTestRule<SignUpActivity>(SignUpActivity.class);
    private String username_t = "newUser";
    private String password_t = "password";

    /*
    @Before
    public void setUp() throws Exception {
    }
    */

    @Test
    public void testSignUp(){
        Espresso.onView(withId(R.id.sign_up_username_button)).perform(typeText(username_t));
        Espresso.onView(withId(R.id.sign_up_password_button)).perform(typeText(password_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());
        intended(hasComponent(ProfileActivity.class.getName()));
        assertEquals(1, 1);
    }

    @Test
    public void testSignUpNoUsername(){
        Espresso.onView(withId(R.id.sign_up_password_button)).perform(typeText(password_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());
        assertEquals(1, 1);
    }

    @Test
    public void testSignUpNoPassword(){
        Espresso.onView(withId(R.id.sign_up_username_button)).perform(typeText(username_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());
        assertEquals(1, 1);
    }

    @Test
    public void testSignUpNoUserNoPass(){
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());
        assertEquals(1, 1);
    }

    /*
    @After
    public void tearDown() throws Exception {
    }
     */
}