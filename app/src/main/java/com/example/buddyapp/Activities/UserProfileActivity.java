package com.example.buddyapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;

import io.paperdb.Paper;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private TextInputLayout InputEmail, InputPhone,
                     InputPassword, InputName, InputAddress;
    private Button updateInfoButton;
    private Toolbar mToolbar;
    private View layout;
    private LinearLayout loadingbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mToolbar = findViewById(R.id.toolbar);
        InputName = findViewById(R.id.update_name);
        InputEmail = findViewById(R.id.update_email);
        InputPhone = findViewById(R.id.update_phone);
        InputAddress = findViewById(R.id.update_address);
        InputPassword = findViewById(R.id.update_password);
        updateInfoButton = findViewById(R.id.update_user_info);

        loadingbar = findViewById(R.id.ProgressBar_user_profile);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        SetFieldWithCurrentInfo();
        Actionbar();
        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = InputEmail.getEditText().getText().toString().trim();
                final String password = InputPassword.getEditText().getText().toString().trim();
                final String Name = InputName.getEditText().getText().toString();
                final String Address = InputAddress.getEditText().getText().toString();
                final String Phone = InputPhone.getEditText().getText().toString();

                boolean error = false;
                if (!error) {
                    if (TextUtils.isEmpty(email)) {
                        InputEmail.setError("Email is Required.");
                        error = true;
                    }
                    if (TextUtils.isEmpty(Name)) {
                        InputName.setError("Name is required");
                        error = true;
                    }
                    if (TextUtils.isEmpty(password)) {
                        InputPassword.setError("Password is Required.");
                        error = true;
                    } else if (password.length() < 7) {
                        InputPassword.setError("Password Must be greater than 6 Characters");
                        error = true;
                    }
                    if (TextUtils.isEmpty(Phone)) {
                        InputPhone.setError("Phone is Required.");
                        error = true;
                    } else if (Phone.length() == 11) {
                        InputPhone.setError("Phone Must be 10 Numbers");
                        error = true;
                    }
                    if (TextUtils.isEmpty(Address)) {
                        InputAddress.setError("Address is Required.");
                        error = true;
                    }

                    if (error)
                        return;
                }

                loadingbar.setVisibility(View.VISIBLE);
                if (Common.isOnline(UserProfileActivity.this)) {
                    mAuth.updateCurrentUser(mAuth.getCurrentUser())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userID = mAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = mStore.collection("users").document(userID);
                                        HashMap<String, Object> user = new HashMap<>();
                                        user.put("userId", userID);
                                        user.put("name", Name);
                                        user.put("address", Address);
                                        user.put("phoneNumber", Phone);
                                        user.put("email", email);
                                        user.put("password", password);
                                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                PopUp.smallToast(UserProfileActivity.this, layout,R.drawable.small_success,"User updated",Toast.LENGTH_SHORT);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                PopUp.smallToast(UserProfileActivity.this, layout,R.drawable.small_error,"Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT);
                                            }
                                        });
                                        mAuth.updateCurrentUser(mAuth.getCurrentUser());
                                        DocumentReference docRef = mStore.collection("users").document(mAuth.getCurrentUser().getUid());
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Common.currentUser = task.getResult().toObject(User.class);
                                                    PopUp.smallToast(UserProfileActivity.this, layout,R.drawable.small_success,"Successfully updated your information",Toast.LENGTH_SHORT);
                                                    startActivity(new Intent(UserProfileActivity.this, MenuActivity.class));
                                                    loadingbar.setVisibility(View.GONE);
                                                }
                                            }

                                        });

                                    }
                                }
                            });
                }else {
                    PopUp.Toast(UserProfileActivity.this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
                    loadingbar.setVisibility(View.GONE);
                }
            }
        });
    }


    private void SetFieldWithCurrentInfo()
    {
        InputName.getEditText().setText(Common.currentUser.getName());
        InputEmail.getEditText().setText(Common.currentUser.getEmail());
        InputPhone.getEditText().setText(Common.currentUser.getPhoneNumber());
        InputAddress.getEditText().setText(Common.currentUser.getAddress());
        InputPassword.getEditText().setText(Common.currentUser.getPassword());

    }

    private void Actionbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });
    }
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.togglemenu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case R.id.signOut:
                    PopUp.SignOut(this);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
}

