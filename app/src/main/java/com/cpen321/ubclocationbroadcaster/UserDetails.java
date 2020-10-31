package com.cpen321.ubclocationbroadcaster;

public class UserDetails {
    public static String username;
    public String real_name;
    public String u_name;
    public String major;
    public String[] courseRegistered ;
    public String school;
    public String phone;
    public String privatePublic;
    public String inactivity;
    public String activityID;

    public UserDetails(){
        u_name = username;
    }


    public UserDetails(String uname){
        username = uname;
    }
}
