package com.bacon.baconproject.togethermap.history_data;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;

public class HistoryDestination {
    private double[] destination_gps;
    private String destination_name;
    private String photo_address;
    private Date arrive_time;
    private String destination_id = "";

    public HistoryDestination(double[] destination_gps, String destination_name, String photo_address, Date arrive_time) {
        this("", destination_gps, destination_name, photo_address, arrive_time);
    }
    public HistoryDestination(String id, double[] destination_gps, String destination_name, String photo_address, Date arrive_time) {
        this.destination_id = id;
        this.destination_gps = destination_gps;
        this.destination_name = destination_name;
        this.photo_address = photo_address;
        this.arrive_time = arrive_time;
    }

    public HashMap<String, Object> get_map(){
        HashMap<String, Object> result = new HashMap<>();

//        MyLog.show_normal_log(destination_gps[0] + " " + destination_gps[1]);

        result.put("arrive_clock", arrive_time);
        result.put("gps", new GeoPoint(destination_gps[0], destination_gps[1]));
        result.put("photo_address", photo_address);
        result.put("target_name", destination_name);

        return result;
    }

    public double[] getDestination_gps() {
        return destination_gps;
    }

    public String getDestination_name() {
        return destination_name;
    }

    public String getPhoto_address() {
        return photo_address;
    }

    public Date getArrive_time() {
        return arrive_time;
    }


    public String getDestination_id() {
        return destination_id;
    }
}
