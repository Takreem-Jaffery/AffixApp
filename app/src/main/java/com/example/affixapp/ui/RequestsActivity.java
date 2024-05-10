package com.example.affixapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.affixapp.Item;
import com.example.affixapp.R;
import com.example.affixapp.AdminAdapter;
import com.example.affixapp.utils.AndroidUtil;
import com.example.affixapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestsActivity extends AppCompatActivity implements AdminAdapter.RequestActionListener{

    private FirebaseFirestore db;
    public ArrayList<Item> items;

    private RecyclerView recyclerView; // Declare recyclerView as a field


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requests);
        // Process the retrieved documents
        items = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Find the home button
        ImageView homeButton = findViewById(R.id.homeButton);

        // Set OnClickListener for the home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ReportActivity
                Intent intent = new Intent(RequestsActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        // RecyclerView initialization
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Fetch documents from Firestore
        db.collection("requests")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String reportedUserID = document.getString("reportedUserID");
                            String reportedUserName = document.getString("reportedUserName");
                            String description = document.getString("description");
                            // Create Item object
                            Item item = new Item(reportedUserName, reportedUserID, description, R.drawable.oggy2);
                            items.add(item);
                        }
                        // Create the adapter instance with fetched data
                        AdminAdapter adapter = new AdminAdapter(getApplicationContext(), items, RequestsActivity.this);

                        // Set RecyclerView adapter after data is fetched
                        recyclerView.setAdapter(adapter);

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequestsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onApproveClicked(int position) {
        if (items == null) {
            // Handle null list case
            Toast.makeText(RequestsActivity.this, "Error!  Items list is Null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Access the Item object at the given position
        Item item = items.get(position);
        String reportedUserID = item.getUserID();

        // Perform a query to find the document with matching reportedUserID in "requests" collection
        db.collection("requests")
                .whereEqualTo("reportedUserID", reportedUserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Get the first document (assuming there's only one matching document)
                            DocumentSnapshot requestDocument = queryDocumentSnapshots.getDocuments().get(0);

                            // Delete the request document from the "requests" collection
                            requestDocument.getReference().delete();

                            // Fetch document from "users" collection with the reportedUserID
                            db.collection("users")
                                    .whereEqualTo("userId", reportedUserID)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot querySnapshot) {
                                            if (!querySnapshot.isEmpty()) {
                                                // Document found in "users" collection
                                                DocumentSnapshot userDocument = querySnapshot.getDocuments().get(0);
                                                // Extract fields from the document
                                                String name = userDocument.getString("username");
                                                long age = userDocument.getLong("age"); // Convert to long

                                                // Create a map to store the data
                                                Map<String, Object> reportedUserData = new HashMap<>();
                                                reportedUserData.put("username", name);
                                                reportedUserData.put("age", age); // Store as long
                                                Timestamp currentTimestamp = Timestamp.now();
                                                reportedUserData.put("createdTimestamp", currentTimestamp);

                                                // Add the data to the "reportedUsers" collection
                                                db.collection("reportedUsers").document(reportedUserID).set(reportedUserData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Document successfully added to "reportedUsers" collection
                                                                // Update the createdTimestamp field with the current timestamp
                                                                /*Timestamp currentTimestamp = Timestamp.now();
                                                                reportedUserData.put("createdTimestamp", currentTimestamp);*/

                                                                // Add the updated data to the "reportedUsers" collection
                                                                db.collection("reportedUsers").document(reportedUserID).set(reportedUserData)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                // Document successfully added to "reportedUsers" collection with updated timestamp
                                                                                // Now delete the document from "users" collection //NOOOO
                                                                                /*userDocument.getReference().delete()
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                // Document successfully deleted from "users" collection
                                                                                                Toast.makeText(RequestsActivity.this, "Request approved", Toast.LENGTH_SHORT).show();
                                                                                                // Remove the approved item from the list
                                                                                                items.remove(position);
                                                                                                // Notify the adapter that the data set has changed
                                                                                                recyclerView.getAdapter().notifyItemRemoved(position);
                                                                                                recyclerView.getAdapter().notifyItemRangeChanged(position, items.size());
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                // Error deleting document from "users" collection
                                                                                                Toast.makeText(RequestsActivity.this, "Failed to approve request", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });*/
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // Error adding document to "reportedUsers" collection with updated timestamp
                                                                                Toast.makeText(RequestsActivity.this, "Failed to update createdTimestamp", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Error adding document to "reportedUsers" collection
                                                                Toast.makeText(RequestsActivity.this, "Failed to approve request", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                // No matching document found in "users" collection
                                                Toast.makeText(RequestsActivity.this, "User document not found", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error querying "users" collection
                                            Toast.makeText(RequestsActivity.this, "Failed to fetch user document", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // No matching document found in "requests" collection
                            Toast.makeText(RequestsActivity.this, "No matching document found in requests", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error querying Firestore
                        Toast.makeText(RequestsActivity.this, "Failed to query Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    // Handle reject button click
    @Override
    public void onRejectClicked(int position) {
        if (items == null) {
            // Handle null list case
            Toast.makeText(RequestsActivity.this, "Error!  Items list is Null", Toast.LENGTH_SHORT).show();
            return;
        }
        // Access the Item object at the given position
        Item item = items.get(position);
        String reportedUserID = item.getUserID();

        // Perform a query to find the document with matching reportedUserID
        db.collection("requests")
                .whereEqualTo("reportedUserID", reportedUserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Check if there are any matching documents
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Delete the first matching document found
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            documentSnapshot.getReference().delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document successfully deleted
                                            Toast.makeText(RequestsActivity.this, "Request rejected", Toast.LENGTH_SHORT).show();
                                            // Remove the rejected item from the list
                                            items.remove(position);
                                            // Notify the adapter that the data set has changed
                                            recyclerView.getAdapter().notifyItemRemoved(position);
                                            recyclerView.getAdapter().notifyItemRangeChanged(position, items.size());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error deleting document
                                            Toast.makeText(RequestsActivity.this, "Failed to reject request", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // No matching documents found
                            Toast.makeText(RequestsActivity.this, "No matching document found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error querying Firestore
                        Toast.makeText(RequestsActivity.this, "Failed to query Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}