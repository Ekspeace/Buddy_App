package com.example.buddyapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buddyapp.Constant.Common;
import com.example.buddyapp.Constant.PopUp;
import com.example.buddyapp.Interface.IRecyclerItemSelectedListener;
import com.example.buddyapp.Model.TimeSlot;
import com.example.buddyapp.R;
import com.example.buddyapp.TimeSlotActivity;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlots;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public TimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlots = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public TimeSlotAdapter(Context context, List<TimeSlot> timeSlots){
        this.context =context;
        this.timeSlots = timeSlots;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.time_slot,parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(Common.currentService.getName().contains(" "))
          holder.txt_time_slot.setText(new StringBuffer(Common.convertTimeToString(position)).toString());
        else
            holder.txt_time_slot.setText(new StringBuffer(Common.ConvertTimeToString(position)).toString());
       if(timeSlots.size() == 0)
       {
           holder.txt_time_slot_des.setText("Available");
           holder.txt_time_slot_des.setTextColor(context.getResources().getColor(android.R.color.black));
           holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
           holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
       }else {
           for(TimeSlot slotValue:timeSlots)
           {
               int slot = Integer.parseInt(slotValue.getSlot().toString());
               if(slot == position)
               {
                   holder.card_time_slot.setTag(Common.DISABLE_TAG);
                   holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                   holder.txt_time_slot_des.setText("Not Available");
                   holder.txt_time_slot_des.setTextColor(context.getResources().getColor(android.R.color.black));
                   holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
               }
           }
       }



       //Add all card to list (20 card because we have 20 time slot)
        //No add card already in cardViewList
        if(!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);

        //Check if card time slot is available
        if (!timeSlots.contains(position)) {
            holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int pos) {
                    //Loop all card in card list
                    for (CardView cardView : cardViewList) {
                        if (cardView.getTag() == null)
                            cardView.setCardBackgroundColor(context.getResources()
                                    .getColor(android.R.color.white));
                    }
                    //Our selected card will change color
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    if(cardViewList.get(position).getTag() != null)
                    {
                        Intent intent = new Intent(Common.KEY_SLOT_BUTTON_NEXT);
                        intent.putExtra(Common.KEY_STEP,2);
                        localBroadcastManager.sendBroadcast(intent);
                    }
                     else {
                        //After that, send broadcast to enable button Next
                        Intent intent = new Intent(Common.KEY_SLOT_BUTTON_NEXT);
                        intent.putExtra(Common.KEY_TIME_SLOT, pos);
                        intent.putExtra(Common.KEY_STEP, 3);
                        localBroadcastManager.sendBroadcast(intent);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(Common.currentService.getName().contains(" "))
          return Common.TIME_SLOT_TOTAL;
        else
            return 4;
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
      private TextView txt_time_slot, txt_time_slot_des;
      private CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_time_slot = itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_des = itemView.findViewById(R.id.txt_time_slot_description);
            card_time_slot = itemView.findViewById(R.id.card_time_slot);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
