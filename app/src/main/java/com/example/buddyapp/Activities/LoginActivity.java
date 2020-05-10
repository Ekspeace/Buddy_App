package com.example.buddyapp.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buddyapp.BookingActivity;
import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Constant.PopUp;
import com.example.buddyapp.Model.User;
import com.example.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.TextView;


import java.util.List;

import io.paperdb.Paper;


public class LoginActivity extends AppCompatActivity {

    private Button newUser, signIN;
    private TextView forgtPass;
    private TextInputLayout email, password;
    private LinearLayout loadingBar;
    private FirebaseAuth mAuth;
    private CheckBox chkBoxRememberMe;
    private FirebaseUser user;
    private View layout;
    private FirebaseFirestore mStore;
    private String UserEmailKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //assign variables
        loadingBar = findViewById(R.id.ProgressBar_login);
        newUser = findViewById(R.id.newuser);
        signIN = findViewById(R.id.SignIn);
        forgtPass = findViewById(R.id.forget_Passsword);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);

        chkBoxRememberMe = findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        UserEmailKey = Paper.book().read(Common.UserEmailKey);



        Dexter.withActivity(this)
                .withPermissions(new String[]{
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                }).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (UserEmailKey != null) {
                        if (user != null && mAuth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                            finish();
                        } else {

                        }
                    }
                }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();





        newUser.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Start NewActivity.class
                Intent myIntent = new Intent(LoginActivity.this,
                        RegistrationActivity.class);
                startActivity(myIntent);
            }
        });

        signIN.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String User_Email = email.getEditText().getText().toString().trim();
                String User_Password = password.getEditText().getText().toString().trim();

                boolean error = false;
                if (!error) {
                    if (TextUtils.isEmpty(User_Email)) {
                        email.setError("Email is Required.");
                        error = true;
                    }

                    if (TextUtils.isEmpty(User_Password)) {
                        password.setError("Password is Required.");
                        error = true;
                    } else if (User_Password.length() < 6) {
                        password.setError("Password Must be greater 5 Characters");
                        error = true;
                    }
                    if (error)
                        return;

                }

                loadingBar.setVisibility(View.VISIBLE);
                if (chkBoxRememberMe.isChecked()) {
                    Paper.book().write(Common.UserEmailKey, User_Email);
                }
                if (Common.isOnline(LoginActivity.this)) {
                    mAuth.signInWithEmailAndPassword(User_Email, User_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    DocumentReference docRef = mStore.collection("users").document(task.getResult().getUser().getUid());
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Common.currentUser = task.getResult().toObject(User.class);
                                                PopUp.smallToast(LoginActivity.this, layout,R.drawable.small_success,"Your have logged in successfully",Toast.LENGTH_SHORT);
                                                loadingBar.setVisibility(View.GONE);
                                                Intent myIntent = new Intent(LoginActivity.this,
                                                        MenuActivity.class);
                                                startActivity(myIntent);
                                            }
                                        }

                                    });
                                } else {
                                    PopUp.Toast(LoginActivity.this,"Email verification", "Please verify your email address", R.drawable.error);
                                    loadingBar.setVisibility(View.GONE);
                                }
                            } else {
                                PopUp.Toast(LoginActivity.this, "Login failed...", "Please check your credentials and try again", R.drawable.error);
                                loadingBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else{
                    PopUp.Toast(LoginActivity.this, "Login failed...", "Please check your internet connectivity and try again", R.drawable.error);
                    loadingBar.setVisibility(View.GONE);
                }
            }
        });
    }


}
