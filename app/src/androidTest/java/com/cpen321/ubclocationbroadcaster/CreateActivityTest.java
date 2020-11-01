package com.cpen321.ubclocationbroadcaster;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.google.gson.JsonIOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.regex.Matcher;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static java.util.EnumSet.allOf;
import static org.junit.Assert.*;

public class CreateActivityTest {
    @Rule
    public IntentsTestRule<CreateActivity> cActivityTestRule = new IntentsTestRule<CreateActivity>(CreateActivity.class);
    private String aid_t = "testid";
    private String name_t = "testname";
    private String info_t = "testinfo";

    @Before
    public void setUp() throws Exception {
    }
    @Test
    public void testCreate() throws JSONException {
        UserDetails.name = "test_name";
        UserDetails.phone = "test_phone";
        UserDetails.school = "test_school";
        UserDetails.major = "test_major";
        UserDetails.privatePublic = "test_private";
        UserDetails.inactivity = "false";
        UserDetails.activityID = "test_id";
        UserDetails.courseRegistered = new String[1];
        UserDetails.courseRegistered[0] = "CPEN 221";

        Espresso.onView(withId(R.id.activity_id)).perform(typeText(aid_t));
        Espresso.onView(withId(R.id.activity_name)).perform(typeText(name_t));
        Espresso.onView(withId(R.id.activity_desc)).perform(typeText(info_t));
        Espresso.onView(withId(R.id.courseBTN)).perform(click());

        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.activity_done)).perform(click());
        intended(hasComponent(MenuActivity.class.getName()));
    }
    @After
    public void tearDown() throws Exception {
    }


}
