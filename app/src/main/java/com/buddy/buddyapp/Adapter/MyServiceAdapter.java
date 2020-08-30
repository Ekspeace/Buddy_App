package com.buddy.buddyapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buddy.buddyapp.Constant.Common;
import com.buddy.buddyapp.Interface.IRecyclerItemSelectedListener;
import com.buddy.buddyapp.Model.Services;
import com.buddy.buddyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.MyViewHolder> {


    final Context context;
    final List<Services> serviceList;
    final List<CardView> cardViewList;
    final LocalBroadcastManager localBroadcastManager;


    public MyServiceAdapter(Context context, List<Services> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_services, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyServiceAdapter.MyViewHolder holder, int position) {

        holder.txt_service_name.setText(serviceList.get(position).getName());
        Picasso.get().load(serviceList.get(position).getImageUrl()).into(holder.img_service);
        if (!cardViewList.contains(holder.card_service))
            cardViewList.add(holder.card_service);
        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {

                for (CardView cardView:cardViewList)
                {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));

                }

                holder.card_service.setCardBackgroundColor(
                        context.getResources()
                                .getColor(R.color.colorPrimary));
                Common.service = serviceList.get(position).getServiceId();
                Common.serviceName = serviceList.get(position).getName();
                Common.currentService = serviceList.get(position);
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_SERVICE);
                localBroadcastManager.sendBroadcast(intent);
            }
        });

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView txt_service_name;
        final CardView card_service;
        final ImageView img_service;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_service = itemView.findViewById(R.id.card_service);
            txt_service_name =  itemView.findViewById(R.id.txt_service_name);
            img_service = itemView.findViewById(R.id.img_service);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());

        }

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


}