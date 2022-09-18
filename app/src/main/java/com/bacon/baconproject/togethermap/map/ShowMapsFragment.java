package com.bacon.baconproject.togethermap.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bacon.baconproject.togethermap.DateProcess;
import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.history_data.HistoryDay;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.main.current_trip.gps.GuyLocationData;
import com.bacon.baconproject.togethermap.main.current_trip.journey.JourneyFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class ShowMapsFragment extends Fragment {

//    private static String TYPE = ""; // private_trip；current_journey
//    private static String journey_ID = "";

//    public static void setTYPE_CURRENT_JOURNEY(){
//        TYPE = "current_journey";
//    }
//    public static void setTYPE_PRIVATE_TRIP(String journey_id){
//        journey_ID = journey_id;
//        TYPE = "private_trip";
//    }

    private String TYPE = JourneyFragment.get_type();
    private String journey_ID = JourneyFragment.get_trip().getHistory_id();

    private HistoryTrip now_trip;
    private HistoryTrip show_destination_trip;
    private HashMap<String, GuyLocationData> guys_location_map;
    private HashMap<Long, GeoPoint> my_history_location_map;
    private GoogleMap googleMap;
    private boolean map_is_ok = false;

    ArrayList<Integer> colors;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap map) {
            googleMap = map;
            LatLng sydney = new LatLng(24.1, 121.01);
//            map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 7.5f));
//            map_or_data_is_complete(TYPE, map_mark_list);
            map_is_ok = true;
//            draw_destination_map(null, true);
//            draw_guys_location_map(null, true);
//            draw_my_history_way_map(null, true);
            draw_map();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert !journey_ID.equals("");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        ShowMaps_ViewModel viewModel = new ViewModelProvider(this).get(ShowMaps_ViewModel.class);
        viewModel.init_viewModel(journey_ID, getContext());

        construct_colors();

        viewModel.getNow_trip().observe(getViewLifecycleOwner(), new Observer<HistoryTrip>() {
            @Override
            public void onChanged(HistoryTrip trip) {
                now_trip = trip;
                draw_map();
            }
        });

        viewModel.getDestination_locations().observe(getViewLifecycleOwner(), new Observer<HistoryTrip>() {
            @Override
            public void onChanged(HistoryTrip trip) {
                show_destination_trip = trip;
                draw_map();
            }
        });

        viewModel.getGuys_location().observe(getViewLifecycleOwner(), new Observer<HashMap<String, GuyLocationData>>() {
            @Override
            public void onChanged(HashMap<String, GuyLocationData> stringMyLocationDataHashMap) {
                guys_location_map = stringMyLocationDataHashMap;
                draw_map();
            }
        });

        viewModel.getMy_way_location().observe(getViewLifecycleOwner(), new Observer<HashMap<Long, GeoPoint>>() {
            @Override
            public void onChanged(HashMap<Long, GeoPoint> longGeoPointHashMap) {
                my_history_location_map = longGeoPointHashMap;
                draw_map();
            }
        });
    }

    private void construct_colors() {
        colors = new ArrayList<>();
        colors.add(0xff00ffff);
        colors.add(0xffff00ff);
        colors.add(0xffffffff);
        colors.add(0xffff00ff);
        colors.add(0xff00ffff);
        colors.add(0xff0000ff);
    }

    private void draw_map(){
//        show_destination_trip = trip;
//        guys_location_map = m_guys_location_map;
//        my_history_location_map = m_my_history_location_map;

        if (!map_is_ok) {
            return;
        }
        googleMap.clear();

        if (show_destination_trip != null) {
            ArrayList<MarkerOptions> markerOptions_list = new ArrayList<>();
            ArrayList<HistoryDay> days = show_destination_trip.getHistory_days_list();
            for (int i = 0; i < days.size(); i++) {
                HistoryDay day = days.get(i);
                day.getDestinations_list().forEach(destination -> {
                    DateProcess convert = new DateProcess();
                    MarkerOptions marker = new MarkerOptions();
                    marker.title(destination.getDestination_name());
                    marker.snippet("第" + day.getWhich_day() + "天 - " + convert.get_date_to_str("hh:mm"));
//                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24));
                    marker.position(
                            new LatLng(
                                    destination.getDestination_gps()[0],
                                    destination.getDestination_gps()[1]
                            )
                    );

                    markerOptions_list.add(marker);
                });
            }
            markerOptions_list.forEach(markerOptions -> googleMap.addMarker(markerOptions));
        }

        if (guys_location_map != null) {
            Set<String> guy_ids = guys_location_map.keySet();
            for (String guy_id : guy_ids){
                GuyLocationData data =  guys_location_map.get(guy_id);
                MarkerOptions marker = new MarkerOptions();
                marker.title(data.getName());
                marker.snippet("位置更新時間：" + data.getTime());
//                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24));
                marker.position(new LatLng(data.getGeoPoint().getLatitude(), data.getGeoPoint().getLongitude()));

                googleMap.addMarker(marker);
            }
        }

        if (my_history_location_map != null && now_trip != null) {
            m_history_draw();
        }

    }

    private void m_history_draw() {
        Long[] time_longs = my_history_location_map.keySet().toArray(new Long[0]);
        if (time_longs.length == 0){
            return;
        }
        Arrays.sort(time_longs);

        int last_which_day = -100;
        long start_day = now_trip.getStart_day().getTime();

        ArrayList<ArrayList<LatLng>> line_location_list = new ArrayList<>();
        ArrayList<LatLng> temp_latLng = new ArrayList<>();

        for (int i =0;i<time_longs.length;i++){
            long time_long = time_longs[i];
            int which_day = cal_day_interval(start_day, time_long);
            if (last_which_day != which_day && i != 0) {
                line_location_list.add(temp_latLng);
                temp_latLng = new ArrayList<>();
            }
            temp_latLng.add(convert_to_LatLng(Objects.requireNonNull(my_history_location_map.get(time_long))));
            last_which_day = which_day;
        }
        line_location_list.add(temp_latLng);


        for (int i = 0; i < line_location_list.size(); i++) {
            googleMap.addPolyline(
                    new PolylineOptions()
                            .add(line_location_list.get(i).toArray(new LatLng[0]))
//                            .add(temp_latLng.toArray(new LatLng[0]))
                            .color(colors.get(i % colors.size()))
            );
        }

    }

    private LatLng convert_to_LatLng(GeoPoint geoPoint){
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    private int cal_day_interval(long start, long end){
        return ((int)((end - start) / (24 * 3600 * 1000))) + 1;
    }

}