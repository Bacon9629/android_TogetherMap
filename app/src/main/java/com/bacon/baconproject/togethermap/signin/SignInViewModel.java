package com.bacon.baconproject.togethermap.signin;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bacon.baconproject.togethermap.UserInfo;
import com.bacon.baconproject.togethermap.database.Firestore_UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInViewModel extends ViewModel {

    private MutableLiveData<UserInfo> userInfo;

    private FirebaseAuth mAuth;


    public SignInViewModel(){
        userInfo = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
    }

    public void activity_on_start(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        when_get_currentUser(currentUser);
    }

    private void when_get_currentUser(FirebaseUser currentUser){
        if (currentUser == null){
            Log.d("TAG", "currentUser is null");
        }else{
            Log.d("TAG", "currentUser = " + currentUser.getEmail());

            get_user_data_from_firebase(currentUser.getEmail());
//            updateUI();

        }
    }

    public interface FailedListener{
        void onFailed(Task<AuthResult> task);
    }

    private void get_user_data_from_firebase(String email){


        Firestore_UserInfo firestore_email_id = new Firestore_UserInfo();
        firestore_email_id.get_user_info_from_firestore(email, info_map -> {
            UserInfo userInfo = UserInfo.getInstance();
            userInfo.set_user_info_with_map(info_map);
            this.userInfo.setValue(userInfo);
        });
    }

    public LiveData<UserInfo> get_userInfo(){
        return userInfo;
    }


    public void firebaseAuthWithGoogle(Activity activity, String idToken, FailedListener failedListener) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            when_get_currentUser(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.c), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            failedListener.onFailed(task);
                            when_get_currentUser(null);
                        }

                    }
                });
    }

}
