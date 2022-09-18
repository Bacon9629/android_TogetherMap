package com.bacon.baconproject.togethermap.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bacon.baconproject.togethermap.MyLog;
import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.history_data.HistoryTripList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Firestore_TripData {

    private CollectionReference db;

    public Firestore_TripData(){
        db = FirebaseFirestore.getInstance().collection("trip_data");
    }

    public interface OnTripListComingListener {
        void onComplete(HistoryTripList historyTripList);
    }

    public void download_trip_list(OnTripListComingListener onComplete){
        UserInfo userInfo = UserInfo.getInstance();
        db.whereArrayContains("participant_email", userInfo.getUser_email()).orderBy("start_day", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                HistoryTripList tripList = new HistoryTripList();
                if (value != null){
                    tripList = new HistoryTripList();
                    tripList.construct_history_trip_list(value.getDocuments());
                }else{
                    tripList.setHistory_trip_list(new ArrayList<>());
                }
                onComplete.onComplete(tripList);
            }
        });
    }

    public void download_journey_no_destination(String journeyId, DB.OnComplete<HistoryTrip> mOnComplete){
        db.document(journeyId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                HistoryTrip trip = new HistoryTrip(task.getResult());
                mOnComplete.onComplete(trip);
            }
        });
    }

    public void download_journey_Destination(HistoryTrip trip, DB.onDataChange<HistoryTrip> onDataChange){

        trip.getDay_db_address().orderBy("arrive_clock").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null){
                    return;
                }
                trip.construct_history_days_list(value.getDocuments());
                onDataChange.onChange(trip);
            }
        });
    }

}
