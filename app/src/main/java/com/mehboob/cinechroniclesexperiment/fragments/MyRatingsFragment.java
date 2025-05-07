package com.mehboob.cinechroniclesexperiment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mehboob.cinechroniclesexperiment.adapters.RatingAdapter;
import com.mehboob.cinechroniclesexperiment.databinding.FragmentMyRatingsBinding;
import com.mehboob.cinechroniclesexperiment.models.Rating;
import com.mehboob.cinechroniclesexperiment.utils.DataProvider;

import java.util.List;

public class MyRatingsFragment extends Fragment {
    private FragmentMyRatingsBinding binding;
    private RatingAdapter ratingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyRatingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up recycler view
        setupRecyclerView();

        // Load data
        loadData();
    }

    private void setupRecyclerView() {
        binding.rvMyRatings.setLayoutManager(new LinearLayoutManager(getContext()));
        ratingAdapter = new RatingAdapter(getContext());
        binding.rvMyRatings.setAdapter(ratingAdapter);
    }

    private void loadData() {
        // Load user ratings
        List<Rating> userRatings = DataProvider.getUserRatings(getContext());
        ratingAdapter.setRatings(userRatings);

        // Show empty state if no ratings
        if (userRatings.isEmpty()) {
            binding.emptyState.setVisibility(View.VISIBLE);
        } else {
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
