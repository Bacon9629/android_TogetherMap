package com.bacon.baconproject.togethermap;

import android.util.Log;

public class MyLog {
    private static String FIREBASE_LOG_ID = "my_firebase";
    private static String NORMAL_LOG_ID = "NORMAL";
    private static String TEST_LOG_ID = "test";
    private static String MAP_LOG_ID = "GPS_LOG";
    private static String SERVICE_LOG_ID = "SERVICE_LOG";

    public static void show_test_log(String show){
        Log.d(MyLog.TEST_LOG_ID, show);
    }

    public static void show_normal_log(String show){
        Log.d(MyLog.NORMAL_LOG_ID, show);
    }

    public static void show_firebase_log(String show){
        Log.d(MyLog.FIREBASE_LOG_ID, show);
    }

    public static void show_map_log(String show){
        Log.d(MyLog.MAP_LOG_ID, show);
    }

    public static void show_service_log(String show){
        Log.d(MyLog.SERVICE_LOG_ID, show);
    }
}
