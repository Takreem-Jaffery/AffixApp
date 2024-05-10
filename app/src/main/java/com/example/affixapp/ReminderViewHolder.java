package com.example.affixapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReminderViewHolder extends RecyclerView.ViewHolder  {

    TextView nameView, descriptionView,dateView,timeView;
    public ReminderViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView=itemView.findViewById(R.id.textTask);
        descriptionView=itemView.findViewById(R.id.textDescription);
        timeView=itemView.findViewById(R.id.textTime);
        dateView=itemView.findViewById(R.id.textforDate);


    }

}