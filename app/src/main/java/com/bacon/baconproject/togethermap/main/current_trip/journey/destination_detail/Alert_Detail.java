package com.bacon.baconproject.togethermap.main.current_trip.journey.destination_detail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bacon.baconproject.togethermap.DateProcess;
import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.history_data.HistoryDestination;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.main.current_trip.journey.DialogFragment_UpdateDestination;

import java.io.File;

public class Alert_Detail extends AlertDialog {
    private HistoryDestination destination;
    private HistoryTrip trip;
    private FragmentManager manager;
    private AlertDialog dialog;

    public Alert_Detail(Context context, FragmentManager manager, HistoryTrip trip, HistoryDestination destination) {
        super(context, R.style.my_alert_dialog_style);
        this.manager = manager;
        this.trip = trip;
        this.destination = destination;
        this.dialog = this;

        this.setView(construct_view(context, destination));

    }

    private View construct_view(Context context, HistoryDestination destination) {
        View v = LayoutInflater.from(context).inflate(R.layout.alert_journey_item_detail, null);

        TextView date_tv, name_tv, send_tv_bt;
        ImageView gps_img_bt, write_img_bt, share_img_bt;
        RecyclerView note_recycler;
        EditText note_et;

        date_tv = v.findViewById(R.id.alert_journey_detail_date_tv);
        name_tv = v.findViewById(R.id.alert_journey_detail_name);
        gps_img_bt = v.findViewById(R.id.alert_journey_detail_gps_img_bt);
        write_img_bt = v.findViewById(R.id.alert_journey_detail_write_img_bt);
        share_img_bt = v.findViewById(R.id.alert_journey_detail_share_img_bt);
        note_recycler = v.findViewById(R.id.alert_journey_detail_note_recycler);
        note_et = v.findViewById(R.id.alert_journey_detail_message_et);

        DateProcess date_process = new DateProcess();
        date_tv.setText(date_process.get_date_to_str(destination.getArrive_time(), "MM/dd\nHH:mm"));
        name_tv.setText(destination.getDestination_name());

        gps_img_bt.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("geo:" + destination.getDestination_gps()[0] + "," + destination.getDestination_gps()[1] + "?q=" + destination.getDestination_name());

//            Uri uri = Uri.parse("geo:" + destination.getDestination_gps()[0] + "," + destination.getDestination_gps()[1]);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null){
                context.startActivity(mapIntent);
            }else{
                Toast.makeText(context, "沒有google map，因此無法開啟導航", Toast.LENGTH_SHORT).show();
            }

        });

        write_img_bt.setOnClickListener(view -> {
            DialogFragment_UpdateDestination dialogFragment_updateDestination = new DialogFragment_UpdateDestination(context, trip, destination, () -> {});
            dialogFragment_updateDestination.show(manager, "tag");
            dialog.cancel();
        });

        share_img_bt.setOnClickListener(view -> {
            String shareUrl = String.format("https://www.google.com/maps/search/%s/@%s,%s", destination.getDestination_name(), destination.getDestination_gps()[0] + "", destination.getDestination_gps()[1] + "");
            shareUrl = "分享地點給你！\n" + shareUrl;

            shareMsg(context, "Together Map", "分享地點", shareUrl, null);
        });

        return v;
    }

    public void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 純文字
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }


}
