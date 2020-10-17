package com.ekspeace.buddyapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private Button btnResetPassword, btnBack;
    private TextInputLayout tvEmail;
    private FirebaseAuth mAuth;
    private LinearLayout loadingbar;
    private View layout;
    private com.ekspeace.buddyapp.Constant.PopUp PopUp = new PopUp();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        btnBack = findViewById(R.id.ResetPassword_back);
        btnResetPassword = findViewById(R.id.ResetPassword);
        tvEmail = findViewById(R.id.ResetEmail);
        loadingbar = findViewById(R.id.ProgressBar_reset);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        mAuth = FirebaseAuth.getInstance();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPassword.this, LoginActivity.class));
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPassword();
            }
        });
    }

    private void ResetPassword(){
        String email = tvEmail.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            tvEmail.setError("Please enter your email");
            return;
        }
        loadingbar.setVisibility(View.VISIBLE);
        if (Common.isOnline(ResetPassword.this)) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingbar.setVisibility(View.GONE);
                                PopUp.smallToast(ResetPassword.this, layout, R.drawable.small_success, "Check your email to reset your password!", Toast.LENGTH_SHORT);
                                startActivity(new Intent(ResetPassword.this, LoginActivity.class));
                            } else {
                                loadingbar.setVisibility(View.GONE);
                                PopUp.smallToast(ResetPassword.this, layout, R.drawable.small_error, "Fail to send reset password email, re-enter the email! ", Toast.LENGTH_SHORT);
                                startActivity(new Intent(ResetPassword.this, LoginActivity.class));
                            }
                        }
                    });
        } else {
            PopUp.Toast(ResetPassword.this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            loadingbar.setVisibility(View.GONE);
        }
    }
    private BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           ResetPassword();
        }
    };
}