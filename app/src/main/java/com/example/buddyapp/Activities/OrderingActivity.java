package com.example.buddyapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buddyapp.Adapter.OrderInfoAdapter;
import com.example.buddyapp.BookingActivity;
import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Constant.PopUp;
import com.example.buddyapp.Constant.SpacesItemDecoration;
import com.example.buddyapp.Interface.IOrderInfoLoadListener;
import com.example.buddyapp.Model.BookingInformation;
import com.example.buddyapp.Model.OrderInformation;
import com.example.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;


import io.paperdb.Paper;


public class OrderingActivity extends AppCompatActivity implements IOrderInfoLoadListener {
    private Toolbar toolbar;
    private Button btn_delete;
    private LinearLayout dialog;
    private FirebaseAuth mAuth;
    private LocalBroadcastManager localBroadcastManager;
    private RecyclerView recyclerView;
    private LinearLayout no_order_txt;
    private IOrderInfoLoadListener iOrderInfoLoadListener;
    private TextView tvTitle, tvDesc;
    private ImageView imIcon;
    private View layout;
    private Button btnClose, btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        toolbar = findViewById(R.id.toolbarOrder);
        btn_delete = findViewById(R.id.btn_delete_ordering);
        no_order_txt = findViewById(R.id.no_order_txt);
        recyclerView = findViewById(R.id.recycler_order_info);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        dialog = findViewById(R.id.ProgressBar_order);
        localBroadcastManager.registerReceiver(deleteBookingInfo,new IntentFilter(Common.KEY_CLICKED_BUTTON_DELETE));
        localBroadcastManager.registerReceiver(setVisibilityOfText,new IntentFilter(Common.KEY_DISABLE_NO_ORDER_TEXT));
        iOrderInfoLoadListener = this;
        mAuth = FirebaseAuth.getInstance();
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        loadUserOrders();
        initView();
        Actionbar();
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.currentOrder != null)
                    DeleteOrderFromUser();
            }
        });
    }
    private BroadcastReceiver deleteBookingInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Common.currentOrder != null)
                btn_delete.setEnabled(true);
        }
    };
    private BroadcastReceiver setVisibilityOfText = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            no_order_txt.setVisibility(View.GONE);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        loadUserOrders();

    }
    private void loadUserOrders() {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            CollectionReference userOrders = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(Common.currentUser.getUserId())
                    .collection("Orders");

            List<OrderInformation> orderInformationList = new ArrayList<>();

            userOrders
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Common.currentOrder = queryDocumentSnapshot.toObject(OrderInformation.class);
                                        Common.currentBookingId1 = queryDocumentSnapshot.getId();
                                        orderInformationList.add(Common.currentOrder);
                                        iOrderInfoLoadListener.onOrderInfoLoadSuccess(orderInformationList);
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
                    iOrderInfoLoadListener.onOrderInfoLoadFailed(e.getMessage());
                }
            });

        } else {
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    private void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new SpacesItemDecoration(4));
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
    public void DeleteOrderFromUser() {

        Dialog alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.pop_up);

        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);


        tvTitle.setText("Delete order");
        tvDesc.setText("Do you really want to delete this order information?");
        imIcon.setImageResource(R.drawable.delete);

        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrderFromUser(true);
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void deleteOrderFromUser(final boolean isChange) {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            DocumentReference userOrderInfo = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(Common.currentUser.getUserId())
                    .collection("Orders")
                    .document(Common.currentOrderId);

            userOrderInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    PopUp.smallToast(OrderingActivity.this, layout,R.drawable.small_error, e.getMessage(),Toast.LENGTH_SHORT);
                    dialog.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Paper.init(OrderingActivity.this);
                    Uri eventUri = Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE).toString());
                    OrderingActivity.this.getContentResolver().delete(eventUri, null, null);
                    dialog.setVisibility(View.GONE);
                    PopUp.smallToast(OrderingActivity.this, layout,R.drawable.small_success, "Successfully deleted the order!",Toast.LENGTH_SHORT);
                    OrderingActivity.this.recreate();

                }
            });
        }else{
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOrderInfoLoadSuccess(List<OrderInformation> orderInformation) {
        OrderInfoAdapter adapter = new OrderInfoAdapter(this,orderInformation);
        recyclerView.setAdapter(adapter);
        dialog.setVisibility(View.GONE);

    }

    @Override
    public void onOrderInfoLoadFailed(String message) {
        Toast.makeText(OrderingActivity.this, message, Toast.LENGTH_SHORT).show();
        dialog.setVisibility(View.GONE);
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
