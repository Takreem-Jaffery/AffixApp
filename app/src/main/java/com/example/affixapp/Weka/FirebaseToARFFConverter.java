package com.example.affixapp.Weka;
/*
import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/

public class FirebaseToARFFConverter {

    /*public static void main(String[] args) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("output.arff"));
                    writer.write("@relation FriendshipData\n\n");

                    // Write attributes
                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                    DataSnapshot friendshipData = firstChild.child("friendship");
                    Iterator<DataSnapshot> iter = friendshipData.getChildren().iterator();
                    while (iter.hasNext()) {
                        String attribute = iter.next().getKey();
                        writer.write("@attribute " + attribute + " string\n");
                    }
                    writer.write("\n@data\n");

                    // Write instances
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot friendshipSnapshot = userSnapshot.child("friendship");
                        StringBuilder instance = new StringBuilder();
                        iter = friendshipSnapshot.getChildren().iterator();
                        while (iter.hasNext()) {
                            instance.append(iter.next().getValue());
                            if (iter.hasNext()) {
                                instance.append(",");
                            }
                        }
                        writer.write(instance.toString() + "\n");
                    }

                    writer.close();
                    Log.d(TAG, "databse converted succesesfully into arff");
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, String.valueOf(databaseError.getCode()));
            }
        });
    }*/
}
