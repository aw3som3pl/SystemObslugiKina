package com.wat.jannowakowski.systemobslugikina.activities.models;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class Movie {

    private Byte[] thumbnail;
    private int ageRating;
    private String description;
    private String title;
    private int duration;
    private String languageMode;


    public Byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(int ageRating) {
        this.ageRating = ageRating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLanguageMode() {
        return languageMode;
    }

    public void setLanguageMode(String languageMode) {
        this.languageMode = languageMode;
    }

    public Movie(Byte[] thumbnail, int ageRating, String description, String title, int duration, String languageMode) {
        this.thumbnail = thumbnail;
        this.ageRating = ageRating;
        this.description = description;
        this.title = title;
        this.duration = duration;
        this.languageMode = languageMode;
    }
}
