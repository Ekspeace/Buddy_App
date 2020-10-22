package com.ekspeace.buddyapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp.Constant.Common;
import com.ekspeace.buddyapp.Interface.IRecyclerItemSelectedListener;
import com.ekspeace.buddyapp.Model.OrderInformation;
import com.ekspeace.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;

public class OrderInfoAdapter extends RecyclerView.Adapter<OrderInfoAdapter.MyViewHolder>  {

    private static final String POSITION = "position";
    final Context context;
    final List<OrderInformation> orderInformationList;
    final List<CardView> cardViewList;
    final LocalBroadcastManager localBroadcastManager;
    public OrderInfoAdapter(Context context, List<OrderInformation> orderinfoList) {
        this.context = context;
        this.orderInformationList = orderinfoList;
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }
    @NonNull
    @Override
    public OrderInfoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_order_info, parent, false);
        return new OrderInfoAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderInfoAdapter.MyViewHolder holder, int position) {
        String[] startTimeConvert = orderInformationList.get(position).getTime().split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

        Calendar bookingDateWithoutHouse = Calendar.getInstance();
        bookingDateWithoutHouse.setTimeInMillis(Common.currentDate.getTimeInMillis());
        bookingDateWithoutHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
        bookingDateWithoutHouse.set(Calendar.MINUTE, startMinInt);

        Timestamp timestamp = new Timestamp(bookingDateWithoutHouse.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                timestamp.toDate().getTime(),
                Calendar.getInstance().getTimeInMillis(),0).toString();

            if (orderInformationList.size() > 0) {
                holder.card_order_info.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Common.KEY_DISABLE_NO_ORDER_TEXT);
                localBroadcastManager.sendBroadcast(intent);
                holder.txt_category_name.setText(orderInformationList.get(position).getCategoryname());
                holder.txt_service_name.setText(orderInformationList.get(position).getServicename());
                holder.txt_time_ago.setText(dateRemain);

                if (orderInformationList.get(position).getCategoryname().contains("b")) {
                    holder.CannabisLayout.setVisibility(View.VISIBLE);
                    holder.txt_CannabisType.setText(orderInformationList.get(position).getCannabisName());
                    holder.txt_CannabisPrice.setText(orderInformationList.get(position).getCannabisPrice());
                }
                if (orderInformationList.get(position).getCategoryname().contains("G")) {
                    holder.GroceryLayout.setVisibility(View.VISIBLE);
                    holder.txt_GroceryStore.setText(orderInformationList.get(position).getGroceryStore());
                    holder.txt_GroceryList.setText(orderInformationList.get(position).getGroceryList());
                }
                if (orderInformationList.get(position).getCategoryname().contains("h")) {
                    holder.OtherLayout.setVisibility(View.VISIBLE);
                    holder.txt_OtherType.setText(orderInformationList.get(position).getOtherType());
                    holder.txt_OtherInfo.setText(orderInformationList.get(position).getOtherInfo());
                }
                if (!cardViewList.contains(holder.card_order_info))
                    cardViewList.add(holder.card_order_info);
                holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                    @Override
                    public void onItemSelectedListener(View view, int pos) {
                        for (CardView cardView : cardViewList) {
                            cardView.setCardBackgroundColor(context.getResources()
                                    .getColor(android.R.color.white));
                        }
                        holder.card_order_info.setCardBackgroundColor(
                                context.getResources()
                                        .getColor(R.color.colorPrimary)

                        );
                        userLoadOrder();
                        Common.currentOrder = new OrderInformation(orderInformationList.get(position).getServicename());
                        Common.categoryEdit = orderInformationList.get(position).getCategoryname();
                        Common.OrderPosition = pos;
                        Intent intent = new Intent(Common.KEY_CLICKED_BUTTON_DELETE);
                        localBroadcastManager.sendBroadcast(intent);
                    }
                });
            }
    }
    private void userLoadOrder(){
        CollectionReference userOrder = FirebaseFirestore.getInstance()
                .collection("users")
                .document(Common.currentUser.getUserId())
                .collection("Orders");

        userOrder
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                            {
                                Common.currentOrderId = queryDocumentSnapshot.getId();
                            }

                        }
                    }
                });
    }
    @Override
    public int getItemCount() {
        return orderInformationList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final CardView card_order_info;
        final TextView txt_service_name;
        final TextView txt_time_ago;
        final TextView txt_category_name;
        final TextView txt_GroceryStore;
        final TextView txt_GroceryList;
        final TextView txt_OtherType;
        final TextView txt_OtherInfo;
        final TextView txt_CannabisType;
        final TextView txt_CannabisPrice;
        final LinearLayout GroceryLayout;
        final LinearLayout OtherLayout;
        final LinearLayout CannabisLayout;


        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            GroceryLayout =itemView.findViewById(R.id.Grocery_layout);
            OtherLayout =itemView.findViewById(R.id.Other_layout);
            CannabisLayout =itemView.findViewById(R.id.Cannabis_layout);

            txt_category_name = itemView.findViewById(R.id.txt_Category_name_order);
            card_order_info = itemView.findViewById(R.id.card_order_info);
            txt_GroceryStore = itemView.findViewById(R.id.txt_user_GroceryStore_order);
            txt_GroceryList = itemView.findViewById(R.id.txt_user_GroceryList_order);
            txt_OtherType = itemView.findViewById(R.id.txt_user_OtherType_order);
            txt_OtherInfo = itemView.findViewById(R.id.txt_user_OtherInfo_order);
            txt_CannabisType =itemView.findViewById(R.id.txt_user_CannabisType_order);
            txt_CannabisPrice =itemView.findViewById(R.id.txt_user_CannabisPrice_order);
            txt_service_name= itemView.findViewById(R.id.txt_service_name_order);
            txt_time_ago = itemView.findViewById(R.id.txt_time_ago_order);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
