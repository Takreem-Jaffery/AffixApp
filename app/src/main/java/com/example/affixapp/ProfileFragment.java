package com.example.affixapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.affixapp.model.UserModel;
import com.example.affixapp.utils.FirebaseUtil;
import com.example.affixapp.utils.AndroidUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText passwordInput;
    Button updateProfileBtn;
    Button changeFriendshipLevelDataBtn;
    Button logoutBtn;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result->{
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data=result.getData();
                        if(data!=null && data.getData()!=null) {
                            selectedImageUri=data.getData();
                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        passwordInput = view.findViewById(R.id.profile_password);
        updateProfileBtn = view.findViewById(R.id.update_profile_btn);
        logoutBtn=view.findViewById(R.id.logout_btn);
        changeFriendshipLevelDataBtn= view.findViewById(R.id.change_friendshipdata_btn);
        getUserData();
        updateProfileBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileFragment.this.getActivity(), LogInActivity.class);
                startActivity(intent);
            }
        });
        changeFriendshipLevelDataBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ProfileFragment.this.getActivity(), FriendshipLevelData.class);
                startActivity(intent);
            }
        });
        profilePic.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        return view;
    }
    void updateBtnClick(){
        String newUsername = usernameInput.getText().toString();
        String newPassword = passwordInput.getText().toString();

        if(newUsername.isEmpty()){
            usernameInput.setError("Username is empty");
            return;
        }
        if(newPassword.isEmpty()){
            passwordInput.setError("Password is empty");
            return;
        }
        currentUserModel.setUsername(newUsername);
        currentUserModel.setPassword(newPassword);
        if(selectedImageUri!=null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        }else{
            updateToFirestore();
        }
    }
    void updateToFirestore(){
            FirebaseUtil.updateToFirestore(getContext(), currentUserModel);
        /*FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(getContext(),"Updated successfully");*//*
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("users");
                        ref.child(ref.push().getKey()).setValue(currentUserModel);*//*
                    }else{
                        AndroidUtil.showToast(getContext(),"Updated failed");
                    }
                });*/
        /*FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");
        ref.child(ref.push().getKey()).setValue(currentUserModel);*/

    }

    void getUserData(){
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri  = task.getResult();
                        AndroidUtil.setProfilePic(getContext(),uri,profilePic);
                    }
                });
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUsername());
            passwordInput.setText(currentUserModel.getPassword());
        });
    }
}