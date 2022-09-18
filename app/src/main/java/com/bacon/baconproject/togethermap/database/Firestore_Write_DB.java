package com.bacon.baconproject.togethermap.database;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bacon.baconproject.togethermap.history_data.HistoryDestination;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;

public class Firestore_Write_DB {

    CollectionReference db;

    public Firestore_Write_DB(){
        db = FirebaseFirestore.getInstance().collection("trip_data");
    }

    public void upload_update_journey(HistoryTrip trip, Context context){
        db.document(trip.getHistory_id()).update(trip.get_map()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "更新完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void upload_new_journey(HistoryTrip new_trip, DB.onNewJourneyUploadComplete complete){

        db.add(new_trip.get_map()).addOnCompleteListener(task -> {
            String id = task.getResult().getId();
            GeoPoint geoPoint = new GeoPoint(24.13906893461306d, 120.68931605767068d);
            Date sample_destination_arrive_date = new Date();
            sample_destination_arrive_date.setTime(new_trip.getStart_day().getTime() + 28800000L);

            HashMap<String, Object> init_day_note = new HashMap<>();
            init_day_note.put("arrive_clock", sample_destination_arrive_date);
            init_day_note.put("gps", geoPoint);
            init_day_note.put("photo_address", "url");
            init_day_note.put("target_name", "台中火車站");

            db.document(id).collection("day_note").add(init_day_note)
                    .addOnCompleteListener(task2 -> {
                        task.getResult().get().addOnCompleteListener(task1 -> {
                            complete.onComplete(new HistoryTrip(task1.getResult()));
                        });
                    });
        });
    }

    public void upload_new_destination(Context context, HistoryTrip trip, HistoryDestination new_destination){
        db.document(trip.getHistory_id()).collection("day_note")
                .add(new_destination.get_map()).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "建立目的地成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "建立目的地失敗，請檢察網路，或再試一次", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void upload_update_destination(Context context, HistoryTrip trip, HistoryDestination update_destination){

        db.document(trip.getHistory_id()).collection("day_note").document(update_destination.getDestination_id())
                .update(update_destination.get_map()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "更新目的地成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "更新目的地失敗，請檢察網路再試一次", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void del_destination(String trip_id, String destination_id){
        db.document(trip_id).collection("day_note").document(destination_id).delete();
    }

}
