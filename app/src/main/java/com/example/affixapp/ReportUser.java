package com.example.affixapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReportUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);

        // Get the reported person's name from the intent (assume it's passed as an extra)
        String currentUserID = getIntent().getStringExtra("keyUserID");
        String reportedUserID = getIntent().getStringExtra("keyReportedUserID");
        String reportedUsername = getIntent().getStringExtra("keyReportedUsername");

        // Display the reported person's name in a TextView (assuming you have a TextView with id 'reportedUserNameTextView')
        TextView reportedUserNameEditText = findViewById(R.id.reportedUsernameEditText);
        reportedUserNameEditText.setText(reportedUsername);

        // Populate the dropdown (Spinner) with report descriptions
        Spinner spinner = findViewById(R.id.reportDescriptionSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.report_descriptions_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final String[] selectedItem = new String[1];
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItem[0] = null;
            }
        });

        Button submitReportBtn = findViewById(R.id.submitBtn);
        submitReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check that a reason has been submitted
                if (selectedItem[0] == null) {
                    // If no reason is selected, display a toast and return
                    Toast.makeText(getApplicationContext(), "Please select a reason.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if block is selected and insert into database
                SwitchCompat blockUserSwitch = findViewById(R.id.blockSwitch);
                boolean isChecked = blockUserSwitch.isChecked();

                try{

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    if (isChecked) {
                        Map<String, Object> blockListEntry = new HashMap<>();
                        blockListEntry.put("userID", currentUserID);
                        blockListEntry.put("blockedUserID", reportedUserID);

                        db.collection("blocklist")
                                .add(blockListEntry)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
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
                    }

                    // add report to database
                    Map<String, Object> request = new HashMap<>();
                    request.put("userID", currentUserID);
                    request.put("reportedUserID", reportedUserID);
                    request.put("reportedUserName", reportedUsername);
                    request.put("description", selectedItem[0]);

                    db.collection("requests")
                            .add(request)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(), "Report Submitted.", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Request added with ID: " + documentReference.getId());
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Log.w(TAG, "Error adding request", e);
                                    finish();
                                }
                            });
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}