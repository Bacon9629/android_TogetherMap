package com.bacon.baconproject.togethermap.history_data;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class HistoryDay {

    private ArrayList<HistoryDestination> destinations_list;
    private int which_day;

    public HistoryDay(int which_day) {
        this.which_day = which_day;
        destinations_list = new ArrayList<>();
    }

    public void add_destination(DocumentSnapshot snapshot){
        GeoPoint geoPoint = snapshot.getGeoPoint("gps");
        double[] gps_ = new double[]{geoPoint.getLatitude(), geoPoint.getLongitude()};

        HistoryDestination destination = new HistoryDestination(
                snapshot.getId(),
                gps_,
                snapshot.getString("target_name"),
                snapshot.getString("photo_address"),
                snapshot.getTimestamp("arrive_clock").toDate()
                );

        destinations_list.add(destination);
    }

    public ArrayList<HistoryDestination> getDestinations_list() {
        return destinations_list;
    }


    public int getWhich_day() {
        return which_day;
    }
}

