package com.example.affixapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderViewHolder> {

    Context context;
    ArrayList<ReminderItem> items;


    public ReminderAdapter(Context context, ArrayList<ReminderItem> items) {
        this.context = context;
        this.items = items;
    }



    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_view, parent, false);
        ReminderViewHolder viewHolder = new ReminderViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {

        holder.nameView.setText(items.get(position).getName());
        holder.descriptionView.setText(items.get(position).getDescription());
        holder.dateView.setText(items.get(position).getDate());
        holder.timeView.setText(items.get(position).getTime());



    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
