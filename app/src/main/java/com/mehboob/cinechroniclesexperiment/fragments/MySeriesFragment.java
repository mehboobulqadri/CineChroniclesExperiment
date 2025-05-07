package com.mehboob.cinechroniclesexperiment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mehboob.cinechroniclesexperiment.adapters.SeriesAdapter;
import com.mehboob.cinechroniclesexperiment.databinding.FragmentMySeriesBinding;
import com.mehboob.cinechroniclesexperiment.models.Series;
import com.mehboob.cinechroniclesexperiment.utils.DataProvider;

import java.util.List;

public class MySeriesFragment extends Fragment {
    private FragmentMySeriesBinding binding;
    private SeriesAdapter seriesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMySeriesBinding.inflate(inflater, container, false);
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
        binding.rvMySeries.setLayoutManager(new GridLayoutManager(getContext(), 2));
        seriesAdapter = new SeriesAdapter(getContext());
        binding.rvMySeries.setAdapter(seriesAdapter);
    }

    private void loadData() {
        // Load watchlist series
        List<Series> watchlistSeries = DataProvider.getWatchlistSeries(getContext());
        seriesAdapter.setSeries(watchlistSeries);

        // Show empty state if no series
        if (watchlistSeries.isEmpty()) {
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
