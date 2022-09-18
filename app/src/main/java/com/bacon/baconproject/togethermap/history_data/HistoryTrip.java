package com.bacon.baconproject.togethermap.history_data;

import android.util.Log;

import com.bacon.baconproject.togethermap.MyLog;
import com.bacon.baconproject.togethermap.UserInfo;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HistoryTrip {

    public boolean isComplete = true;

    private ArrayList<HistoryDay> history_days_list;
    private String photo_address = "";
    private Date start_day, end_day;
    private ArrayList<String> participant;  // 參加人email
    private DocumentReference this_history_db_address;
    private CollectionReference main_note_db_address;
    private CollectionReference day_db_address;
    private String history_id;
    private String trip_name;
    private ArrayList<String> tag;

    public HistoryTrip(DocumentSnapshot snapshot){
        this.start_day = ((Timestamp) snapshot.get("start_day")).toDate();
        this.end_day = ((Timestamp) snapshot.get("end_day")).toDate();
        this.photo_address = snapshot.getString("photo_address");
        this.trip_name = snapshot.getString("trip_name");
        this.tag = (ArrayList) snapshot.get("tag");
        this.participant = (ArrayList) snapshot.get("participant_email");

        this.history_days_list = new ArrayList<>();
        set_db_reference(snapshot.getReference());
    }

    public HistoryTrip(Date start_day, Date end_day, String trip_name){

        this.start_day = start_day;
        this.end_day = end_day;
        this.trip_name = trip_name;

        this.photo_address = "";
        this.tag = new ArrayList<>();
        this.participant = new ArrayList<>();

        participant.add(UserInfo.getInstance().getUser_email());
        this.history_days_list = new ArrayList<>();

        this.isComplete = false;
    }

    public HistoryTrip(Date start_day, Date end_day, String photo_address, String trip_name, ArrayList<String> tag, ArrayList<String> participant, DocumentReference this_history_db_address) {
//        this.history_day_map = history_day_map;
        this.start_day = start_day;
        this.end_day = end_day;
        this.photo_address = photo_address;
        this.trip_name = trip_name;
        this.tag = tag;
        this.participant = participant;
        this.history_days_list = new ArrayList<>();

        set_db_reference(this_history_db_address);

    }

    public void construct_history_days_list(List<DocumentSnapshot> snapshots){
        history_days_list = new ArrayList<>();
        long last_day = 0;

        if (snapshots.size() == 0){
            return;
        }

        for (DocumentSnapshot snapshot : snapshots) {
            int which_day = get_this_day_num(start_day, snapshot.getDate("arrive_clock"));
            if (which_day > last_day){
                history_days_list.add(new HistoryDay(which_day));
                last_day = which_day;
            }
            history_days_list.get(history_days_list.size()-1).add_destination(snapshot);
            // 如果這裡錯了，就是目的地抵達時間設置 <= 旅程開始時間
        }
    }

    private void set_db_reference(DocumentReference documentReference){
        this_history_db_address = documentReference;
        main_note_db_address = documentReference.collection("main_note");
        day_db_address = documentReference.collection("day_note");
        history_id = documentReference.getId();
    }

    public HashMap<String, Object> get_map(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("start_day", start_day);
        result.put("end_day", end_day);
        result.put("photo_address", photo_address);
        result.put("participant_email", participant);
        result.put("tag", tag);
        result.put("trip_name", trip_name);
//        result.put();
        return result;
    };

    private int get_this_day_num(Date journey_begin_date, Date compare_target){
        long DAY_SEC = 86400000L;
        long temp_a = journey_begin_date.getTime();

        int i;
        for(i=0; temp_a < compare_target.getTime() ; i++){
            temp_a+=DAY_SEC;
        }
        return i;

    }

    public void change_trip_data(String trip_name, Date start_day, Date end_day){

        this.trip_name = trip_name;
        this.start_day = start_day;
        this.end_day = end_day;
    }

    public DocumentReference getThis_history_db_address() {
        return this_history_db_address;
    }

    public CollectionReference getMain_note_db_address() {
        return main_note_db_address;
    }

    public CollectionReference getDay_db_address() {
        return day_db_address;
    }

    public String getHistory_id() {
        return history_id;
    }

    public ArrayList<String> getParticipant() {
        return participant;
    }

    public Date getStart_day() {
        return start_day;
    }

    public Date getEnd_day() {
        return end_day;
    }

    public String getPhoto_address() {
        return photo_address;
    }

    public String getTrip_name() {
        return trip_name;
    }

    public ArrayList<String> getTag() {
        return tag;
    }

    public ArrayList<HistoryDay> getHistory_days_list() {
        return history_days_list;
    }

}

