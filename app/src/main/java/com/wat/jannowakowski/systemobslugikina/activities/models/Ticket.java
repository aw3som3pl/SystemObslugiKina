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

    public String getMovieDbRef() {
        return movieDbRef;
    }

    public void setMovieDbRef(String movieDbRef) {
        this.movieDbRef = movieDbRef;
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
    private String movieDbRef;
    private String seatCollumn;
    private String seatRow;

    public Ticket(String temp_movie_ref, int temp_discountType, String temp_seatCollumn, String temp_seatRow){
        this.discountType = temp_discountType;
        this.movieDbRef = temp_movie_ref;
        this.seatCollumn = temp_seatCollumn;
        this.seatRow = temp_seatRow;
    }

}
