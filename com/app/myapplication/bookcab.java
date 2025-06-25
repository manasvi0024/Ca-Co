package com.app.myapplication;

import android.content.Intent;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class bookcab extends AppCompatActivity {

    private EditText fromLocation, toLocation, travelDate, requirements;
    private Button searchButton;
    private String selectedDate;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookcab);

        fromLocation = findViewById(R.id.editTextText3);
        toLocation = findViewById(R.id.editTextText4);
        travelDate = findViewById(R.id.editTextDate2);
        requirements = findViewById(R.id.editTextText5);
        searchButton = findViewById(R.id.button4);

        databaseReference = FirebaseDatabase.getInstance().getReference("cab_bookings");

        travelDate.setOnClickListener(v -> showDatePicker());

        searchButton.setOnClickListener(v -> {
            bookCab();
            String frloc = fromLocation.getText().toString().trim();

            if (frloc.isEmpty()) {
                Toast.makeText(bookcab.this, "Please enter a pickup location!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(bookcab.this, DriverMatchingActivity.class);
            intent.putExtra("location", frloc);
            startActivity(intent);
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
                    travelDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void bookCab() {
        String from = fromLocation.getText().toString().trim();
        String to = toLocation.getText().toString().trim();
        String date = travelDate.getText().toString().trim();
        String req = requirements.getText().toString().trim();

        if (from.isEmpty() || to.isEmpty() || date.isEmpty() || req.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String bookingId = databaseReference.push().getKey();
        HashMap<String, String> bookingDetails = new HashMap<>();
        bookingDetails.put("from", from);
        bookingDetails.put("to", to);
        bookingDetails.put("date", date);
        bookingDetails.put("requirements", req);
        bookingDetails.put("bookingId", bookingId);
        bookingDetails.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        if (bookingId != null) {
            databaseReference.child(bookingId).setValue(bookingDetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(bookcab.this, "Cab booked successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(bookcab.this, "Failed to book cab", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
