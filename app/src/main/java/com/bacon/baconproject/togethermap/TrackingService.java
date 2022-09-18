package com.bacon.baconproject.togethermap;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bacon.baconproject.togethermap.database.DB;
import com.bacon.baconproject.togethermap.database.Firebase_NowGuysGps;
import com.bacon.baconproject.togethermap.database.Firestore_MyGPSData;
import com.bacon.baconproject.togethermap.database.SharePreference_My_GPS_Save;

public class TrackingService extends Service {

    private SharePreference_My_GPS_Save my_preference;
    private Firebase_NowGuysGps db;
    private LocationManager locationManager;
    private String trip_id, trip_name, user_name, user_email;
    private Context context;
    private double last_distance = 1;
    private Location last_location;
    private int last_delay = 5 * 60 * 1000;

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            db.update_my(location);
            save_now_gps_to_preference(location);
            locationManager.removeUpdates(this);
            if (last_location != null){
                last_distance = Math.sqrt(
                        Math.pow(location.getLatitude(), 2) - Math.pow(last_location.getLatitude(), 2) +
                        Math.pow(location.getLongitude(), 2) - Math.pow(last_location.getLongitude(), 2)
                );
            }
            last_location = location;
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this.getApplicationContext();
        trip_id = intent.getStringExtra("trip_id");
        trip_name = intent.getStringExtra("trip_name");
        user_name = intent.getStringExtra("user_name");
        user_email = intent.getStringExtra("user_email");

        my_preference = new SharePreference_My_GPS_Save(context, trip_id);

        if (trip_id.equals("")){
            MyLog.show_service_log("trip id is null, destroy service");
            this.stopSelf();
        }

        db =  new Firebase_NowGuysGps(trip_id, user_name, user_email);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(false).setContentTitle("足跡追蹤ING - " + trip_name)
                .setContentText("請保持GPS服務開啟狀態，若是想取消足跡追蹤，請至APP內關閉")
                .setSmallIcon(R.drawable.ic_my_icon2)
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(trip_id, "gps_tracking", NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            builder.setChannelId(trip_id);
        }


        Toast.makeText(context, "開始足跡追蹤", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        check_gps_on();
//        open_tracking_location(locationManager);
        add_force_refresh_location(locationManager);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                open_tracking_location(locationManager);

                last_delay = cal_delay_millis(last_distance);
                handler.postDelayed(this, last_delay);
            }
        };
        handler.postDelayed(runnable, 1000);

        stop_service_receiver(this);

        startForeground(100, builder.build());
        return START_STICKY;
    }

    private void stop_service_receiver(Service context) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context __, Intent intent) {
                if (intent.getAction().equals("stop_tracking_action")){
                    context.stopForeground(true);
                    context.stopSelf();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("stop_tracking_action");
        context.registerReceiver(receiver, filter);
    }

    private int cal_delay_millis(double last_distance){
//        if (last_distance < 0.002){
//            return 60 * 60 * 1000;
//        } else if (last_distance < 0.005){
//            return 30 * 60 * 1000;
//        } else
        if (last_distance < 0.01){
            return 10 * 60 * 1000;
        } else{
            return 5 * 60 * 1000;
        }
//        return 5 * 60 * 1000;
//        return 5 * 1000;
    }

    private void add_force_refresh_location(LocationManager locationManager) {

        db.add_force_update_snapshot(new DB.onDataChange<Void>() {
            @Override
            public void onChange(Void item) {
                remove_tracking_location(locationManager);
                open_tracking_location(locationManager);
            }
        });

    }

//    private void check_gps_on() {
//
//    }

    @SuppressLint("MissingPermission")
    private void open_tracking_location(LocationManager locationManager) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, locationListener);
    }

    private void remove_tracking_location(LocationManager locationManager){
        locationManager.removeUpdates(locationListener);
    }

    private void save_now_gps_to_preference(Location location){
        my_preference.save_now_gps(location);
        MyLog.show_normal_log("a");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MyLog.show_normal_log("close tracking");
        remove_tracking_location(locationManager);
        Firestore_MyGPSData gpsData = new Firestore_MyGPSData(user_email, trip_id);
        gpsData.upload_my_gps_history(my_preference.get_trip_my_way());
        my_preference.del_this_trip_data();

    }
}
