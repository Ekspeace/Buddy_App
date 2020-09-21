package com.ekspeace.buddyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekspeace.buddyapp.R;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.List;

public class RecyclerViewLicenseAdapter extends RecyclerView.Adapter<RecyclerViewLicenseAdapter.MyViewHolder> {

    Context context;
    List<Library> List;

    public RecyclerViewLicenseAdapter(Context context, List<Library> List) {
        this.context = context;
        this.List = List;
    }

    @NonNull
    @Override
    public RecyclerViewLicenseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_license_info, parent, false);
        return new RecyclerViewLicenseAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewLicenseAdapter.MyViewHolder holder, int position) {
        holder.txt_author_name.setText(List.get(position).getAuthor());
        holder.txt_license_name.setText(List.get(position).getLibraryName());
        holder.txt_license_description.setText(List.get(position).getLibraryDescription());
        holder.txt_version.setText(List.get(position).getLibraryVersion());
    }


    @Override
    public int getItemCount() {
        return List.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_author_name, txt_license_name, txt_license_description, txt_version;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_author_name = itemView.findViewById(R.id.license_Author);
            txt_license_name = itemView.findViewById(R.id.license_library);
            txt_license_description = itemView.findViewById(R.id.license_Description);
            txt_version = itemView.findViewById(R.id.license_version);
        }

    }
}