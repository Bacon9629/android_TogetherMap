package com.bacon.baconproject.togethermap.main.historylist;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bacon.baconproject.togethermap.R;

public class AlertDialog_ChooseWhich extends AlertDialog {

    public interface OnChoose{
        void choose_join_journey();
        void choose_add_journey();
    }

    public AlertDialog_ChooseWhich(@NonNull Context context, @NonNull OnChoose onChoose) {
        super(context, R.style.my_alert_dialog_style);

        View v = construct_view(context, onChoose);
        this.setView(v);
    }

    private View construct_view(Context context, OnChoose onChoose) {

        View v = LayoutInflater.from(context).inflate(R.layout.alert_choose_add_or_join_journey, null, false);
        TextView join_et_bt = v.findViewById(R.id.join_journey);
        TextView construct_et_bt = v.findViewById(R.id.construct_journey);

        join_et_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChoose.choose_join_journey();
                closeAlert();
            }
        });

        construct_et_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChoose.choose_add_journey();
                closeAlert();
            }
        });

        return v;
    }

    public void closeAlert(){
        this.cancel();
    }
}
