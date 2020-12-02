package com.cpen321.ubclocationbroadcaster;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

/** Execute Tests in order **/
@Suite.SuiteClasses({
        MainActivityTest.class,
        SignUpActivityTest.class,
        ProfileActivityTest.class,
        MenuActivityTest.class,
        MainActivityLoginTest.class,
        NonFuncReqTest1.class,
        NonFuncReqTest2.class,
        NonFuncReqTest3.class,
        MenuProfilesTest.class
})
public class TestSuite {
}
