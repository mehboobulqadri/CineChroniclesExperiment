package com.mehboob.cinechroniclesexperiment.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.mehboob.cinechroniclesexperiment.R;
import com.mehboob.cinechroniclesexperiment.databinding.FragmentProfileBinding;
import com.mehboob.cinechroniclesexperiment.models.User;
import com.mehboob.cinechroniclesexperiment.utils.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private PreferenceManager preferenceManager;
    private User currentUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private boolean isEditMode = false;
    private DatabaseReference userRef;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Initialize preference manager
            preferenceManager = new PreferenceManager(requireContext());

            // Initialize Firebase Database reference
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            // Initialize calendar and date formatter
            calendar = Calendar.getInstance();
            dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

            // Load user data
            loadUserData();

            // Set up click listeners
            setupClickListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            Toast.makeText(requireContext(), "Error initializing profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        // Show loading indicator
        binding.profileProgressBar.setVisibility(View.VISIBLE);

        // First try to load from Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.profileProgressBar.setVisibility(View.GONE);

                try {
                    if (dataSnapshot.exists()) {
                        currentUser = dataSnapshot.getValue(User.class);
                        displayUserData();

                        // Update local storage with latest data
                        saveUserLocally(currentUser);
                    } else {
                        // If not in Firebase, try to load from local storage
                        loadUserFromLocalStorage();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing user data", e);
                    loadUserFromLocalStorage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.profileProgressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load user data: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();

                // Try to load from local storage as fallback
                loadUserFromLocalStorage();
            }
        });
    }

    private void loadUserFromLocalStorage() {
        String userJson = preferenceManager.getString("user_data", "");
        if (!userJson.isEmpty()) {
            try {
                Gson gson = new Gson();
                currentUser = gson.fromJson(userJson, User.class);
                displayUserData();
            } catch (Exception e) {
                Log.e(TAG, "Error loading user from local storage", e);
                Toast.makeText(requireContext(), "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayUserData() {
        if (currentUser != null) {
            binding.tvName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            binding.tvUsername.setText("@" + currentUser.getUsername());
            binding.tvEmail.setText(currentUser.getEmail());
            binding.tvPhone.setText(currentUser.getPhone());
            binding.tvDob.setText(currentUser.getDob());

            if (currentUser.getProfilePictureUrl() != null && !currentUser.getProfilePictureUrl().isEmpty()) {
                try {
                    Glide.with(this)
                            .load(currentUser.getProfilePictureUrl())
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.default_profile)
                            .into(binding.profileImage);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading profile image", e);
                }
            }
        }
    }

    private void setupClickListeners() {
        // Change profile picture
        binding.profileImage.setOnClickListener(v -> {
            if (isEditMode) {
                requestStoragePermission();
            }
        });

        binding.btnChangeProfilePicture.setOnClickListener(v -> {
            requestStoragePermission();
        });

        // Edit profile
        binding.btnEditProfile.setOnClickListener(v -> {
            if (isEditMode) {
                // Save changes
                saveChanges();
            } else {
                // Enter edit mode
                toggleEditMode(true);
            }
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, "Error opening image picker", e);
            Toast.makeText(requireContext(), "Error opening image picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();

                // Update profile image
                Glide.with(this)
                        .load(imageUri)
                        .into(binding.profileImage);

                // Update user data
                if (currentUser != null) {
                    currentUser.setProfilePictureUrl(imageUri.toString());

                    // Save updated user data
                    saveUserToFirebase();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing selected image", e);
                Toast.makeText(requireContext(), "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleEditMode(boolean edit) {
        isEditMode = edit;

        // Create editable fields
        if (isEditMode) {
            // Create editable layout
            binding.cardProfileInfo.setVisibility(View.GONE);
            binding.cardEditProfile.setVisibility(View.VISIBLE);

            // Populate editable fields
            binding.etEditFirstName.setText(currentUser.getFirstName());
            binding.etEditLastName.setText(currentUser.getLastName());
            binding.etEditPhone.setText(currentUser.getPhone());
            binding.etEditUsername.setText(currentUser.getUsername());
            binding.etEditDob.setText(currentUser.getDob());

            // Set up date picker for DOB
            binding.etEditDob.setOnClickListener(v -> showDatePickerDialog());

            // Change button text
            binding.btnEditProfile.setText("Save Changes");
        } else {
            // Restore view-only layout
            binding.cardProfileInfo.setVisibility(View.VISIBLE);
            binding.cardEditProfile.setVisibility(View.GONE);

            // Change button text
            binding.btnEditProfile.setText("Edit Profile");
        }
    }

    private void showDatePickerDialog() {
        try {
            // Parse current date
            String currentDate = binding.etEditDob.getText().toString();
            if (!currentDate.isEmpty()) {
                calendar.setTime(dateFormatter.parse(currentDate));
            }
        } catch (Exception e) {
            // Use current date if parsing fails
            calendar = Calendar.getInstance();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    binding.etEditDob.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveChanges() {
        try {
            // Show loading indicator
            binding.profileProgressBar.setVisibility(View.VISIBLE);

            // Validate inputs
            if (!validateInputs()) {
                binding.profileProgressBar.setVisibility(View.GONE);
                return;
            }

            // Update user object with edited values
            currentUser.setFirstName(binding.etEditFirstName.getText().toString().trim());
            currentUser.setLastName(binding.etEditLastName.getText().toString().trim());
            currentUser.setPhone(binding.etEditPhone.getText().toString().trim());
            currentUser.setUsername(binding.etEditUsername.getText().toString().trim());
            currentUser.setDob(binding.etEditDob.getText().toString().trim());

            // Save to Firebase
            saveUserToFirebase();
        } catch (Exception e) {
            binding.profileProgressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error saving changes", e);
            Toast.makeText(requireContext(), "Error saving changes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate first name
        if (binding.etEditFirstName.getText().toString().trim().isEmpty()) {
            binding.etEditFirstName.setError("First name cannot be empty");
            isValid = false;
        }

        // Validate last name
        if (binding.etEditLastName.getText().toString().trim().isEmpty()) {
            binding.etEditLastName.setError("Last name cannot be empty");
            isValid = false;
        }

        // Validate phone
        if (binding.etEditPhone.getText().toString().trim().isEmpty()) {
            binding.etEditPhone.setError("Phone cannot be empty");
            isValid = false;
        }

        // Validate username
        if (binding.etEditUsername.getText().toString().trim().isEmpty()) {
            binding.etEditUsername.setError("Username cannot be empty");
            isValid = false;
        }

        // Validate date of birth
        if (binding.etEditDob.getText().toString().trim().isEmpty()) {
            binding.etEditDob.setError("Date of birth cannot be empty");
            isValid = false;
        }

        return isValid;
    }

    private void saveUserToFirebase() {
        // Show loading indicator
        binding.profileProgressBar.setVisibility(View.VISIBLE);

        try {
            // Save to Firebase
            userRef.setValue(currentUser)
                    .addOnCompleteListener(task -> {
                        binding.profileProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Save locally
                            saveUserLocally(currentUser);

                            // Update UI
                            displayUserData();

                            // Exit edit mode
                            if (isEditMode) {
                                toggleEditMode(false);
                            }

                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update profile: " +
                                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            binding.profileProgressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error saving to Firebase", e);
            Toast.makeText(requireContext(), "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserLocally(User user) {
        // Save user to SharedPreferences
        try {
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            preferenceManager.setString("user_data", userJson);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user locally", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

