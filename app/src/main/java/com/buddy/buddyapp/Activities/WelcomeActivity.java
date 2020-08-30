package com.buddy.buddyapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDex;

import com.buddy.buddyapp.Constant.Common;
import com.buddy.buddyapp.Constant.PopUp;
import com.buddy.buddyapp.Model.User;
import com.buddy.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {

    private Button btn_login, btn_register;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseUser user;
    private com.buddy.buddyapp.Constant.PopUp PopUp = new PopUp();
    private LinearLayout loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        loadingBar = findViewById(R.id.ProgressBar_main);
        btn_login = findViewById(R.id.main_login_btn);
        btn_register = findViewById(R.id.main_register_btn);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
       LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));

        CheckUser();
                btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, RegistrationActivity.class));
            }
        });
    }
    private void CheckUser(){
        loadingBar.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            if (user != null) {
                DocumentReference docRef = mStore.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Common.currentUser = task.getResult().toObject(User.class);
                            loadingBar.setVisibility(View.GONE);
                        }
                    }

                });
            }else{
                loadingBar.setVisibility(View.GONE);
            }
        }else {
            loadingBar.setVisibility(View.GONE);
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
        }
    }
    private BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          CheckUser();
        }
    };
}
