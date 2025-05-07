package com.mehboob.cinechroniclesexperiment.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mehboob.cinechroniclesexperiment.models.Movie;
import com.mehboob.cinechroniclesexperiment.models.NewsArticle;
import com.mehboob.cinechroniclesexperiment.models.Series;
import com.mehboob.cinechroniclesexperiment.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieApiClient {
    private static final String TAG = "MovieApiClient";
    // API key should ideally be in BuildConfig or encrypted
    private static final String API_KEY = "3e4d90cc78f94327d74c8438c9927505";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String NEWS_API_KEY = "YOUR_NEWS_API_KEY"; // Replace with actual key
    private static final String NEWS_BASE_URL = "https://newsapi.org/v2";

    private final OkHttpClient client;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;

    public interface OnMoviesLoadedListener {
        void onMoviesLoaded(List<Movie> movies);
        void onError(String errorMessage);
    }

    public interface OnSeriesLoadedListener {
        void onSeriesLoaded(List<Series> series);
        void onError(String errorMessage);
    }

    public interface OnNewsLoadedListener {
        void onNewsLoaded(List<NewsArticle> news);
        void onError(String errorMessage);
    }

    public MovieApiClient(Context context) {
        this.context = context;

        // Setup OkHttpClient with cache for offline support
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);

        client = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void getTrendingMovies(OnMoviesLoadedListener listener) {
        String url = BASE_URL + "/trending/movie/week?api_key=" + API_KEY;
        fetchMovies(url, listener, false);
    }

    public void getNewReleases(OnMoviesLoadedListener listener) {
        String url = BASE_URL + "/movie/now_playing?api_key=" + API_KEY;
        fetchMovies(url, listener, false);
    }

    public void getPopularSeries(OnSeriesLoadedListener listener) {
        String url = BASE_URL + "/tv/popular?api_key=" + API_KEY;
        fetchSeries(url, listener, false);
    }

    public void searchMovies(String query, OnMoviesLoadedListener listener) {
        if (query == null || query.trim().isEmpty()) {
            listener.onMoviesLoaded(new ArrayList<>());
            return;
        }

        String encodedQuery = query.trim().replace(" ", "+");
        String url = BASE_URL + "/search/movie?api_key=" + API_KEY + "&query=" + encodedQuery;
        fetchMovies(url, listener, true);
    }

    public void searchSeries(String query, OnSeriesLoadedListener listener) {
        if (query == null || query.trim().isEmpty()) {
            listener.onSeriesLoaded(new ArrayList<>());
            return;
        }

        String encodedQuery = query.trim().replace(" ", "+");
        String url = BASE_URL + "/search/tv?api_key=" + API_KEY + "&query=" + encodedQuery;
        fetchSeries(url, listener, true);
    }

    public void getMovieNews(OnNewsLoadedListener listener) {
        // Using TMDB for movie news (upcoming movies as news)
        String url = BASE_URL + "/movie/upcoming?api_key=" + API_KEY;
        fetchMovieNews(url, listener);
    }

    private void fetchMovies(String url, OnMoviesLoadedListener listener, boolean isSearch) {
        executor.execute(() -> {
            Request.Builder requestBuilder = new Request.Builder().url(url);

            // Add cache control for offline support
            if (!NetworkUtils.isNetworkAvailable(context) && !isSearch) {
                requestBuilder.cacheControl(new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build());
            }

            Request request = requestBuilder.build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    List<Movie> movies = parseMoviesResponse(responseData);

                    handler.post(() -> listener.onMoviesLoaded(movies));
                    retryCount = 0; // Reset retry count on success
                } else {
                    handleError(url, listener, isSearch, "Failed to fetch movies: " +
                            (response.message() != null ? response.message() : "Unknown error"));
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching movies", e);
                handleError(url, listener, isSearch, "Network error: " + e.getMessage());
            }
        });
    }

    private void handleError(String url, OnMoviesLoadedListener listener, boolean isSearch, String errorMessage) {
        if (retryCount < MAX_RETRIES && NetworkUtils.isNetworkAvailable(context)) {
            retryCount++;
            Log.d(TAG, "Retrying request (" + retryCount + "/" + MAX_RETRIES + ")");

            // Retry after a delay
            handler.postDelayed(() -> fetchMovies(url, listener, isSearch), 1000 * retryCount);
        } else {
            retryCount = 0;
            handler.post(() -> listener.onError(errorMessage));
        }
    }

    private void fetchSeries(String url, OnSeriesLoadedListener listener, boolean isSearch) {
        executor.execute(() -> {
            Request.Builder requestBuilder = new Request.Builder().url(url);

            // Add cache control for offline support
            if (!NetworkUtils.isNetworkAvailable(context) && !isSearch) {
                requestBuilder.cacheControl(new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build());
            }

            Request request = requestBuilder.build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    List<Series> seriesList = parseSeriesResponse(responseData);

                    handler.post(() -> listener.onSeriesLoaded(seriesList));
                    retryCount = 0; // Reset retry count on success
                } else {
                    handleSeriesError(url, listener, isSearch, "Failed to fetch series: " +
                            (response.message() != null ? response.message() : "Unknown error"));
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching series", e);
                handleSeriesError(url, listener, isSearch, "Network error: " + e.getMessage());
            }
        });
    }

    private void handleSeriesError(String url, OnSeriesLoadedListener listener, boolean isSearch, String errorMessage) {
        if (retryCount < MAX_RETRIES && NetworkUtils.isNetworkAvailable(context)) {
            retryCount++;
            Log.d(TAG, "Retrying request (" + retryCount + "/" + MAX_RETRIES + ")");

            // Retry after a delay
            handler.postDelayed(() -> fetchSeries(url, listener, isSearch), 1000 * retryCount);
        } else {
            retryCount = 0;
            handler.post(() -> listener.onError(errorMessage));
        }
    }

    private void fetchMovieNews(String url, OnNewsLoadedListener listener) {
        executor.execute(() -> {
            Request.Builder requestBuilder = new Request.Builder().url(url);

            // Add cache control for offline support
            if (!NetworkUtils.isNetworkAvailable(context)) {
                requestBuilder.cacheControl(new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build());
            }

            Request request = requestBuilder.build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    List<NewsArticle> news = parseNewsResponse(responseData);

                    handler.post(() -> listener.onNewsLoaded(news));
                } else {
                    handler.post(() -> listener.onError("Failed to fetch news: " +
                            (response.message() != null ? response.message() : "Unknown error")));
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching news", e);
                handler.post(() -> listener.onError("Network error: " + e.getMessage()));
            }
        });
    }

    private List<Movie> parseMoviesResponse(String responseData) {
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject movieObject = results.getJSONObject(i);

                String id = movieObject.getString("id");
                String title = movieObject.getString("title");
                String posterPath = movieObject.optString("poster_path", "");
                String backdropPath = movieObject.optString("backdrop_path", "");
                String overview = movieObject.optString("overview", "No description available");

                // Get genre IDs and convert to genre names (simplified)
                StringBuilder genreBuilder = new StringBuilder();
                if (movieObject.has("genre_ids")) {
                    JSONArray genreIds = movieObject.getJSONArray("genre_ids");
                    for (int j = 0; j < Math.min(genreIds.length(), 3); j++) {
                        int genreId = genreIds.getInt(j);
                        String genreName = getGenreName(genreId);
                        if (j > 0) {
                            genreBuilder.append(", ");
                        }
                        genreBuilder.append(genreName);
                    }
                }

                int releaseYear = 0;
                if (movieObject.has("release_date") && !movieObject.isNull("release_date")) {
                    String releaseDate = movieObject.getString("release_date");
                    if (releaseDate.length() >= 4) {
                        try {
                            releaseYear = Integer.parseInt(releaseDate.substring(0, 4));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing release year", e);
                        }
                    }
                }

                float rating = 0;
                if (movieObject.has("vote_average")) {
                    rating = (float) movieObject.getDouble("vote_average");
                }

                String posterUrl = posterPath.isEmpty() ? "" : IMAGE_BASE_URL + posterPath;
                String backdropUrl = backdropPath.isEmpty() ? "" : IMAGE_BASE_URL + backdropPath;

                Movie movie = new Movie(
                        id,
                        title,
                        posterUrl,
                        backdropUrl,
                        overview,
                        genreBuilder.toString(),
                        releaseYear,
                        rating,
                        false
                );

                movies.add(movie);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing movie data", e);
        }

        return movies;
    }

    private List<Series> parseSeriesResponse(String responseData) {
        List<Series> seriesList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject seriesObject = results.getJSONObject(i);

                String id = seriesObject.getString("id");
                String title = seriesObject.getString("name");
                String posterPath = seriesObject.optString("poster_path", "");
                String backdropPath = seriesObject.optString("backdrop_path", "");
                String overview = seriesObject.optString("overview", "No description available");

                // Get genre IDs and convert to genre names (simplified)
                StringBuilder genreBuilder = new StringBuilder();
                if (seriesObject.has("genre_ids")) {
                    JSONArray genreIds = seriesObject.getJSONArray("genre_ids");
                    for (int j = 0; j < Math.min(genreIds.length(), 3); j++) {
                        int genreId = genreIds.getInt(j);
                        String genreName = getGenreName(genreId);
                        if (j > 0) {
                            genreBuilder.append(", ");
                        }
                        genreBuilder.append(genreName);
                    }
                }

                int firstAirYear = 0;
                if (seriesObject.has("first_air_date") && !seriesObject.isNull("first_air_date")) {
                    String firstAirDate = seriesObject.getString("first_air_date");
                    if (firstAirDate.length() >= 4) {
                        try {
                            firstAirYear = Integer.parseInt(firstAirDate.substring(0, 4));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing first air year", e);
                        }
                    }
                }

                float rating = 0;
                if (seriesObject.has("vote_average")) {
                    rating = (float) seriesObject.getDouble("vote_average");
                }

                String posterUrl = posterPath.isEmpty() ? "" : IMAGE_BASE_URL + posterPath;
                String backdropUrl = backdropPath.isEmpty() ? "" : IMAGE_BASE_URL + backdropPath;

                // Number of seasons is not available in this API response, use a default value
                int seasons = seriesObject.optInt("number_of_seasons", 1);

                Series series = new Series(
                        id,
                        title,
                        posterUrl,
                        backdropUrl,
                        overview,
                        genreBuilder.toString(),
                        firstAirYear,
                        seasons,
                        rating,
                        false
                );

                seriesList.add(series);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing series data", e);
        }

        return seriesList;
    }

    private List<NewsArticle> parseNewsResponse(String responseData) {
        List<NewsArticle> newsList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject movieObject = results.getJSONObject(i);

                String id = movieObject.getString("id");
                String title = movieObject.getString("title");
                String posterPath = movieObject.optString("poster_path", "");
                String overview = movieObject.optString("overview", "No description available");

                String releaseDate = "";
                if (movieObject.has("release_date") && !movieObject.isNull("release_date")) {
                    releaseDate = movieObject.getString("release_date");
                }

                String posterUrl = posterPath.isEmpty() ? "" : IMAGE_BASE_URL + posterPath;

                // Create a news article from upcoming movie
                NewsArticle article = new NewsArticle(
                        id,
                        title,
                        "Upcoming Release: " + title,
                        overview,
                        releaseDate,
                        posterUrl,
                        "TMDB"
                );

                newsList.add(article);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing news data", e);
        }

        return newsList;
    }

    private String getGenreName(int genreId) {
        // Simplified genre mapping
        switch (genreId) {
            case 28: return "Action";
            case 12: return "Adventure";
            case 16: return "Animation";
            case 35: return "Comedy";
            case 80: return "Crime";
            case 99: return "Documentary";
            case 18: return "Drama";
            case 10751: return "Family";
            case 14: return "Fantasy";
            case 36: return "History";
            case 27: return "Horror";
            case 10402: return "Music";
            case 9648: return "Mystery";
            case 10749: return "Romance";
            case 878: return "Sci-Fi";
            case 10770: return "TV Movie";
            case 53: return "Thriller";
            case 10752: return "War";
            case 37: return "Western";
            // TV Series genres
            case 10759: return "Action & Adventure";
            case 10762: return "Kids";
            case 10763: return "News";
            case 10764: return "Reality";
            case 10765: return "Sci-Fi & Fantasy";
            case 10766: return "Soap";
            case 10767: return "Talk";
            case 10768: return "War & Politics";
            default: return "Unknown";
        }
    }
}
