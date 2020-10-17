package com.ekspeace.buddyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp.Adapter.RecyclerViewLicenseAdapter;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.R;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.ArrayList;
import java.util.List;

public class LibraryLicense extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private LinearLayout loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_license);

        recyclerView = findViewById(R.id.recycler_licence);
        mToolbar = findViewById(R.id.toolbarLicence);
        loadingBar = findViewById(R.id.ProgressBar_licence);

        Actionbar();
        loadingBar.setVisibility(View.VISIBLE);
        ProvideLibraries();
        loadingBar.setVisibility(View.GONE);
    }
    private void ProvideLibraries(){
        Libs libraries = new Libs(this);
        List<Library> libraryList = libraries.getLibraries();
        List<Library> List = new ArrayList<>(libraryList);
        GetLibraries(List);

    }
    private void GetLibraries(List<Library> library){
        RecyclerViewLicenseAdapter adapter = new RecyclerViewLicenseAdapter(this,library);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        loadingBar.setVisibility(View.GONE);
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