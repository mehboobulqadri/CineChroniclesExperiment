package com.mehboob.cinechroniclesexperiment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mehboob.cinechroniclesexperiment.adapters.MovieAdapter;
import com.mehboob.cinechroniclesexperiment.databinding.FragmentMyMoviesBinding;
import com.mehboob.cinechroniclesexperiment.models.Movie;
import com.mehboob.cinechroniclesexperiment.utils.DataProvider;

import java.util.List;

public class MyMoviesFragment extends Fragment {
    private FragmentMyMoviesBinding binding;
    private MovieAdapter movieAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyMoviesBinding.inflate(inflater, container, false);
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
        binding.rvMyMovies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        movieAdapter = new MovieAdapter(getContext());
        binding.rvMyMovies.setAdapter(movieAdapter);
    }

    private void loadData() {
        // Load favorite movies
        List<Movie> favoriteMovies = DataProvider.getFavoriteMovies(getContext());
        movieAdapter.setMovies(favoriteMovies);

        // Show empty state if no movies
        if (favoriteMovies.isEmpty()) {
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
