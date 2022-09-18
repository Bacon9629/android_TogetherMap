package com.bacon.baconproject.togethermap.main.current_trip.participate;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.main.current_trip.journey.JourneyFragment;

public class ParticipateFragment extends Fragment {

    private ParticipateViewModel mViewModel;

    private String TYPE = JourneyFragment.get_type();
    private HistoryTrip trip = JourneyFragment.get_trip();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.current_participate_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ParticipateViewModel.class);
        // TODO: Use the ViewModel
    }

}