package com.mehboob.cinechroniclesexperiment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mehboob.cinechroniclesexperiment.databinding.ActivityRegisterBinding;
import com.mehboob.cinechroniclesexperiment.models.User;
import com.mehboob.cinechroniclesexperiment.utils.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private PreferenceManager preferenceManager;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize calendar and date formatter
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        // Initialize preference manager
        preferenceManager = new PreferenceManager(this);

        // Set up click listeners
        setupClickListeners();

        // Set up text watchers
        setupTextWatchers();
    }

    private void setupClickListeners() {
        // Register button click listener
        binding.btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                binding.registerProgressBar.setVisibility(View.VISIBLE);
                registerUser();
            }
        });

        // Login text click listener
        binding.tvLogin.setOnClickListener(v -> finish());

        // Date of birth field click listener
        binding.etDob.setOnClickListener(v -> showDatePickerDialog());
    }

    private void setupTextWatchers() {
        // Password strength indicator
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updatePasswordStrengthIndicator(s.toString());
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    binding.etDob.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate first name
        if (binding.etFirstName.getText().toString().trim().isEmpty()) {
            binding.etFirstName.setError("First name cannot be empty");
            isValid = false;
        }

        // Validate last name
        if (binding.etLastName.getText().toString().trim().isEmpty()) {
            binding.etLastName.setError("Last name cannot be empty");
            isValid = false;
        }

        // Validate phone
        if (binding.etPhone.getText().toString().trim().isEmpty()) {
            binding.etPhone.setError("Phone cannot be empty");
            isValid = false;
        }

        // Validate email
        String email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Enter a valid email address");
            isValid = false;
        }

        // Validate username
        if (binding.etUsername.getText().toString().trim().isEmpty()) {
            binding.etUsername.setError("Username cannot be empty");
            isValid = false;
        }

        // Validate password
        String password = binding.etPassword.getText().toString().trim();
        if (password.isEmpty() || password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // Validate confirm password
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        if (!confirmPassword.equals(password)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        // Validate date of birth
        if (binding.etDob.getText().toString().trim().isEmpty()) {
            binding.etDob.setError("Date of birth cannot be empty");
            isValid = false;
        }

        return isValid;
    }

    private void registerUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        Log.d("FirebaseDebug", "Starting user registration with email: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();  // your updated line
                        saveUserToFirebase(userId);
                    } else {
                        binding.registerProgressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Registration failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirebase(String userId) {
        Log.d("FirebaseDebug", "Saving user to Firebase: " + userId);

        User user = new User(
                binding.etFirstName.getText().toString().trim(),
                binding.etLastName.getText().toString().trim(),
                binding.etPhone.getText().toString().trim(),
                binding.etEmail.getText().toString().trim(),
                binding.etUsername.getText().toString().trim(),
                binding.etDob.getText().toString().trim(),
                ""  // Default empty profile picture URL
        );
        Log.d("FirebaseDebug", "Saving user data to: users/" + userId);
        Log.d("FirebaseDebug", "User data: " + new Gson().toJson(user));
        // Save user to Firebase Database
        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    binding.registerProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Save user to SharedPreferences
                        saveUserLocally(user);

                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(RegisterActivity.this, "Registration successful",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to save user data: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserLocally(User user) {
        // Save user to SharedPreferences
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        preferenceManager.setString("user_data", userJson);
    }

    private void updatePasswordStrengthIndicator(String password) {
        // Simple password strength indicator
        if (password.isEmpty()) {
            binding.passwordStrengthIndicator.setProgress(0);
            binding.tvPasswordStrength.setText("Password Strength: None");
            binding.tvPasswordStrength.setTextColor(getResources().getColor(R.color.silver));
        } else if (password.length() < 6) {
            binding.passwordStrengthIndicator.setProgress(25);
            binding.tvPasswordStrength.setText("Password Strength: Weak");
            binding.tvPasswordStrength.setTextColor(getResources().getColor(R.color.crimson_red));
        } else if (password.length() < 8) {
            binding.passwordStrengthIndicator.setProgress(50);
            binding.tvPasswordStrength.setText("Password Strength: Medium");
            binding.tvPasswordStrength.setTextColor(getResources().getColor(R.color.gold));
        } else if (password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*")) {
            binding.passwordStrengthIndicator.setProgress(100);
            binding.tvPasswordStrength.setText("Password Strength: Strong");
            binding.tvPasswordStrength.setTextColor(getResources().getColor(R.color.green));
        } else {
            binding.passwordStrengthIndicator.setProgress(75);
            binding.tvPasswordStrength.setText("Password Strength: Good");
            binding.tvPasswordStrength.setTextColor(getResources().getColor(R.color.gold));
        }
    }
}
