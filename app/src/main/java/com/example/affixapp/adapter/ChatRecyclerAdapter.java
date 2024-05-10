package com.example.affixapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.affixapp.ChatActivity;
import com.example.affixapp.R;
import com.example.affixapp.model.ChatMessageModel;
import com.example.affixapp.utils.AndroidUtil;
import com.example.affixapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        Log.i("haushd","asjd");
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            if (model.getType() != null && model.getType().equals("text")) {
                holder.rightChatTextview.setVisibility(View.VISIBLE);
                holder.rightChatImageView.setVisibility(View.GONE);

                holder.rightChatTextview.setText(model.getMessage());
            } else {
                holder.rightChatImageView.setVisibility(View.VISIBLE);
                holder.rightChatTextview.setVisibility(View.GONE);

                if (model.getImage() != null) {
                    Picasso.get().load(model.getImage()).placeholder(R.drawable.camera_icon).into(holder.rightChatImageView);
                } else {
                    holder.rightChatImageView.setImageResource(R.drawable.camera_icon);
                }
            }

        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            if (model.getType() != null && model.getType().equals("text")) {
                holder.leftChatTextview.setVisibility(View.VISIBLE);
                holder.leftChatImageView.setVisibility(View.GONE);

                holder.leftChatTextview.setText(model.getMessage());
            } else {
                holder.leftChatImageView.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setVisibility(View.GONE);
                if (model.getImage() != null) {
                    Picasso.get().load(model.getImage()).placeholder(R.drawable.camera_icon).into(holder.leftChatImageView);
                } else {
                    holder.leftChatImageView.setImageResource(R.drawable.camera_icon);
                }
            }

        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview;

        ImageView leftChatImageView,rightChatImageView;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatImageView=itemView.findViewById(R.id.left_chat_imageview);
            rightChatImageView=itemView.findViewById(R.id.right_chat_imageview);
        }
    }
}