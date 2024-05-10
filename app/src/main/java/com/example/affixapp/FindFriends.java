package com.example.affixapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.affixapp.Person.User;
import com.example.affixapp.model.UserModel;
import com.example.affixapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class FindFriends {
    private final Context context;
    private final String ID_userLoggedIn;
    List<String> userIDList;
    List<User> userSnapshotList;
    int position;

    public FindFriends(Context context, String ID_userLoggedIn) {
        this.context=context;
        this.ID_userLoggedIn = ID_userLoggedIn;
        userIDList= new ArrayList<>();
        userSnapshotList = new ArrayList<>();
        position = 0;
    }
    public void createTemporaryTable(int noOfClusters){
        try {
            //----------------------FIND CLUSTERS--------------------------
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("users.arff");
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(inputStream);
            Instances data = source.getDataSet();

            // Print dataset summary
            Log.d(TAG, data.toSummaryString());

            // clustering set according to dataset
            SimpleKMeans model = new SimpleKMeans();
            model.setNumClusters(noOfClusters);
            model.buildClusterer(data);
            Log.d(TAG, model.toString());

            ClusterEvaluation clsEval = new ClusterEvaluation();
            clsEval.setClusterer(model);
            clsEval.evaluateClusterer(data);
            Log.d(TAG, String.valueOf(clsEval.getNumClusters()));

            // instance belongs to cluster x
            for (int i = 0; i < data.numInstances(); i++) {
                int cluster = model.clusterInstance(data.instance(i));
                Log.d(TAG, ("Instance " + i + " is in cluster " + cluster));
            }

            //-----------------GET CURRENT DATABASE STATE---------------------------
            // firebase ref has same list of users as users.arff
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Log.d(TAG, "(1) reached single value event");
                    // position indicates which instance no. our user is
                    int iterate = 0;

                    //------------------CREATE LIST OF USER SNAPSHOTS---------------------------
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // the first userID must map onto the first instance of users.arff
                        UserModel usermodel = userSnapshot.getValue(UserModel.class);
                        User user=new User( usermodel.getUsername(), usermodel.getPassword(), usermodel.getEmail());
                        user.setAge(usermodel.getAge());
                        user.setFears(usermodel.getFears());
                        user.setGender(usermodel.getGender());
                        user.setHobby(usermodel.getHobby());
                        user.setProfession(usermodel.getProfession());
                        user.setZodiacSign(usermodel.getZodiacSign());
                        user.setUserId(usermodel.getUserId());
                        //lets try
                        String profilepic=FirebaseUtil.getOtherProfilePicStorageRef(usermodel.getUserId()).getDownloadUrl().toString();
                        user.setProfilePhoto(profilepic);
                        //
                        userIDList.add(userSnapshot.getKey());
                        userSnapshotList.add(user);
                        if (Objects.equals(userSnapshot.getKey(), ID_userLoggedIn)) {
                            position = iterate;
                            Log.d(TAG, "user[position] in database-->" + position);
                        }
                        iterate++;
                    }

                    //------------------FIND FRIEND INSTANCES---------------------------
                    try {
                        int userCluster = model.clusterInstance(data.instance(position));
                        Log.d(TAG, "user[position] cluster# --> " + userCluster);

                        // now iterate through each instance
                        // and find instances who have same clusters as our user
                        int i;
                        for (i = 0; i < data.numInstances(); i++) {
                            int friendCluster = model.clusterInstance(data.instance(i));
                            if (friendCluster == userCluster && i != position) {
                                Log.d(TAG, "found a friend");
                                Log.d(TAG, "findCluster->" + friendCluster + ", friendInstance->" + i);
                                Log.d(TAG, "userCluster->" + userCluster + "userInstance->" + position);

                                // instance [i] is a friend of user
                                // make a temp table for them
                                FirebaseDatabase.getInstance().getReference("temporary")
                                        .child(userIDList.get(i)).setValue(userSnapshotList.get(i));
                                Log.d(TAG, "friends key -->" + userIDList.get(i));


                            }
                        }
                    }catch(Exception e){
                        Log.e(TAG, "Error", e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error getting user IDs", error.toException());
                }
            });


        }
        catch(Exception e){
            Log.e(TAG, "Error", e);
        }
    }

    public void removeTemporaryTable(){
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("temporary");

        tempRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Deletion successful
                        Log.d(TAG, "All children under 'temporary' deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete
                        Log.e(TAG, "Failed to delete all children under 'temporary': " + e.getMessage());
                    }
                });
    }
}
