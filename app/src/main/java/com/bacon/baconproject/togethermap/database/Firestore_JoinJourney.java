package com.bacon.baconproject.togethermap.database;

import com.bacon.baconproject.togethermap.MyLog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Firestore_JoinJourney {

    DocumentReference trip_data_reference;

    public Firestore_JoinJourney(String journey_ID){
        trip_data_reference = FirebaseFirestore.getInstance().collection("trip_data")
                .document(journey_ID);

//        join(email, onJoinJourney);

    }

    public void join(String email, DB.onJoinJourney onJoinJourney){

        trip_data_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.getData() == null){
                    onJoinJourney.joinFailed();
                    MyLog.show_firebase_log("no journey ID : " + snapshot.getId());
                    return;
                }

                ArrayList<String> data = (ArrayList<String>) snapshot.get("participant_email");
                data.add(email);
                trip_data_reference.update("participant_email", data)
                        .addOnSuccessListener(unused -> onJoinJourney.joinSuccess());

            }
        });

    }

}
