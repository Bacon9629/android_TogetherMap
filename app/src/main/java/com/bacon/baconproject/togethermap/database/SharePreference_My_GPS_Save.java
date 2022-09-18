package com.bacon.baconproject.togethermap.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SharePreference_My_GPS_Save {
    private String my_journey_gps_id = "my_journey_gps=";
    private String my_journey_gps_time_list_id = "my_journey_gps_time_list=";
    private SharedPreferences preferences;
    private int call_update_time_gape = 5 * 60 * 1000;


    public SharePreference_My_GPS_Save(Context context, String journey_id){
        my_journey_gps_id += journey_id;
        my_journey_gps_time_list_id += journey_id;
        preferences = context.getSharedPreferences(my_journey_gps_id, Context.MODE_PRIVATE);

    }

    public void save_now_gps(Location location) {
        String now_time_stamp = new Date().getTime() + "";
        Set<String> last_all_time_stamps = preferences.getStringSet(my_journey_gps_time_list_id, null);
        Set<String> all_time_stamps;
        if (last_all_time_stamps == null) {
            all_time_stamps = new HashSet<>();
        } else {
            all_time_stamps = new HashSet<>(last_all_time_stamps);
        }
        all_time_stamps.add(now_time_stamp);

        preferences.edit()
                .putLong(now_time_stamp + "_Latitude", Double.doubleToRawLongBits(location.getLatitude()))
                .putLong(now_time_stamp + "_Longitude", Double.doubleToRawLongBits(location.getLongitude()))
                .putStringSet(my_journey_gps_time_list_id, all_time_stamps).apply();

    }

    public void del_this_trip_data(){
        preferences.edit().clear().apply();
    }

    public boolean check_can_call_update_gps(){
        long last_time = preferences.getLong("last_time", 0);
        long current_time = System.currentTimeMillis() + call_update_time_gape;
        return last_time != 0 && last_time > current_time;
    }

    public void call_update_every_one_gps(){

    }

    public HashMap<Long, GeoPoint> get_trip_my_way(){
        HashMap<Long, GeoPoint> result = new HashMap<>();

        Set<String> time_stamps_set = preferences.getStringSet(my_journey_gps_time_list_id, null);
        if (time_stamps_set == null){
            return result;
        }

        time_stamps_set.forEach(time_stamp->{
            GeoPoint geoPoint = new GeoPoint(
                    Double.longBitsToDouble(preferences.getLong(time_stamp + "_Latitude", 0)),
                    Double.longBitsToDouble(preferences.getLong(time_stamp + "_Longitude", 0))
            );
            result.put(Long.parseLong(time_stamp), geoPoint);
        });
        return result;
    }
}
