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
import com.ekspeace.buddyapp.Model.BookingInformation;
import com.ekspeace.buddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;


public class BookingInfoAdapter extends RecyclerView.Adapter<BookingInfoAdapter.MyViewHolder> {


    final Context context;
    final List<BookingInformation> bookinginfoList;
    final List<CardView> cardViewList;
    final LocalBroadcastManager localBroadcastManager;


    public BookingInfoAdapter(Context context, List<BookingInformation> bookinginfoList)
    {
        this.context = context;
        this.bookinginfoList = bookinginfoList;
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_booking_info, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookinginfoList.get(position).getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(),0).toString();
            if (bookinginfoList.size() > 0) {
                holder.card_booking_info.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Common.KEY_DISABLE_NO_BOOKING_TEXT);
                localBroadcastManager.sendBroadcast(intent);
                holder.txt_ServicePrice.setText(bookinginfoList.get(position).getPrice());
                holder.txt_category_name.setText(bookinginfoList.get(position).getCategoryName());
                holder.txt_service_name.setText(bookinginfoList.get(position).getServiceName());
                holder.txt_time.setText(bookinginfoList.get(position).getTime());
                holder.txt_time_remain.setText(dateRemain);

                if (!cardViewList.contains(holder.card_booking_info))
                    cardViewList.add(holder.card_booking_info);
                holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                    @Override
                    public void onItemSelectedListener(View view, int pos) {
                        for (CardView cardView : cardViewList) {
                            cardView.setCardBackgroundColor(context.getResources()
                                    .getColor(android.R.color.white));
                        }
                        holder.card_booking_info.setCardBackgroundColor(
                                context.getResources()
                                        .getColor(R.color.colorPrimary)

                        );
                        UserLoadBooking(position);
                        Common.BookPosition = pos;
                        Common.currentBooking = new BookingInformation(bookinginfoList.get(position).getServiceName(), bookinginfoList.get(position).getSlot(), bookinginfoList.get(position).getTimestamp());
                        Intent intent = new Intent(Common.KEY_CLICKED_BUTTON_DELETE);
                        localBroadcastManager.sendBroadcast(intent);
                    }
                });
            }
    }
    private void UserLoadBooking(int position) {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("users")
                .document(Common.currentUser.getUserId())
                .collection(Booking(bookinginfoList.get(position).getServiceName()));

        userBooking
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                            {
                                Common.currentBookingId = queryDocumentSnapshot.getId();
                            }
                        }
                    }
                });
    }
    public String Booking(String name)
    {
        if(name.contains(" "))
           return "Booking_Car_Wash";
        else
            return "Booking_Cleaning";
    }

    @Override
    public int getItemCount() {
        return bookinginfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final CardView card_booking_info;
        final TextView txt_service_name;
        final TextView txt_ServicePrice;
        final TextView txt_time;
        final TextView txt_time_remain;
        final TextView txt_category_name;
        LinearLayout dialog;


        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_category_name = itemView.findViewById(R.id.txt_Category_name);
            card_booking_info = itemView.findViewById(R.id.card_booking_info);
            txt_ServicePrice =itemView.findViewById(R.id.txt_user_ServicePrice_booking);
            txt_service_name= itemView.findViewById(R.id.txt_service_name);
            txt_time_remain = itemView.findViewById(R.id.txt_time_remain);
            txt_time = itemView.findViewById(R.id.txt_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
