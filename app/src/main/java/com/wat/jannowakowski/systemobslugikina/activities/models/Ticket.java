package com.wat.jannowakowski.systemobslugikina.activities.models;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class Ticket {

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String getSeatCollumn() {
        return seatCollumn;
    }

    public void setSeatCollumn(String seatCollumn) {
        this.seatCollumn = seatCollumn;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    private int discountType;
    private Movie movie;
    private String seatCollumn;
    private String seatRow;

    public Ticket(Movie temp_movie, int temp_discountType, String temp_seatCollumn, String temp_seatRow){
        this.discountType = temp_discountType;
        this.movie = temp_movie;
        this.seatCollumn = temp_seatCollumn;
        this.seatRow = temp_seatRow;
    }

}
