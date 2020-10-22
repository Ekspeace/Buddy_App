package com.ekspeace.buddyapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.Model.OrderInformation;
import com.ekspeace.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class OrderConfirmActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private final PopUp PopUp = new PopUp();
    String currentTime;
    private LocalBroadcastManager localBroadcastManager;
    private TextView txt_services_Order_text,txt_order_time_text,txt_order_date,txt_order_type
            ,txt_order_price,txt_email_user_booking,txt_order_category,
            txt_user_phone_booking,txt_user_address_booking;
    private LinearLayout dialog;
    private Button btn_confirm;
    private LinearLayout layout_cannabis;
    private LinearLayout layout_grocery;
    private LinearLayout layout_other;
    private View layout;
    private final BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addToUserOrder(Common.currentOrderInfo);
        }
    };
    private TextView txt_grocery_store,txt_grocery_list,txt_category_grocery;
    private TextView txt_other_type,txt_other_info,txt_category_other;
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
    final BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        setData();
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));
        localBroadcastManager.registerReceiver(confirmBookingReceiver,new IntentFilter(Common.KEY_CONFIRM_BOOKING));
        Actionbar();
        confirmOrder();
    }
    public void confirmOrder() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setVisibility(View.VISIBLE);
                currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                HashMap<String, Object> orderInformation = new HashMap<>();

                orderInformation.put("CustomerName",Common.currentUser.getName());
                orderInformation.put("CustomerEmail",Common.currentUser.getEmail());
                orderInformation.put("CustomerPhone",Common.currentUser.getPhoneNumber());
                orderInformation.put("CustomerAddress",Common.currentUser.getAddress());
                orderInformation.put("ServiceId",Common.currentService.getServiceId());
                orderInformation.put("CategoryName",Common.currentCategory.getCategoryName());
                orderInformation.put("ServiceName",Common.currentService.getName());
                orderInformation.put("Date",currentDate);
                orderInformation.put("Time",currentTime);
                if(Common.currentCategory.getCategoryName().contains("b")) {
                    orderInformation.put("CannabisName",Common.currentCannabis.getCannabisName());
                    orderInformation.put("CannabisPrice",Common.currentCannabisPrice);
                }
                if(Common.currentCategory.getCategoryName().contains("G")) {
                   orderInformation.put("GroceryList",Common.groceryList);
                   orderInformation.put("GroceryStore",Common.groceryStore);
                }
                if(Common.currentCategory.getCategoryName().contains("h")) {
                    orderInformation.put("OtherInfo",Common.otherInfo);
                    orderInformation.put("OtherType",Common.otherType);
                }
                Common.currentOrderInfo = orderInformation;
                addToUserOrder(orderInformation);
            }
        });
    }
    private void addToUserOrder(final HashMap<String, Object> orderInformation) {
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
                            if (task.getResult().size() < 3) {
                                String Id = userOrder.document().getId();
                                RealTimeDatabase(orderInformation, Id);
                                userOrder.document(Id)
                                        .set(orderInformation)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Notify();
                                                dialog.setVisibility(View.GONE);
                                                com.ekspeace.buddyapp.Constant.PopUp.smallToast(OrderConfirmActivity.this, layout, R.drawable.small_success,"Successfully Ordered!",Toast.LENGTH_SHORT);
                                                startActivity(new Intent(OrderConfirmActivity.this, MenuActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.setVisibility(View.GONE);
                                                com.ekspeace.buddyapp.Constant.PopUp.smallToast(OrderConfirmActivity.this, layout, R.drawable.small_error,e.getMessage(),Toast.LENGTH_SHORT);
                                            }
                                        });
                            } else {
                                dialog.setVisibility(View.GONE);
                                com.ekspeace.buddyapp.Constant.PopUp.smallToast(OrderConfirmActivity.this, layout, R.drawable.small_error,"Sorry... but already ordered, Please Delete old orders",Toast.LENGTH_SHORT);
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
                com.ekspeace.buddyapp.Constant.PopUp.SignOut(this);
                return true;
            case R.id.aboutUs:
                startActivity(new Intent(this, AboutUs.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void RealTimeDatabase(HashMap<String, Object> orderInformation, String Id){
        CollectionReference userOrder = FirebaseFirestore.getInstance().collection("Orders");
        orderInformation.put("OrderId", Id);
        userOrder.document(Id).set(orderInformation);
        orderInformation.put("OrderId", Id);
    }
    private void Notify(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                int SDK_INT = android.os.Build.VERSION.SDK_INT;

                if (SDK_INT > 8) {

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                            .permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    String message = "Buddy User have placed an order on your service";

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MmE1YTc1YjEtMWY0OS00NmFjLTkwNTAtMjQxNDczZGViYjli");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                +   "\"app_id\": \"6c67ad02-1006-47b2-9ea3-6bafa83c943a\","
                                +   "\"included_segments\": [\"All\"],"
                                +   "\"data\": {\"foo\": \"bar\"},"
                                +   "\"contents\": {\"en\": \"Buddy User have ordered your service\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (  httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch(Throwable t) {
                        t.printStackTrace();
                    }

                }

            }

        });
    }
}
