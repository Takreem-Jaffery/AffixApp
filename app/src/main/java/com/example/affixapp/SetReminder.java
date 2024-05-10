package com.example.affixapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SetReminder extends AppCompatActivity {

    private EditText editTextReminderName, editTextReminderDescription;
    private Button btnDatePicker, btnTimePicker, btnSetReminder;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private FirebaseFirestore db;
    private FirebaseUser currentUser; // Current logged-in user
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get the current logged-in user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        editTextReminderName = findViewById(R.id.editTextReminderName);
        editTextReminderDescription = findViewById(R.id.editTextReminderDescription);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnTimePicker = findViewById(R.id.btnTimePicker);
        btnSetReminder = findViewById(R.id.btnSetReminder);

        btnDatePicker.setOnClickListener(v -> showDatePickerDialog());
        btnTimePicker.setOnClickListener(v -> showTimePickerDialog());
        btnSetReminder.setOnClickListener(v -> setReminder());

        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> {
            Intent intent= new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(SetReminder.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(SetReminder.this,
                (view, hourOfDay, minute) -> {
                    mHour = hourOfDay;
                    mMinute = minute;
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void setReminder() {
        String reminderName = editTextReminderName.getText().toString().trim();
        String reminderDescription = editTextReminderDescription.getText().toString().trim();

        if (reminderName.isEmpty() || reminderDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String reminderDate = mDay + "/" + (mMonth + 1) + "/" + mYear;
        String reminderTime = mHour + ":" + mMinute;

        Map<String, Object> reminder = new HashMap<>();
        reminder.put("name", reminderName);
        reminder.put("description", reminderDescription);
        reminder.put("date", reminderDate);
        reminder.put("time", reminderTime);
        reminder.put("userId", currentUser.getUid()); // Save current user's ID

        db.collection("reminders")
                .add(reminder)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        scheduleAlarm(this, reminderName, reminderDescription, mYear, mMonth, mDay, mHour, mMinute);
                        Toast.makeText(SetReminder.this, "Reminder added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SetReminder.this, "Failed to add reminder", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void scheduleAlarm(Context context, String reminderName, String reminderDescription,
                               int year, int month, int day, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("name", reminderName);
        intent.putExtra("description", reminderDescription);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}