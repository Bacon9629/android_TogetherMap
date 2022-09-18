package com.bacon.baconproject.togethermap.main.historylist;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.database.DB;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.history_data.HistoryTripList;
import com.bacon.baconproject.togethermap.main.current_trip.journey.JourneyFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HistoryListFragment extends Fragment {

    public static HistoryListFragment newInstance() {
        return new HistoryListFragment();
    }

    Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        HistoryListViewModel mViewModel = new ViewModelProvider(this).get(HistoryListViewModel.class);
        context = root.getContext();
        // TODO: Use the ViewModel

        RecyclerView history_item_list_view = root.findViewById(R.id.fragment_history_list_recycler);
        Recycler_HistoryListItem adapter = new Recycler_HistoryListItem(context, new OnCall.OnCallMemory() {
            @Override
            public void call(HistoryTrip trip) {
                change_fragment_to_journey(root, trip);
            }
        });

        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        history_item_list_view.setLayoutManager(manager);
        history_item_list_view.setAdapter(adapter);

        mViewModel.get_trip_history_list().observe(getViewLifecycleOwner(), new Observer<HistoryTripList>() {
            @Override
            public void onChanged(HistoryTripList historyTripList) {
//                Recycler_HistoryListItem adapter = new Recycler_HistoryListItem(context, historyTripList.getHistory_trip_list(), null);
//                history_item_list_view.setAdapter(adapter);

                adapter.notify_change(historyTripList);

            }
        });

        FloatingActionButton float_bottom = root.findViewById(R.id.fragment_history_float_button);

        float_bottom.setOnClickListener(get_float_bottom_attach_listener(root, mViewModel));

    }

    private View.OnClickListener get_float_bottom_attach_listener(View root, HistoryListViewModel vm) {
        return abc -> {

            AlertDialog_ChooseWhich alertDialog_chooseWhich = new AlertDialog_ChooseWhich(context,
                    new AlertDialog_ChooseWhich.OnChoose() {
                        @Override
                        public void choose_join_journey() {
                            AlertDialog_JoinJourney alertDialog_joinJourney = new AlertDialog_JoinJourney(context,
                                    new AlertDialog_JoinJourney.OnJoinSuccess() {
                                        @Override
                                        public void Success() {
                                            vm.download_trip_list();
                                        }
                                    });
                            alertDialog_joinJourney.show();
                        }

                        @Override
                        public void choose_add_journey() {
                            AlertDialog_AddNewJourney alertDialog_addNewJourney = new AlertDialog_AddNewJourney(context,
                                    new DB.onNewJourneyUploadComplete() {
                                        @Override
                                        public void onComplete(HistoryTrip history_trip) {
                                            vm.download_trip_list();
                                            change_fragment_to_journey(root, history_trip);
                                        }
                                    }
                            );
                            alertDialog_addNewJourney.show();
                        }
                    });
            alertDialog_chooseWhich.show();


        };
    }

    private void change_fragment_to_journey(View root, HistoryTrip trip){
        JourneyFragment.setTYPE_PRIVATE_TRIP(trip);
        Navigation.findNavController(root).navigate(R.id.action_historyListFragment_to_journeyFragment);
    }

}