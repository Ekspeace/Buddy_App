package com.example.buddyapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Constant.PopUp;
import com.example.buddyapp.Model.User;
import com.example.buddyapp.R;
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
import java.util.Map;
import java.util.jar.Attributes;

public class RegistrationActivity extends AppCompatActivity {
    private Button signIn, signnUp;
    private TextInputLayout regUsername,
            regEmail, regPassword, phone, homeAddress;
    private LinearLayout loadingbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userID;
    private  View layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        SetVariables();
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
                    } else if (password.length() < 7) {
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
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    PopUp.smallToast(RegistrationActivity.this, layout,R.drawable.small_success,"Registration successfully, Please check your email for verification",Toast.LENGTH_LONG);
                                                    userID = mAuth.getCurrentUser().getUid();
                                                    DocumentReference documentReference = mStore.collection("users").document(userID);
                                                    User user = new User(userID, Name, Address, Phone, email, password);
                                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            PopUp.smallToast(RegistrationActivity.this, layout,R.drawable.small_success,"User ended",Toast.LENGTH_SHORT);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            PopUp.smallToast(RegistrationActivity.this, layout,R.drawable.small_error, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT);
                                                        }
                                                    });
                                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                                    loadingbar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            } else {
                                PopUp.smallToast(RegistrationActivity.this, layout,R.drawable.small_error, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT);
                                loadingbar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    PopUp.Toast(RegistrationActivity.this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
                    loadingbar.setVisibility(View.GONE);
                }
            }
        });
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
    }


}
