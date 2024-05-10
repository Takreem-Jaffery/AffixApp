package com.example.affixapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.affixapp.Person.Admin;
import com.example.affixapp.model.UserModel;
import com.example.affixapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentSnapshot;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    AppCompatSpinner spinner;

    private UserModel userModel;
    private Admin admin;
    private EditText signupEmail, signupPassword, signupUsername;
    private Button signupButton;
    private TextView loginRedirectText;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupUsername=findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        spinner = findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        final String[] selectedItem = new String[1];
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem[0] = parent.getItemAtPosition(position).toString();
                Log.d("Spinner", "Selected Item: " + selectedItem[0]);
                userType=selectedItem[0];
                /*handleSelectedItem(selectedItem[0]);*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItem[0] = null;
                /*userType=selectedItem[0];*/
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = signupUsername.getText().toString().trim();
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();


                if(username.isEmpty()){
                    signupUsername.setError("Username cannot be empty");
                }
                if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");

                }else {

                        /*auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                setDetails();
                            }
                        });*/
                    setDetails();
                }
                }

            });



    }
    private void handleSelectedItem(String selectedItem) {
        // Use the selected item here or trigger further actions
        userType = selectedItem;
    }
    void setDetails(){

        String username = signupUsername.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String pass = signupPassword.getText().toString().trim();
        String type=userType;
        Log.d("User Type","UserType="+type);

        if(type.equals("User")) {
            if (userModel != null) {
                userModel.setUsername(username);
            } else {
                userModel = new UserModel(email, username, Timestamp.now(),null, pass);
            }
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //userModel.setUserId(FirebaseUtil.currentUserId());
                            FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userModel.setUserId(FirebaseUtil.currentUserId());
                                        FirebaseUtil.currentUserDetails().set(userModel);
                                        Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle registration failure (e.g., email already in use)
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                signupEmail.setError("Email is already in use");
                            } else {
                                // Handle other registration failures
                                Toast.makeText(getApplicationContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        else if(type.equals("Admin")){
            if(admin!=null) {
                admin.setUsername(username);
            }
            else {
                admin= new Admin(username,pass,email);
            }
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            admin.setUserID(FirebaseUtil.currentUserId());
                            FirebaseUtil.currentAdminDetails().set(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                                    }
                                    else {
                                        Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle registration failure (e.g., email already in use)
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                signupEmail.setError("Email is already in use");
                            } else {
                                // Handle other registration failures
                                Toast.makeText(getApplicationContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(SignUpActivity.this, "Please Select a User Type", Toast.LENGTH_SHORT).show();
        }
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
            }
        });

    }

}