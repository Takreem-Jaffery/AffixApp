package com.example.affixapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.google.common.io.Files.getFileExtension;
import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.affixapp.adapter.ChatRecyclerAdapter;
import com.example.affixapp.adapter.SearchUserRecyclerAdapter;
import com.example.affixapp.model.ChatMessageModel;
import com.example.affixapp.model.ChatroomModel;
import com.example.affixapp.model.UserModel;
import com.example.affixapp.ui.ReportActivity;
import com.example.affixapp.ui.model.ViewReminderActivity;
import com.example.affixapp.utils.AndroidUtil;
import com.example.affixapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;

    ActivityResultLauncher<Intent> imagePickLauncher;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;

    ImageButton attachMediaBtn;
    Dialog myDialog;//=new Dialog(this);
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    Uri selectedImageUri;
    ImageButton bottomsheet;
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myDialog=new Dialog(this);

        //get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        attachMediaBtn=findViewById(R.id.attach_media_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
       // imageView = findViewById(R.id.profile_pic_layout);

        bottomsheet = findViewById(R.id.bottom_sheet);
        bottomsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();

            }
        });

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(ChatActivity.this,uri,findViewById(R.id.profile_pic_image_view));
                    }
                });

        imagePickLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result->{
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data=result.getData();
                        if(data!=null && data.getData()!=null) {
                            selectedImageUri=data.getData();
                            sendImageToUser(selectedImageUri);
                        }
                    }
                });

        backBtn.setOnClickListener((v)->{
            Intent intent= new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        attachMediaBtn.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout reminderLayout = dialog.findViewById(R.id.layoutReminder);
        LinearLayout ViewReminderLayout= dialog.findViewById(R.id.layoutViewReminders);
        LinearLayout blockLayout = dialog.findViewById(R.id.layoutBlock);
        LinearLayout reportLayout = dialog.findViewById(R.id.layoutReport);

        reminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                startActivity(new Intent(ChatActivity.this, SetReminder.class));
                finish();
            }
        });
        ViewReminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                startActivity(new Intent(ChatActivity.this, ViewReminderActivity.class));
                finish();
            }
        });
        blockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ADD USERID AND OTHER USER ID
                // pls add firebase query to fetch current user & reported user :^)
                String currentUserId =FirebaseUtil.currentUserId();
                String reportedUserId =otherUser.getUserId();
                String reportedUsername =otherUser.getUsername();

                dialog.dismiss();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.block_dialog_layout, null);

                // CHANGE THIS TO CHAT ACTIVITY
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setView(dialogView);

                TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
                dialogMessage.setText("Are you sure you want to block this user?");

                Button buttonYes = dialogView.findViewById(R.id.dialog_button_yes);
                Button buttonNo = dialogView.findViewById(R.id.dialog_button_no);

                AlertDialog dialog = builder.create();
                dialog.show();

                buttonYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle "Yes" button click
                        dialog.dismiss();
                        blockUser(currentUserId,reportedUserId); // Call your method to block the user

                        // commenting because im unsure about its implementation
                        // call function to remove chat from user's feed


                        //deleteBlockedChat();
                    }
                });

                buttonNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle "No" button click
                        dialog.dismiss();
                    }
                });

            }
        });

        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ADD USERID AND OTHER USER ID
                // pls add firebase query to fetch current user & reported user :^)
                String currentUserId =FirebaseUtil.currentUserId();
                String reportedUserId =otherUser.getUserId();
                String reportedUsername =otherUser.getUsername();


                dialog.dismiss();


                // CHANGE THIS TO CHAT ACTIVITY
                Intent reportUserIntent= new Intent(ChatActivity.this,ReportUser.class);
                reportUserIntent.putExtra("keyUserID", currentUserId);
                reportUserIntent.putExtra("keyReportedUserID", reportedUserId);
                reportUserIntent.putExtra("keyReportedUsername", reportedUsername);
                startActivity(reportUserIntent);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
    private void blockUser(String currentUserID, String reportedUserID){
        // this method adds user to a BLOCKLIST in database
        // [!] yet to decide if we want to delete user's chat as well

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> blockListEntry = new HashMap<>();
            blockListEntry.put("userID", currentUserID);
            blockListEntry.put("blockedUserID", reportedUserID);

            db.collection("blocklist")
                    .add(blockListEntry)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "User blocked", Toast.LENGTH_SHORT).show();
                            deleteBlockedChat(chatroomId);
                            Log.d(TAG, "User added to blocklist with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error adding user to blocklist", e);
                        }
                    });

        }catch(Exception e){
            Log.d(TAG, e.toString());
        }
    }
    private void deleteBlockedChat(String chatroomID){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("chatrooms").document(chatroomID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, add it to the blockedchatrooms collection
                            Map<String, Object> blockedChatroom = documentSnapshot.getData();

                            db.collection("blockedchatrooms")
                                    .add(blockedChatroom)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Document added to blockedchatrooms collection, now delete it from chatrooms collection
                                            db.collection("chatrooms").document(chatroomID)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "onSuccess: ChatroomID deleted");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "onFailure: could not delete ChatroomID");
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure to add to blockedchatrooms collection
                                            Log.d(TAG, "onFailure: could not move ChatroomID to Blocked Chatrooms");
                                        }
                                    });
                        } else {
                            Toast.makeText(ChatActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to query the chatrooms collection
                        Log.d(TAG, e.toString());
                    }
                });

    }
    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }


    void sendMessageToUser(String message){
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,"text",null,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                            sendNotification(message);
                        }
                    }
                });
    }
    void sendImageToUser(Uri image){

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage("sent a photo...");
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        String img=image.toString();
        ChatMessageModel chatMessageModel = new ChatMessageModel("","image",img,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){

                            messageInput.setText("");
                            sendNotification("sent a photo...");
                        }
                    }
                });

        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(img));

        // Upload image to Firebase Storage
        imageReference.putFile(image).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return imageReference.getDownloadUrl();
        });
    }
    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void sendNotification(String message){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try{
                    JSONObject jsonObject  = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);


                }catch (Exception e){

                }

            }
        });

    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer YOUR_API_KEY")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }
}
