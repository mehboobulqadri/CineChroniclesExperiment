package com.mehboob.cinechroniclesexperiment.models;

public class Series {
    private String id;
    private String title;
    private String posterUrl;
    private String backdropUrl;
    private String synopsis;
    private String genre;
    private int firstAirYear;
    private int seasons;
    private float rating;
    private boolean isInWatchlist;

    public Series(String id, String title, String posterUrl, String backdropUrl, String synopsis,
                  String genre, int firstAirYear, int seasons, float rating, boolean isInWatchlist) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.synopsis = synopsis;
        this.genre = genre;
        this.firstAirYear = firstAirYear;
        this.seasons = seasons;
        this.rating = rating;
        this.isInWatchlist = isInWatchlist;
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

    public int getFirstAirYear() {
        return firstAirYear;
    }

    public int getSeasons() {
        return seasons;
    }

    public float getRating() {
        return rating;
    }

    public boolean isInWatchlist() {
        return isInWatchlist;
    }

    public void setInWatchlist(boolean inWatchlist) {
        isInWatchlist = inWatchlist;
    }
}
