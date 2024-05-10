package com.example.affixapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.affixapp.AdminViewHolder;
import com.example.affixapp.Item;
import com.example.affixapp.R;
import com.example.affixapp.utils.AndroidUtil;
import com.example.affixapp.utils.FirebaseUtil;

import java.util.ArrayList;
public class AdminAdapter extends RecyclerView.Adapter<AdminViewHolder>{

        Context context;
        ArrayList<Item> items;

        private RequestActionListener listener; // Listener interface for button clicks

    public AdminAdapter(Context context, ArrayList<Item> items, RequestActionListener listener) {
            this.context = context;

            this.listener = listener;
            this.items = items;
        }

        @NonNull
        @Override
        public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
            AdminViewHolder viewHolder = new AdminViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
            holder.nameView.setText("Name: "+items.get(position).getName());
            holder.IDView.setText("ID: "+items.get(position).getUserID());
            holder.descriptionView.setText("Reason: "+items.get(position).getDescription());

            Log.d("UserId ReportRequests:","message"+items.get(position).getUserID());
            if(items.get(position).getUserID()!=null) {
                FirebaseUtil.getOtherProfilePicStorageRef(items.get(position).getUserID()).getDownloadUrl()
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                Uri uri = t.getResult();
                                AndroidUtil.setProfilePic(context, uri, holder.imageView);
                            }
                        });
            }
            // Approve button click listener
            holder.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle approve button click
                    listener.onApproveClicked(position);
                }
            });

            // Reject button click listener
            holder.btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle reject button click
                    listener.onRejectClicked(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }


        // Listener interface for button clicks
        public interface RequestActionListener {
            void  onApproveClicked(int position);
            void onRejectClicked(int position);
        }
}
