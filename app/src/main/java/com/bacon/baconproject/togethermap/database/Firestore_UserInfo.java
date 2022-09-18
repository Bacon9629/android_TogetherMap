package com.bacon.baconproject.togethermap.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bacon.baconproject.togethermap.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.VerifyException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Firestore_UserInfo {

    public static final String UserInfo_EMAIL_INDEX = "email";
    public static final String UserInfo_NAME_INDEX = "name";
    public static final String UserInfo_ID_INDEX = "id";
    public static final String UserInfo_NOW_JOIN_TRIP_INDEX = "now_join_trip";

    private CollectionReference db = FirebaseFirestore.getInstance().collection("user_id");

    public Firestore_UserInfo(){

    }

    public interface OnGetInfoListener {
        void onGet(Map<String, Object> info_map);
    }

    public void get_user_info_from_firestore(String email, OnGetInfoListener onGetInfoListener){
        /*
        * 從DB取得user_id，若無此ID則為新用戶，自動執行新增新用戶的動作
        * */
        db.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    if (task.getResult().size() == 0){
                        Log.d("my_firebase", "db don't have email-id-data of this guy, adding new email-id to db now");
                        construct_new_user_info_to_firestore(email);
                        construct_new_user_private_trip_to_firestore(email);
                        onGetInfoListener.onGet(UserInfo.get_default_user_info_map());
                        return;
                    }else{
                        if (task.getResult().size() > 1){
                            throw new VerifyException("搜尋此email時取得超過兩個以上的ID");
                        }
                        for (DocumentSnapshot snapshot : task.getResult()){
                            Log.d("my_firebase","get info data now!");
                            onGetInfoListener.onGet(snapshot.getData());
                        }
                    }

                }else{
                    Log.d("my_firebase", "get_Id_from_firestore is failed", task.getException());
                }
            }
        });
    }

    private void construct_new_user_private_trip_to_firestore(String email) {
        Log.d("my_firebase", "construct_new_user_info_to_firestore");
        Firestore_PrivateTrip privateTrip = new Firestore_PrivateTrip(email);
        privateTrip.construct_new_user_private_trip_to_firestore();
    }

    private void construct_new_user_info_to_firestore(String email){

        db.document("info").get().addOnCompleteListener(task -> {
//            Log.d("TAG", task.getResult().get("number_of_user") + "");
            if (task.isSuccessful()){
                int new_id = 1;
                if (task.getResult().getData() != null) {
                    if (Objects.requireNonNull(task.getResult().getData()).size() != 0) {
                        new_id = Integer.parseInt(task.getResult().get("number_of_user").toString()) + 1;
                    }
                }

                int t_new_id = new_id;
                HashMap<String, Object> new_info_data = get_default_user_info_data(email, new_id);
                db.add(new_info_data).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        db.document("info").update("number_of_user", t_new_id);
                        Log.d("my_firebase", "new guy's ID is : " + t_new_id);

                        UserInfo userInfo = UserInfo.getInstance();
                        userInfo.setUser_email(email);
                        userInfo.setUser_id(t_new_id);

                    }else{
                        task1.getException().printStackTrace();
                        throw new RuntimeException("construct_new_user_info_to_firestore error");
                    }
                });
            }
        });

    }

    private HashMap<String, Object> get_default_user_info_data(String email, int new_id){
        HashMap<String, Object> add_data = new HashMap<>();

        add_data.put(UserInfo_EMAIL_INDEX, email);
        add_data.put(UserInfo_ID_INDEX, new_id);
        add_data.put(UserInfo_NAME_INDEX, UserInfo.DEFAULT_NAME);
        add_data.put(UserInfo_NOW_JOIN_TRIP_INDEX, UserInfo.DEFAULT_NOW_JOIN_TRIP);
        return add_data;
    }

}
