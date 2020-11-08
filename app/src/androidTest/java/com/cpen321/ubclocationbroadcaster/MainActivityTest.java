package com.cpen321.ubclocationbroadcaster;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<MainActivity>(MainActivity.class);
    private String username_t = "Kyle";
    private String password1_t = "wrongPassword";
    private String password2_t = "password";

    /*
    @Before
    public void setUp() throws Exception {
    }
    */

    @Test
    public void testSignInWithWrongPassword() {
        //testing sign in with wrong username and password
        Espresso.onView(withId(R.id.username_button)).perform(typeText(username_t));
        Espresso.onView(withId(R.id.password_button)).perform(typeText(password1_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());
        assertEquals(1, 1);
    }

    @Test
    public void testSignInWithNoUsername() {
        //testing sign in with no username
        Espresso.onView(withId(R.id.password_button)).perform(typeText(password2_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());
        assertEquals(1, 1);
    }

    @Test
    public void testSignInWithNoPassword() {
        //testing sign in with no password entered
        Espresso.onView(withId(R.id.username_button)).perform(typeText(username_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());
        assertEquals(1, 1);
    }

    @Test
    public void testSignInWithNoPassNoUsername() {
        //testing sign in with no password entered
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());
        assertEquals(1, 1);
    }

    @Test
    public void testSignIn(){
        //test if correct username/password leads to the corresponding intent
        Espresso.onView(withId(R.id.username_button)).perform(typeText(username_t));
        Espresso.onView(withId(R.id.password_button)).perform(typeText(password2_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());
        intended(hasComponent(MenuActivity.class.getName()));
        assertEquals(1, 1);
    }

    @Test
    public void testSignUp(){
        Espresso.onView(withId(R.id.username_button)).perform(typeText(username_t));
        Espresso.onView(withId(R.id.password_button)).perform(typeText(password1_t));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.sign_in_button)).perform(click());
        Espresso.onView(withId(R.id.sign_up_button)).perform(click());
        intended(hasComponent(SignUpActivity.class.getName()));
        assertEquals(1, 1);
    }

    /*
    @After
    public void tearDown() throws Exception {
    }
    */
}