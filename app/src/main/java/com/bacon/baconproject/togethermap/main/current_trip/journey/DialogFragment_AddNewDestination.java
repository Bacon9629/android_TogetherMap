package com.bacon.baconproject.togethermap.main.current_trip.journey;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bacon.baconproject.togethermap.DateProcess;
import com.bacon.baconproject.togethermap.MyLog;
import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.database.Firestore_Write_DB;
import com.bacon.baconproject.togethermap.history_data.HistoryDestination;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class DialogFragment_AddNewDestination extends androidx.fragment.app.DialogFragment {

    private Context context;
    private OnAddSuccess onAddSuccess;
    private HistoryTrip trip;
    private HistoryDestination destination;
    private Address address = null;
    private Address final_address = null;

    public interface OnAddSuccess{
        void OnSuccess();
    }

//    public DialogFragment_AddNewDestination(Context context, HistoryTrip trip, OnAddSuccess onAddSuccess){
//        this(context, trip, null, onAddSuccess);
//    }

    public DialogFragment_AddNewDestination(Context context, HistoryTrip trip, OnAddSuccess onAddSuccess){

        this.context = context;
        this.trip = trip;
//        this.destination = destination;
        this.onAddSuccess = onAddSuccess;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        MyLog.show_normal_log("DialogFragment_AddNewDestination construct");

        return inflater.inflate(R.layout.alert_add_destination, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(construct_view());


        return builder.create();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private View construct_view(){

        View v = getActivity().getLayoutInflater().from(context).inflate(R.layout.alert_add_destination, null, false);
        DialogFragment dialog = this;

        EditText day_et, hour_et, min_et, destination_et;
        ImageButton gps_bt;
        TextView date_tv, check_bt, cancel_bt;
        WebView webView;

        day_et = v.findViewById(R.id.alert_add_destination_day_et);
        hour_et = v.findViewById(R.id.alert_add_destination_hour_et);
        min_et = v.findViewById(R.id.alert_add_destination_min_et);
        destination_et = v.findViewById(R.id.alert_add_destination_destination_et);
        gps_bt = v.findViewById(R.id.alert_add_destination_gps_img_bt);
        date_tv = v.findViewById(R.id.alert_add_destination_date_tv);
        check_bt = v.findViewById(R.id.alert_add_destination_check_tv_bt);
        cancel_bt = v.findViewById(R.id.alert_add_destination_cancel_tv_bt2);
        webView = v.findViewById(R.id.alert_add_destination_web);

        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
//                trip.getStart_day()
                if (editable_is_not_int_access(day_et.getEditableText()) || editable_is_not_int_access(hour_et.getEditableText()) || editable_is_not_int_access(min_et.getEditableText())){
                    date_tv.setText("");
                }else{
                    date_tv.setText(calculate_date_intToString(
                            Integer.parseInt(day_et.getEditableText().toString()),
                            Integer.parseInt(hour_et.getEditableText().toString()),
                            Integer.parseInt(min_et.getEditableText().toString())
                    ));
                }
            }
        };

        day_et.addTextChangedListener(textWatcher);
        hour_et.addTextChangedListener(textWatcher);
        min_et.addTextChangedListener(textWatcher);

        check_bt.setOnClickListener(view -> {

            double[] destination_gps;
            String destination_name;
            String photo_address;
            Date arrive_time;

            if (date_tv.getText().toString().equals("")){
                Toast.makeText(context, "請先確認已填入時間，且上方的日期正確，再按下確認鍵", Toast.LENGTH_SHORT).show();
                return;
            }

            if (final_address != address || address == null){
                Toast.makeText(context, "請先確認地圖標示之地點是否正確，再按下確認鍵", Toast.LENGTH_SHORT).show();
                gps_bt.callOnClick();
                final_address = address;
                return;
            }

//            address.getLatitude(), address.getLongitude()
            destination_gps = new double[]{final_address.getLatitude(), final_address.getLongitude()};
            destination_name = destination_et.getEditableText().toString();
            photo_address = "";
            try {
                arrive_time = calculate_date_stringToDate(date_tv.getText().toString());
            } catch (ParseException e) {
                Toast.makeText(context, "日期格式錯誤", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }

            HistoryDestination new_destination = new HistoryDestination(
                    "",
                    destination_gps,
                    destination_name,
                    photo_address,
                    arrive_time
            );

            Firestore_Write_DB db_upload = new Firestore_Write_DB();

            if (new_destination.getDestination_id().equals("")){
                db_upload.upload_new_destination(context, trip, new_destination);
            }else{
                db_upload.upload_update_destination(context, trip, new_destination);
            }
            dialog.dismiss();
        });

        cancel_bt.setOnClickListener(view -> dialog.dismiss());

        gps_bt.setOnClickListener(view -> {
            String destination_name = destination_et.getEditableText().toString();
            address = destination_search(destination_name);

            if (address != null){
//                webView.loadUrl("https://www.google.com/maps/search/?api=1&query=" + address.getLatitude() + "%2c" + address.getLongitude());
//                String url = String.format("https://www.google.com/maps/search/%s/@%s,%s", destination_name, destination.getDestination_gps()[0] + "", destination.getDestination_gps()[1] + "");
                String url = String.format("https://www.google.com/maps/search/%s/@%s,%s", destination_name, address.getLatitude() + "", address.getLongitude() + "");
                webView.loadUrl(url);

            }else{
                Toast.makeText(context, "查無此地點", Toast.LENGTH_SHORT).show();
            }
        });

//        if (destination != null){
//            Date date = destination.getArrive_time();
//            Date_String_Convert date_string_convert = new Date_String_Convert();
//            date_tv.setText(date_string_convert.get_date_to_str(date, "yyyy/MM/dd HH:mm"));
//            destination_et.setText(destination.getDestination_name());
////            gps_bt.callOnClick();
//            Toast.makeText(context, "若時間沒有要更改，不須打上時間", Toast.LENGTH_SHORT).show();
//
//            show_del_button(v, destination, this);
//
//        }

        return v;

    }

    private void show_del_button(View v, HistoryDestination destination, DialogFragment dialog) {
        TextView del_tv_bt = v.findViewById(R.id.alert_add_destination_del_tv_bt);
        del_tv_bt.setVisibility(View.VISIBLE);
        del_tv_bt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Firestore_Write_DB write_db = new Firestore_Write_DB();
                write_db.del_destination(trip.getHistory_id(), destination.getDestination_id());
                dialog.dismiss();
                return false;
            }
        });
    }


    private String calculate_date_intToString(int which_day, int hour, int min){
        DateProcess convert = new DateProcess();
        return convert.get_int_day_to_string(trip.getStart_day(), which_day, hour, min);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(trip.getStart_day());
//        calendar.add(Calendar.DAY_OF_MONTH, which_day<0 ? 0:which_day);
//        if (hour >= 24){
//            hour = 23;
//            min = 59;
//        }else if(hour == 0){
//            min = 1;
//        }else if(min >= 60){
//            min = 59;
//        }
////        calendar.add(Calendar.HOUR_OF_DAY);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, min);
//        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
//        return format.format(calendar.getTime());
    }

    private Date calculate_date_stringToDate(String date) throws ParseException {

        DateProcess convert = new DateProcess();
        return convert.get_str_to_date(date, "yyyy/MM/dd HH:mm");

//        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(format.parse(date));
//        calendar.add(Calendar.HOUR_OF_DAY, -8);

//        return calendar.getTime();
    }

    private boolean editable_is_not_int_access(Editable editable){
        String s = editable.toString();
        return s.equals("") || s.contains(".");
    }

    private Address destination_search(String location) {
        List<Address> addressList = null;

        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(context);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList == null){
                return null;
            }
            if (addressList.size() == 0){
                return null;
            }
//            Toast.makeText(context,address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();
            return addressList.get(0);
        }else{
            return null;
        }
    }

}
