package com.bacon.baconproject.togethermap.main.historylist;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.database.DB;
import com.bacon.baconproject.togethermap.database.Firestore_JoinJourney;

public class AlertDialog_JoinJourney extends AlertDialog {

    public interface OnJoinSuccess {
        void Success();
    }

    protected AlertDialog_JoinJourney(Context context, OnJoinSuccess onJoinSuccess) {
        super(context);

        View v = construct_view(context, onJoinSuccess);
        this.setView(v);
        this.setCancelable(false);

    }

    private View construct_view(Context context, OnJoinSuccess onJoinSuccess) {
        View v = LayoutInflater.from(context).inflate(R.layout.alert_join_journey, null, false);
        EditText id_input_et = v.findViewById(R.id.alert_join_id_et);
        TextView check_tv_bt = v.findViewById(R.id.alert_join_check_tv_bt);
        TextView cancel_tv_bt = v.findViewById(R.id.alert_join_cancel_tv_bt);

        check_tv_bt.setOnClickListener(view -> {
            String join_id = id_input_et.getEditableText().toString();
            if (join_id.equals("")){
                Toast.makeText(context, "請輸入ID", Toast.LENGTH_SHORT).show();
                return;
            }

            Firestore_JoinJourney joinJourney = new Firestore_JoinJourney(join_id);
            joinJourney.join(UserInfo.getInstance().getUser_email(),
                new DB.onJoinJourney() {
                    @Override
                    public void joinSuccess() {
                        onJoinSuccess.Success();
                        Toast.makeText(context, "加入成功", Toast.LENGTH_SHORT).show();
                        closeAlert();
                    }

                    @Override
                    public void joinFailed() {
                        Toast.makeText(context, "加入失敗，請確認ID是否正確", Toast.LENGTH_SHORT).show();
                        id_input_et.requestFocus();
                    }
                });

        });

        cancel_tv_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAlert();
            }
        });

        return v;
    }

    private void closeAlert(){
        this.cancel();
    }

}
