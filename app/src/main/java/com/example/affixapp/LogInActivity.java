package com.example.affixapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.affixapp.Person.Admin;
import com.example.affixapp.model.UserModel;
import com.example.affixapp.ui.ReportActivity;
import com.example.affixapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Time;


public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private UserModel userModel;
    private Admin admin;
    protected EditText loginEmail, loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth=FirebaseAuth.getInstance();
        loginEmail= findViewById(R.id.login_email);
        loginPassword= findViewById(R.id.login_password);
        loginButton= findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Timestamp LastSignIn=Timestamp.now();
                                        FirebaseUtil firebaseUtil = new FirebaseUtil();
                                        final Boolean[] suspendedOrReported = {false};
                                        //Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        firebaseUtil.isAdmin(FirebaseUtil.currentUserId())
                                                .addOnSuccessListener(isAdmin -> {
                                                    if (isAdmin) {
                                                        Log.d(TAG, "User " + FirebaseUtil.currentUserId() + " is an admin.");
                                                        Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(LogInActivity.this, ReportActivity.class));
                                                        finish();
                                                    } else {
                                                        firebaseUtil.isReported(FirebaseUtil.currentUserId()).addOnSuccessListener(isReported->{
                                                            if(isReported) {
                                                                Toast.makeText(LogInActivity.this, "This account is under review.", Toast.LENGTH_SHORT).show();
                                                                suspendedOrReported[0] =true;
                                                                startActivity(new Intent(LogInActivity.this, LogInActivity.class));
                                                                finish();
                                                            }
                                                            else{

                                                                suspendedOrReported[0] =false;
                                                            }
                                                        });
                                                        if(suspendedOrReported[0]==false) {
                                                            firebaseUtil.isSuspended(FirebaseUtil.currentUserId()).addOnSuccessListener(isSuspended -> {
                                                                if (isSuspended) {
                                                                    Toast.makeText(LogInActivity.this, "This account has been suspended", Toast.LENGTH_SHORT).show();
                                                                    suspendedOrReported[0]=true;
                                                                    startActivity(new Intent(LogInActivity.this, LogInActivity.class));
                                                                    finish();
                                                                } else {
                                                                    suspendedOrReported[0]=false;
                                                                }
                                                            });
                                                        }
                                                        if(suspendedOrReported[0]!=true) {
                                                            Log.d(TAG, "User " + FirebaseUtil.currentUserId() + " is not an admin.");
                                                            FirebaseUtil.currentUserDetails().update("LastSignIn", LastSignIn)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Log.d("UserDetailsUpdate", "Last sign-in timestamp updated successfully.");
                                                                        // Handle success if needed
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Log.e("UserDetailsUpdate", "Error updating last sign-in timestamp: " + e.getMessage());
                                                                        // Handle error if needed
                                                                    });
                                                            startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                                            finish();
                                                        }

                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error checking admin status: ", e);
                                                    // Handle error
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LogInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        loginPassword.setError("Empty fields are not allowed");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("Empty fields are not allowed");
                } else {
                    loginEmail.setError("Please enter correct email");
                }
            }
        });
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        });
    }
}
