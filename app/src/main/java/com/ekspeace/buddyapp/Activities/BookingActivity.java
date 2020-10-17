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

import com.ekspeace.buddyapp.Adapter.BookingInfoAdapter;
import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.Constant.SpacesItemDecoration;
import com.ekspeace.buddyapp.Interface.IBookingInfoLoadListener;
import com.ekspeace.buddyapp.Model.BookingInformation;
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

public class BookingActivity extends AppCompatActivity implements IBookingInfoLoadListener {
    private Toolbar toolbar;
    private Button btn_delete;
    private LinearLayout dialog;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private LinearLayout no_booking_txt;
    private View layout;
    private final PopUp PopUp = new PopUp();

    IBookingInfoLoadListener iBookingInfoLoadListener;
    private final BroadcastReceiver deleteBookingInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Common.currentBooking != null) {
                btn_delete.setEnabled(true);
            }
        }};
    private final BroadcastReceiver setVisibilityOfText = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            no_booking_txt.setVisibility(View.GONE); }
    };
    private final BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          loadUserBooking();
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        toolbar = findViewById(R.id.toolbarbook);
        btn_delete = findViewById(R.id.btn_delete_booking);
        no_booking_txt = findViewById(R.id.no_booking_txt);
        recyclerView = findViewById(R.id.recycler_booking_info);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        dialog = findViewById(R.id.ProgressBar_booking);
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));
        localBroadcastManager.registerReceiver(deleteBookingInfo,new IntentFilter(Common.KEY_CLICKED_BUTTON_DELETE));
        localBroadcastManager.registerReceiver(setVisibilityOfText,new IntentFilter(Common.KEY_DISABLE_NO_BOOKING_TEXT));
        iBookingInfoLoadListener = this;
        mAuth = FirebaseAuth.getInstance();

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        Actionbar();
        initView();
        loadUserBooking();
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.currentBooking != null)
                    DeleteBookingFromUser();
            }
        });


    }

    private void loadUserBooking() {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            CollectionReference userBookingCar = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(Common.currentUser.getUserId())
                    .collection("Booking_Car_Wash");

            List<BookingInformation> bookingInformationList = new ArrayList<>();

            userBookingCar
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Common.currentBooking = queryDocumentSnapshot.toObject(BookingInformation.class);
                                        Common.currentBookingId1 = queryDocumentSnapshot.getId();
                                        bookingInformationList.add(Common.currentBooking);
                                        iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformationList);
                                    }
                                    dialog.setVisibility(View.GONE);
                                }

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
                }
            });
            CollectionReference userBookingClean = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(Common.currentUser.getUserId())
                    .collection("Booking_Cleaning");

            userBookingClean
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {

                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Common.currentBooking = queryDocumentSnapshot.toObject(BookingInformation.class);
                                        Common.currentBookingId2 = queryDocumentSnapshot.getId();
                                        bookingInformationList.add(Common.currentBooking);
                                        iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformationList);
                                    }
                                }
                                dialog.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
                }
            });

        }else {
            dialog.setVisibility(View.GONE);
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);

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
    public void DeleteBookingFromUser() {
        Dialog alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.pop_up);

        TextView tvTitle = alertDialog.findViewById(R.id.dialog_title);
        TextView tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        ImageView imIcon = alertDialog.findViewById(R.id.dialog_icon);
        Button btnClose = alertDialog.findViewById(R.id.dialog_close);
        Button btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);


        tvTitle.setText("Delete booking");
        tvDesc.setText("Do you really want to delete this booking information?");
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
                deleteBookingFromSlot();
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    private void deleteBookingFromSlot() {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            DocumentReference BookingInfo = FirebaseFirestore.getInstance()
                    .collection(BookingSlot(Common.currentBooking.getServiceName()))
                    .document("Slot")
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());
            BookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    com.ekspeace.buddyapp.Constant.PopUp.smallToast(BookingActivity.this, layout, R.drawable.small_error,e.getMessage(),Toast.LENGTH_SHORT);
                    dialog.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    deleteBookingFromUser(true);
                }
            });
        } else {
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    private void deleteBookingFromUser(final boolean isChange) {
        if (Common.isOnline(this)) {
            DocumentReference userBookingInfo1 = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(BookingService(Common.currentBooking.getServiceName()))
                    .document(Common.currentBookingId);

            userBookingInfo1.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    com.ekspeace.buddyapp.Constant.PopUp.smallToast(BookingActivity.this, layout, R.drawable.small_error, e.getMessage(),Toast.LENGTH_SHORT);
                    dialog.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Paper.init(BookingActivity.this);
                    Uri eventUri = Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE).toString());
                    BookingActivity.this.getContentResolver().delete(eventUri, null, null);
                    dialog.setVisibility(View.GONE);
                    com.ekspeace.buddyapp.Constant.PopUp.smallToast(BookingActivity.this, layout, R.drawable.small_success,"Successfully deleted the booking !",Toast.LENGTH_SHORT);
                    BookingActivity.this.recreate();

                }
            });
        }else{
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    public String BookingService(String name) {
        if(name.contains(" "))
            return "Booking_Car_Wash";
        else
            return "Booking_Cleaning";
    }
    public String BookingSlot(String name) {
        if(name.contains(" "))
            return "Time_Slot_Car_Wash";
        else
            return "Time_Slot_Cleaning";

    }

    @Override
    public void onBookingInfoLoadEmpty() {

    }

    @Override
    public void onBookingInfoLoadSuccess(List<BookingInformation> bookingInformation) {
        BookingInfoAdapter adapter = new BookingInfoAdapter(this,bookingInformation);
        recyclerView.setAdapter(adapter);
        dialog.setVisibility(View.GONE);

    }
    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(BookingActivity.this, message, Toast.LENGTH_SHORT).show();
        dialog.setVisibility(View.GONE);
    }

}
