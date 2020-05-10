package com.example.buddyapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buddyapp.Adapter.MyServiceAdapter;
import com.example.buddyapp.BookingActivity;
import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Constant.PopUp;
import com.example.buddyapp.Constant.SpacesItemDecoration;
import com.example.buddyapp.Interface.ICategoryLoadListener;
import com.example.buddyapp.Model.Services;
import com.example.buddyapp.R;
import com.example.buddyapp.Services.PickUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MenuActivity extends AppCompatActivity implements ICategoryLoadListener, NavigationView.OnNavigationItemSelectedListener{
    private static String TAG;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    CollectionReference serviceRef;
    ICategoryLoadListener iCategoryLoadListener;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private TextView tvName, tvEmail;
    private RecyclerView recycler_service;
    private Button btn_next;
    private LocalBroadcastManager localBroadcastManager;
    private LinearLayout ArcProgress;
    private View layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        loadingBar = new ProgressDialog(this);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        setSupportActionBar(toolbar);
        View headerView = navigationView.getHeaderView(0);
        tvName = headerView.findViewById(R.id.user_profile_name);
        tvEmail = headerView.findViewById(R.id.user_profile_email);
        btn_next = findViewById(R.id.btn_next_step);
        recycler_service = findViewById(R.id.recycler_service);
        serviceRef = FirebaseFirestore.getInstance().collection("Service");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawerOpen,R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        iCategoryLoadListener =this;

        mAuth = FirebaseAuth.getInstance();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(enablebutton, new IntentFilter(Common.KEY_ENABLE_BUTTON_SERVICE));
        tvName.setText(Common.currentUser.getName());
        tvEmail.setText(Common.currentUser.getEmail());

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        ArcProgress = findViewById(R.id.ProgressBar_menu);


        initView();
        loadService();


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.currentService.getName().contains("p"))
                {
                    startActivity(new Intent(MenuActivity.this, PickUp.class));
                }
                else
                    startActivity(new Intent(MenuActivity.this, ServicesActivity.class));
            }
        });

    }
    public BroadcastReceiver enablebutton = new BroadcastReceiver() {
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
                                    service.setServiceId(documentSnapshot.getId());
                                    service.setImageUrl(documentSnapshot.get("image").toString());
                                    list.add(service);

                                }
                                Intent intent = new Intent(Common.KEY_SERVICE_LOAD_DONE);
                                intent.putParcelableArrayListExtra(Common.KEY_SERVICE_LOAD_DONE, list);
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

        //loadingBar.dismiss();
        ArcProgress.setVisibility(View.GONE);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        PopUp.smallToast(MenuActivity.this, layout,R.drawable.small_error,message,Toast.LENGTH_SHORT);
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
               PopUp.ToastBtn(this, "About us", "The oil drop experiment was performed by Robert A. Millikan and Harvey Fletcher in 1909 to measure the elementary electric charge. The experiment took place in the Ryerson Physical Laboratory at the University of Chicago." +
                       "Millikan received the Nobel Prize in Physics in 1923", R.drawable.info );
                break;
            case R.id.email:
                PopUp.emailUserCredential(this, "Send Email","Please provide your email credentials", R.drawable.real_email);
                break;
            case R.id.share:
                PopUp.smallToast(this, layout, R.drawable.small_success, "Coming Soon !", Toast.LENGTH_SHORT);
                break;
            case R.id.sms:
                startActivity(new Intent(this, SMSActivity.class));
                //PopUp.smallToast(this, layout, R.drawable.small_success, "Coming Soon !", Toast.LENGTH_SHORT);
                break;
            case R.id.whatsapp:
                PopUp.Whatsapp(this, "WhatsApp...", "Chat with us on WhatsApp",R.drawable.whatsapp_b);
                break;
            case R.id.SignOut :
                PopUp.SignOut(this);
                break;
            default:
                break;
        }
        return false;
    }
}