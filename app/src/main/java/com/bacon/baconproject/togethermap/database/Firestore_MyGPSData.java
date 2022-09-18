package com.bacon.baconproject.togethermap.database;

import com.bacon.baconproject.togethermap.DateProcess;
import com.bacon.baconproject.togethermap.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Firestore_MyGPSData {
    DocumentReference db;
    public Firestore_MyGPSData(UserInfo userInfo, String trip_id){
        db = FirebaseFirestore.getInstance().collection("private_trip").document(userInfo.getUser_email())
                .collection("gps_list").document(trip_id);
    }

    public Firestore_MyGPSData(String user_email, String trip_id){
        db = FirebaseFirestore.getInstance().collection("private_trip").document(user_email)
                .collection("gps_list").document(trip_id);
    }

    public void download_my_gps_history(DB.OnComplete<HashMap<Long, GeoPoint>> complete){
        db.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Map<String, Object> ori = snapshot.getData();
                if(ori == null){
                    complete.onComplete(new HashMap<>());
                    return;
                }
                DateProcess convert = new DateProcess();
                HashMap<Long, GeoPoint> result = new HashMap<>();
                try {
                    for (String key : ori.keySet()) {
                        result.put(
                                convert.get_str_to_date(key, DateProcess.NORMAL_FORMAT).getTime(), (GeoPoint) ori.get(key)
                        );
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
                complete.onComplete(result);
            }
        });
    }

    public void upload_my_gps_history(HashMap<Long, GeoPoint> trip_my_way){
        DateProcess convert = new DateProcess();
        HashMap<String, GeoPoint> data = new HashMap<>();
        trip_my_way.forEach((key, value) -> {
            data.put(convert.get_date_to_str(new Date(key), DateProcess.NORMAL_FORMAT), value);
        });
        db.set(data, SetOptions.merge());
    }
}
