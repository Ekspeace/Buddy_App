package com.ekspeace.buddyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp.Adapter.TimeSlotAdapter;
import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.Constant.SpacesItemDecoration;
import com.ekspeace.buddyapp.Interface.ITimeSlotLoadListener;
import com.ekspeace.buddyapp.Model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class TimeSlotActivity extends AppCompatActivity implements ITimeSlotLoadListener {
    private Toolbar mToolbar;
    private Button btn_next;
    private LinearLayout dialog;
    private RecyclerView recycler_time_slot;
    private DocumentReference serviceDoc;
    private ITimeSlotLoadListener iTimeSlotLoadListener;
    private HorizontalCalendarView horizontalCalendar;
    private SimpleDateFormat simpleDateFormat;
    private LocalBroadcastManager localBroadcastManager, local;
    private View layout;
    private com.ekspeace.buddyapp.Constant.PopUp PopUp = new PopUp();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot);
        mToolbar = findViewById(R.id.toolbarC);
        btn_next = findViewById(R.id.btn_next_step);
        recycler_time_slot = findViewById(R.id.recycler_time_slot);
        horizontalCalendar = findViewById(R.id.calenderView);

        iTimeSlotLoadListener = this;
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        local = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(displayTimeSlot, new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));
        localBroadcastManager.registerReceiver(buttonNextReceiver,new IntentFilter(Common.KEY_SLOT_BUTTON_NEXT));
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 0);
        dialog = findViewById(R.id.ProgressBar_time_slot);
        loadAvailableTimeSlot(Common.service, simpleDateFormat.format(date.getTime()));
        init();
        Actionbar();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TimeSlotActivity.this, BookingConfirmActivity.class));
            }
        });
    }
    private BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, 0);
            loadAvailableTimeSlot(Common.service, simpleDateFormat.format(date.getTime()));
        }
    };
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra(Common.KEY_STEP,0);
            if(step == 2){
                btn_next.setEnabled(false);
                PopUp.smallToast(TimeSlotActivity.this, layout, R.drawable.small_error,"Time Slot not available",Toast.LENGTH_SHORT);}
             else if (step == 3) {
                 Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);
                 confirmBooking();
                 btn_next.setEnabled(true);
             }
        }
    };
    private void confirmBooking() {
        //Send Local Broadcast to fragment step3
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }
    private void loadAvailableTimeSlot(String serviceId, final String bookDate) {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            if (Common.currentService.getName().contains(" ")) {
                serviceDoc = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Car_Wash")
                        .document("Slot");
            } else {
                serviceDoc = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Cleaning")
                        .document("Slot");
            }


            serviceDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        CollectionReference date;
                        if (documentSnapshot.exists()) {
                            if (Common.currentService.getName().contains(" ")) {
                                date = FirebaseFirestore.getInstance()
                                        .collection("Time_Slot_Car_Wash")
                                        .document("Slot")
                                        .collection(bookDate);
                            } else {
                                date = FirebaseFirestore.getInstance()
                                        .collection("Time_Slot_Cleaning")
                                        .document("Slot")
                                        .collection(bookDate);
                            }

                            date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot.isEmpty())
                                            iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                        else {
                                            List<TimeSlot> timeSlots = new ArrayList<>();
                                            for (QueryDocumentSnapshot document : task.getResult())
                                                timeSlots.add(document.toObject(TimeSlot.class));
                                            iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);

                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                                }
                            });
                        }
                    }
                }
            });
        }else{
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE,0);
            loadAvailableTimeSlot(Common.currentService.getServiceId(),
                    simpleDateFormat.format(date.getTime())); }};
    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
    }
    private void init() {
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,2);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calenderView)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .configure()
                .formatTopText("dd")
                .formatMiddleText("MMM")
                .formatBottomText("yyyy")
                .textSize(10, 14,10)
                .showTopText(true)
                .showBottomText(true)
                .end()
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (Common.currentDate.getTimeInMillis() != date.getTimeInMillis())
                {
                    Common.currentDate = date;
                    loadAvailableTimeSlot(Common.service, simpleDateFormat.format(date.getTime()));
                }
            }
        });
    }
    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        TimeSlotAdapter adapter = new TimeSlotAdapter(this,timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        dialog.setVisibility(View.GONE);
    }
    @Override
    public void onTimeSlotLoadFailed(String message) {
        PopUp.smallToast(TimeSlotActivity.this, layout, R.drawable.small_error,message,Toast.LENGTH_SHORT);
        dialog.setVisibility(View.GONE);
    }
    @Override
    public void onTimeSlotLoadEmpty() {
        TimeSlotAdapter adapter = new TimeSlotAdapter(this);
        recycler_time_slot.setAdapter(adapter);
        dialog.setVisibility(View.GONE);
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
