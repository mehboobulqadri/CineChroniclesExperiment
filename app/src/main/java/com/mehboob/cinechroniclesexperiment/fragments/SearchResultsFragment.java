package com.mehboob.cinechroniclesexperiment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mehboob.cinechroniclesexperiment.adapters.MovieAdapter;
import com.mehboob.cinechroniclesexperiment.adapters.SeriesAdapter;
import com.mehboob.cinechroniclesexperiment.api.MovieApiClient;
import com.mehboob.cinechroniclesexperiment.databinding.FragmentSearchResultsBinding;
import com.mehboob.cinechroniclesexperiment.models.Movie;
import com.mehboob.cinechroniclesexperiment.models.Series;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment {
    private static final String ARG_QUERY = "query";

    private FragmentSearchResultsBinding binding;
    private MovieAdapter movieAdapter;
    private SeriesAdapter seriesAdapter;
    private MovieApiClient apiClient;
    private String searchQuery;

    public static SearchResultsFragment newInstance(String query) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchQuery = getArguments().getString(ARG_QUERY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchResultsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize API client
        apiClient = new MovieApiClient(requireContext());

        // Set up RecyclerViews
        setupMoviesRecyclerView();
        setupSeriesRecyclerView();

        // Perform search
        if (searchQuery != null && !searchQuery.isEmpty()) {
            performSearch(searchQuery);
        }
    }

    private void setupMoviesRecyclerView() {
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(requireContext()));
        movieAdapter = new MovieAdapter(requireContext());
        binding.rvMovies.setAdapter(movieAdapter);

        // Set click listener
        movieAdapter.setOnMovieClickListener(movie -> {
            // Show movie details
            Toast.makeText(requireContext(), "Selected: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to movie details screen
        });
    }

    private void setupSeriesRecyclerView() {
        binding.rvSeries.setLayoutManager(new LinearLayoutManager(requireContext()));
        seriesAdapter = new SeriesAdapter(requireContext());
        binding.rvSeries.setAdapter(seriesAdapter);

        // Set click listener
        seriesAdapter.setOnSeriesClickListener(series -> {
            // Show series details
            Toast.makeText(requireContext(), "Selected: " + series.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to series details screen
        });
    }

    private void performSearch(String query) {
        // Show loading indicators
        binding.moviesProgressBar.setVisibility(View.VISIBLE);
        binding.seriesProgressBar.setVisibility(View.VISIBLE);

        // Search for movies
        apiClient.searchMovies(query, new MovieApiClient.OnMoviesLoadedListener() {
            @Override
            public void onMoviesLoaded(List<Movie> movies) {
                binding.moviesProgressBar.setVisibility(View.GONE);

                if (movies.isEmpty()) {
                    binding.tvNoMovies.setVisibility(View.VISIBLE);
                    binding.rvMovies.setVisibility(View.GONE);
                } else {
                    binding.tvNoMovies.setVisibility(View.GONE);
                    binding.rvMovies.setVisibility(View.VISIBLE);
                    movieAdapter.setMovies(movies);
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.moviesProgressBar.setVisibility(View.GONE);
                binding.tvNoMovies.setVisibility(View.VISIBLE);
                binding.rvMovies.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error searching movies: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Search for series
        apiClient.searchSeries(query, new MovieApiClient.OnSeriesLoadedListener() {
            @Override
            public void onSeriesLoaded(List<Series> series) {
                binding.seriesProgressBar.setVisibility(View.GONE);

                if (series.isEmpty()) {
                    binding.tvNoSeries.setVisibility(View.VISIBLE);
                    binding.rvSeries.setVisibility(View.GONE);
                } else {
                    binding.tvNoSeries.setVisibility(View.GONE);
                    binding.rvSeries.setVisibility(View.VISIBLE);
                    seriesAdapter.setSeries(series);
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.seriesProgressBar.setVisibility(View.GONE);
                binding.tvNoSeries.setVisibility(View.VISIBLE);
                binding.rvSeries.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error searching series: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
