package com.app.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class DriverMatchingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DrivAdapter driverAdapter;
    private List<Driver> driverList;
    private String pickupLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_matching);

        pickupLocation = getIntent().getStringExtra("pickupLocation"); // optional location filter

        recyclerView = findViewById(R.id.recyclerViewDrivers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        driverList = new ArrayList<>();
        driverAdapter = new DrivAdapter(driverList);
        recyclerView.setAdapter(driverAdapter);

        fetchDrivers();
    }

    private void fetchDrivers() {
        DatabaseReference driversRef = FirebaseDatabase.getInstance().getReference("drivers");

        driversRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driverList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    if (driver != null) {
                        Log.d("DriverMatch", "Loaded driver: " + driver.getName());
                        driverList.add(driver);
                    } else {
                        Log.d("DriverMatch", "Null driver found");
                    }
                }

                if (driverList.isEmpty()) {
                    Toast.makeText(DriverMatchingActivity.this, "No drivers found", Toast.LENGTH_SHORT).show();
                }

                driverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverMatchingActivity.this, "Failed to load drivers: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
}
