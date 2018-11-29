package com.wat.jannowakowski.systemobslugikina.activities.models;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class Screening {

    private Movie movie;
    private int takenSeats;
    private ScreeningRoom screeningRoom;
    private String shortDescription;
    private long baseTicketPrice;
    private String timeOfScreening;
    private boolean isPremiere;


    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public int getTakenSeats() {
        return takenSeats;
    }

    public void setTakenSeats(int takenSeats) {
        this.takenSeats = takenSeats;
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



    public Screening(Movie movie, int takenSeats, ScreeningRoom screeningRoom, String shortDescription, long baseTicketPrice, String timeOfScreening, boolean isPremiere) {
        this.movie = movie;
        this.takenSeats = takenSeats;
        this.screeningRoom = screeningRoom;
        this.shortDescription = shortDescription;
        this.baseTicketPrice = baseTicketPrice;
        this.timeOfScreening = timeOfScreening;
        this.isPremiere = isPremiere;
    }



}
