package com.bacon.baconproject.togethermap.main.current_trip.journey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bacon.baconproject.togethermap.DateProcess;
import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.history_data.HistoryDay;
import com.bacon.baconproject.togethermap.history_data.HistoryDestination;
import com.bacon.baconproject.togethermap.history_data.HistoryTrip;
import com.bacon.baconproject.togethermap.main.current_trip.journey.destination_detail.Alert_Detail;

import java.util.ArrayList;
import java.util.Locale;

public class Recycler_JourneyItem extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private HistoryTrip trip;
    private Recycler_ListItem listItem;
    private FragmentManager manager;

    public Recycler_JourneyItem(Context context, HistoryTrip trip, FragmentManager manager){
        this.trip = trip;
        this.context = context;
        listItem = new Recycler_ListItem(trip);
        this.manager = manager;
    }

    public void notify_data_first(HistoryTrip new_trip){
//        trip = new_trip;
    }
    public void notify_data_change(HistoryTrip trip){
        listItem = new Recycler_ListItem(trip);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return listItem.get_type(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == 1){
            View v = LayoutInflater.from(context).inflate(R.layout.recycler_current_journey_item_middle, parent, false);
            holder = new MiddleHolder(v);
        } else if (viewType == 0){
            View v = LayoutInflater.from(context).inflate(R.layout.recycler_current_journey_item_top, parent, false);
            holder = new HeadHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.recycler_current_journey_item_bottom, parent, false);
            holder = new BottomHolder(v);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int view_type = listItem.get_type(position);
        switch (view_type) {
            case 0:
                HistoryDay day = listItem.getDay(position);
                HeadHolder headHolder = (HeadHolder) holder;
                headHolder.date_tv.setText(String.format(Locale.getDefault(), "第%d天", day.getWhich_day()));

                headHolder.note_tv_bt.setOnClickListener(null);

                break;
            case 1:
                HistoryDestination destination = listItem.getDestination(position);
                MiddleHolder middleHolder = (MiddleHolder) holder;
                middleHolder.content_tv.setText(destination.getDestination_name());

                DateProcess date_process = new DateProcess();
                String destination_time_str = date_process.get_date_to_str(destination.getArrive_time(), "HH:mm");
                middleHolder.time_stamp_tv.setText(destination_time_str);

                middleHolder.detail_button.setOnClickListener(a -> {
                    Alert_Detail alert_detail = new Alert_Detail(context, manager, trip, destination);
                    alert_detail.show();
                });



                break;
        }

    }

    @Override
    public int getItemCount() {
        return listItem.getItemCount();
    }


    private static class Recycler_ListItem {
//        private HistoryTrip trip;
        private ArrayList<HistoryDay> days;
        private ArrayList<Integer> days_position;  // 建立每個day的position
        private int ItemCount;

        public Recycler_ListItem(HistoryTrip trip){
//            this.trip = trip;
            days = trip.getHistory_days_list();
            days_position = new ArrayList<>();

            if (days.size() == 0){
                return;
            }

            days_position.add(0);

            int temp = 1;
            for (int i=0;i< days.size()-1;i++){
                temp = temp + days.get(i).getDestinations_list().size();
                days_position.add(temp);
                temp += 1;
            }

            ItemCount = temp + days.get(days.size()-1).getDestinations_list().size() + 1;

//            for (int i = 0; i < days.size(); i++) {
//                // 清空每一個day裡面的destination_list陣列
//                HistoryDay temp_day = days.get(i);
//                temp_day.getDestinations_list().clear();
//                days.set(i, temp_day);
//            }

        }

        public int get_type(int position){
            if (days_position.contains(position)){
                return 0;  // head
            }else if(position <= ItemCount-2){
                return 1;  // middle
            }else{
                return 2;  // bottom
            }
        }

        public HistoryDay getDay(int position) {
            for (int i = 0; i < days_position.size(); i++) {
                int days_index = days_position.get(i);
                if (position == days_index){
                    return days.get(i);
                }
            }
            return null;
        }

        public HistoryDestination getDestination(int position){
            int belong_day_index = -1;  // 屬於某天的行程，那個某天的position
            for (int i = 1; i < days_position.size(); i++) {
                if (position == days_position.get(i)){
                    return null;
                }
                if (position < days_position.get(i)){
                    belong_day_index = i-1;
                    break;
                }
            }

            if (belong_day_index == -1){
                belong_day_index = days.size()-1;
            }

            int destination_index_in_this_day = position - days_position.get(belong_day_index) - 1;

            return days.get(belong_day_index).getDestinations_list().get(destination_index_in_this_day);

        }

        public int getItemCount() {
            return ItemCount;
        }
    }

    static class HeadHolder extends RecyclerView.ViewHolder{
        TextView note_tv_bt, date_tv;
        public HeadHolder(@NonNull View v) {
            super(v);
            note_tv_bt = v.findViewById(R.id.recycler_current_journey_item_note_tv_bt);
            date_tv = v.findViewById(R.id.recycler_current_journey_item_date_tv);
        }
    }

    static class MiddleHolder extends RecyclerView.ViewHolder{
        TextView time_stamp_tv, content_tv;
        ImageButton detail_button;
        public MiddleHolder(@NonNull View v) {
            super(v);
            time_stamp_tv = v.findViewById(R.id.recycler_current_journey_item_time_tv);
            content_tv = v.findViewById(R.id.recycler_current_journey_item_target_name_tv);
            detail_button = v.findViewById(R.id.recycler_current_journey_item_detail_img_bt);
        }
    }

    static class BottomHolder extends RecyclerView.ViewHolder{
        public BottomHolder(@NonNull View v) {
            super(v);
        }
    }
}
