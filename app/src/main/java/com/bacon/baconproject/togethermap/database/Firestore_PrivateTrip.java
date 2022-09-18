package com.bacon.baconproject.togethermap.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class Firestore_PrivateTrip {

    static final String COLLECTION_TAG_LIST_DIR_NAME = "tag_list";
    static final String COLLECTION_GPS_LIST_DIR_NAME = "gps_list";
    static final String DOCUMENT_INFO_DIR_NAME = "info";
    static final String TRIP_HISTORY_LIST_INDEX = "info";
    static final String SIZE_INDEX = "size";

    private final DocumentReference db;
    private String email;

    public Firestore_PrivateTrip(@NonNull String email){
        db =  FirebaseFirestore.getInstance().collection("private_trip").document(email);
        this.email = email;
    }

    public void construct_new_user_private_trip_to_firestore(){

        Log.d("my_firebase", "construct_new_user_private_trip_to_firestore");

        HashMap<String, Object> info_map = new HashMap<>();
        info_map.put(SIZE_INDEX, 0);

        db.collection(COLLECTION_TAG_LIST_DIR_NAME).document(DOCUMENT_INFO_DIR_NAME).set(info_map);
        db.collection(COLLECTION_TAG_LIST_DIR_NAME).document(DOCUMENT_INFO_DIR_NAME).set(info_map);


        HashMap<String, Object> trip_map = new HashMap<>();
        db.set(trip_map);

    }

    private void set_new_tag_list(String trip_id, HashMap<String, ArrayList<Object>> data){
        db.collection(COLLECTION_TAG_LIST_DIR_NAME).document(trip_id).set(data);
    }

    private void set_gps_list(String trip_id, int which_day, HashMap<String, Object> data){
        db.collection(COLLECTION_GPS_LIST_DIR_NAME).document(trip_id).collection("day" + which_day).add(data);
    }

}
