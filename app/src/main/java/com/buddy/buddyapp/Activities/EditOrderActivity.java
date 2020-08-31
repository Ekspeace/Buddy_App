package com.buddy.buddyapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.buddy.buddyapp.AboutUs;
import com.buddy.buddyapp.Constant.Common;
import com.buddy.buddyapp.Constant.PopUp;
import com.buddy.buddyapp.Model.Cannabis;
import com.buddy.buddyapp.Model.OrderInformation;
import com.buddy.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class EditOrderActivity extends AppCompatActivity {
    private final PopUp PopUp = new PopUp();
    private Toolbar mToolbar;
    private int priceD;
    private LinearLayout dialog;
    private LinearLayout layout_cannabis;
    private MaterialSpinner cannabis_type;
    private TextView range_price,service_txt;
    private LinearLayout layout_range_price;
    private EditText price;
    private String Id;
    private LinearLayout layout_grocery;
    private EditText et_grocery_store;
    private EditText et_grocery_list;
    private LinearLayout layout_other;
    private EditText et_other_type;
    private View layout;
    private EditText et_other_type_info;
    private final BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetOrderInfo();
        }
    };
    private Button btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        mToolbar = findViewById(R.id.toolbar_edit_order);
        dialog = findViewById(R.id.ProgressBar_pickUps_edit);
        btn_update = findViewById(R.id.btn_update_edit);
        layout_range_price = findViewById(R.id.layout_range_cannabis_edit_price);
        range_price = findViewById(R.id.range_price_edit);
        layout_cannabis = findViewById(R.id.layout_edit_cannabis);
        cannabis_type = findViewById(R.id.cannabis_spinner_edit);
        price = findViewById(R.id.price_pay_edit);
        layout_grocery = findViewById(R.id.layout_grocery_edit);
        et_grocery_store = findViewById(R.id.grocery_store_edit);
        et_grocery_list = findViewById(R.id.grocery_list_edit);
        layout_other = findViewById(R.id.layout_other_pickups_edit);
        et_other_type = findViewById(R.id.other_pickups_edit);
        et_other_type_info = findViewById(R.id.other_piokup_more_info_edit);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        Actionbar();
        GetOrderInfo();
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hashtable<String, Object> orderInformation = new Hashtable<>();
                if(Common.categoryEdit.contains("b")) {
                    String altPrice = price.getText().toString().trim();
                   int priceU = Integer.parseInt(altPrice);
                    if (TextUtils.isEmpty(altPrice)) {
                        price.setError("Price Required");
                        return;
                    }
                    if (priceD >= priceU+1) {
                        price.setError("Price must be equal or greater");
                        return;
                    }
                    Common.currentCannabisPrice = "R "+altPrice;
                    orderInformation.put("cannabisName", Common.currentCannabis.getCannabisName());
                    orderInformation.put("cannabisPrice", Common.currentCannabisPrice);
                }
                if(Common.categoryEdit.contains("G")) {
                    String store = et_grocery_store.getText().toString().trim();
                    String list = et_grocery_list.getText().toString().trim();
                    boolean error = false;
                    if(!error) {
                        if (TextUtils.isEmpty(store)) {
                            et_grocery_store.setError("Please Provide A Store");
                            error = true;
                        }
                        if(TextUtils.isEmpty(list))
                        {
                            et_grocery_list.setError("Please Provide A List");
                            error =true;
                        }
                        if(error)
                            return;
                    }
                    Common.groceryList = list;
                    Common.groceryStore = store;
                    orderInformation.put("groceryList", Common.groceryList);
                    orderInformation.put("groceryStore", Common.groceryStore);
                }
                if(Common.categoryEdit.contains("h")) {
                    String type = et_other_type.getText().toString().trim();
                    String info = et_other_type_info.getText().toString().trim();
                    boolean error = false;
                    if(!error) {
                        if (TextUtils.isEmpty(type)) {
                            et_other_type.setError("Please Provide The Type Of The Pick Up");
                            error = true;
                        }
                        if(TextUtils.isEmpty(info))
                        {
                            et_other_type_info.setError("Please Provide Additional Information About The Pick Up");
                            error =true;
                        }
                        if(error)
                            return;
                    }
                    Common.otherType = type;
                    Common.otherInfo = info;
                    orderInformation.put("otherInfo", Common.otherInfo);
                    orderInformation.put("otherType", Common.otherType);
                }
                dialog.setVisibility(View.VISIBLE);
                if (Common.isOnline(EditOrderActivity.this)) {
                    final DocumentReference userOrder = FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(Common.currentUser.getUserId())
                            .collection("Orders")
                            .document(Id);
                    userOrder.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (Objects.requireNonNull(task.getResult()).exists()) {
                                        userOrder
                                                .update(orderInformation)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        dialog.setVisibility(View.GONE);
                                                        com.buddy.buddyapp.Constant.PopUp.smallToast(EditOrderActivity.this, layout, R.drawable.small_success,"Successfully Order is update!",Toast.LENGTH_SHORT);
                                                        startActivity(new Intent(EditOrderActivity.this, OrderingActivity.class));
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.setVisibility(View.GONE);
                                                        com.buddy.buddyapp.Constant.PopUp.smallToast(EditOrderActivity.this, layout, R.drawable.small_error,e.getMessage(),Toast.LENGTH_SHORT);
                                                    }
                                                });
                                    } else {
                                        dialog.setVisibility(View.GONE);
                                        com.buddy.buddyapp.Constant.PopUp.smallToast(EditOrderActivity.this, layout, R.drawable.small_error,"Sorry... your order is not updated",Toast.LENGTH_SHORT);
                                        startActivity(new Intent(EditOrderActivity.this, OrderingActivity.class));
                                        finish();
                                    }
                                }
                            });
                }else {
                    PopUp.Toast(EditOrderActivity.this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
                    dialog.setVisibility(View.GONE);
                }
            }
        });
    }
    private void GetOrderInfo() {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            CollectionReference userOrders = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(Common.currentUser.getUserId())
                    .collection("Orders");

            List<OrderInformation> orderInformationList = new ArrayList<>();

            userOrders.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                OrderInformation orderInformation = queryDocumentSnapshot.toObject(OrderInformation.class);
                                Id = queryDocumentSnapshot.getId();
                                orderInformationList.add(orderInformation);
                                AssignEditText(orderInformation);
                            }
                            dialog.setVisibility(View.GONE);
                        } else {

                            dialog.setVisibility(View.GONE);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.setVisibility(View.GONE);
                    com.buddy.buddyapp.Constant.PopUp.smallToast(EditOrderActivity.this, layout, R.drawable.small_error, e.getMessage(), Toast.LENGTH_SHORT);
                }
            });

        } else {
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    private void AssignEditText(OrderInformation information) {
        if(Common.categoryEdit.contains("b")) {
            layout_cannabis.setVisibility(View.VISIBLE);
            layout_grocery.setVisibility(View.GONE);
            layout_other.setVisibility(View.GONE);
            loadCannabis(information);
            price.setText(information.getCannabisPrice());

        }
        if(Common.categoryEdit.contains("G")){
            layout_grocery.setVisibility(View.VISIBLE);
            layout_other.setVisibility(View.GONE);
            layout_cannabis.setVisibility(View.GONE);
            btn_update.setEnabled(true);
            et_grocery_store.setText(information.getGroceryStore());
            et_grocery_list.setText(information.getGroceryList());
        }
        if(Common.categoryEdit.contains("h")){
            btn_update.setEnabled(true);
            layout_other.setVisibility(View.VISIBLE);
            layout_cannabis.setVisibility(View.GONE);
            layout_grocery.setVisibility(View.GONE);
            et_other_type.setText(information.getOtherType());
            et_other_type_info.setText(information.getOtherInfo());
        }


    }
    private void loadCannabis(OrderInformation orderInformation) {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            CollectionReference cannabisType = FirebaseFirestore.getInstance()
                    .collection("Service")
                    .document(orderInformation.getServiceId())
                    .collection("Category")
                    .document(orderInformation.getCategoryname())
                    .collection("Types");

            cannabisType.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        layout_cannabis.setVisibility(View.VISIBLE);
                        List<String> items = new ArrayList<>();
                        items.add("Choose a Cannabis Type");
                        List<Cannabis> rangePrices = new ArrayList<>();
                        for (QueryDocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                            Cannabis cannabis = querySnapshot.toObject(Cannabis.class);
                            cannabis.setCannabisName(querySnapshot.getId());
                            cannabis.setCannabisPrice(querySnapshot.get("price").toString());
                            rangePrices.add(cannabis);
                            items.add(querySnapshot.getId());
                        }
                        spinnerCannabis(items, rangePrices);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    com.buddy.buddyapp.Constant.PopUp.smallToast(EditOrderActivity.this, layout, R.drawable.small_error,e.getMessage(),Toast.LENGTH_SHORT);
                }
            });

        } else {
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    private void spinnerCannabis(List<String> items, List<Cannabis> prices) {
        dialog.setVisibility(View.GONE);
        cannabis_type.setItems(items);
        cannabis_type.setOnItemSelectedListener((view, position, id, item) -> {
            if (position > 0 )
            {
                Common.currentCannabis = new Cannabis(item.toString(),prices.get((int)id).getCannabisPrice());
                range_price.setText((Common.currentCannabis.getCannabisPrice()));
                layout_range_price.setVisibility(View.VISIBLE);
                btn_update.setEnabled(true);
                String splitPrice = prices.get((int)id).getCannabisPrice();
                String split = splitPrice.substring(1,splitPrice.length());
                priceD = Integer.parseInt(split);
             }
            else {
                btn_update.setEnabled(false);
            }
        });
    }
    public void Actionbar() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
                com.buddy.buddyapp.Constant.PopUp.SignOut(this);
                return true;
            case R.id.aboutUs:
                startActivity(new Intent(this, AboutUs.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
       startActivity(new Intent(this, OrderingActivity.class));
    }
}
