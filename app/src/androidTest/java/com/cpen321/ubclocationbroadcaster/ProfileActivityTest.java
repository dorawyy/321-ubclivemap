package com.cpen321.ubclocationbroadcaster;


import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;


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

    /*
    @Before
    public void setUp() throws Exception {
    }
    */

    /*
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.cpen321.ubclocationbroadcaster", appContext.getPackageName());
    }
    */

    //helper functions
    public void setEntry() {
        //testing sign in with wrong username and password
        onView(withId(R.id.sign_up_name_button)).perform(typeText(name_t));
        onView(withId(R.id.school_button)).perform(typeText(school_t));
        onView(withId(R.id.major_button)).perform(typeText(major_t));
        onView(withId(R.id.phone_number_button)).perform(typeText(number_t));
        closeSoftKeyboard();
    }

    public void checkEntry() {
        try {
            onView(withId(R.id.sign_up_name_button)).check(ViewAssertions.matches(withText(name_t)));
            onView(withId(R.id.school_button)).check(ViewAssertions.matches(withText(school_t)));
            onView(withId(R.id.major_button)).check(ViewAssertions.matches(withText(major_t)));
            onView(withId(R.id.phone_number_button)).check(ViewAssertions.matches(withText(number_t)));
        }catch(Exception e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testStringEntry() {
        setEntry();

        //check to make sure entry works
        checkEntry();
    }

    @Test
    public void testSpinner() {
        //place 2 items in list
        onView(withId(R.id.course_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(course_sel1))).perform(click());
        onView(withId(R.id.course_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(course_sel2))).perform(click());

        //check if it has items in list
        try {
            onView(withId(R.id.course_list)).check(ViewAssertions.matches(hasDescendant(withText(course_sel1))));
            onView(withId(R.id.course_list)).check(ViewAssertions.matches(hasDescendant(withText(course_sel2))));
        }catch(Exception e){
            fail("Exception thrown.");
        }
    }

    @Test
    public void createProfileWithInvalidData(){
        //trying to create a profile with empty fields
        onView(withId(R.id.sign_up_name_button)).perform(typeText(name_t));
        onView(withId(R.id.school_button)).perform(typeText(school_t));
        closeSoftKeyboard();
        onView(withId(R.id.course_page_done_button)).perform(click());

        try {
            onView(withId(R.id.sign_up_name_button)).check(ViewAssertions.matches(withText(name_t)));
            onView(withId(R.id.school_button)).check(ViewAssertions.matches(withText(school_t)));
        }catch(Exception e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void createProfileWithValidInfo(){
        //trying to create a profile with empty fields
        setEntry();
        onView(withId(R.id.course_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(course_sel1))).perform(click());
        onView(withId(R.id.course_page_done_button)).perform(click());

        try {
            onView(withId(R.id.sign_up_name_button)).check(ViewAssertions.matches(withText(name_t)));
            onView(withId(R.id.school_button)).check(ViewAssertions.matches(withText(school_t)));
            onView(withId(R.id.major_button)).check(ViewAssertions.matches(withText(major_t)));
            onView(withId(R.id.phone_number_button)).check(ViewAssertions.matches(withText(number_t)));
            onView(withId(R.id.course_list)).check(ViewAssertions.matches(hasDescendant(withText(course_sel1))));
        }catch (Exception e) {
            fail("Exception thrown");
        }
    }

    /*
    @After
    public void tearDown() throws Exception {
    }
    */
}
