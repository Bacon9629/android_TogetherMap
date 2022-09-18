package com.bacon.baconproject.togethermap.main.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.database.DB;
import com.bacon.baconproject.togethermap.database.Firestore_TripData;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.main.current_trip.journey.JourneyFragment;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel


        Button bt = root.findViewById(R.id.main_fragment_bt);
//        bt.setOnClickListener(v -> {

//            NavController controller = Navigation.findNavController(root);
//            NavController controller = NavHostFragment.findNavController(this);
//            controller.navigate(R.id.action_mainFragment_to_currentTripFragment);

        UserInfo info = UserInfo.getInstance();
        if (info.getNow_join_trip().equals("")){
            Navigation.findNavController(root).navigate(R.id.action_mainFragment_to_historyListFragment);
        }else{

            Firestore_TripData tripData = new Firestore_TripData();
            tripData.download_journey_no_destination(info.getNow_join_trip(),
                    new DB.OnComplete<HistoryTrip>() {
                        @Override
                        public void onComplete(HistoryTrip trip) {
                            JourneyFragment.setTYPE_CURRENT_JOURNEY(trip);
                            Navigation.findNavController(root).navigate(R.id.action_mainFragment_to_journeyFragment);
                        }
                    });


        }


//            Navigation.findNavController(root).navigate(R.id.action_mainFragment_to_journeyFragment);


//        });

    }

}