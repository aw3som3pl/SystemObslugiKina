package com.wat.jannowakowski.systemobslugikina.activities.models;

import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class Screening {

    private String screeningDbRef;
    private String movieDbRef;
    private String screeningRoomDbRef;
    private Movie movie;
    private int takenSeats;
    private int screeningTechnology;
    private ScreeningRoom screeningRoom;
    private String shortDescription;
    private long baseTicketPrice;
    private String dateOfScreening;
    private String timeOfScreening;
    private boolean isPremiere;

    public String getScreeningRoomDbRef() {
        return screeningRoomDbRef;
    }

    public void setScreeningRoomDbRef(String screeningRoomDbRef) {
        this.screeningRoomDbRef = screeningRoomDbRef;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String getScreeningDbRef() {
        return screeningDbRef;
    }

    public void setScreeningDbRef(String screeningDbRef) {
        this.screeningDbRef = screeningDbRef;
    }

    public String getMovieDbRef() {
        return movieDbRef;
    }

    public void setMovieDbRef(String movieDbRef) {
        this.movieDbRef = movieDbRef;
    }

    public int getTakenSeats() {
        return takenSeats;
    }

    public void setTakenSeats(int takenSeats) {
        this.takenSeats = takenSeats;
    }

    public int getScreeningTechnology() {
        return screeningTechnology;
    }

    public void setScreeningTechnology(int screeningTechnology) {
        this.screeningTechnology = screeningTechnology;
    }

    public ScreeningRoom getScreeningRoom() {
        return screeningRoom;
    }

    public void setScreeningRoom(ScreeningRoom screeningRoom) {
        this.screeningRoom = screeningRoom;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public long getBaseTicketPrice() {
        return baseTicketPrice;
    }

    public void setBaseTicketPrice(long baseTicketPrice) {
        this.baseTicketPrice = baseTicketPrice;
    }

    public String getDateOfScreening() {
        return dateOfScreening;
    }

    public void setDateOfScreening(String dateOfScreening) {
        this.dateOfScreening = dateOfScreening;
    }

    public String getTimeOfScreening() {
        return timeOfScreening;
    }

    public void setTimeOfScreening(String timeOfScreening) {
        this.timeOfScreening = timeOfScreening;
    }

    public boolean isPremiere() {
        return isPremiere;
    }

    public void setPremiere(boolean premiere) {
        isPremiere = premiere;
    }


    public Screening(String screeningDbRef,String movieDbRef,String screeningRoomDbRef,int screeningTechnology, int takenSeats, String shortDescription, long baseTicketPrice, String dateOfScreening, String timeOfScreening, int isPremiere) {

        this.screeningRoomDbRef = screeningRoomDbRef;
        this.screeningDbRef = screeningDbRef;
        this.movieDbRef = movieDbRef;
        this.takenSeats = takenSeats;
        this.screeningTechnology = screeningTechnology;
        this.shortDescription = shortDescription;
        this.baseTicketPrice = baseTicketPrice;
        this.dateOfScreening = dateOfScreening;
        this.timeOfScreening = timeOfScreening;
        this.isPremiere = EnumHandler.parsePremiereFlagState(isPremiere);
        movie = null;
    }



}
