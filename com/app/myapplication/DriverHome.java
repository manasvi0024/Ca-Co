package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DriverHome extends AppCompatActivity {

    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home); // Make sure this matches your layout name

        logoutBtn = findViewById(R.id.button6);

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Logout from Firebase
            Intent intent = new Intent(DriverHome.this, firstpage.class); // Go back to login screen
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
            startActivity(intent);
            finish();
        });
    }
}
