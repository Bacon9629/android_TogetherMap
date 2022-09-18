package com.bacon.baconproject.togethermap.main.historylist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.database.SharePreference_Parameter;

public class AlertDialog_TrackingActive extends AlertDialog {

    interface OkListener{
        void onOk(String name);
    }

    String trip_id;
    private OkListener okListener;

    protected AlertDialog_TrackingActive(Context context, String trip_id, OkListener okListener) {
        super(context);
        this.trip_id = trip_id;
        this.okListener = okListener;

        this.setView(construct_view(context));
//        this.show();

    }

    private View construct_view(Context context) {
        AlertDialog me = this;
        SharedPreferences preferences = context.getSharedPreferences(SharePreference_Parameter.my_data_id, Context.MODE_PRIVATE);
        View v = LayoutInflater.from(context).inflate(R.layout.alert_tracking_active, null, false);

        EditText name_et = v.findViewById(R.id.alert_tracking_name_et);
        TextView ok_tv_bt = v.findViewById(R.id.alert_tracking_ok_tv_bt);
        TextView cancel_tv_bt = v.findViewById(R.id.alert_tracking_cancel_tv_bt);

        name_et.setText(preferences.getString("name", ""));

        ok_tv_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_et.getEditableText().toString();
                preferences.edit().putString("name", name).apply();
                okListener.onOk(name);
                me.dismiss();
            }
        });

        cancel_tv_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                me.dismiss();
            }
        });

        return v;
    }
}
