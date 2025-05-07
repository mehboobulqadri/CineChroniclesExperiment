package com.mehboob.cinechroniclesexperiment.models;

public class Movie {
    private String id;
    private String title;
    private String posterUrl;
    private String backdropUrl;
    private String synopsis;
    private String genre;
    private int releaseYear;
    private float rating;
    private boolean isFavorite;

    public Movie(String id, String title, String posterUrl, String backdropUrl, String synopsis,
                 String genre, int releaseYear, float rating, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.synopsis = synopsis;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getGenre() {
        return genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public float getRating() {
        return rating;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
