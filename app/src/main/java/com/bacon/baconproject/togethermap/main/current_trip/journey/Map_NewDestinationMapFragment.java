package com.bacon.baconproject.togethermap.main.current_trip.journey;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bacon.baconproject.togethermap.MyLog;
import com.bacon.baconproject.togethermap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;

public class Map_NewDestinationMapFragment extends Fragment {

//    private static Map_NewDestinationMapFragment me = null;
//    public static Map_NewDestinationMapFragment getInstance(){
//        // 可能會回傳null
//        return me;
//    }

//    private static Map_NewDestinationMapFragment me = null;
//    public static Map_NewDestinationMapFragment getInstance(){
//        return me==null ? new Map_NewDestinationMapFragment() : me;
//    }

    private GoogleMap googleMap;
    private Context context;

    public Map_NewDestinationMapFragment(){}


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

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
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.show_map_log("Map_NewDestinationMapFragment onCreate");
    }

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
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }else{
            MyLog.show_map_log("Map_NewDestinationMapFragment noooooooo");
        }

        MyLog.show_map_log("Map_NewDestinationMapFragment onViewCreated");
//        context = getContext();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyLog.show_map_log("Map_NewDestinationMapFragment onDestroyView");
    }

    public void test_destination_search() {
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
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng).title(location));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            Toast.makeText(context,address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();
        }
    }

//    public GeoPoint get_
//    public void show_destination

}
