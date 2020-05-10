package com.example.buddyapp.Services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.example.buddyapp.Activities.LoginActivity;
import com.example.buddyapp.Activities.OrderConfirmActivity;
import com.example.buddyapp.BookingConfirmActivity;
import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Constant.PopUp;
import com.example.buddyapp.Interface.IServiceLoadListener;
import com.example.buddyapp.Model.Cannabis;
import com.example.buddyapp.Model.Category;
import com.example.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;


import java.util.ArrayList;
import java.util.List;


import io.paperdb.Paper;

public class PickUp extends AppCompatActivity implements IServiceLoadListener{
     private Toolbar mToolbar;
     private MaterialSpinner spinner;
     private Button btn_next;
     private IServiceLoadListener iServiceLoadListener;
     private LinearLayout dialog;
     private CollectionReference categoryTypeRef;
     private LinearLayout layout_cannabis;
     private MaterialSpinner cannabis_type;
     private TextView range_price,service_txt;
     private LinearLayout layout_range_price;
     private EditText alternative_cannabis_type;
     private EditText price;
     private LinearLayout layout_grocery;
     private EditText et_grocery_store;
     private EditText et_grocery_list;
    private LinearLayout layout_other;
    private EditText et_other_type;
    private View layout;
    private EditText et_other_type_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up);
        mToolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.spinner);
        service_txt = findViewById(R.id.Service_PickUp_text);
        layout_range_price = findViewById(R.id.layout_range_cannabis_price);
        range_price = findViewById(R.id.range_price);
        btn_next = findViewById(R.id.btn_next_step);
        layout_cannabis = findViewById(R.id.layout_cannabis);
        cannabis_type = findViewById(R.id.cannabis_spinner);
        alternative_cannabis_type = findViewById(R.id.alternative_cannabis_choice);
        price = findViewById(R.id.price_pay);
        layout_grocery = findViewById(R.id.layout_grocery);
        et_grocery_store = findViewById(R.id.grocery_store);
        et_grocery_list = findViewById(R.id.grocery_list);
        layout_other = findViewById(R.id.layout_other_pickups);
        et_other_type = findViewById(R.id.other_pickups);
        et_other_type_info = findViewById(R.id.other_piokup_more_info);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        iServiceLoadListener = this;
        service_txt.setText(Common.currentService.getName());
        dialog = findViewById(R.id.ProgressBar_pickUps);
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
                                    items.add(documentSnapshot.getId());
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
    private void loadCannabis() {
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(this)) {
            CollectionReference cannabisType = FirebaseFirestore.getInstance()
                    .collection("Service")
                    .document(Common.currentService.getServiceId())
                    .collection("Category")
                    .document(Common.currentCategory.getCategoryName())
                    .collection("Types");

            cannabisType.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> items = new ArrayList<>();
                        items.add("Choose a Cannabis Type");
                        List<Cannabis> rangePrices = new ArrayList<>();
                        for (QueryDocumentSnapshot querySnapshot : task.getResult()) {
                            Cannabis cannabis = querySnapshot.toObject(Cannabis.class);
                            cannabis.setCannabisName(querySnapshot.getId());
                            cannabis.setCannabisPrice(querySnapshot.get("price").toString());
                            rangePrices.add(cannabis);
                            items.add(querySnapshot.getId());
                        }
                        layout_cannabis.setVisibility(View.VISIBLE);
                        spinnerCannabis(items, rangePrices);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    PopUp.smallToast(PickUp.this, layout,R.drawable.small_error,e.getMessage(),Toast.LENGTH_SHORT);
                }
            });

        } else {
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }

    private void spinnerCannabis(List<String> items, List<Cannabis> prices)
    {
        dialog.setVisibility(View.GONE);
        cannabis_type.setItems(items);
        cannabis_type.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0 )
                {
                    Common.currentCannabis = new Cannabis(item.toString(),prices.get((int)id).getCannabisPrice());
                    range_price.setText((Common.currentCannabis.getCannabisPrice()));
                    layout_range_price.setVisibility(View.VISIBLE);
                    btn_next.setEnabled(true);

                    btn_next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String altPrice = price.getText().toString().trim();
                            String AlterType = alternative_cannabis_type.getText().toString().trim();

                                if (TextUtils.isEmpty(altPrice)) {
                                    price.setError("Price Required");
                                    return;
                                }

                                Common.currentCannabisPrice = altPrice;
                                Common.currentAlterType = AlterType;
                                startActivity(new Intent(PickUp.this, OrderConfirmActivity.class));
                            }
                    });
                }
                else {
                    btn_next.setEnabled(false);
                }
            }

        });
    }

    private void Grocery()
    {
        layout_grocery.setVisibility(View.VISIBLE);
        btn_next.setEnabled(true);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
               startActivity(new Intent(PickUp.this, OrderConfirmActivity.class));
            }
        });
    }
    private void Other()
    {
        layout_other.setVisibility(View.VISIBLE);
        btn_next.setEnabled(true);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                startActivity(new Intent(PickUp.this, OrderConfirmActivity.class));
            }
        });
    }


 public void Actionbar()
 {
     // Required to use Toolbar as ActionBar
     setSupportActionBar(mToolbar);
     // Dismiss Action
     mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
     mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             finish();
         }
     });
 }

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
    @Override
    public void onServiceLoadSuccess(List<Category> nameList, List<String> items) {

        dialog.setVisibility(View.GONE);
        spinner.setItems(items);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0 )
                {
                    Common.currentCategory = new Category(item.toString());
                    if(Common.currentCategory.getCategoryName().contains("b")) {
                        layout_grocery.setVisibility(View.GONE);
                        layout_other.setVisibility(View.GONE);
                        loadCannabis();
                    }
                    if(Common.currentCategory.getCategoryName().contains("G")){
                        layout_other.setVisibility(View.GONE);
                        layout_cannabis.setVisibility(View.GONE);
                        Grocery();
                    }
                    if(Common.currentCategory.getCategoryName().contains("h")){
                        layout_cannabis.setVisibility(View.GONE);
                        layout_grocery.setVisibility(View.GONE);
                        Other();
                    }
                }
                else {
                    btn_next.setEnabled(false);
                    layout_cannabis.setVisibility(View.GONE);
                    layout_grocery.setVisibility(View.GONE);
                    layout_other.setVisibility(View.GONE);
                }
            }

        });
    }
    @Override
    public void onServiceLoadFailed(String message) {
        PopUp.smallToast(PickUp.this, layout,R.drawable.small_error, message,Toast.LENGTH_SHORT);
    }
}
