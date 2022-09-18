package com.bacon.baconproject.togethermap.main.historylist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bacon.baconproject.togethermap.database.Firestore_TripData;
import com.bacon.baconproject.togethermap.history_data.HistoryTripList;

public class HistoryListViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final MutableLiveData<HistoryTripList> trip_list_live_data;

    public HistoryListViewModel(){

        trip_list_live_data = new MutableLiveData<>();

        download_trip_list();
//        setChangeListener_historyList();

    }

    public void download_trip_list(){

        Firestore_TripData db = new Firestore_TripData();
        db.download_trip_list(new Firestore_TripData.OnTripListComingListener() {
            @Override
            public void onComplete(HistoryTripList historyTripList) {
                trip_list_live_data.setValue(historyTripList);
            }
        });

    }

//    private void setChangeListener_historyList(){
//
//    }


    public LiveData<HistoryTripList> get_trip_history_list(){
        return trip_list_live_data;
    }



}