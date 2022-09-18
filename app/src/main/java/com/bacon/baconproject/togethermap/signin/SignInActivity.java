package com.bacon.baconproject.togethermap.signin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.bacon.baconproject.togethermap.R;
import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.main.main.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity{

    private GoogleSignInClient googleSignInClient;

    private SignInViewModel signInViewModel;

    private Button signOut_bt;
    private SignInButton signInButton;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("TAG", "Intent result is coming");
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSingInResult(task);
                }
            });

    private void handleGoogleSingInResult(Task<GoogleSignInAccount> completeTask) {
        try{
            GoogleSignInAccount account = completeTask.getResult(ApiException.class);
            if (account != null) {
                signInViewModel.firebaseAuthWithGoogle(this, account.getIdToken(), new SignInViewModel.FailedListener() {
                            @Override
                            public void onFailed(Task<AuthResult> task) {
                                Snackbar.make(findViewById(R.id.c), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (ApiException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("124158456996-2rd2p3h1898i7urk2uvo5mte2g3is4mf.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signOut_bt = findViewById(R.id.signout_bt);

        signInButton.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            launcher.launch(intent);
        });
        signOut_bt.setOnClickListener(view -> {
            Log.d("TAG", "account signOut");
            account_signOut();
        });

        signInViewModel.get_userInfo().observe(this, new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo userInfo) {
                when_get_current_userInfo(userInfo);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        signInViewModel.activity_on_start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void when_get_current_userInfo(UserInfo userInfo){
        change_activate();
        Log.d("TAG", "finish activity now!!!");
        this.finish();
    }

    private void change_activate(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        Log.d("TAG", "change activity now!!!");

    }

    private void account_signOut(){
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();
    }

}