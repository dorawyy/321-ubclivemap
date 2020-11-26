package com.cpen321.ubclocationbroadcaster;

/*Public Static String array 'aids' holds the Activity Ids of all the activities
* in the database ordered by most suggested at position aids[0] and least suggested at aids[total number of activities-1] */
public class SortedlistclassUtil {
    public static String [] aids;
    public static String [] anames;

    public static String activity_to_be_displayed;
    public static String aname;
    public static String major;
    public static String aschool;
    public static String[] course;
    public static String[] users;
    public static String info;
    public static Double lat;
    public static Double lon;
    public static String leader;

    /**This is called on sign out*/
    public static void cleanup(){
        aids = null;
        activity_to_be_displayed = null;
        aname = null;
        major = null;
        aschool = null;
        course = null;
        users = null;
        info = null;
        lat = null;
        lon = null;
        leader = null;
    }
}
