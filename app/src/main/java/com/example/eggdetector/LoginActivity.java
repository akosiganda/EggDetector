package com.example.eggdetector;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private int loginAttempts = 0;
    private long lastAttemptTime = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOGIN_ATTEMPT_INTERVAL = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendUserToDashboardActivity();
            return;
        }

        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        Button btnSignUp = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);

        TextView forgotPassword = findViewById(R.id.textForgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword.setTypeface(null, Typeface.BOLD);
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        TextView createAccount = findViewById(R.id.textCreateAccount);

        createAccount.setOnClickListener(v -> {
            createAccount.setTypeface(null, Typeface.BOLD);
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                long currentTime = System.currentTimeMillis();
                long timeSinceLastAttempt = currentTime - lastAttemptTime;

                if (timeSinceLastAttempt < LOGIN_ATTEMPT_INTERVAL) {
                    Toast.makeText(LoginActivity.this, "Please wait for 1 minute before another attempt.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                    Toast.makeText(LoginActivity.this, "Too many failed attempts. Please wait for 1 minute.", Toast.LENGTH_SHORT).show();
                    lastAttemptTime = currentTime;
                    return;
                } else if (!email.matches(emailPattern)) {
                    inputEmail.setError("Enter a valid email");
                    loginAttempts++;
                } else if (password.isEmpty() || password.length() < 6) {
                    inputPassword.setError("Enter a valid password (min 6 characters)");
                    loginAttempts++;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            loginAttempts = 0;
                            sendUserToNextActivity();
                        } else {
                            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                            if (errorMessage.contains("There is no user record")) {
                                Toast.makeText(LoginActivity.this, "Email not registered", Toast.LENGTH_SHORT).show();
                            } else if (errorMessage.contains("The password is invalid")) {
                                loginAttempts++;
                                int attemptsRemaining = MAX_LOGIN_ATTEMPTS - loginAttempts;
                                if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                                    Toast.makeText(LoginActivity.this, "Too many failed attempts. Please wait for 1 minute.", Toast.LENGTH_SHORT).show();
                                    lastAttemptTime = currentTime;
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid password (Attempts remaining: " + attemptsRemaining + ")", Toast.LENGTH_SHORT).show();
                                }
                            } else if (errorMessage.contains("Network error")) {
                                Toast.makeText(LoginActivity.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            private void sendUserToNextActivity() {
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                finishAffinity();
            }
        });
    }
    private void sendUserToDashboardActivity() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
        finishAffinity();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}