package com.bacon.baconproject.togethermap.history_data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;

public class HistoryTripList {

//    private static HistoryTripList me = null;

    private ArrayList<HistoryTrip> history_trip_list = null;

//    public static HistoryTripList getInstance(){
//        if (me == null) {
//            me = new HistoryTripList();
//        }
//        return me;
//    }

    public HistoryTripList() {
    }

    public ArrayList<HistoryTrip> getHistory_trip_list() {
        return history_trip_list;
    }

    public void construct_history_trip_list(List<DocumentSnapshot> documentSnapshotList){

        history_trip_list = new ArrayList<>();
        for (DocumentSnapshot snapshot : documentSnapshotList) {
            if (snapshot.getData() == null){
                continue;
            }
//            HistoryTrip historyTrip = new HistoryTrip(
//                    ((Timestamp) snapshot.get("start_day")).toDate(),
//                    ((Timestamp) snapshot.get("end_day")).toDate(),
//                    snapshot.getString("photo_address"),
//                    snapshot.getString("trip_name"),
//                    (ArrayList) snapshot.get("tag"),
//                    (ArrayList) snapshot.get("participant_email"),
//                    snapshot.getReference()
//            );
            HistoryTrip historyTrip = new HistoryTrip(snapshot);

            history_trip_list.add(historyTrip);

        }
    }

    public void setHistory_trip_list(ArrayList<HistoryTrip> history_trip_list) {
        this.history_trip_list = history_trip_list;
    }


}
