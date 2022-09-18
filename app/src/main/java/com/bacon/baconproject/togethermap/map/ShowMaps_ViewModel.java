package com.bacon.baconproject.togethermap.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bacon.baconproject.togethermap.MyLog;
import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.database.DB;
import com.bacon.baconproject.togethermap.database.Firebase_NowGuysGps;
import com.bacon.baconproject.togethermap.database.Firestore_MyGPSData;
import com.bacon.baconproject.togethermap.database.Firestore_TripData;
import com.bacon.baconproject.togethermap.database.SharePreference_My_GPS_Save;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.main.current_trip.gps.GuyLocationData;
import com.bacon.baconproject.togethermap.main.current_trip.gps.Parameter_GPSCommunication;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;

public class ShowMaps_ViewModel extends ViewModel {

    private final MutableLiveData<HistoryTrip> now_trip;
    private final MutableLiveData<HistoryTrip> destination_locations;
    private final MutableLiveData<HashMap<String, GuyLocationData>> guys_locations;
    private final MutableLiveData<HashMap<Long, GeoPoint>> my_way_locations;

    private String journey_id;

    public ShowMaps_ViewModel(){
        now_trip = new MutableLiveData<>();
        destination_locations = new MutableLiveData<>();
        guys_locations = new MutableLiveData<>();
        my_way_locations = new MutableLiveData<>();
    }

    public void init_viewModel(String journey_id, Context context){
        this.journey_id = journey_id;

        Firestore_TripData tripData = new Firestore_TripData();
        tripData.download_journey_no_destination(journey_id, new DB.OnComplete<HistoryTrip>() {
            @Override
            public void onComplete(HistoryTrip item) {
                tripData.download_journey_Destination(item, new DB.onDataChange<HistoryTrip>() {
                    @Override
                    public void onChange(HistoryTrip item) {
                        now_trip.setValue(item);
                    }
                });
            }
        });

        Construct_Broadcast_Receive.On_Receive on_receive = new Construct_Broadcast_Receive.On_Receive() {
            @Override
            public void destination_active(boolean is_active) {
                if (is_active){
                    destination_locations.setValue(now_trip.getValue());
                }else{
                    destination_locations.setValue(null);
                }
            }

            @Override
            public void my_way_active(boolean is_active) {
                if (is_active){
                    SharePreference_My_GPS_Save preference = new SharePreference_My_GPS_Save(context, journey_id);
                    HashMap<Long, GeoPoint> temp = preference.get_trip_my_way();
                    MyLog.show_test_log(temp.size() + "");
//                    if (temp.size() == 0){
                    Firestore_MyGPSData myGPSData = new Firestore_MyGPSData(UserInfo.getInstance(), journey_id);
                    myGPSData.download_my_gps_history(map->{
                        temp.putAll(map);
                        my_way_locations.setValue(temp);
                    });
//                    }
//                    my_way_locations.setValue(temp);
                }else{
                    my_way_locations.setValue(new HashMap<>());
                }
            }

            @Override
            public void guys_location_active(boolean is_active) {
                if (is_active){
                    Firebase_NowGuysGps db = new Firebase_NowGuysGps(journey_id, UserInfo.getInstance());
                    db.download_guys_location(new DB.OnComplete<HashMap<String, GuyLocationData>>() {
                        @Override
                        public void onComplete(HashMap<String, GuyLocationData> item) {
                            guys_locations.setValue(item);
                        }
                    });
                }else{
                    guys_locations.setValue(new HashMap<>());
                }
            }
        };

        Construct_Broadcast_Receive broadcast_receive = new Construct_Broadcast_Receive(on_receive);
        broadcast_receive.registerReceiver(context);

    }

    public LiveData<HistoryTrip> getNow_trip(){
        return now_trip;
    }
    public LiveData<HistoryTrip> getDestination_locations(){
        return destination_locations;
    }
    public LiveData<HashMap<String, GuyLocationData>> getGuys_location(){
        return guys_locations;
    }
    public LiveData<HashMap<Long, GeoPoint>> getMy_way_location(){
        return my_way_locations;
    }


    static class Construct_Broadcast_Receive {
//        private On_Receive on_receive;
        private BroadcastReceiver receiver;
        private IntentFilter filter;

        public interface On_Receive{
            void destination_active(boolean is_active);
            void my_way_active(boolean is_active);
            void guys_location_active(boolean is_active);
        }

        public Construct_Broadcast_Receive(On_Receive on_receive){

//            this.on_receive = on_receive;
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    switch (action){
                        case Parameter_GPSCommunication.SHOW_DESTINATION_ACTION:{
                            on_receive.destination_active(intent.getBooleanExtra(Parameter_GPSCommunication.SHOW_DESTINATION_EXTRA, false));
                            break;
                        }
                        case Parameter_GPSCommunication.SHOW_GUYS_LOCATION_ACTION:{
                            on_receive.guys_location_active(intent.getBooleanExtra(Parameter_GPSCommunication.SHOW_GUYS_LOCATION_EXTRA, false));
                            break;
                        }
                        case Parameter_GPSCommunication.SHOW_MY_WAY_ACTION:{
                            on_receive.my_way_active(intent.getBooleanExtra(Parameter_GPSCommunication.SHOW_MY_WAY_EXTRA, false));
                            break;
                        }
                    }
                }
            };

            filter = new IntentFilter();
            filter.addAction(Parameter_GPSCommunication.SHOW_DESTINATION_ACTION);
            filter.addAction(Parameter_GPSCommunication.SHOW_GUYS_LOCATION_ACTION);
            filter.addAction(Parameter_GPSCommunication.SHOW_MY_WAY_ACTION);

        }

        public void registerReceiver(Context context){
            context.registerReceiver(receiver, filter);
        }
    }

}
