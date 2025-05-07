package com.mehboob.cinechroniclesexperiment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mehboob.cinechroniclesexperiment.databinding.ActivityHomeBinding;
import com.mehboob.cinechroniclesexperiment.fragments.HomeFragment;
import com.mehboob.cinechroniclesexperiment.fragments.MyMoviesFragment;
import com.mehboob.cinechroniclesexperiment.fragments.MyRatingsFragment;
import com.mehboob.cinechroniclesexperiment.fragments.MySeriesFragment;
import com.mehboob.cinechroniclesexperiment.fragments.NewsFragment;
import com.mehboob.cinechroniclesexperiment.fragments.NotesFragment;
import com.mehboob.cinechroniclesexperiment.fragments.ProfileFragment;
import com.mehboob.cinechroniclesexperiment.fragments.SearchResultsFragment;
import com.mehboob.cinechroniclesexperiment.models.User;
import com.mehboob.cinechroniclesexperiment.utils.PreferenceManager;
import com.mehboob.cinechroniclesexperiment.utils.ThemeUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private ActivityHomeBinding binding;
    private ActionBarDrawerToggle toggle;
    private PreferenceManager preferenceManager;
    private User currentUser;
    private Fragment currentFragment;
    private boolean isSearchActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setting content view
        preferenceManager = new PreferenceManager(this);
        boolean isDarkMode = preferenceManager.getBoolean("dark_mode", false);
        ThemeUtils.setDarkMode(isDarkMode);

        try {
            super.onCreate(savedInstanceState);
            binding = ActivityHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Load user data
            loadUserData();

            // Set up toolbar
            setSupportActionBar(binding.toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("CineChronicles");
            }

            // Set up navigation drawer
            setupNavigationDrawer();

            // Set up navigation view listener
            binding.navView.setNavigationItemSelectedListener(this);

            // Set up bottom navigation
            binding.bottomNavigation.setOnNavigationItemSelectedListener(this);

            // Load default fragment
            if (savedInstanceState == null) {
                currentFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, currentFragment)
                        .commit();
                binding.navView.setCheckedItem(R.id.nav_home);
                binding.bottomNavigation.setSelectedItemId(R.id.nav_bottom_home);

                // Show search view only on home fragment
                binding.searchView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing Home: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        // Try to load from Firebase first
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);

                    // Update local storage with latest data
                    saveUserLocally(currentUser);

                    // Update UI
                    updateNavigationHeader();
                } else {
                    // If not in Firebase, try to load from local storage
                    loadUserFromLocalStorage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Try to load from local storage as fallback
                loadUserFromLocalStorage();
            }
        });
    }

    private void loadUserFromLocalStorage() {
        String userJson = preferenceManager.getString("user_data", "");
        if (!userJson.isEmpty()) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(userJson, User.class);
            updateNavigationHeader();
        }
    }

    private void saveUserLocally(User user) {
        // Save user to SharedPreferences
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        preferenceManager.setString("user_data", userJson);
    }

    private void setupNavigationDrawer() {
        try {
            toggle = new ActionBarDrawerToggle(
                    this, binding.drawerLayout, binding.toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            binding.drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            // Set up search view
            setupSearchView();

            // Update navigation header with user data
            updateNavigationHeader();
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up navigation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateNavigationHeader() {
        try {
            // Set up header view with user data
            View headerView = binding.navView.getHeaderView(0);
            if (headerView != null) {
                CircleImageView profileImage = headerView.findViewById(R.id.profile_image);
                TextView userName = headerView.findViewById(R.id.user_name);
                TextView userEmail = headerView.findViewById(R.id.user_email);

                if (currentUser != null) {
                    userName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
                    userEmail.setText(currentUser.getEmail());

                    if (currentUser.getProfilePictureUrl() != null && !currentUser.getProfilePictureUrl().isEmpty()) {
                        Glide.with(this)
                                .load(currentUser.getProfilePictureUrl())
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .into(profileImage);
                    }
                } else {
                    // Use Firebase Auth data if local data is not available
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        if (displayName != null) {
                            userName.setText(displayName);
                        } else {
                            userName.setText("User");
                        }

                        if (email != null) {
                            userEmail.setText(email);
                        } else {
                            userEmail.setText("");
                        }
                    }
                }

                // Set up social media icons
                ImageView instagramIcon = headerView.findViewById(R.id.instagram_icon);
                ImageView twitterIcon = headerView.findViewById(R.id.twitter_icon);

                if (instagramIcon != null) {
                    instagramIcon.setOnClickListener(v -> {
                        Toast.makeText(HomeActivity.this, "Instagram clicked", Toast.LENGTH_SHORT).show();
                        // Intent to open Instagram
                    });
                }

                if (twitterIcon != null) {
                    twitterIcon.setOnClickListener(v -> {
                        Toast.makeText(HomeActivity.this, "Twitter clicked", Toast.LENGTH_SHORT).show();
                        // Intent to open Twitter
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                if (!query.trim().isEmpty()) {
                    performSearch(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // We'll only search on submit to avoid excessive API calls
                return true;
            }
        });
    }

    private void performSearch(String query) {
        // Create and show search results fragment
        SearchResultsFragment searchFragment = SearchResultsFragment.newInstance(query);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, searchFragment)
                .addToBackStack(null)
                .commit();

        // Update UI state
        isSearchActive = true;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Search: " + query);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        boolean showSearchView = false;

        int itemId = item.getItemId();

        // Handle drawer menu items
        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
            showSearchView = true;
            binding.bottomNavigation.setSelectedItemId(R.id.nav_bottom_home);
        } else if (itemId == R.id.nav_my_movies) {
            selectedFragment = new MyMoviesFragment();
        } else if (itemId == R.id.nav_my_series) {
            selectedFragment = new MySeriesFragment();
        } else if (itemId == R.id.nav_my_ratings) {
            selectedFragment = new MyRatingsFragment();
            binding.bottomNavigation.setSelectedItemId(R.id.nav_bottom_ratings);
        } else if (itemId == R.id.nav_my_notes) {
            selectedFragment = new NotesFragment();
            binding.bottomNavigation.setSelectedItemId(R.id.nav_bottom_notes);
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.nav_support) {
            // Open email client
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@cinechronicles.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "CineChronicles Support Request");
            try {
                startActivity(Intent.createChooser(intent, "Send email..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email client installed.", Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.nav_logout) {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Clear preferences
            preferenceManager.clear();

            // Navigate to login screen
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (itemId == R.id.nav_dark_mode) {
            // Toggle dark mode
            boolean isDarkMode = preferenceManager.getBoolean("dark_mode", false);
            preferenceManager.setBoolean("dark_mode", !isDarkMode);

            // Apply theme without restarting
            ThemeUtils.toggleDarkMode(this, preferenceManager);
            return true;
        }
        // Handle bottom navigation items
        else if (itemId == R.id.nav_bottom_home) {
            selectedFragment = new HomeFragment();
            showSearchView = true;
            binding.navView.setCheckedItem(R.id.nav_home);
        } else if (itemId == R.id.nav_bottom_ratings) {
            selectedFragment = new MyRatingsFragment();
            binding.navView.setCheckedItem(R.id.nav_my_ratings);
        } else if (itemId == R.id.nav_bottom_news) {
            selectedFragment = new NewsFragment();
        } else if (itemId == R.id.nav_bottom_notes) {
            selectedFragment = new NotesFragment();
            binding.navView.setCheckedItem(R.id.nav_my_notes);
        }

        if (selectedFragment != null) {
            currentFragment = selectedFragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            // Reset search state
            isSearchActive = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("CineChronicles");
            }

            // Show/hide search view based on fragment
            binding.searchView.setVisibility(showSearchView ? View.VISIBLE : View.GONE);
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (isSearchActive) {
            // Return to home fragment when back is pressed from search
            getSupportFragmentManager().popBackStack();
            isSearchActive = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("CineChronicles");
            }
            binding.searchView.setVisibility(View.VISIBLE);
            binding.searchView.setQuery("", false);
        } else {
            super.onBackPressed();
        }
    }
}
