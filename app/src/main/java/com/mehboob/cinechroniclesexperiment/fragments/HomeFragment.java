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
import com.mehboob.cinechroniclesexperiment.databinding.FragmentHomeBinding;
import com.mehboob.cinechroniclesexperiment.models.Movie;
import com.mehboob.cinechroniclesexperiment.models.Series;
import com.mehboob.cinechroniclesexperiment.utils.DataProvider;

import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private MovieAdapter trendingMoviesAdapter;
    private MovieAdapter newReleasesAdapter;
    private SeriesAdapter seriesAdapter;
    private MovieApiClient apiClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize API client
        apiClient = new MovieApiClient(requireContext());

        // Set up trending movies recycler view
        setupTrendingMoviesRecyclerView();

        // Set up new releases recycler view
        setupNewReleasesRecyclerView();

        // Set up series recycler view
        setupSeriesRecyclerView();

        // Load data
        loadDataFromApi();
    }


    private void setupTrendingMoviesRecyclerView() {
        binding.rvTrendingMovies.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trendingMoviesAdapter = new MovieAdapter(getContext());
        binding.rvTrendingMovies.setAdapter(trendingMoviesAdapter);

        // Set click listener
        trendingMoviesAdapter.setOnMovieClickListener(movie -> {
            // Show movie details
            Toast.makeText(getContext(), "Selected: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to movie details screen
        });
    }

    private void setupNewReleasesRecyclerView() {
        binding.rvNewReleases.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newReleasesAdapter = new MovieAdapter(getContext());
        binding.rvNewReleases.setAdapter(newReleasesAdapter);

        // Set click listener
        newReleasesAdapter.setOnMovieClickListener(movie -> {
            // Show movie details
            Toast.makeText(getContext(), "Selected: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to movie details screen
        });
    }

    private void setupSeriesRecyclerView() {
        binding.rvSeries.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        seriesAdapter = new SeriesAdapter(getContext());
        binding.rvSeries.setAdapter(seriesAdapter);

        // Set click listener
        seriesAdapter.setOnSeriesClickListener(series -> {
            // Show series details
            Toast.makeText(getContext(), "Selected: " + series.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to series details screen
        });
    }

    private void loadDataFromApi() {
        // Show loading indicators
        binding.trendingMoviesProgressBar.setVisibility(View.VISIBLE);
        binding.newReleasesProgressBar.setVisibility(View.VISIBLE);
        binding.seriesProgressBar.setVisibility(View.VISIBLE);

        // Load trending movies from API
        apiClient.getTrendingMovies(new MovieApiClient.OnMoviesLoadedListener() {
            @Override
            public void onMoviesLoaded(List<Movie> movies) {
                binding.trendingMoviesProgressBar.setVisibility(View.GONE);
                if (trendingMoviesAdapter != null) {
                    trendingMoviesAdapter.setMovies(movies);
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.trendingMoviesProgressBar.setVisibility(View.GONE);
                // Fallback to local data
                loadLocalTrendingMovies();
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading trending movies: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load new releases from API
        apiClient.getNewReleases(new MovieApiClient.OnMoviesLoadedListener() {
            @Override
            public void onMoviesLoaded(List<Movie> movies) {
                binding.newReleasesProgressBar.setVisibility(View.GONE);
                if (newReleasesAdapter != null) {
                    newReleasesAdapter.setMovies(movies);
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.newReleasesProgressBar.setVisibility(View.GONE);
                // Fallback to local data
                loadLocalNewReleases();
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading new releases: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load popular series from API
        apiClient.getPopularSeries(new MovieApiClient.OnSeriesLoadedListener() {
            @Override
            public void onSeriesLoaded(List<Series> series) {
                binding.seriesProgressBar.setVisibility(View.GONE);
                if (seriesAdapter != null) {
                    seriesAdapter.setSeries(series);
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.seriesProgressBar.setVisibility(View.GONE);
                // Fallback to local data
                loadLocalSeries();
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading series: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadLocalTrendingMovies() {
        try {
            // Load trending movies from local data
            List<Movie> trendingMovies = DataProvider.getTrendingMovies(getContext());
            if (trendingMoviesAdapter != null) {
                trendingMoviesAdapter.setMovies(trendingMovies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLocalNewReleases() {
        try {
            // Load new releases from local data
            List<Movie> newReleases = DataProvider.getNewReleases(getContext());
            if (newReleasesAdapter != null) {
                newReleasesAdapter.setMovies(newReleases);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLocalSeries() {
        try {
            // Load series from local data
            List<Series> series = DataProvider.getSeries(getContext());
            if (seriesAdapter != null) {
                seriesAdapter.setSeries(series);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
