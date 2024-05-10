package com.example.affixapp.ui.model;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.affixapp.R;
import com.example.affixapp.ui.ReportActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ActiveUser2 extends AppCompatActivity {

    private LineChart lineChart;
    private Button btnBack;
    private Button btnHome;
    private List<String> xValues;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_active_user2);

        //Initializing Buttons
        btnBack=findViewById(R.id.backButton5);
        btnHome=findViewById(R.id.homeButton2);

        // Initialize FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();

        // Initialize lineChart by finding it using findViewById()
        lineChart = findViewById(R.id.chart6);

        Description description = new Description();
        description.setText("Monthly Active Users Report");
        description.setPosition(150f, 15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        xValues = Arrays.asList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(12); // 12 months in a year
        xAxis.setGranularity(1f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(Color.BLACK);
        yAxis.setLabelCount(10);

// Count new users and populate the graph
        countNewUsers();


        //Setup Listeners for Both Buttons
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//This will take you to the NewUser Page(Weekly report page)
                finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect to ReportActivity Page(where the three buttons are located)
                Intent intent=new Intent(ActiveUser2.this, ReportActivity.class);
                startActivity(intent);
            }
        });

    }

    private void countNewUsers() {
        int[] newUsersCount = new int[12]; // 12 months in a year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JANUARY); // Start from January
        AtomicInteger queryCounter = new AtomicInteger(0); // Counter to track completed queries

        for (int i = 0; i < 12; i++) {
            final int queryCount = i; // Create a final copy of i
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set day to 1st of the month
            calendar.set(Calendar.MONTH, i); // Set month
            // Set start and end date for the current month
            Date startDate = setStartOfDay(calendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // Set to last day of the month
            Date endDate = setEndOfDay(calendar.getTime());

            db.collection("users")
                    .whereGreaterThanOrEqualTo("LastSignIn", startDate)
                    .whereLessThan("LastSignIn", endDate)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size(); // Number of new users for the current month
                            newUsersCount[queryCount] = count; // Assign count to the appropriate index
                            if (queryCounter.incrementAndGet() == 12) { // Check if all queries have completed
                                updateGraph(newUsersCount);
                            }
                        } else {
                            // Handle errors (e.g., network issues)
                            Log.e("ActiveUser", "Error fetching users:", task.getException());
                        }
                    });
        }
    }

    private void updateGraph(int[] newUsersCount) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < newUsersCount.length; i++) {
            entries.add(new Entry(i, newUsersCount[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Active Users");
        dataSet.setColor(Color.BLUE);

        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    // Helper method to set the start of the day (00:00:00.000)
    private Date setStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Helper method to set the end of the day (23:59:59.999)
    private Date setEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

}