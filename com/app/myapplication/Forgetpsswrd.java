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

public class Forgetpsswrd extends AppCompatActivity {

    private EditText emailInput;
    private Button resetPasswordButton;
    private TextView backToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpsswrd); // Ensure this matches your XML filename

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Link UI elements
        emailInput = findViewById(R.id.editTextText6); // Email input field
        resetPasswordButton = findViewById(R.id.button5); // Reset Password button
        backToLogin = findViewById(R.id.textView5); // Back to Login link

        // Reset Password Button Click
        resetPasswordButton.setOnClickListener(v -> resetPassword());

        // Back to Login Click
        backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Forgetpsswrd.this, login.class);
            startActivity(intent);
            finish(); // Close Forget Password page after navigation
        });
    }

    private void resetPassword() {
        String email = emailInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Enter your registered email");
            return;
        }

        // Send password reset email
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Forgetpsswrd.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Forgetpsswrd.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
