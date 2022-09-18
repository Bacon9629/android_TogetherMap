package com.bacon.baconproject.togethermap.main.current_trip.journey;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.main.current_trip.gps.GpsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class JourneyFragment extends Fragment {

    private static String TYPE = ""; // private_trip；current_journey
    private static HistoryTrip TRIP = null;

    public static void setTYPE_CURRENT_JOURNEY(@NonNull HistoryTrip trip){
        TYPE = "current_journey";
        TRIP = trip;
    }
    public static void setTYPE_PRIVATE_TRIP(@NonNull HistoryTrip trip){
        TYPE = "private_trip";
        TRIP = trip;
    }

    public static HistoryTrip get_trip(){
        return TRIP;
    }
    public static String get_type(){
        return TYPE;
    }


    private JourneyViewModel viewModel;
    private RecyclerView recyclerView;
    private Recycler_JourneyItem adapter = null;
    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.current_journey_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        assert TRIP != null;

        viewModel = new ViewModelProvider(this).get(JourneyViewModel.class);

        context = getContext();

//        set_gps_fragment_data();
        set_navigation_bar(root);

        recyclerView = root.findViewById(R.id.current_journey_recycler);
        FloatingActionButton floating_bt = root.findViewById(R.id.current_floating_bt);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewModel.download_trip_data(TRIP);

        viewModel.get_trip_live_data().observe(getViewLifecycleOwner(), new Observer<HistoryTrip>() {
            @Override
            public void onChanged(HistoryTrip trip) {
                if (adapter == null){
                    // first load recycler list
                    adapter = new Recycler_JourneyItem(context, trip, getChildFragmentManager());
                    recyclerView.setAdapter(adapter);
                }else{
                    // do update recycler list
                    adapter.notify_data_change(trip);
                }
            }
        });

        floating_bt.setOnClickListener(construct_floating_bt_listener());

        String fragment_id = "Map_NewDestinationMapFragment";

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        View v = LayoutInflater.from(context).inflate(R.layout.alert_add_destination, null, false);
        FragmentManager fragmentManager = getChildFragmentManager();




    }

    private View.OnClickListener construct_floating_bt_listener() {
        return bt -> {
//            AlertDialog_AddDestination alertDialog_addDestination = new AlertDialog_AddDestination(context, getChildFragmentManager(),
//                    new AlertDialog_AddDestination.OnAddSuccess() {
//                        @Override
//                        public void OnSuccess() {
//
//                        }
//                    });

            DialogFragment_AddNewDestination dialogFragment = new DialogFragment_AddNewDestination(context, TRIP,
                    new DialogFragment_AddNewDestination.OnAddSuccess() {
                        @Override
                        public void OnSuccess() {

                        }
                    });

            dialogFragment.show(getChildFragmentManager(), "dialogFragment");
        };
    }

//    private void set_gps_fragment_data() {
//        GpsFragment.setTYPE_PRIVATE_TRIP(TRIP.getHistory_id());
//    }

    private void set_navigation_bar(View root){
        TextView[] navigation_item = {
                root.findViewById(R.id.navigation_journey_trip_tv_bt),
                root.findViewById(R.id.navigation_journey_photo_tv_bt),
                root.findViewById(R.id.navigation_journey_gps_tv_bt),
                root.findViewById(R.id.navigation_journey_friend_tv_bt),
        } ;

        int[] navigation_action_id = {
                -1,
                -1,
                R.id.action_journeyFragment_to_gpsFragment,
                R.id.action_journeyFragment_to_participateFragment
        };

        for (int i=0;i< navigation_item.length;i++){
            if (navigation_action_id[i] != -1){
                int finalI = i;
                navigation_item[i].setOnClickListener(view -> Navigation.findNavController(root).navigate(navigation_action_id[finalI]));
            }
        }

    }
}