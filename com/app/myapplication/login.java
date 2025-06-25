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
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView forgotPassword, signUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // 🔥 If already logged in → go to home directly
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(login.this, Home.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Link UI elements
        inputEmail = findViewById(R.id.inputUser1); // Use Email for login
        inputPassword = findViewById(R.id.inputPass1);
        btnLogin = findViewById(R.id.btnlogin);
        forgotPassword = findViewById(R.id.forgo);
        signUp = findViewById(R.id.signup);

        // Login button click listener
        btnLogin.setOnClickListener(view -> loginUser());

        // Navigate to SignUpActivity
        signUp.setOnClickListener(view -> {
            startActivity(new Intent(login.this, register.class));
            finish();
        });

        // Handle forgot password
        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(login.this, Forgetpsswrd.class));
        });
    }

    private void loginUser() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email is required");
            return;
        }
        if (!email.endsWith("@banasthali.in")) {
            inputEmail.setError("Only Banasthali University students can login!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Password is required");
            return;
        }

        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();

                        // Get userType from "users" node
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(userId)
                                .child("userType")
                                .get()
                                .addOnSuccessListener(dataSnapshot -> {
                                    String userType = dataSnapshot.getValue(String.class);
                                    if (userType != null && userType.equals("driver")) {
                                        startActivity(new Intent(login.this, DriverHome.class));
                                    } else {
                                        startActivity(new Intent(login.this, MainHomePage.class)); // for student
                                    }
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(login.this, "Failed to fetch user type", Toast.LENGTH_SHORT).show();
                                });
                    }
                    else {
                        Toast.makeText(login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
