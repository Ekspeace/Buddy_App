package com.buddy.buddyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.buddy.buddyapp.AboutUs;
import com.buddy.buddyapp.Constant.PopUp;
import com.buddy.buddyapp.R;
import com.google.android.material.textfield.TextInputLayout;

public class EmailActivity extends AppCompatActivity {
    private Button btn_Send;
    private TextInputLayout InputSubject, InputMessage;
    private Toolbar mToolbar;
    private LinearLayout dialog;
    private View layout;
    private com.buddy.buddyapp.Constant.PopUp PopUp = new PopUp();


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
       // /dialog.setVisibility(View.VISIBLE);
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
            Intent intent = new Intent(Intent.ACTION_SEND);
            String mailto = "BuddyApp4@gmail.com";
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailto});
            intent.putExtra(Intent.EXTRA_SUBJECT, Subject);
            intent.putExtra(Intent.EXTRA_TEXT, Message);
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an email client"));
            finish();

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
            case R.id.aboutUs:
                startActivity(new Intent(this, AboutUs.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
