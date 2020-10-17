package com.ekspeace.buddyapp.Activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Constant.PopUp;
import com.ekspeace.buddyapp.Interface.IServiceLoadListener;
import com.ekspeace.buddyapp.Model.Category;
import com.ekspeace.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity implements IServiceLoadListener {

    private Toolbar mToolbar;
    private MaterialSpinner spinner;
    private CollectionReference categoryTypeRef;
    private IServiceLoadListener iServiceLoadListener;
    private Button btn_next;
    private LinearLayout dialog;
    private  View layout;
    private LinearLayout layout_txt_price;
    private TextView txt_category_price, txt_service;
    private com.ekspeace.buddyapp.Constant.PopUp PopUp = new PopUp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        mToolbar = findViewById(R.id.toolbarC);
        spinner = findViewById(R.id.spinner);
        btn_next = findViewById(R.id.btn_next_step);
        layout_txt_price = findViewById(R.id.layout_category_price);
        txt_category_price = findViewById(R.id.text_price);
        txt_service = findViewById(R.id.Service_text);
        dialog = findViewById(R.id.ProgressBar_services);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        iServiceLoadListener = this;
        txt_service.setText(Common.currentService.getName());
        loadCategory();
        Actionbar();

    }
    private void loadCategory() {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            categoryTypeRef = FirebaseFirestore.getInstance()
                    .collection("Service")
                    .document(Common.currentService.getServiceId())
                    .collection("Category");

            categoryTypeRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<String> items = new ArrayList<>();
                                List<Category> list = new ArrayList<>();
                                items.add("Please choose your option");
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Category category = documentSnapshot.toObject(Category.class);
                                    category.setCategoryName(documentSnapshot.getId());
                                    category.setCategoryPrice(documentSnapshot.get("price").toString());
                                    items.add(documentSnapshot.getId());
                                    list.add(category);
                                }
                                iServiceLoadListener.onServiceLoadSuccess(list, items);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iServiceLoadListener.onServiceLoadFailed(e.getMessage());
                }
            });
        }else{
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    @Override
    public void onServiceLoadSuccess(List<Category> nameList, List<String> items) {

        dialog.setVisibility(View.GONE);
        spinner.setItems(items);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0 )
                {
                    Common.currentCategory = new Category(item.toString(), nameList.get((int)id).getCategoryPrice());
                    txt_category_price.setText(Common.currentCategory.getCategoryPrice());
                    btn_next.setEnabled(true);
                    layout_txt_price.setVisibility(View.VISIBLE);

                    btn_next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(ServicesActivity.this, TimeSlotActivity.class));

                        }
                    });
                }
                else {
                    btn_next.setEnabled(false);
                    layout_txt_price.setVisibility(View.GONE);
                }
            }

        });
    }
    private BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           loadCategory();
        }
    };
    @Override
    public void onServiceLoadFailed(String message) {
        PopUp.smallToast(ServicesActivity.this, layout, R.drawable.small_error, message,Toast.LENGTH_SHORT);
        dialog.setVisibility(View.GONE);
    }

    public void Actionbar()
    {
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
