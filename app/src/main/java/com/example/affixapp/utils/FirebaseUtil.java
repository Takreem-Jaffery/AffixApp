package com.example.affixapp.utils;

import static com.example.affixapp.utils.AndroidUtil.showToast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.affixapp.Person.User;
import com.example.affixapp.model.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    public static String currentUserId(){
        Log.d("Uid", "Variable value returned by currentUserId: " + FirebaseAuth.getInstance().getUid());
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){
        Log.d("UserDocReference", "Variable value returned by currentUserDetails: " + FirebaseFirestore.getInstance().collection("users").document(currentUserId()));
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    public static DocumentReference currentAdminDetails(){
        return FirebaseFirestore.getInstance().collection("admins").document(currentUserId());
    }

    public Task<Boolean> isAdmin(String userid) {
        // Reference to the admin document with the specified userid
        return db.collection("admins")
                .document(userid)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document with userid exists in admin collection
                            return true;
                        } else {
                            // Document does not exist in admin collection
                            return false;
                        }
                    } else {
                        // Failed to fetch document
                        Log.d("ErrorInIsAdmin", "Error fetching document: ", task.getException());
                        throw task.getException(); // Propagate error
                    }
                });
    }
    public Task<Boolean> isReported(String userid) {
        // Reference to the admin document with the specified userid
        return db.collection("requests")
                .document(userid)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document with userid exists in admin collection
                            return true;
                        } else {
                            // Document does not exist in admin collection
                            return false;
                        }
                    } else {
                        // Failed to fetch document
                        Log.d("ErrorInIsRequests", "Error fetching document: ", task.getException());
                        throw task.getException(); // Propagate error
                    }
                });
    }

    public Task<Boolean> isSuspended(String userid) {
        // Reference to the admin document with the specified userid
        return db.collection("reportedUsers")
                .document(userid)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document with userid exists in admin collection
                            return true;
                        } else {
                            // Document does not exist in admin collection
                            return false;
                        }
                    } else {
                        // Failed to fetch document
                        Log.d("ErrorInIsRequests", "Error fetching document: ", task.getException());
                        throw task.getException(); // Propagate error
                    }
                });
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference  getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
    public static void updateToFirestore(Context context, UserModel currentUserModel) {
        // Update data in Firestore
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast(context, "Updated successfully in Firestore");

                        // Update data in Realtime Database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                        String key = currentUserModel.getUserId();
                        User user1 = new User();
                        user1.setAge(currentUserModel.getAge());
                        user1.setEmail(currentUserModel.getEmail());
                        user1.setFears(currentUserModel.getFears());
                        user1.setUsername(currentUserModel.getUsername());
                        user1.setGender(currentUserModel.getGender());
                        user1.setHobby(currentUserModel.getHobby());
                        user1.setPassword(currentUserModel.getPassword());
                        user1.setProfession(currentUserModel.getProfession());
                        String profilepic=FirebaseUtil.getOtherProfilePicStorageRef(currentUserModel.getUserId()).getDownloadUrl().toString();

                        user1.setProfilePhoto(profilepic);
                        user1.setZodiacSign(currentUserModel.getZodiacSign());
                        user1.setUserId(currentUserModel.getUserId());
                        databaseReference.child(key).setValue(user1);
                        /*String profilepic=FirebaseUtil.getOtherProfilePicStorageRef(currentUserModel.getUserId()).getDownloadUrl().toString();
                        databaseReference.child(key).child("profilePic").setValue(profilepic);*/

                        showToast(context, "Updated successfully in Realtime Database");
                    } else {
                        showToast(context, "Update failed");
                    }
                });
    }
    private static void showToast(@NonNull Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}