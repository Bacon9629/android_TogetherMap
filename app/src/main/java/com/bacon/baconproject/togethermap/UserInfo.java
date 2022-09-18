package com.bacon.baconproject.togethermap;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bacon.baconproject.togethermap.database.Firestore_UserInfo;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {
    public static final String DEFAULT_NAME = "new guy";
    public static final String DEFAULT_NOW_JOIN_TRIP = "";
    public static final String DEFAULT_USER_EMAIL = "";
    public static final int DEFAULT_ID = 0;

    private static UserInfo INSTANCE = null;

    private static String user_email = DEFAULT_USER_EMAIL;
    private static int user_id = DEFAULT_ID;
    private static String user_name = DEFAULT_NAME;
    private static String now_join_trip = DEFAULT_NOW_JOIN_TRIP;

    public static UserInfo getInstance(){
        if (INSTANCE == null){
            INSTANCE = new UserInfo();
        }
        return INSTANCE;
    }

    public static HashMap<String, Object> get_default_user_info_map(){
        HashMap<String, Object> result = new HashMap<>();
        result.put(Firestore_UserInfo.UserInfo_NAME_INDEX, DEFAULT_NAME);
        result.put(Firestore_UserInfo.UserInfo_ID_INDEX, DEFAULT_ID);
        result.put(Firestore_UserInfo.UserInfo_EMAIL_INDEX, DEFAULT_USER_EMAIL);
        result.put(Firestore_UserInfo.UserInfo_NOW_JOIN_TRIP_INDEX, DEFAULT_NOW_JOIN_TRIP);

        return result;
    }

    private UserInfo(){}

    public void set_user_info_with_map(@NonNull Map<String, Object> info_map){

        user_id = Integer.parseInt(info_map.get(Firestore_UserInfo.UserInfo_ID_INDEX) + "");
        user_email = info_map.get(Firestore_UserInfo.UserInfo_EMAIL_INDEX).toString();
        user_name = info_map.get(Firestore_UserInfo.UserInfo_NAME_INDEX).toString();

        Log.d("TAG", "user_info has been set：" + user_id + " , " + user_email + " , " + user_name);

    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        UserInfo.user_name = user_name;
    }

    public String getNow_join_trip() {
        return now_join_trip;
    }

    public void setNow_join_trip(String now_join_trip) {
        UserInfo.now_join_trip = now_join_trip;
    }

    public boolean data_has_been_entered(){
        boolean result = true;

        if (UserInfo.user_email.equals("")){
            result = false;
        }else if(UserInfo.user_id == 0){
            result = false;
        }

        return result;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        UserInfo.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        UserInfo.user_email = user_email;
    }
}
