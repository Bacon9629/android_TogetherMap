package com.bacon.baconproject.togethermap.main.historylist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bacon.baconproject.togethermap.DateProcess;
import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.database.Firestore_Write_DB;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AlertDialog_SetJourney extends AlertDialog {

    HistoryTrip trip;
    String m_date_format = "yyyy/MM/dd";

    public AlertDialog_SetJourney(@NonNull Context context, HistoryTrip trip) {
        super(context, R.style.my_alert_dialog_style);

        this.trip = trip;
        View v = construct_view(context);
        this.setView(v);
        this.setCancelable(false);

    }

    public interface onCloseDialog{
        void onClose();
    }

    private View construct_view(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.add_current_journey, null, false);
        EditText start_date_et = v.findViewById(R.id.add_current_journey_start_date_et);
        EditText end_date_et = v.findViewById(R.id.add_current_journey_ent_date_et);
        EditText trip_name_et = v.findViewById(R.id.add_current_journey_trip_name_et);
        ImageButton a = v.findViewById(R.id.add_current_journey_start_date_img_bt);
        ImageButton b = v.findViewById(R.id.add_current_journey_end_date_img_bt);
        TextView title_tv = v.findViewById(R.id.add_current_journey_title_tv);
        TextView check_tv_bt = v.findViewById(R.id.check_tv_bt);
        TextView cancel_tv_bt = v.findViewById(R.id.cancel_tv_bt);
        ProgressBar progressBar = v.findViewById(R.id.progressBar);

        title_tv.setText("設定旅程資訊");

        DateProcess convert = new DateProcess();
        start_date_et.setText(convert.get_date_to_str(trip.getStart_day(), m_date_format));
        end_date_et.setText(convert.get_date_to_str(trip.getEnd_day(), m_date_format));
        trip_name_et.setText(trip.getTrip_name());

        progressBar.setVisibility(View.GONE);


        View.OnClickListener date_item_listener = touch_item -> {
            Calendar calendar = Calendar.getInstance();

            try {
                if (touch_item.getTag().toString().equals("start")){
                    calendar.setTime(convert.get_str_to_date(start_date_et.getText().toString(), m_date_format));
                }else{
                    calendar.setTime(convert.get_str_to_date(end_date_et.getText().toString(), m_date_format));
                }

            } catch (ParseException e){
                e.printStackTrace();
            }

            int[] year_month_day = {calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)};
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month += 1;
                    String result = String.format(Locale.getDefault(), "%04d/%02d/%02d", year, month, day);
                    if (touch_item.getTag().toString().equals("start")){
                        start_date_et.setText(result);
                    }else{
                        end_date_et.setText(result);
                    }
                }
            };
            DatePickerDialog date_dialog = new DatePickerDialog(context,dateSetListener, year_month_day[0], year_month_day[1], year_month_day[2]);
            date_dialog.show();
        };

        start_date_et.setOnClickListener(date_item_listener);
        end_date_et.setOnClickListener(date_item_listener);
        a.setOnClickListener(date_item_listener);
        b.setOnClickListener(date_item_listener);

        check_tv_bt.setOnClickListener(bt -> {
            // do upload and show

            if (end_date_et.getEditableText().toString().equals("")
                    || start_date_et.getEditableText().toString().equals("")
                    || trip_name_et.getEditableText().toString().equals("")){

                Toast.makeText(context, "填寫不完全", Toast.LENGTH_SHORT).show();
                return;
            }

            Firestore_Write_DB upload_db = new Firestore_Write_DB();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd,hh:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            try {
                Date[] dates = {
//                            Calendar.getInstance().getTime(),
                        dateFormat.parse(start_date_et.getEditableText().toString() + ",00:00:00"),
                        dateFormat.parse(end_date_et.getEditableText().toString() + ",23:59:00")
                };
                trip.change_trip_data(trip_name_et.getEditableText().toString(),
                        dates[0],
                        dates[1]
                );
                upload_db.upload_update_journey(trip, context);
//                progressBar.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            this.cancel();

        });

        cancel_tv_bt.setOnClickListener(bt -> {
            this.cancel();
        });

        return v;
    }




}
