package com.mehboob.cinechroniclesexperiment.models;

public class Rating {
    private String id;
    private String title;
    private String posterUrl;
    private float rating;
    private String comment;
    private String date;
    private boolean isMovie; // true for movie, false for series

    public Rating(String id, String title, String posterUrl, float rating, String comment,
                  String date, boolean isMovie) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.isMovie = isMovie;
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

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public boolean isMovie() {
        return isMovie;
    }
}
