package com.bacon.baconproject.togethermap.database;

import android.location.Location;

import androidx.annotation.NonNull;

import com.bacon.baconproject.togethermap.DateProcess;
import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.main.current_trip.gps.GuyLocationData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;

public class Firebase_NowGuysGps {

    private String trip_id;
    private String user_name;
    private String user_email;

    public Firebase_NowGuysGps(String trip_id, String user_name, String user_email) {
        this.trip_id = trip_id;
        this.user_name = user_name;
        this.user_email = user_email.split("@")[0];
    }

    public Firebase_NowGuysGps(String trip_id, UserInfo userInfo){
        this(trip_id, userInfo.getUser_name(), userInfo.getUser_email());
    }

    public void update_my(Location location) {
        DatabaseReference db = get_my_location_dir();
        DateProcess convert = new DateProcess();
        String now_time = convert.get_date_to_str(Calendar.getInstance(TimeZone.getDefault()).getTime(), "yyyy/MM/dd hh:mm:ss");
//        String now_time = convert.get_date_to_str(new Date(System.currentTimeMillis()), "yyyy/MM/dd hh:mm:ss");

        HashMap<String, Object> data = new HashMap<>();
        data.put("gps", location.getLatitude() + "," + location.getLongitude());
        data.put("time", now_time);
        data.put("name", user_name);

        db.setValue(data);
    }

    public void add_force_update_snapshot(DB.onDataChange<Void> mOnDataChange){
        DatabaseReference db = get_force_update_dir();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mOnDataChange.onChange(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void download_guys_location(DB.OnComplete<HashMap<String, GuyLocationData>> complete){
        DatabaseReference db = get_this_trip_dir();
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    HashMap<String, GuyLocationData> temp = new HashMap<>();
                    for (DataSnapshot mSnapshot : task.getResult().getChildren()) {
                        if (!Objects.equals(mSnapshot.getKey(), "tool")){
                            temp.put(mSnapshot.getKey(), GuyLocationData.construct(mSnapshot));
                        }
                    }
                    complete.onComplete(temp);
                }else{
                    complete.onComplete(new HashMap<>());
                }
            }
        });

    }

    private DatabaseReference get_my_location_dir(){
        return FirebaseDatabase.getInstance().getReference("trip/" + trip_id + "/" + user_email);
    }

    private DatabaseReference get_force_update_dir(){
        return FirebaseDatabase.getInstance().getReference("trip/" + trip_id + "/tool" + "/call_update");
    }

    private DatabaseReference get_this_trip_dir(){
        return FirebaseDatabase.getInstance().getReference("trip/" + trip_id);
    }

}
