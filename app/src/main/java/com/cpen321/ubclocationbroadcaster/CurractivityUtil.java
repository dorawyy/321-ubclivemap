package com.cpen321.ubclocationbroadcaster;

public class CurractivityUtil {
    public static String curr_aid;
    public static String curr_name;
    public static String curr_leader;
    public static String[] curr_usernames;
    public static String curr_info;
    public static String curr_major;
    public static String[] curr_course;
    public static String curr_school;
    public static String curr_lat;
    public static String curr_lon;
    public static String curr_status;

    public static void cleanup(){
        curr_aid = null;
        curr_course = null;
        curr_info = null;
        curr_lat = null;
        curr_leader = null;
        curr_lon = null;
        curr_major = null;
        curr_name = null;
        curr_school = null;
        curr_status = null;
    }
}
