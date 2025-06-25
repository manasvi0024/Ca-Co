package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();

                FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("userType")
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            String userType = snapshot.getValue(String.class);

                            if ("driver".equals(userType)) {
                                startActivity(new Intent(Splashscreen.this, DriverHome.class));
                            } else {
                                startActivity(new Intent(Splashscreen.this, MainHomePage.class));
                            }

                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error fetching user type", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Splashscreen.this, MainHomePage.class));
                            finish();
                        });

            } else {
                startActivity(new Intent(Splashscreen.this, firstpage.class));
                finish();
            }
        }, 2000); // Show splash screen for 2 seconds
    }
}
