package com.example.buddyapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Interface.IRecyclerItemSelectedListener;
import com.example.buddyapp.Model.BookingInformation;
import com.example.buddyapp.Model.OrderInformation;
import com.example.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class OrderInfoAdapter extends RecyclerView.Adapter<OrderInfoAdapter.MyViewHolder>  {

    Context context;
    List<OrderInformation> orderInformationList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;


    public OrderInfoAdapter(Context context, List<OrderInformation> orderinfoList)
    {
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
        if(orderInformationList != null) {
            holder.card_order_info.setVisibility(View.VISIBLE);
            Intent intent = new Intent(Common.KEY_DISABLE_NO_ORDER_TEXT);
            localBroadcastManager.sendBroadcast(intent);
        }

        holder.txt_address.setText(orderInformationList.get(position).getCustomerAddress());
        holder.txt_category_name.setText(orderInformationList.get(position).getCategoryname());
        holder.txt_service_name.setText(orderInformationList.get(position).getServicename());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(String.valueOf(Timestamp.now().toDate().getTime())),
                Calendar.getInstance().getTimeInMillis(),0).toString();
        holder.txt_time_ago.setText(dateRemain);

        if (!cardViewList.contains(holder.card_order_info))
            cardViewList.add(holder.card_order_info);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {


                for (CardView cardView:cardViewList)
                {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));
                }

                holder.card_order_info.setCardBackgroundColor(
                        context.getResources()
                                .getColor(R.color.colorPrimary)

                );
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
                Common.currentOrder = new OrderInformation(orderInformationList.get(position).getServicename());
                Intent intent = new Intent(Common.KEY_CLICKED_BUTTON_DELETE);
                localBroadcastManager.sendBroadcast(intent);
            }
        });

    }





    @Override
    public int getItemCount() {
        return orderInformationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView card_order_info;
        TextView txt_service_name, txt_address,txt_time_ago, txt_category_name;
        AlertDialog dialog;


        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_category_name = itemView.findViewById(R.id.txt_Category_name_order);
            card_order_info = itemView.findViewById(R.id.card_order_info);
            txt_address =itemView.findViewById(R.id.txt_user_address_order);
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
