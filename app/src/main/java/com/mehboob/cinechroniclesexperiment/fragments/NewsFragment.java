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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mehboob.cinechroniclesexperiment.adapters.NewsAdapter;
import com.mehboob.cinechroniclesexperiment.api.MovieApiClient;
import com.mehboob.cinechroniclesexperiment.databinding.FragmentNewsBinding;
import com.mehboob.cinechroniclesexperiment.models.NewsArticle;
import com.mehboob.cinechroniclesexperiment.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {
    private FragmentNewsBinding binding;
    private NewsAdapter newsAdapter;
    private MovieApiClient apiClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize API client
        apiClient = new MovieApiClient(requireContext());

        // Set up RecyclerView
        setupRecyclerView();

        // Set up SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener(this::loadNews);

        // Load news
        loadNews();
    }

    private void setupRecyclerView() {
        binding.rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        newsAdapter = new NewsAdapter(requireContext(), new ArrayList<>());
        binding.rvNews.setAdapter(newsAdapter);

        // Set item click listener
        newsAdapter.setOnItemClickListener(article -> {
            // Show news details
            Toast.makeText(requireContext(), "Selected: " + article.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to news details screen or show dialog
        });
    }

    private void loadNews() {
        // Show loading indicator
        binding.swipeRefresh.setRefreshing(true);

        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            binding.swipeRefresh.setRefreshing(false);
            binding.tvNoInternet.setVisibility(View.VISIBLE);
            binding.rvNews.setVisibility(View.GONE);
            return;
        }

        // Hide no internet message
        binding.tvNoInternet.setVisibility(View.GONE);
        binding.rvNews.setVisibility(View.VISIBLE);

        // Load news from API
        apiClient.getMovieNews(new MovieApiClient.OnNewsLoadedListener() {
            @Override
            public void onNewsLoaded(List<NewsArticle> news) {
                if (binding == null) return;  // ✅ Null check

                binding.swipeRefresh.setRefreshing(false);

                if (news.isEmpty()) {
                    binding.tvNoNews.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNoNews.setVisibility(View.GONE);
                    newsAdapter.updateNews(news);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (binding == null) return;  // ✅ Null check

                binding.swipeRefresh.setRefreshing(false);
                binding.tvNoNews.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
