package com.ekspeace.buddyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.R;

import mehdi.sakout.aboutpage.BuildConfig;

public class AboutUs extends AppCompatActivity {
 private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        mToolbar = findViewById(R.id.Toolbar);
        Button btnLicence = findViewById(R.id.Licence);
        TextView tvDescription = findViewById(R.id.Description_About);
        TextView tvVersion = findViewById(R.id.version);

        tvDescription.setText(R.string.about_us_description);
        tvVersion.setText("Version " + BuildConfig.VERSION_NAME);

        Actionbar();

        btnLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutUs.this, LibraryLicense.class));
            }
        });
    }
    private void Actionbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.togglemenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
