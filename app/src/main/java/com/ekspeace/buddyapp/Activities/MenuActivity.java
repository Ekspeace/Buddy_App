package com.ekspeace.buddyapp.Activities;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp.AboutUs;
import com.ekspeace.buddyapp.Adapter.MyServiceAdapter;
import com.ekspeace.buddyapp.BookingActivity;
import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.Constant.SpacesItemDecoration;
import com.ekspeace.buddyapp.Interface.ICategoryLoadListener;
import com.ekspeace.buddyapp.Model.Services;
import com.ekspeace.buddyapp.R;
import com.ekspeace.buddyapp.Services.PickUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements ICategoryLoadListener, NavigationView.OnNavigationItemSelectedListener{

    private final PopUp PopUp = new PopUp();
    private long backPressedTime = 0;
    private CollectionReference serviceRef;
    private RecyclerView recycler_service;
    private Button btn_next;
    private LinearLayout ArcProgress;
    private View layout;
    private ICategoryLoadListener iCategoryLoadListener;
    private final BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadService();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigationView);
        setSupportActionBar(toolbar);
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.user_profile_name);
        TextView tvEmail = headerView.findViewById(R.id.user_profile_email);
        btn_next = findViewById(R.id.btn_next_step);
        recycler_service = findViewById(R.id.recycler_service);
        serviceRef = FirebaseFirestore.getInstance().collection("Service");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        iCategoryLoadListener =this;

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));
        localBroadcastManager.registerReceiver(enablebutton, new IntentFilter(Common.KEY_ENABLE_BUTTON_SERVICE));
        tvName.setText(Common.currentUser.getName());
        tvEmail.setText(Common.currentUser.getEmail());

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        ArcProgress = findViewById(R.id.ProgressBar_menu);


        initView();
        loadService();


        btn_next.setOnClickListener(v -> {
            if(Common.currentService.getName().contains("p"))
            {
                startActivity(new Intent(MenuActivity.this, PickUp.class));
            }
            else
                startActivity(new Intent(MenuActivity.this, ServicesActivity.class));
        });

    }
    public final BroadcastReceiver enablebutton = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            btn_next.setEnabled(true);
        }
    };
    public void loadService() {
        ArcProgress.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            serviceRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Services> list = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Services service = documentSnapshot.toObject(Services.class);
                                    service.setName(documentSnapshot.get("name").toString());
                                    service.setServiceId(documentSnapshot.getId());
                                    service.setImageUrl(documentSnapshot.get("image").toString());
                                    list.add(service);
                                }
                                iCategoryLoadListener.onCategoryLoadSuccess(list);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iCategoryLoadListener.onCategoryLoadFailed(e.getMessage());
                }
            });
        }else
        {
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            ArcProgress.setVisibility(View.GONE);
        }
    }
    @Override
    public void onCategoryLoadSuccess(List<Services> serviceList) {
        MyServiceAdapter adapter = new MyServiceAdapter(this,serviceList);
        recycler_service.setAdapter(adapter);
        ArcProgress.setVisibility(View.GONE);
    }
    @Override
    public void onCategoryLoadFailed(String message) {
        com.ekspeace.buddyapp.Constant.PopUp.smallToast(MenuActivity.this, layout, R.drawable.small_error,message,Toast.LENGTH_SHORT);
        ArcProgress.setVisibility(View.GONE);

    }
    private void initView() {
        recycler_service.setHasFixedSize(true);
        recycler_service.setLayoutManager(new GridLayoutManager(this, 1));
        recycler_service.addItemDecoration(new SpacesItemDecoration(4));
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.profile:
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.Bookings:
                startActivity(new Intent(this, BookingActivity.class));
                break;
            case R.id.orders:
                startActivity(new Intent(this, OrderingActivity.class));
                break;
            case R.id.aboutUs:
                startActivity(new Intent(this, AboutUs.class));
                break;
            case R.id.email:
                startActivity(new Intent(this, EmailActivity.class));
                break;
            case R.id.rate:
               rateMe();
                break;
            case R.id.whatsapp:
                com.ekspeace.buddyapp.Constant.PopUp.Whatsapp(this, "WhatsApp...", "Chat with us on WhatsApp", R.drawable.whatsapp);
                break;
            case R.id.SignOut :
                com.ekspeace.buddyapp.Constant.PopUp.SignOut(this);
                break;
            default:
                break;
        }
        return false;
    }
    private void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            com.ekspeace.buddyapp.Constant.PopUp.smallToast(MenuActivity.this, layout, R.drawable.small_error,"Please press again to exit...",Toast.LENGTH_SHORT);
        } else {
            super.onBackPressed();
        }
    }
}