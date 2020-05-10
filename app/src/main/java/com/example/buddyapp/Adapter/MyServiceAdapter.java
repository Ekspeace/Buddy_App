package com.example.buddyapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Interface.IRecyclerItemSelectedListener;
import com.example.buddyapp.Model.Services;
import com.example.buddyapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.MyViewHolder> {


    Context context;
    List<Services> serviceList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;


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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_service_name;
        CardView card_service;
        ImageView img_service;
        AlertDialog dialog;

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
                                .getColor(R.color.colorPrimary)

                );

                Common.service = serviceList.get(position).getServiceId();
                Common.serviceName = serviceList.get(position).getName();
                Common.currentService = new Services(Common.serviceName, Common.service, serviceList.get(position).getImageUrl());
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_SERVICE);
                localBroadcastManager.sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


}