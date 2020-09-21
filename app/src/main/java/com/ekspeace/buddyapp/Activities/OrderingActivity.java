package com.ekspeace.buddyapp.Activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp.AboutUs;
import com.ekspeace.buddyapp.Adapter.OrderInfoAdapter;
import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.Constant.SpacesItemDecoration;
import com.ekspeace.buddyapp.Interface.IOrderInfoLoadListener;
import com.ekspeace.buddyapp.Model.OrderInformation;
import com.ekspeace.buddyapp.R;
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
import java.util.Objects;

import io.paperdb.Paper;


public class OrderingActivity extends AppCompatActivity implements IOrderInfoLoadListener {
    private Toolbar toolbar;
    private final PopUp PopUp = new PopUp();
    private LinearLayout dialog;
    private RecyclerView recyclerView;
    private LinearLayout no_order_txt;
    private IOrderInfoLoadListener iOrderInfoLoadListener;
    private View layout;
    private final BroadcastReceiver setVisibilityOfText = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                no_order_txt.setVisibility(View.VISIBLE);
        }
    };
    private final BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          loadUserOrders();
        }
    };
    private Button btn_delete, btn_Edit;
    private final BroadcastReceiver deleteBookingInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Common.currentOrder != null) {
                btn_delete.setEnabled(true);
                btn_Edit.setEnabled(true);
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        loadUserOrders();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        toolbar = findViewById(R.id.toolbarOrder);
        btn_delete = findViewById(R.id.btn_delete_ordering);
        no_order_txt = findViewById(R.id.no_order_txt);
        recyclerView = findViewById(R.id.recycler_order_info);
        btn_Edit = findViewById(R.id.btn_Edit_ordering);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        dialog = findViewById(R.id.ProgressBar_order);
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));
        localBroadcastManager.registerReceiver(deleteBookingInfo,new IntentFilter(Common.KEY_CLICKED_BUTTON_DELETE));
        localBroadcastManager.registerReceiver(setVisibilityOfText,new IntentFilter(Common.KEY_DISABLE_NO_ORDER_TEXT));
        iOrderInfoLoadListener = this;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        loadUserOrders();
        initView();
        Actionbar();
        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderingActivity.this, EditOrderActivity.class));
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.currentOrder != null)
                    DeleteOrderFromUser();
            }
        });
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
                                if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Common.currentOrder = queryDocumentSnapshot.toObject(OrderInformation.class);
                                        Common.currentOrderId = queryDocumentSnapshot.getId();
                                        orderInformationList.add(Common.currentOrder);
                                        iOrderInfoLoadListener.onOrderInfoLoadSuccess(orderInformationList);
                                    }
                                }
                                dialog.setVisibility(View.GONE);
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

        TextView tvTitle = alertDialog.findViewById(R.id.dialog_title);
        TextView tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        ImageView imIcon = alertDialog.findViewById(R.id.dialog_icon);
        Button btnClose = alertDialog.findViewById(R.id.dialog_close);
        Button btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);


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
                deleteOrderFromUser();
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    private void deleteOrderFromUser() {
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
                    com.ekspeace.buddyapp.Constant.PopUp.smallToast(OrderingActivity.this, layout, R.drawable.small_error, e.getMessage(),Toast.LENGTH_SHORT);
                    dialog.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Paper.init(OrderingActivity.this);
                    Uri eventUri = Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE).toString());
                    OrderingActivity.this.getContentResolver().delete(eventUri, null, null);
                    dialog.setVisibility(View.GONE);
                    com.ekspeace.buddyapp.Constant.PopUp.smallToast(OrderingActivity.this, layout, R.drawable.small_success, "Successfully deleted the order!",Toast.LENGTH_SHORT);
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
                com.ekspeace.buddyapp.Constant.PopUp.SignOut(this);
                return true;
            case R.id.aboutUs:
                startActivity(new Intent(this, AboutUs.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
