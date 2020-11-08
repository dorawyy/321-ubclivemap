package com.cpen321.ubclocationbroadcaster;


import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {
    @Rule
    public IntentsTestRule<ProfileActivity> mActivityTestRule = new IntentsTestRule<ProfileActivity>(ProfileActivity.class);

    private String name_t = "Jack";
    private String school_t = "UBC";
    private String major_t = "Computer Engineering";
    private String number_t = "6045557777";
    private String course_sel1 = "CPEN 211";
    private String course_sel2 = "CPEN 331";

    @Before
    public void setUp() throws Exception {
    }

    /*
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.cpen321.ubclocationbroadcaster", appContext.getPackageName());
    }
    */
    @Test
    public void testStringEntry() {
        //testing sign in with wrong username and password
        onView(withId(R.id.sign_up_name_button)).perform(typeText(name_t));
        onView(withId(R.id.school_button)).perform(typeText(school_t));
        onView(withId(R.id.major_button)).perform(typeText(major_t));
        onView(withId(R.id.phone_number_button)).perform(typeText(number_t));
        closeSoftKeyboard();

        //check to make sure entry works
        onView(withId(R.id.sign_up_name_button)).check(matches(withText(name_t)));
        onView(withId(R.id.school_button)).check(matches(withText(school_t)));
        onView(withId(R.id.major_button)).check(matches(withText(major_t)));
        onView(withId(R.id.phone_number_button)).check(matches(withText(number_t)));
    }

    @Test
    public void testSpinner() {
        //place 2 items in list
        onView(withId(R.id.course_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(course_sel1))).perform(click());
        onView(withId(R.id.course_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(course_sel2))).perform(click());

        //check if it has items in list
        onView(withId(R.id.course_list)).check(matches(hasDescendant(withText(course_sel1))));
        onView(withId(R.id.course_list)).check(matches(hasDescendant(withText(course_sel2))));
    }

    @Test
    public void createProfileWithInvalidData(){
        //trying to create a profile with empty fields
        onView(withId(R.id.sign_up_name_button)).perform(typeText(name_t));
        onView(withId(R.id.school_button)).perform(typeText(school_t));
        closeSoftKeyboard();
        onView(withId(R.id.course_page_done_button)).perform(click());
    }

    @Test
    public void createProfileWithNoCourse(){
        //trying to create a profile with empty course list
        onView(withId(R.id.sign_up_name_button)).perform(typeText(name_t));
        onView(withId(R.id.school_button)).perform(typeText(school_t));
        onView(withId(R.id.major_button)).perform(typeText(major_t));
        onView(withId(R.id.phone_number_button)).perform(typeText(number_t));
        closeSoftKeyboard();
        onView(withId(R.id.course_page_done_button)).perform(click());
    }

    @Test
    public void createProfileWithValidInfo(){
        //trying to create a profile with empty fields
        onView(withId(R.id.sign_up_name_button)).perform(typeText(name_t));
        onView(withId(R.id.school_button)).perform(typeText(school_t));
        onView(withId(R.id.major_button)).perform(typeText(major_t));
        onView(withId(R.id.phone_number_button)).perform(typeText(number_t));
        closeSoftKeyboard();
        onView(withId(R.id.course_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(course_sel1))).perform(click());
        onView(withId(R.id.course_page_done_button)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
    }
}
