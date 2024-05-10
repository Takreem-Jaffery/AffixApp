package com.example.affixapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class AdminViewHolder extends RecyclerView.ViewHolder{

    TextView nameView, IDView, descriptionView;
    ImageView imageView;

    public Button btnApprove;
    public Button btnReject;
    public AdminViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageView);
        nameView=itemView.findViewById(R.id.textUserName);
        IDView=itemView.findViewById(R.id.textforID);
        descriptionView=itemView.findViewById(R.id.textDescription);
        btnApprove = itemView.findViewById(R.id.btnApprove);
        btnReject = itemView.findViewById(R.id.btnReject);


    }

}
