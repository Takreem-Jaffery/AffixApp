package com.example.affixapp.ui.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.affixapp.MainActivity;
import com.example.affixapp.R;
import com.example.affixapp.ReminderAdapter;
import com.example.affixapp.ReminderDecoration;
import com.example.affixapp.ReminderItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewReminderActivity extends AppCompatActivity  {

    private FirebaseFirestore db;
    public ArrayList<ReminderItem> items;

    private RecyclerView recyclerView; // Declare recyclerView as a field
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_reminder);

        // Process the retrieved documents
        items = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Find the home button
        ImageView homeButton = findViewById(R.id.homeButton1);

        // Set OnClickListener for the home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ReportActivity
                Intent intent = new Intent(ViewReminderActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // RecyclerView initialization
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add item decoration with desired vertical space between items (e.g., 20dp)
        int verticalSpaceHeight = getResources().getDimensionPixelSize(R.dimen.vertical_space_height);
        recyclerView.addItemDecoration(new ReminderDecoration(verticalSpaceHeight));

        // Fetch documents from Firestore
        db.collection("reminders")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String date = document.getString("date");
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String time= document.getString("time");
                            // Create Item object
                            ReminderItem item = new ReminderItem("Date: "+ date,"Time: "+ time ,"TaskName: "+ name,"Description: "+ description);
                            items.add(item);
                        }
                        // Create the adapter instance with fetched data
                        ReminderAdapter adapter = new ReminderAdapter(getApplicationContext(), items);

                        // Set RecyclerView adapter after data is fetched
                        recyclerView.setAdapter(adapter);

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewReminderActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
