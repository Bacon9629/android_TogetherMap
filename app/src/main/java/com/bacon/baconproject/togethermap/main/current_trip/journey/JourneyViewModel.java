package com.bacon.baconproject.togethermap.main.current_trip.journey;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bacon.baconproject.togethermap.database.Firestore_TripData;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;

public class JourneyViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final MutableLiveData<HistoryTrip> trip_live_data;


    public JourneyViewModel(){
        trip_live_data = new MutableLiveData<>();
    }

    public void download_trip_data(HistoryTrip trip_data){
        Firestore_TripData db = new Firestore_TripData();
        db.download_journey_Destination(trip_data, historyTrip -> trip_live_data.setValue(historyTrip));
    }

    public LiveData<HistoryTrip> get_trip_live_data(){
        return trip_live_data;
    }


}