package com.bacon.baconproject.togethermap.main.current_trip.gps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bacon.baconproject.togethermap.MyLog;
import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.main.current_trip.journey.JourneyFragment;
import com.bacon.baconproject.togethermap.map.ShowMapsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class GpsFragment extends Fragment {

//    private static String TYPE = ""; // private_trip；current_journey
//    private static String journey_ID = "";

//    public static void setTYPE_CURRENT_JOURNEY(){
//        TYPE = "current_journey";
//    }
//    public static void setTYPE_PRIVATE_TRIP(String journey_id){
////        journey_ID = journey_id;
//        TYPE = "private_trip";
//    }

    public static GpsFragment newInstance() {
        return new GpsFragment();
    }

    private String TYPE = JourneyFragment.get_type();
    private String journey_ID = JourneyFragment.get_trip().getHistory_id();
    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

//        ShowMapsFragment.setTYPE_PRIVATE_TRIP(journey_ID);

        return inflater.inflate(R.layout.current_gps_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        set_navigation_bar(root);

        context = getContext();
        create_show_maps_fragment();

        Chip guys_location_chip = root.findViewById(R.id.current_gps_guys_location_chip);
        Chip history_way_chip = root.findViewById(R.id.current_gps_my_way_chip);
        Chip destination_chip = root.findViewById(R.id.current_gps_destination_chip);

        guys_location_chip.setOnCheckedChangeListener((compoundButton, b) -> {
            Intent intent = new Intent();
            intent.setAction(Parameter_GPSCommunication.SHOW_GUYS_LOCATION_ACTION);
            intent.putExtra(Parameter_GPSCommunication.SHOW_GUYS_LOCATION_EXTRA, b);
            context.sendBroadcast(intent);
        });

        history_way_chip.setOnCheckedChangeListener((compoundButton, b) -> {
            Intent intent = new Intent();
            intent.setAction(Parameter_GPSCommunication.SHOW_MY_WAY_ACTION);
            intent.putExtra(Parameter_GPSCommunication.SHOW_MY_WAY_EXTRA, b);
            context.sendBroadcast(intent);
        });

        destination_chip.setOnCheckedChangeListener((compoundButton, b) -> {
            Intent intent = new Intent();
            intent.setAction(Parameter_GPSCommunication.SHOW_DESTINATION_ACTION);
            intent.putExtra(Parameter_GPSCommunication.SHOW_DESTINATION_EXTRA, b);
            context.sendBroadcast(intent);
        });

    }

    private void create_show_maps_fragment() {

        ShowMapsFragment showMapsFragment = (ShowMapsFragment) getChildFragmentManager().findFragmentById(R.id.current_gps_fragment);
        if (showMapsFragment == null){
            MyLog.show_normal_log("new a showMapsFragment");
            showMapsFragment = new ShowMapsFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.current_gps_fragment, showMapsFragment).commit();
        }else{
            MyLog.show_normal_log("use old showMapsFragment");
        }

    }

    private void set_navigation_bar(View root){
        TextView[] navigation_item = {
                root.findViewById(R.id.navigation_gps_trip_tv_bt),
                root.findViewById(R.id.navigation_gps_photo_tv_bt),
                root.findViewById(R.id.navigation_gps_gps_tv_bt),
                root.findViewById(R.id.navigation_gps_friend_tv_bt),
        } ;

        int[] navigation_action_id = {
                R.id.action_gpsFragment_to_journeyFragment,
                -1,
                -1,
                R.id.action_gpsFragment_to_participateFragment
        };

        for (int i=0;i< navigation_item.length;i++){
            if (navigation_action_id[i] != -1){
                int finalI = i;
                navigation_item[i].setOnClickListener(view -> Navigation.findNavController(root).navigate(navigation_action_id[finalI]));
            }
        }

    }

}