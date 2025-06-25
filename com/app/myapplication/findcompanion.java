package com.app.myapplication;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class findcompanion extends AppCompatActivity {

    private EditText editTextDate, editTextTime, editTextLocation, editTextModeOfTravel;
    private Button searchButton;
    private String selectedDate;
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findcompanion);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("companion_requests");

        // Initialize UI elements
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextLocation = findViewById(R.id.editTextText);
        editTextModeOfTravel = findViewById(R.id.editTextText2);
        searchButton = findViewById(R.id.button3);

        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCompanionRequest();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editTextDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Open TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                findcompanion.this,
                (view, selectedHour, selectedMinute) -> {
                    // Format the time (e.g., 08:30 AM)
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    editTextTime.setText(formattedTime);
                },
                hour,
                minute,
                true // Use 24-hour format (change to false for AM/PM format)
        );

        timePickerDialog.show();
    }

    private void saveCompanionRequest() {
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String modeOfTravel = editTextModeOfTravel.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || location.isEmpty() || modeOfTravel.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String requestId = databaseReference.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CompanionRequest request = new CompanionRequest(date, time, location, modeOfTravel, userId);

        if (requestId != null) {
            databaseReference.child(requestId).setValue(request).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(findcompanion.this, "Request submitted successfully!", Toast.LENGTH_SHORT).show();
                    editTextDate.setText("");
                    editTextTime.setText("");
                    editTextLocation.setText("");
                    editTextModeOfTravel.setText("");
                } else {
                    Toast.makeText(findcompanion.this, "Failed to submit request", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Pass data to CompanionResult
        Intent intent = new Intent(findcompanion.this, CompanionMatchingActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("location", location);
        intent.putExtra("mode_of_travel", modeOfTravel);
        startActivity(intent);
    }

}