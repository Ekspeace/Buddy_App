package com.ekspeace.buddyapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.Model.User;
import com.ekspeace.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import io.paperdb.Paper;

public class RegistrationActivity extends AppCompatActivity {
    private Button signIn, signnUp;
    private TextInputLayout regUsername,
            regEmail, regPassword, phone, homeAddress;
    private LinearLayout loadingbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userID;
    private  View layout;
    private final PopUp PopUp = new PopUp();
    private final BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RegistrationProcess();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        SetVariables();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));

        loadingbar = findViewById(R.id.ProgressBar_register);
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        signIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent( RegistrationActivity.this,LoginActivity.class));
            }
        });

        signnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               RegistrationProcess();
            }
        });
    }

    private void RegistrationProcess(){
        final String email = regEmail.getEditText().getText().toString().trim();
        final String password = regPassword.getEditText().getText().toString().trim();
        final String Name = regUsername.getEditText().getText().toString();
        final String Address = homeAddress.getEditText().getText().toString();
        final String Phone = phone.getEditText().getText().toString();

        boolean error = false;
        if (!error) {
            if (TextUtils.isEmpty(email)) {
                regEmail.setError("Email is Required.");
                error = true;
            }
            if (TextUtils.isEmpty(Name)) {
                regUsername.setError("Name is required");
                error = true;
            }
            if (TextUtils.isEmpty(password)) {
                regPassword.setError("Password is Required.");
                error = true;
            } else if (password.length() < 6) {
                regPassword.setError("Password Must be greater than 6 Characters");
                error = true;
            }
            if (TextUtils.isEmpty(Phone)) {
                phone.setError("Phone is Required.");
                error = true;
            } else if (Phone.length() == 11) {
                phone.setError("Phone Must be 10 Numbers");
                error = true;
            }
            if (TextUtils.isEmpty(Address)) {
                homeAddress.setError("Address is Required.");
                error = true;
            }

            if (error)
                return;
        }
        loadingbar.setVisibility(View.VISIBLE);
        if (Common.isOnline(RegistrationActivity.this)) {
            // register the user in firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    com.ekspeace.buddyapp.Constant.PopUp.smallToast(RegistrationActivity.this, layout, R.drawable.small_success,"Registration successfully, Please verify your phone number",Toast.LENGTH_LONG);
                    Paper.book().destroy();
                    userID = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = mStore.collection("users").document(userID);
                    HashMap<String,Object> user = new HashMap<>();
                    user.put("userId", userID);
                    user.put("name", Name);
                    user.put("address", Address);
                    user.put("phone", Phone);
                    user.put("email", email);
                    user.put("password", password);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingbar.setVisibility(View.GONE);
                            com.ekspeace.buddyapp.Constant.PopUp.smallToast(RegistrationActivity.this, layout, R.drawable.small_error, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT);
                        }
                    });
                    Intent intent = new Intent(RegistrationActivity.this, VerifyPhoneActivity.class);
                    intent.putExtra("mobile", Phone);
                    loadingbar.setVisibility(View.GONE);
                    startActivity(intent);

                }else {
                    loadingbar.setVisibility(View.GONE);
                    com.ekspeace.buddyapp.Constant.PopUp.smallToast(RegistrationActivity.this, layout, R.drawable.small_error, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingbar.setVisibility(View.GONE);
                    com.ekspeace.buddyapp.Constant.PopUp.smallToast(RegistrationActivity.this, layout, R.drawable.small_error, "Error ! " + e.getMessage(),Toast.LENGTH_SHORT);
                }
            });
        } else {
            loadingbar.setVisibility(View.GONE);
            PopUp.Toast(RegistrationActivity.this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
        }
    }
    private void SetVariables(){
        signIn = findViewById(R.id.alreadyUser);
        signnUp = findViewById(R.id.SignUp);
        regUsername = findViewById(R.id.name);
        regEmail = findViewById(R.id.Regemail);
        phone = findViewById(R.id.phone);
        homeAddress = findViewById(R.id.address);
        regPassword = findViewById(R.id.Regpassword);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        Paper.init(this);
    }

    @Override
    public void onBackPressed() {
        long backPressedTime = 0;
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            com.ekspeace.buddyapp.Constant.PopUp.smallToast(RegistrationActivity.this, layout, R.drawable.small_error,"Please press again to exit...",Toast.LENGTH_SHORT);
        } else {
            System.exit(0);
        }
    }
}
