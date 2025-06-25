package com.app.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainHomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Redirect to login if no user is logged in
        if (currentUser == null) {
            startActivity(new Intent(MainHomePage.this, login.class));
            finish();
            return;
        }

        ImageButton bookCabButton = findViewById(R.id.imageButton6);
        ImageButton findCompanionButton = findViewById(R.id.imageButton7);
        Button logoutButton = findViewById(R.id.button9);
        ImageButton menuButton = findViewById(R.id.menubutton);

        // Navigate to Book Cab Page
        bookCabButton.setOnClickListener(v -> startActivity(new Intent(MainHomePage.this, bookcab.class)));

        // Navigate to Find Companion Page
        findCompanionButton.setOnClickListener(v -> startActivity(new Intent(MainHomePage.this, findcompanion.class)));

        // Logout and redirect to Login
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();

            // Redirect to the first page (user chooses Student/Driver again)
            Intent intent = new Intent(MainHomePage.this, firstpage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Optional: clear backstack
            startActivity(intent);
            finish();
        });


        // Menu button to show user details
        menuButton.setOnClickListener(v -> showUserDetailsDialog());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to login page if user is not logged in
            startActivity(new Intent(MainHomePage.this, login.class));
            finish();
        }
        String uid = currentUser.getUid();

        // Check if the user is a driver
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("drivers").child(uid);
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // It's a driver → redirect to DriverHomePage
                    startActivity(new Intent(MainHomePage.this, DriverHome.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainHomePage.this, "Error checking user role", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserDetailsDialog() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid(); // Get current user UID

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("username").getValue(String.class); // Fetch username
                    String email = snapshot.child("email").getValue(String.class); // Fetch email

                    // Show user details in dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainHomePage.this);
                    View customView = getLayoutInflater().inflate(R.layout.dialog_user_details, null);
                    builder.setView(customView);

                    TextView userName = customView.findViewById(R.id.user_name);
                    TextView userEmail = customView.findViewById(R.id.user_email);

                    userName.setText(name != null ? "Name: " + name : "Name: N/A");
                    userEmail.setText(email != null ? "Email: " + email : "Email: N/A");

                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainHomePage.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
