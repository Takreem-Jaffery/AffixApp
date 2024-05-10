package com.example.affixapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.affixapp.model.UserModel;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.affixapp.model.UserModel;
import com.example.affixapp.utils.AndroidUtil;
import com.example.affixapp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class FriendshipLevelData extends AppCompatActivity {
    ImageButton backButton;
    EditText ageInput;
    Button confirmChanges;
    UserModel currentUserModel;
    String[]zodiacSign={"Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo", "Libra", "Scorpio", "Aquarius", "Sagittarius", "Capricorn", "Pisces"};
    String[]gender={"Male","Female","Other"};
    String[] hobby={ "Sports", "Travel", "Books", "Gym", "Games", "Movies", "Music", "Other"};
    String[]profession={ "Computer", "Engineering", "Arts", "Architecture", "Math", "Science", "Psychology", "Languages", "Business", "Other"};
    String[] fears={"Insects", "Heights", "Darkness", "Dentists", "Crowds", "Thunder", "Other"};
    AutoCompleteTextView zodiacAutoCompleteTextView;
    AutoCompleteTextView genderAutoCompleteTextView;
    AutoCompleteTextView hobbyAutoCompleteTextView;
    AutoCompleteTextView professionAutoCompleteTextView;
    AutoCompleteTextView fearsAutoCompleteTextView;
    ArrayAdapter<String>zodiacAdapter;
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> hobbyAdapter;
    ArrayAdapter<String> professionAdapter;
    ArrayAdapter<String> fearsAdapter;
    void updateBtnClick(){
        String newAge = ageInput.getText().toString();
        String newZodiacSign = zodiacAutoCompleteTextView.getText().toString();
        String newGender = genderAutoCompleteTextView.getText().toString();
        String newHobby = hobbyAutoCompleteTextView.getText().toString();
        String newProfession = professionAutoCompleteTextView.getText().toString();
        String newFears = fearsAutoCompleteTextView.getText().toString();

        if(newAge.isEmpty()){
            ageInput.setError("Age is empty");
            return;
        }
        if(newZodiacSign.isEmpty()){
            zodiacAutoCompleteTextView.setError("zodiacSign is empty");
            return;
        }
        if(newGender.isEmpty()){
            genderAutoCompleteTextView.setError("Gender is empty");
            return;
        } if(newHobby.isEmpty()){
            hobbyAutoCompleteTextView.setError("Hobby is empty");
            return;
        } if(newProfession.isEmpty()){
            professionAutoCompleteTextView.setError("Profession is empty");
            return;
        } if(newFears.isEmpty()){
            fearsAutoCompleteTextView.setError("Fears is empty");
            return;
        }
        //currentUserModel.setAge(newAge);
        currentUserModel.setUserId(FirebaseUtil.currentUserId());
        currentUserModel.setAge(Integer.parseInt(newAge));
        currentUserModel.setZodiacSign(newZodiacSign);
        currentUserModel.setGender(newGender);
        currentUserModel.setHobby(newHobby);
        currentUserModel.setProfession(newProfession);
        currentUserModel.setFears(newFears);

        updateToFirestore();
    }
    void updateToFirestore(){
        Log.d("Current User","Current UserID="+currentUserModel.getUserId());
            FirebaseUtil.updateToFirestore(this, currentUserModel);


        /*FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(this,"Updated successfully");
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("users");
                        ref.child(ref.push().getKey()).setValue(currentUserModel);
                    }else{
                        AndroidUtil.showToast(this,"Updated failed");
                    }
                });*/
    }

    void getUserData(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            if (currentUserModel != null) {
                ageInput.setText(String.valueOf(currentUserModel.getAge()));
                zodiacAutoCompleteTextView.setText(currentUserModel.getZodiacSign());
                genderAutoCompleteTextView.setText(currentUserModel.getGender());
                hobbyAutoCompleteTextView.setText(currentUserModel.getHobby());
                professionAutoCompleteTextView.setText(currentUserModel.getProfession());
                fearsAutoCompleteTextView.setText(currentUserModel.getFears());
                // Reinitialize adapters with data
                zodiacAdapter = new ArrayAdapter<>(this, R.layout.list_item, zodiacSign);
                genderAdapter = new ArrayAdapter<>(this, R.layout.list_item, gender);
                hobbyAdapter = new ArrayAdapter<>(this, R.layout.list_item, hobby);
                professionAdapter = new ArrayAdapter<>(this, R.layout.list_item, profession);
                fearsAdapter = new ArrayAdapter<>(this, R.layout.list_item, fears);
                // Set adapters to AutoCompleteTextViews again
                zodiacAutoCompleteTextView.setAdapter(zodiacAdapter);
                genderAutoCompleteTextView.setAdapter(genderAdapter);
                hobbyAutoCompleteTextView.setAdapter(hobbyAdapter);
                professionAutoCompleteTextView.setAdapter(professionAdapter);
                fearsAutoCompleteTextView.setAdapter(fearsAdapter);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendship_level_data);
        // Initialize currentUserModel
        currentUserModel = new UserModel();
        currentUserModel.setAge(0);
        currentUserModel.setZodiacSign("");
        currentUserModel.setGender("");
        currentUserModel.setHobby("");
        currentUserModel.setProfession("");
        currentUserModel.setFears("");
        // Initialize views
        ageInput = findViewById(R.id.age);
        zodiacAutoCompleteTextView = findViewById(R.id.auto_complete_text_zodiac);
        genderAutoCompleteTextView = findViewById(R.id.auto_complete_text_gender);
        hobbyAutoCompleteTextView = findViewById(R.id.auto_complete_text_hobby);
        professionAutoCompleteTextView = findViewById(R.id.auto_complete_text_profession);
        fearsAutoCompleteTextView = findViewById(R.id.auto_complete_text_fears);
        confirmChanges = findViewById(R.id.confirm_button);
        // Set up click listener for the confirm button
        confirmChanges.setOnClickListener(v -> {
            updateBtnClick();
        });

        /*
        // Initialize AutoCompleteTextViews
        zodiacAutoCompleteTextView = findViewById(R.id.auto_complete_text_zodiac);
        genderAutoCompleteTextView = findViewById(R.id.auto_complete_text_gender);
        hobbyAutoCompleteTextView = findViewById(R.id.auto_complete_text_hobby);
        professionAutoCompleteTextView = findViewById(R.id.auto_complete_text_profession);
        fearsAutoCompleteTextView = findViewById(R.id.auto_complete_text_fears);*/

        // Set up adapters for AutoCompleteTextViews
        zodiacAdapter = new ArrayAdapter<>(this, R.layout.list_item, zodiacSign);
        genderAdapter = new ArrayAdapter<>(this, R.layout.list_item, gender);
        hobbyAdapter = new ArrayAdapter<>(this, R.layout.list_item, hobby);
        professionAdapter = new ArrayAdapter<>(this, R.layout.list_item, profession);
        fearsAdapter = new ArrayAdapter<>(this, R.layout.list_item, fears);

        // Set adapters to AutoCompleteTextViews
        zodiacAutoCompleteTextView.setAdapter(zodiacAdapter);
        genderAutoCompleteTextView.setAdapter(genderAdapter);
        hobbyAutoCompleteTextView.setAdapter(hobbyAdapter);
        professionAutoCompleteTextView.setAdapter(professionAdapter);
        fearsAutoCompleteTextView.setAdapter(fearsAdapter);

        // Set item click listeners for AutoCompleteTextViews
        zodiacAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(FriendshipLevelData.this, "Zodiac Sign: " + item, Toast.LENGTH_SHORT).show();
        });
        genderAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(FriendshipLevelData.this, "Gender: " + item, Toast.LENGTH_SHORT).show();
        });
        hobbyAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(FriendshipLevelData.this, "Hobby: " + item, Toast.LENGTH_SHORT).show();
        });
        professionAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(FriendshipLevelData.this, "Profession: " + item, Toast.LENGTH_SHORT).show();
        });
        fearsAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(FriendshipLevelData.this, "Fear: " + item, Toast.LENGTH_SHORT).show();
        });
        // Get user data from Firestore
        getUserData();
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}