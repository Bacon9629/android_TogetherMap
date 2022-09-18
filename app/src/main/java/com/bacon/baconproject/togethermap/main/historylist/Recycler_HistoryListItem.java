package com.bacon.baconproject.togethermap.main.historylist;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bacon.baconproject.togethermap.DateProcess;
import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.TrackingService;
import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.history_data.HistoryTripList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recycler_HistoryListItem extends RecyclerView.Adapter<Recycler_HistoryListItem.MyHolder> {

    private final Context context;
    private ArrayList<HistoryTrip> historyTrips;

    private OnCall.OnCallMemory callMemory;


    public Recycler_HistoryListItem(Context context, OnCall.OnCallMemory callMemory) {

        this.context = context;
        this.callMemory = callMemory;
        this.historyTrips = new ArrayList<>();
    }

    public Recycler_HistoryListItem(Context context, ArrayList<HistoryTrip> trips, OnCall.OnCallMemory callMemory) {

        this.context = context;
        this.historyTrips = trips;
        this.callMemory = callMemory;
        this.historyTrips = new ArrayList<>();
    }

    public void notify_change(@NonNull HistoryTripList historyTripList) {
        historyTrips = historyTripList.getHistory_trip_list();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_history_list_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

//        MyLog.show_normal_log(historyTrips.get(position).getTrip_name());
//        MyLog.show_normal_log(position + "");

        HistoryTrip trip = historyTrips.get(position);

        holder.title_tv.setText(trip.getTrip_name());

        StringBuilder tag_str_builder = new StringBuilder();
        for (String item : trip.getTag()) {
            tag_str_builder.append(item);
            tag_str_builder.append(", ");
        }
        holder.tag_tv.setText(tag_str_builder.toString());

        DateProcess date_process = new DateProcess();
        String start_d_str = date_process.get_date_to_str(trip.getStart_day(), "yyyy/MM/dd");
        String end_d_str = date_process.get_date_to_str(trip.getEnd_day(), "yyyy/MM/dd");

        holder.date_tv.setText(String.format("%s ~ %s", start_d_str, end_d_str));

        holder.memory_tv_bt.setOnClickListener(view -> callMemory.call(trip));
        holder.invite_tv_bt.setOnClickListener(null);
        holder.setting_tv_bt.setOnClickListener(null);

        Date today_date = new Date();
        today_date.setTime(System.currentTimeMillis());
        boolean was_begin = today_date.after(trip.getStart_day());
        boolean not_finish = today_date.before(trip.getEnd_day());

        if (was_begin && not_finish) {
            holder.tracking_tv_bt.setVisibility(View.VISIBLE);
            holder.tracking_tv_bt.setOnClickListener(view -> {
                open_tracking(trip.getHistory_id(), trip.getTrip_name(), UserInfo.getInstance());
            });
        } else {
            holder.tracking_tv_bt.setVisibility(View.INVISIBLE);
        }

        holder.setting_tv_bt.setOnClickListener(view -> {
            AlertDialog_SetJourney setJourney = new AlertDialog_SetJourney(context, trip);
            setJourney.show();
        });

        holder.invite_tv_bt.setOnClickListener(view -> {
            shareMsg(context, "Together Map", "Together Map - 分享行程", trip.getHistory_id());
        });

    }

    public void shareMsg(Context context, String activityTitle, String msgTitle, String msgText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain"); // 純文字
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    private void open_tracking(String history_id, String history_name, UserInfo userInfo) {
        if (check_permission()){

            if (isServiceRunning(context, TrackingService.class.getName())){
                Intent intent = new Intent();
                intent.setAction("stop_tracking_action");
                context.sendBroadcast(intent);
                return;
            }

            AlertDialog_TrackingActive alert = new AlertDialog_TrackingActive(context, history_id, new AlertDialog_TrackingActive.OkListener() {
                @Override
                public void onOk(String name) {
                    Intent intent = new Intent(context, TrackingService.class);
                    intent.putExtra("trip_id", history_id);
                    intent.putExtra("trip_name", history_name);
                    intent.putExtra("user_name", name);
                    intent.putExtra("user_email", userInfo.getUser_email());
                    context.startService(intent);
                }
            });
            alert.show();
        }else{
            open_permission();
            Toast.makeText(context, "開啟權限後請再次點擊按鈕", Toast.LENGTH_LONG).show();
        }
    }

    private boolean check_permission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return context.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ;
        }else {
            return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ;
        }
    }

    private void open_permission() {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("需要執行權限").setMessage("請允許權限後再次點擊按鈕")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((Activity) context).requestPermissions(new String[]{
                                Manifest.permission.INTERNET,
                                Manifest.permission.FOREGROUND_SERVICE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 1);
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public int getItemCount() {
        return historyTrips.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        TextView title_tv, date_tv, tag_tv;
        TextView memory_tv_bt, invite_tv_bt, setting_tv_bt, tracking_tv_bt;

        public MyHolder(@NonNull View v) {
            super(v);

            title_tv = v.findViewById(R.id.recycler_history_list_item_title);
            date_tv = v.findViewById(R.id.recycler_history_list_item_date_tv);
            tag_tv = v.findViewById(R.id.recycler_history_list_item_tag_tv);
            memory_tv_bt = v.findViewById(R.id.recycler_history_list_item_open_memory_tv);
            invite_tv_bt = v.findViewById(R.id.recycler_history_list_item_invite_tv);
            setting_tv_bt = v.findViewById(R.id.recycler_history_list_item_setting_tv);
            tracking_tv_bt = v.findViewById(R.id.recycler_history_list_item_set_tracking_tv_bt);

        }
    }
}
