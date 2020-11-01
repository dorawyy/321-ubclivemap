package com.cpen321.ubclocationbroadcaster;

public class UserDetails {
    public static String username;
<<<<<<< Updated upstream
    public static String name;
    //public static String u_name;
    public static String major;
    public static String[] courseRegistered ;
=======
    public static String real_name;
    public static String u_name;
    public static String major;
    public static String[] courseRegistered;
>>>>>>> Stashed changes
    public static String school;
    public static String phone;
    public static String privatePublic;
    public static String inactivity;
    public static String activityID;

    /*public UserDetails(){
        u_name = username;
    }*/


    UserDetails(String uname){
        username = uname;
    }
}
