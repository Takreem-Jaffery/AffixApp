package com.example.affixapp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract reminder name and description from the intent extras
        String reminderName = intent.getStringExtra("name");
        String reminderDescription = intent.getStringExtra("description");

        // Construct the toast message with reminder name and description
        String toastMessage = "Reminder triggered!\nName: " + reminderName + "\nDescription: " + reminderDescription;

        // Show the toast message
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }
}