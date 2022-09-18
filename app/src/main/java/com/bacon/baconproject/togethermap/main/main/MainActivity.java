package com.bacon.baconproject.togethermap.main.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.main.historylist.HistoryListFragment;

public class MainActivity extends AppCompatActivity {

//    FragmentManager manager;

    boolean have_current_trip = false;

//    NavHostFragment navHostFragment;
//    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
//        if (savedInstanceState == null) {
//            manager = getSupportFragmentManager();
//            .beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
//                    .commitNow();
//        }

//        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
//        navController = navHostFragment.getNavController();

//        Log.d("test", )

//        findViewById(R.id.main_fragment_bt).setOnClickListener(v -> {
//            navController.navigate(R.id.action_mainFragment_to_currentTripFragment);
//        });

        UserInfo userInfo = UserInfo.getInstance();
        if (userInfo.getUser_email().equals("")){
            userInfo.setUser_email("qwe43213652@gmail.com");
        }



    }
}