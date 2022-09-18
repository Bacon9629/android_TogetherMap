package com.bacon.baconproject.togethermap.database;

import com.bacon.baconproject.togethermap.MyLog;
import com.bacon.baconproject.togethermap.UserInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class Firestore_PrivateGps {

    DocumentReference db;

    public Firestore_PrivateGps(String trip_id){
        UserInfo info = UserInfo.getInstance();
        db = FirebaseFirestore.getInstance().collection("private_trip").document(info.getUser_email())
                .collection("gps_list").document(trip_id);
        MyLog.show_firebase_log(db.getPath());
    }

    public void download_gps_list(DB.OnComplete<HashMap<String, LatLng>> onComplete){

        db.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                MyLog.show_firebase_log("private map mark is down");
                onComplete.onComplete(snapshot_to_gps_process(task.getResult()));
            }else{
                MyLog.show_normal_log("from class Firebase_PrivateGps , fail to download private gps");
            }
        });
    };

    private HashMap<String, LatLng> snapshot_to_gps_process(DocumentSnapshot data){
        Map<String, Object> temp = data.getData();
        HashMap<String, LatLng> result = new HashMap<>();
        if (temp == null){
            return result;
        }

        temp.keySet().forEach(key_str -> {
            if (temp.get(key_str).getClass() == GeoPoint.class){
                GeoPoint p = (GeoPoint) temp.get(key_str);
                LatLng pp = new LatLng(p.getLatitude(), p.getLongitude());
                result.put(key_str, pp);
            }
        });

        return result;

    }

}
