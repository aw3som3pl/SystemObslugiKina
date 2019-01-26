package com.wat.jannowakowski.systemobslugikina.activities.models;

import android.graphics.drawable.Drawable;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class Movie {

    private String movieDbRef;
    private Drawable thumbnail;
    private String rawThumbnail;
    private int ageRating;
    private String description;
    private String title;
    private int duration;
    private int languageMode;
    private int screeningTechnology;

    public String getMovieDbRef() {
        return movieDbRef;
    }

    public void setMovieDbRef(String movieDbRef) {
        this.movieDbRef = movieDbRef;
    }

    public String getRawThumbnail() {
        return rawThumbnail;
    }

    public void setRawThumbnail(String rawThumbnail) {
        this.rawThumbnail = rawThumbnail;
    }

    public int getScreeningTechnology() {
        return screeningTechnology;
    }

    public void setScreeningTechnology(int screeningTechnology) {
        this.screeningTechnology = screeningTechnology;
    }

    public Drawable getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
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

    public int getLanguageMode() {
        return languageMode;
    }

    public void setLanguageMode(int languageMode) {
        this.languageMode = languageMode;
    }

    public Movie(Drawable thumbnail, int ageRating, String description, String title, int screeningTechnology, int duration, int languageMode, String movieDbRef) {
        this.thumbnail = thumbnail;
        this.ageRating = ageRating;
        this.description = description;
        this.title = title;
        this.duration = duration;
        this.languageMode = languageMode;
        this.screeningTechnology = screeningTechnology;
        this.movieDbRef = movieDbRef;
    }

    public Movie(String rawThumbnail, int ageRating, String description, String title, int screeningTechnology, int duration, int languageMode) {
        this.rawThumbnail = rawThumbnail;
        this.ageRating = ageRating;
        this.description = description;
        this.title = title;
        this.duration = duration;
        this.languageMode = languageMode;
        this.screeningTechnology = screeningTechnology;
    }

    public Movie(int ageRating, String description, String title, int screeningTechnology, int languageMode, String movieDbRef) {
        this.ageRating = ageRating;
        this.description = description;
        this.title = title;
        this.languageMode = languageMode;
        this.screeningTechnology = screeningTechnology;
        this.movieDbRef = movieDbRef;
    }

    public boolean checkValidityBeforeSending() {
        return rawThumbnail.length() != 0 && description.length() != 0 && title.length() != 0;
    }

}
