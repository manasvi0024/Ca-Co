package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    private EditText inputUsername, inputEmail, inputPassword, inputConfirmPassword;
    private Button btnRegister;
    private TextView alreadyHaveAcc;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth & Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Link UI elements
        inputUsername = findViewById(R.id.inputUser2);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPass2);
        inputConfirmPassword = findViewById(R.id.inputconfirm);
        btnRegister = findViewById(R.id.btnregister);
        alreadyHaveAcc = findViewById(R.id.alreadyhaveacc);

        // Register button click listener
        btnRegister.setOnClickListener(view -> registerUser());

        // Navigate to login if the user already has an account
        alreadyHaveAcc.setOnClickListener(view -> {
            startActivity(new Intent(register.this, login.class));
            finish();
        });
    }

    private void registerUser() {
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(username)) {
            inputUsername.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email is required");
            return;
        }
        if (!email.endsWith("@banasthali.in")) {
            inputEmail.setError("Only Banasthali University students can register!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Password is required");
            return;
        }
        if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Get User ID from Firebase Authentication
                            String userId = firebaseUser.getUid();

                            // Create user object
                            User user = new User(username, email);

                            // Store user data in Firebase Realtime Database
                            databaseReference.child(userId).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(register.this, login.class));
                                            finish();
                                        } else {
                                            Toast.makeText(register.this, "Database Error: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(register.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // User model class
    public static class User {
        public String username, email;

        public User() {
            // Default constructor required for Firebase
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}
