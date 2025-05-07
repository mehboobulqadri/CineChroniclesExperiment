package com.mehboob.cinechroniclesexperiment.utils;

import android.content.Context;
import android.util.Log;

import com.mehboob.cinechroniclesexperiment.R;
import com.mehboob.cinechroniclesexperiment.models.Movie;
import com.mehboob.cinechroniclesexperiment.models.Rating;
import com.mehboob.cinechroniclesexperiment.models.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataProvider {
    private static final String TAG = "DataProvider";

    public static List<Movie> getTrendingMovies(Context context) {
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "trending_movies.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("movies");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObject = jsonArray.getJSONObject(i);

                Movie movie = new Movie(
                        movieObject.getString("id"),
                        movieObject.getString("title"),
                        movieObject.getString("poster_url"),
                        movieObject.getString("backdrop_url"),
                        movieObject.getString("synopsis"),
                        movieObject.getString("genre"),
                        movieObject.getInt("release_year"),
                        (float) movieObject.getDouble("rating"),
                        movieObject.getBoolean("is_favorite")
                );

                movies.add(movie);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing trending movies JSON", e);
            // If JSON parsing fails, add mock data
            movies = getMockMovies(context);
        }

        return movies;
    }

    public static List<Movie> getNewReleases(Context context) {
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "new_releases.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("movies");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObject = jsonArray.getJSONObject(i);

                Movie movie = new Movie(
                        movieObject.getString("id"),
                        movieObject.getString("title"),
                        movieObject.getString("poster_url"),
                        movieObject.getString("backdrop_url"),
                        movieObject.getString("synopsis"),
                        movieObject.getString("genre"),
                        movieObject.getInt("release_year"),
                        (float) movieObject.getDouble("rating"),
                        movieObject.getBoolean("is_favorite")
                );

                movies.add(movie);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing new releases JSON", e);
            // If JSON parsing fails, add mock data
            movies = getMockMovies(context);
        }

        return movies;
    }

    public static List<Movie> getFavoriteMovies(Context context) {
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "favorite_movies.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("movies");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObject = jsonArray.getJSONObject(i);

                Movie movie = new Movie(
                        movieObject.getString("id"),
                        movieObject.getString("title"),
                        movieObject.getString("poster_url"),
                        movieObject.getString("backdrop_url"),
                        movieObject.getString("synopsis"),
                        movieObject.getString("genre"),
                        movieObject.getInt("release_year"),
                        (float) movieObject.getDouble("rating"),
                        true
                );

                movies.add(movie);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing favorite movies JSON", e);
            // If JSON parsing fails, add mock data
            addMockFavoriteMovies(movies);
        }

        return movies;
    }

    public static List<Series> getSeries(Context context) {
        List<Series> seriesList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "series.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("series");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject seriesObject = jsonArray.getJSONObject(i);

                Series series = new Series(
                        seriesObject.getString("id"),
                        seriesObject.getString("title"),
                        seriesObject.getString("poster_url"),
                        seriesObject.getString("backdrop_url"),
                        seriesObject.getString("synopsis"),
                        seriesObject.getString("genre"),
                        seriesObject.getInt("first_air_year"),
                        seriesObject.getInt("seasons"),
                        (float) seriesObject.getDouble("rating"),
                        seriesObject.getBoolean("is_in_watchlist")
                );

                seriesList.add(series);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing series JSON", e);
            // If JSON parsing fails, add mock data
            seriesList = getMockSeries(context);
        }

        return seriesList;
    }

    public static List<Series> getWatchlistSeries(Context context) {
        List<Series> seriesList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "watchlist_series.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("series");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject seriesObject = jsonArray.getJSONObject(i);

                Series series = new Series(
                        seriesObject.getString("id"),
                        seriesObject.getString("title"),
                        seriesObject.getString("poster_url"),
                        seriesObject.getString("backdrop_url"),
                        seriesObject.getString("synopsis"),
                        seriesObject.getString("genre"),
                        seriesObject.getInt("first_air_year"),
                        seriesObject.getInt("seasons"),
                        (float) seriesObject.getDouble("rating"),
                        true
                );

                seriesList.add(series);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing watchlist series JSON", e);
            // If JSON parsing fails, add mock data
            addMockWatchlistSeries(seriesList);
        }

        return seriesList;
    }

    public static List<Rating> getUserRatings(Context context) {
        List<Rating> ratings = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "user_ratings.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("ratings");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject ratingObject = jsonArray.getJSONObject(i);

                Rating rating = new Rating(
                        ratingObject.getString("id"),
                        ratingObject.getString("title"),
                        ratingObject.getString("poster_url"),
                        (float) ratingObject.getDouble("rating"),
                        ratingObject.getString("comment"),
                        ratingObject.getString("date"),
                        ratingObject.getBoolean("is_movie")
                );

                ratings.add(rating);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing user ratings JSON", e);
            // If JSON parsing fails, add mock data
            addMockRatings(ratings);
        }

        return ratings;
    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e(TAG, "Error loading JSON from asset", ex);
            // Try to load from raw resources if asset fails
            json = loadJSONFromRaw(context, fileName);
        }
        return json;
    }

    private static String loadJSONFromRaw(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        InputStream is = null;

        try {
            // Map filename to resource ID
            int resourceId = 0;
            if (fileName.equals("trending_movies.json") || fileName.equals("new_releases.json")) {
                resourceId = R.raw.mock_movies;
            } else if (fileName.equals("series.json") || fileName.equals("watchlist_series.json")) {
                resourceId = R.raw.mock_series;
            }

            if (resourceId != 0) {
                is = context.getResources().openRawResource(resourceId);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading JSON from raw resource", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing input stream", e);
                }
            }
        }

        return sb.toString();
    }

    public static List<Movie> getMockMovies(Context context) {
        List<Movie> movies = new ArrayList<>();

        try {
            InputStream is = context.getResources().openRawResource(R.raw.mock_movies);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("movies");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObject = jsonArray.getJSONObject(i);

                Movie movie = new Movie(
                        movieObject.getString("id"),
                        movieObject.getString("title"),
                        movieObject.getString("poster_url"),
                        movieObject.getString("backdrop_url"),
                        movieObject.getString("synopsis"),
                        movieObject.getString("genre"),
                        movieObject.getInt("release_year"),
                        (float) movieObject.getDouble("rating"),
                        movieObject.getBoolean("is_favorite")
                );

                movies.add(movie);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading mock movies", e);
            // If all else fails, add hardcoded mock data
            addMockMovies(movies);
        }

        return movies;
    }

    public static List<Series> getMockSeries(Context context) {
        List<Series> seriesList = new ArrayList<>();

        try {
            InputStream is = context.getResources().openRawResource(R.raw.mock_series);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("series");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject seriesObject = jsonArray.getJSONObject(i);

                Series series = new Series(
                        seriesObject.getString("id"),
                        seriesObject.getString("title"),
                        seriesObject.getString("poster_url"),
                        seriesObject.getString("backdrop_url"),
                        seriesObject.getString("synopsis"),
                        seriesObject.getString("genre"),
                        seriesObject.getInt("first_air_year"),
                        seriesObject.getInt("seasons"),
                        (float) seriesObject.getDouble("rating"),
                        seriesObject.getBoolean("is_in_watchlist")
                );

                seriesList.add(series);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading mock series", e);
            // If all else fails, add hardcoded mock data
            addMockSeries(seriesList);
        }

        return seriesList;
    }

    private static void addMockMovies(List<Movie> movies) {
        // Add mock data if JSON parsing fails
        movies.add(new Movie(
                "1",
                "The Dark Knight",
                "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                "https://image.tmdb.org/t/p/original/hkBaDkMWbLaf8B1lsWsKX7Ew3Xq.jpg",
                "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                "Action, Crime, Drama",
                2008,
                9.0f,
                false
        ));

        movies.add(new Movie(
                "2",
                "Inception",
                "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
                "https://image.tmdb.org/t/p/original/s3TBrRGB1iav7gFOCNx3H31MoES.jpg",
                "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
                "Action, Adventure, Sci-Fi",
                2010,
                8.8f,
                false
        ));

        movies.add(new Movie(
                "3",
                "Interstellar",
                "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
                "https://image.tmdb.org/t/p/original/xJHokMbljvjADYdit5fK5VQsXEG.jpg",
                "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                "Adventure, Drama, Sci-Fi",
                2014,
                8.6f,
                false
        ));

        movies.add(new Movie(
                "4",
                "The Shawshank Redemption",
                "https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg",
                "https://image.tmdb.org/t/p/original/kXfqcdQKsToO0OUXHcrrNCHDBzO.jpg",
                "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                "Drama",
                1994,
                9.3f,
                false
        ));

        movies.add(new Movie(
                "5",
                "Pulp Fiction",
                "https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg",
                "https://image.tmdb.org/t/p/original/suaEOtk1N1sgg2MTM7oZd2cfVp3.jpg",
                "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
                "Crime, Drama",
                1994,
                8.9f,
                false
        ));
    }

    private static void addMockFavoriteMovies(List<Movie> movies) {
        // Add mock favorite movies if JSON parsing fails
        movies.add(new Movie(
                "5",
                "The Godfather",
                "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg",
                "https://image.tmdb.org/t/p/original/rSPw7tgCH9c6NqICZef4kZjFOQ5.jpg",
                "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                "Crime, Drama",
                1972,
                9.2f,
                true
        ));

        movies.add(new Movie(
                "6",
                "Pulp Fiction",
                "https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg",
                "https://image.tmdb.org/t/p/original/suaEOtk1N1sgg2MTM7oZd2cfVp3.jpg",
                "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
                "Crime, Drama",
                1994,
                8.9f,
                true
        ));
    }

    private static void addMockSeries(List<Series> seriesList) {
        // Add mock series if JSON parsing fails
        seriesList.add(new Series(
                "1",
                "Breaking Bad",
                "https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg",
                "https://image.tmdb.org/t/p/original/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg",
                "A high school chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine in order to secure his family's future.",
                "Crime, Drama, Thriller",
                2008,
                5,
                9.5f,
                false
        ));

        seriesList.add(new Series(
                "2",
                "Game of Thrones",
                "https://image.tmdb.org/t/p/w500/u3bZgnGQ9T01sWNhyveQz0wH0Hl.jpg",
                "https://image.tmdb.org/t/p/original/suopoADq0k8YZr4dQXcU6pToj6s.jpg",
                "Nine noble families fight for control over the lands of Westeros, while an ancient enemy returns after being dormant for millennia.",
                "Action, Adventure, Drama",
                2011,
                8,
                9.3f,
                false
        ));

        seriesList.add(new Series(
                "3",
                "Stranger Things",
                "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
                "https://image.tmdb.org/t/p/original/56v2KjBlU4XaOv9rVYEQypROD7P.jpg",
                "When a young boy disappears, his mother, a police chief, and his friends must confront terrifying supernatural forces in order to get him back.",
                "Drama, Fantasy, Horror",
                2016,
                4,
                8.7f,
                false
        ));

        seriesList.add(new Series(
                "4",
                "The Mandalorian",
                "https://image.tmdb.org/t/p/w500/sWgBv7LV2PRoQgkxwlibdGXKz1S.jpg",
                "https://image.tmdb.org/t/p/original/o7qi2v4uWQ8bZ1tW3KI0Ztn2epk.jpg",
                "The travels of a lone bounty hunter in the outer reaches of the galaxy, far from the authority of the New Republic.",
                "Action, Adventure, Sci-Fi",
                2019,
                2,
                8.8f,
                false
        ));
    }

    private static void addMockWatchlistSeries(List<Series> seriesList) {
        // Add mock watchlist series if JSON parsing fails
        seriesList.add(new Series(
                "5",
                "The Witcher",
                "https://image.tmdb.org/t/p/w500/7vjaCdMw15FEbXyLQTVa04URsPm.jpg",
                "https://image.tmdb.org/t/p/original/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg",
                "Geralt of Rivia, a solitary monster hunter, struggles to find his place in a world where people often prove more wicked than beasts.",
                "Action, Adventure, Fantasy",
                2019,
                2,
                8.2f,
                true
        ));

        seriesList.add(new Series(
                "6",
                "The Crown",
                "https://image.tmdb.org/t/p/w500/vUUqzWa2LnHIVqkaKVlVGkVcZIW.jpg",
                "https://image.tmdb.org/t/p/original/hQjKNIlrr4D9rID7QQmdQAf7Twj.jpg",
                "Follows the political rivalries and romance of Queen Elizabeth II's reign and the events that shaped the second half of the twentieth century.",
                "Drama, History",
                2016,
                4,
                8.7f,
                true
        ));
    }

    private static void addMockRatings(List<Rating> ratings) {
        // Add mock ratings if JSON parsing fails
        ratings.add(new Rating(
                "1",
                "The Dark Knight",
                "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                5.0f,
                "One of the best superhero movies ever made. Heath Ledger's performance as the Joker is legendary.",
                "2023-05-15",
                true
        ));

        ratings.add(new Rating(
                "2",
                "Breaking Bad",
                "https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg",
                4.5f,
                "Incredible character development and storytelling. Bryan Cranston deserved all his awards.",
                "2023-04-20",
                false
        ));

        ratings.add(new Rating(
                "3",
                "Inception",
                "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
                4.0f,
                "Mind-bending plot with amazing visuals. Christopher Nolan at his best.",
                "2023-03-10",
                true
        ));
    }
}
