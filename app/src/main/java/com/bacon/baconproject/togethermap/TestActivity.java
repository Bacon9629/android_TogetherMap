package com.bacon.baconproject.togethermap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.bacon.baconproject.togethermap.database.Firestore_UserInfo;
import com.bacon.baconproject.togethermap.main.current_trip.gps.GuyLocationData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class TestActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        context = this;

//        get_user_data_from_firebase("qwe43213652@gmail.com");

//        Log.d(MyLog.TEST_LOG_ID, ");
        MyLog.show_test_log("TEST BEGIN");


//        test_join();

    }

    public void button_click(View v){
        test_database();
    }

    private void test_database(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("test/NJrC4BYRid0fy6IDy2Tk");
        db.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                HashMap<String, GuyLocationData> temp = new HashMap<>();
                for (DataSnapshot mSnapshot : snapshot.getChildren()) {

                    GeoPoint point = null;
                    String name = "";
                    String time = "";

                    for (DataSnapshot ms : mSnapshot.getChildren()){
                        switch (ms.getKey()){
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
//                    assert point != null;
//                    temp.put(mSnapshot.getKey(), new MyLocationData(mSnapshot.getKey(), name, point, time));
                    temp.put(mSnapshot.getKey(), GuyLocationData.construct(mSnapshot));
                }
                temp.forEach((k, item)->{
                    MyLog.show_test_log(k + "===" + item.getName());
                });

            }

        });
    }

    private LocationListener locationListener = null;
    @NonNull
    private LocationListener construct_locationListener(LocationManager manager) {
        if (locationListener == null){
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    MyLog.show_test_log(location.getLongitude() + "");
                    MyLog.show_test_log(location.getLatitude() + "");
                    MyLog.show_test_log(location.getAltitude() + "");
//                    manager.removeUpdates(locationListener);
                }
            };
        }
        return locationListener;
    }

    public boolean checkAndRequestPermissions(Context mContext) {
        int internet = mContext.checkSelfPermission(
                Manifest.permission.INTERNET);
        int loc = mContext.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = mContext.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ((Activity) mContext).requestPermissions(listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

    private Address test_destination_search() {

        String location = "勤益科技大學";
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(context);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            Toast.makeText(context,address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();

        }

        if (addressList != null){
            return addressList.get(0);
        }
        return null;
    }

    private void test_join() {

        CollectionReference trip_data_reference = FirebaseFirestore.getInstance().collection("test")
                .document("test").collection("test");

        HashMap<String, Object> map = new HashMap<>();
        map.put("test", new Date());
//        map.put("")
        MyLog.show_test_log("do");
        trip_data_reference.add(map);
        MyLog.show_test_log("ok");
    }

    private void test_map_data() {
//        DocumentReference db;
//        UserInfo info = UserInfo.getInstance();
//        db = FirebaseFirestore.getInstance().collection("private_trip").document("qwe43213652@gmail.com")
//                .collection("gps_list").document("Uvd21nrWCdeoNvYqUkUX");
//        db.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot snapshot) {
//
//                MyLog.show_test_log((snapshot.getData() == null) + "");
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                MyLog.show_test_log("fail");
//            }
//        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("private_trip").document("qwe43213652@gmail.com")
//                .collection("gps_list").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                MyLog.show_test_log(list.size() + "");
//                list.forEach(snapshot -> {
//                    MyLog.show_test_log(snapshot.getId());
//                });
//            }
//        });
//        db.collection("/private_trip/qwe43213652@gmail.com/gps_list/ Uvd21nrWCdeoNvYqUkUX")
        db.collection("private_trip").document("qwe43213652@gmail.com")
                .collection("gps_list").document("Uvd21nrWCdeoNvYqUkUX")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                MyLog.show_test_log(snapshot.getId());
                Map<String, Object> map = snapshot.getData();

                if (map != null){
                    map.forEach((id, item) -> {
                        MyLog.show_test_log(id + " " + item.toString());
                    });
                }else{
                    MyLog.show_test_log("map no item");
                }

            }
        });

    }

    private void test_add_float_button(){
        View v = LayoutInflater.from(this).inflate(R.layout.add_current_journey, null, false);
        EditText a = v.findViewById(R.id.add_current_journey_start_date_et);

        View.OnClickListener listener = view -> {
            Calendar calendar = Calendar.getInstance();
            int[] year_month_day = {calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)};
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    String result = String.format("%d/%d/%d", year, month, day);
                    a.setText(result);
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(context,dateSetListener, year_month_day[0], year_month_day[1], year_month_day[2]);
            dialog.show();
        };
        a.setOnClickListener(listener);
        new AlertDialog.Builder(this).setView(v)
                .show();
    }

    private void test_date(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("test").document("test").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot snapshot = task.getResult();

                        Date a = snapshot.getDate("date");
                        Date b = Calendar.getInstance().getTime();
                        Date c = new Date();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(b);
                        calendar.add(Calendar.HOUR_OF_DAY, 8);

                        TimeZone timeZone = TimeZone.getTimeZone("GMT 7");
//                        TimeZone timeZone1 = TimeZone.;


                        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm", Locale.getDefault());

//                        format.setTimeZone(TimeZone.getTimeZone("GMT 7"));
//                        format1.setTimeZone(timeZone1);

//                        int a_int = get_this_day_num(a, b);

//                        Log.d(MyLog.TEST_LOG_ID, a.getTime() + "  " + b.getTime());
//                        Log.d(MyLog.TEST_LOG_ID, a_int + "");


//                        MyLog.show_test_log(format.format(calendar.getTime()));

                    }
                });
    }

    private int get_this_day_num(Date journey_begin_date, Date compare_target){
        long DAY_SEC = 86400000L;
        long temp_a = journey_begin_date.getTime();

        int i;
        for(i=0; temp_a < compare_target.getTime() ; i++){
//            Log.d(MyLog.TEST_LOG_ID, "inner :" + temp_a + "   " + compare_target.getTime());
            temp_a+=DAY_SEC;
        }
        return i;

    }

    private void get_user_data_from_firebase(String email){

        UserInfo userInfo = UserInfo.getInstance();

        Firestore_UserInfo firestore_email_id = new Firestore_UserInfo();
        firestore_email_id.get_user_info_from_firestore(email, info_map -> userInfo.set_user_info_with_map(info_map));
    }

    private void test1(){
        DocumentReference db = FirebaseFirestore.getInstance()
                .collection("test").document("test");

        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    Log.d("test", "a: " + snapshot.get("test").toString() + "");
                    Log.d("test", "b: " + snapshot.get("test").getClass().getName() + "");
                    Log.d("test", "c: " + snapshot.get("test").getClass().getTypeName() + "");
                }
            }
        });
    }
}