package com.example.affixapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.affixapp.ChatActivity;
import com.example.affixapp.Person.User;
import com.example.affixapp.R;
import com.example.affixapp.model.UserModel;
import com.example.affixapp.utils.AndroidUtil;
import com.example.affixapp.utils.FirebaseUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ExploreAdapter extends FirebaseRecyclerAdapter
        <User,ExploreAdapter.ExploreViewHolder> {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    Context context;
    private static final String TAG = ContentValues.TAG;

    public ExploreAdapter(@NonNull FirebaseRecyclerOptions<User> options,Context context) {

        super(options);
        this.context=context;
    }
    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position, @NonNull User model) {

        try {
            holder.nameTextView.setText(model.getUsername());
            holder.bioTextView.setText(model.getZodiacSign());
            holder.bioTextView2.setText(String.valueOf(model.getAge()));
            holder.bioTextView3.setText(model.getHobby());
            /*Glide.with(context).load(model.getProfilePhoto()).apply(RequestOptions.circleCropTransform()).into(holder.userImage);
*/
            FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                    .addOnCompleteListener(t -> {
                        if(t.isSuccessful()){
                            Uri uri  = t.getResult();
                            AndroidUtil.setProfilePic(context,uri,holder.userImage);
                        }
                    });
            /*StorageReference profilePicRef = storage.getReference().child("profile_pic/" + model.getUserId() + ".jpeg");
            //Picasso.get().load(model.getProfilePhoto()).placeholder(R.drawable.person_icon).into(holder.userImage);
            Glide.with(context)
                    .load(profilePicRef)
                    .into(holder.userImage);*/
            /*Glide.with(holder.userImage.getContext())
                    .load(model.getProfilePhoto())
                    .into(holder.userImage);*/
        }
        catch(Exception e){
            Log.d(TAG, "Could not Bind View Holder");
        }


        holder.chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //navigate to chat activity
                    Intent intent = new Intent(context, ChatActivity.class);
                UserModel userModel=new UserModel("",model.getUsername(),null,model.getUserId(),model.getPassword());
                    AndroidUtil.passUserModelAsIntent(intent,userModel);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                //Toast.makeText(v.getContext(), "Open chat", Toast.LENGTH_SHORT).show();
                // Get the position of the item to be removed
                int position = holder.getBindingAdapterPosition();
                // Remove the item from the adapter's dataset
                getSnapshots().getSnapshot(position).getRef().removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Item removed successfully
                                Toast.makeText(v.getContext(), "User rejected", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                                Toast.makeText(v.getContext(), "Failed to reject user", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.rejectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the position of the item to be removed
                int position = holder.getBindingAdapterPosition();
                // Remove the item from the adapter's dataset
                getSnapshots().getSnapshot(position).getRef().removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Item removed successfully
                                Toast.makeText(v.getContext(), "User rejected", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                                Toast.makeText(v.getContext(), "Failed to reject user", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }
    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(com.example.affixapp.R.layout.item_user_layout, parent, false);

        return new ExploreViewHolder(itemView);
    }
    public static class ExploreViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView bioTextView;
        private TextView bioTextView2;
        private TextView bioTextView3;
        private ImageView userImage;
        private ImageButton chatbtn;
        private ImageButton rejectbtn;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(com.example.affixapp.R.id.nameTextView);
            bioTextView = itemView.findViewById(com.example.affixapp.R.id.bioTextView);
            bioTextView2 = itemView.findViewById(com.example.affixapp.R.id.bioTextView2);
            bioTextView3 = itemView.findViewById(com.example.affixapp.R.id.bioTextView3);
            userImage = itemView.findViewById(R.id.profile_pic_image_view);
            chatbtn = itemView.findViewById(R.id.chatBtn);
            rejectbtn = itemView.findViewById(R.id.rejectBtn);
        }
    }

}