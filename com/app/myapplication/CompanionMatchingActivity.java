package com.app.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CompanionMatchingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CompanionAdapter companionAdapter;
    private List<CompanionRequest> companionList;
    private String date, time, location, modeOfTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion_matching);

        // Get data passed from findcompanion activity
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        location = getIntent().getStringExtra("location");
        modeOfTravel = getIntent().getStringExtra("mode_of_travel");

        recyclerView = findViewById(R.id.recyclerViewCompanions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        companionList = new ArrayList<>();
        companionAdapter = new CompanionAdapter(this,companionList);
        recyclerView.setAdapter(companionAdapter);

        fetchCompanions();
    }

    private void fetchCompanions() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("companion_requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companionList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        CompanionRequest request = dataSnapshot.getValue(CompanionRequest.class);
                        if (request != null &&
                                request.getDate().equals(date) &&
                                request.getLocation().equals(location) &&
                                request.getModeOfTravel().equals(modeOfTravel)) {

                            // Fetch the user's name from the 'users' node using userId
                            fetchUserName(request);
                        }
                    } catch (Exception e) {
                        Log.e("CompanionMatch", "Invalid entry found", e);
                    }
                }
                //if (companionList.isEmpty()) {
                   //Toast.makeText(CompanionMatchingActivity.this, "No matching companions found", Toast.LENGTH_SHORT).show();
                //}

                //companionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CompanionMatchingActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Fetch user name based on the userId in the CompanionRequest
    private void fetchUserName(CompanionRequest request) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(request.getUserId());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("username").getValue(String.class);
                    request.setUserName(userName);  // Add name to the request
                    companionList.add(request);
                    companionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CompanionMatchingActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
