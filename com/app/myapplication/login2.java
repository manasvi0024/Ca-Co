package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class login2 extends AppCompatActivity {

    private EditText inputname, inputcontact, inputvehicle, inputseat, inputlocation, inputOtp;
    private Button btnSendOtp, btnVerifyOtp;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("drivers");

        inputname = findViewById(R.id.editTextText7);
        inputcontact = findViewById(R.id.editTextPhone);
        inputvehicle = findViewById(R.id.editTextText9);
        inputseat = findViewById(R.id.editTextText10);
        inputlocation = findViewById(R.id.editTextText11);
        inputOtp = findViewById(R.id.editTextOTP);
        btnSendOtp = findViewById(R.id.buttonSendOTP);
        btnVerifyOtp = findViewById(R.id.buttonVerifyOTP);

        btnSendOtp.setOnClickListener(v -> sendVerificationCode());
        btnVerifyOtp.setOnClickListener(v -> verifyCode());
    }

    private void sendVerificationCode() {
        String phone = inputcontact.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            inputcontact.setError("Enter phone number");
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(login2.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(@NonNull String verId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(verId, token);
                    verificationId = verId;
                    Toast.makeText(login2.this, "OTP sent", Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode() {
        String code = inputOtp.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            inputOtp.setError("Enter OTP");
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveDriverDetails(); // Save details in database
            } else {
                Toast.makeText(login2.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDriverDetails() {
        String name = inputname.getText().toString().trim();
        String phone = inputcontact.getText().toString().trim();
        String vehicle = inputvehicle.getText().toString().trim();
        String seat = inputseat.getText().toString().trim();
        String location = inputlocation.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(vehicle) || TextUtils.isEmpty(seat) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Driver driver = new Driver(name, phone, vehicle, Integer.parseInt(seat), location);
        String userId = mAuth.getCurrentUser().getUid();

        databaseReference.child(userId).setValue(driver)
                .addOnSuccessListener(unused -> {
                    // 👉 Store userType as 'driver'
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(userId)
                            .child("userType")
                            .setValue("driver");

                    Toast.makeText(this, "Driver Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(login2.this, DriverHome.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to Save Details", Toast.LENGTH_SHORT).show());
    }
}
