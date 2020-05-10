package com.example.buddyapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActionBar;
import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.buddyapp.BookingConfirmActivity;
import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Constant.PopUp;
import com.example.buddyapp.Model.BookingInformation;
import com.example.buddyapp.Model.OrderInformation;
import com.example.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import io.paperdb.Paper;

public class OrderConfirmActivity extends AppCompatActivity {
    SimpleDateFormat simpleDateFormat;
    private Toolbar toolbar;
    LocalBroadcastManager localBroadcastManager;
    TextView txt_services_Order_text,txt_order_time_text,txt_order_date,txt_order_type
            ,txt_order_price,txt_email_user_booking,txt_order_category,
            txt_user_phone_booking,txt_user_address_booking;
    LinearLayout dialog;
    Button btn_confirm;
    LinearLayout layout_cannabis;
    LinearLayout layout_grocery;
    LinearLayout layout_other;
    View layout;
    TextView txt_grocery_store,txt_grocery_list,txt_category_grocery;
    TextView txt_other_type,txt_other_info,txt_category_other;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        txt_services_Order_text = findViewById(R.id.txt_services_Order_text);
        txt_order_time_text = findViewById(R.id.txt_order_time_text);
        txt_order_date = findViewById(R.id.txt_order_date_text);
        txt_order_type = findViewById(R.id.txt_order_type_text);
        txt_order_category= findViewById(R.id.txt_order_category_text);
        txt_order_price = findViewById(R.id.txt_order_price_text);
        txt_email_user_booking = findViewById(R.id.txt_email_user_booking);
        txt_user_phone_booking = findViewById(R.id.txt_user_phone_booking);
        txt_user_address_booking= findViewById(R.id.txt_user_address_booking);
        layout_cannabis = findViewById(R.id.layout_order_confirm_cannabis);
        layout_grocery = findViewById(R.id.layout_order_confirm_grocery);
        layout_other = findViewById(R.id.layout_order_confirm_other);
        txt_grocery_store = findViewById(R.id.txt_grocery_store);
        txt_grocery_list = findViewById(R.id.txt_grocery_list);
        txt_category_grocery = findViewById(R.id.txt_order_Category_grocery);
        txt_other_type = findViewById(R.id.txt_other_type);
        txt_other_info = findViewById(R.id.txt_other_info);
        txt_category_other = findViewById(R.id.txt_order_Category_other);
        txt_user_address_booking= findViewById(R.id.txt_user_address_booking);
        btn_confirm = findViewById(R.id.btn_confirm);
        toolbar = findViewById(R.id.toolbaruserOrder);
        dialog = findViewById(R.id.ProgressBar_order_confirm);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));


        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        setData();
        localBroadcastManager.registerReceiver(confirmBookingReceiver,new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        Actionbar();
        confirmOrder();

    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void confirmOrder() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setVisibility(View.VISIBLE);
                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                final OrderInformation orderInformation = new OrderInformation();

                orderInformation.setCustomerName(Common.currentUser.getName());
                orderInformation.setCustomerEmail(Common.currentUser.getEmail());
                orderInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());
                orderInformation.setCustomerAddress(Common.currentUser.getAddress());
                orderInformation.setServiceId(Common.currentService.getServiceId());
                orderInformation.setCategoryname(Common.currentCategory.getCategoryName());
                orderInformation.setServicename(Common.currentService.getName());
                orderInformation.setDate(currentDate);
                orderInformation.setTime(currentTime);
                if(Common.currentCategory.getCategoryName().contains("b")) {
                    orderInformation.setCannabisName(Common.currentCannabis.getCannabisName());
                    orderInformation.setCannabisPrice(Common.currentCannabisPrice);
                }
                if(Common.currentCategory.getCategoryName().contains("G")) {
                   orderInformation.setGroceryList(Common.groceryList);
                   orderInformation.setGroceryStore(Common.groceryStore);
                }
                if(Common.currentCategory.getCategoryName().contains("h")) {
                    orderInformation.setOtherInfo(Common.otherInfo);
                    orderInformation.setOtherType(Common.otherType);
                }

                addToUserOrder(orderInformation);
            }
        });
    }

    private void addToUserOrder(final OrderInformation orderInformation) {
        if (Common.isOnline(this)) {
            final CollectionReference userOrder = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(Common.currentUser.getUserId())
                    .collection("Orders");
            userOrder
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()) {
                                userOrder.document()
                                        .set(orderInformation)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                resetStaticData();
                                                dialog.setVisibility(View.GONE);
                                                PopUp.smallToast(OrderConfirmActivity.this, layout,R.drawable.small_success,"Successfully Ordered!",Toast.LENGTH_SHORT);
                                                startActivity(new Intent(OrderConfirmActivity.this, MenuActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.setVisibility(View.GONE);
                                                PopUp.smallToast(OrderConfirmActivity.this, layout,R.drawable.small_error,e.getMessage(),Toast.LENGTH_SHORT);
                                            }
                                        });
                            } else {
                                dialog.setVisibility(View.GONE);
                                resetStaticData();
                                PopUp.smallToast(OrderConfirmActivity.this, layout,R.drawable.small_error,"Sorry... but already ordered",Toast.LENGTH_SHORT);
                                startActivity(new Intent(OrderConfirmActivity.this, MenuActivity.class));
                                finish();
                            }
                        }
                    });
        }else {
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentService = null;
        Common.currentDate.add(Calendar.DATE,0);
    }
    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        txt_services_Order_text.setText(Common.currentService.getName());
        txt_order_time_text.setText(currentTime);
        txt_order_date.setText(currentDate);
        txt_email_user_booking.setText(Common.currentUser.getEmail());
        txt_user_phone_booking.setText(Common.currentUser.getPhoneNumber());
        txt_user_address_booking.setText(Common.currentUser.getAddress());
        if(Common.currentCategory.getCategoryName().contains("b"))
        {
            layout_cannabis.setVisibility(View.VISIBLE);
            txt_order_price.setText(Common.currentCannabisPrice);
            txt_order_category.setText(Common.currentCannabis.getCannabisName());
            txt_order_type.setText(Common.currentCategory.getCategoryName());
        }
        if(Common.currentCategory.getCategoryName().contains("G")){
            layout_grocery.setVisibility(View.VISIBLE);
            txt_grocery_store.setText(Common.groceryStore);
            txt_grocery_list.setText(Common.groceryList);
            txt_category_grocery.setText(Common.currentCategory.getCategoryName());
        }
        if(Common.currentCategory.getCategoryName().contains("h")){
            layout_other.setVisibility(View.VISIBLE);
            txt_other_info.setText(Common.otherInfo);
            txt_other_type.setText(Common.otherType);
            txt_category_other.setText(Common.currentCategory.getCategoryName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
