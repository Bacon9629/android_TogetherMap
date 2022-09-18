package com.bacon.baconproject.togethermap.main.current_trip.gps;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

public class GuyLocationData {
    private final String guy_id;
    private final String name;
    private final GeoPoint geoPoint;
    private final String time;
//    private String email;

    static public GuyLocationData construct(@NonNull DataSnapshot mSnapshot){
        assert !mSnapshot.getKey().equals("tool");

        GeoPoint point = null;
        String name = "";
        String time = "";

        for (DataSnapshot ms : mSnapshot.getChildren()){
            switch (Objects.requireNonNull(ms.getKey())){
                case "gps": {
                    String[] a = ms.getValue().toString().split(",");
                    point = new GeoPoint(Double.parseDouble(a[0]), Double.parseDouble(a[1]));
                    break;
                }
                case "name": {
                    name = ms.getValue().toString();
                    break;
                }
                case "time": {
                    time = ms.getValue().toString();
                    break;
                }
            }
        }
        return new GuyLocationData(mSnapshot.getKey(), name, point, time);
    }

    public GuyLocationData(@NonNull String guy_id, @NonNull String name, @NonNull GeoPoint geoPoint, @NonNull String time){
        this.guy_id = guy_id;
        this.name = name;
        this.geoPoint = geoPoint;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public String getTime() {
        return time;
    }

    public String getGuy_id() {
        return guy_id;
    }
}
