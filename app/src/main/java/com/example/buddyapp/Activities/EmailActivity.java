package com.example.buddyapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Constant.PopUp;
import com.example.buddyapp.MailSender;
import com.example.buddyapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


import io.paperdb.Paper;

public class EmailActivity extends AppCompatActivity {
    private Button btn_Send;
    private TextInputLayout InputSubject, InputMessage;
    private Toolbar mToolbar;
    private LinearLayout dialog;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        InputSubject = findViewById(R.id.email_subject);
        InputMessage = findViewById(R.id.email_massage);
        btn_Send = findViewById(R.id.btn_email_send);
        mToolbar = findViewById(R.id.toolbar);
        dialog =  findViewById(R.id.ProgressBar_email);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        Actionbar();
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        dialog.setVisibility(View.VISIBLE);
        String Subject = InputSubject.getEditText().getText().toString().trim();
        String Message = InputMessage.getEditText().getText().toString().trim();
        boolean error = false;
        if(!error)
        {
            if(TextUtils.isEmpty(Subject)){
                InputSubject.setError("Subject is Required.");
                error = true;
            }

            if(TextUtils.isEmpty(Message)) {
                InputMessage.setError("Message is Required.");
                error = true;
            }
            if(error)
                return;

        }

        if (Common.isOnline(this)) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        MailSender sender = new MailSender(Common.real_user_email, Common.real_user_password);
                        sender.sendMail(Subject, Message,
                                Common.real_user_email,
                                "tshelezas@gmail.com");
                        EmailActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                PopUp.smallToast(EmailActivity.this, layout,R.drawable.small_success,"The email is successfully sent",Toast.LENGTH_SHORT);
                                dialog.setVisibility(View.GONE);
                                finish();
                            }
                        });
                    } catch (Exception e) {
                        if (e.getMessage() == null) {
                            EmailActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    PopUp.Toast(EmailActivity.this, "Error occurred ...", "Gmail is blocking the communication, enable app access", R.drawable.error);
                                    dialog.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            if(e.getMessage().equals("Authentication"))
                            {
                                EmailActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        PopUp.Toast(EmailActivity.this, "Error occurred ...", "The email is not sent, check you credentials and try again", R.drawable.error);
                                        dialog.setVisibility(View.GONE);
                                        finish();
                                    }
                                });
                            }
                            EmailActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    PopUp.Toast(EmailActivity.this, "Error occurred ...", e.getMessage(), R.drawable.error);
                                    dialog.setVisibility(View.GONE);
                                    finish();
                                }
                            });
                        }
                    }
                }
            }).start();
        }else {
            PopUp.Toast(EmailActivity.this, "Email failed to send...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }

    private void Actionbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
