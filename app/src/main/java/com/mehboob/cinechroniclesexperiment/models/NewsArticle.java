package com.mehboob.cinechroniclesexperiment.models;

public class NewsArticle {
    private String id;
    private String title;
    private String headline;
    private String content;
    private String date;
    private String imageUrl;
    private String source;

    public NewsArticle(String id, String title, String headline, String content, String date, String imageUrl, String source) {
        this.id = id;
        this.title = title;
        this.headline = headline;
        this.content = content;
        this.date = date;
        this.imageUrl = imageUrl;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getHeadline() {
        return headline;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSource() {
        return source;
    }
}
