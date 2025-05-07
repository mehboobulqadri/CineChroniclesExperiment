package com.mehboob.cinechroniclesexperiment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mehboob.cinechroniclesexperiment.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up click listeners
        setupClickListeners();

        // Set up animations
        setupAnimations();

        // Check if user is already logged in
        checkCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly
        checkCurrentUser();
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, go to HomeActivity
            navigateToHome();
        }
    }

    private void setupClickListeners() {
        // Login button click listener
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                binding.loginProgressBar.setVisibility(View.VISIBLE);
                loginUser(email, password);
            }
        });

        // Register text click listener
        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Google login button click listener
        binding.btnGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Google login will be implemented in future versions",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void setupAnimations() {
        // Start the movie reel animation
        binding.movieReelAnimation.playAnimation();
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            binding.etEmail.setError("Email cannot be empty");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password cannot be empty");
            isValid = false;
        }

        return isValid;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    binding.loginProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        navigateToHome();
                    } else {
                        // If sign in fails, display a message to the user.
                        String errorMessage = "Authentication failed";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                        }
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHome() {
        try {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Ensure MainActivity is finished
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error navigating to Home: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
