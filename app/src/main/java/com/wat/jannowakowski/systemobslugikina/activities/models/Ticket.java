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

    public String getTicketOwner() {
        return ticketOwner;
    }

    public void setTicketOwner(String ticketOwner) {
        this.ticketOwner = ticketOwner;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getScreeningRoomNumber() {
        return screeningRoomNumber;
    }

    public void setScreeningRoomNumber(int screeningRoomNumber) {
        this.screeningRoomNumber = screeningRoomNumber;
    }

    public String getMovieStartDate() {
        return movieStartDate;
    }

    public void setMovieStartDate(String movieStartDate) {
        this.movieStartDate = movieStartDate;
    }

    public String getMovieStartTime() {
        return movieStartTime;
    }

    public void setMovieStartTime(String movieStartTime) {
        this.movieStartTime = movieStartTime;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getMovieTechnology() {
        return movieTechnology;
    }

    public void setMovieTechnology(int movieTechnology) {
        this.movieTechnology = movieTechnology;
    }



    private String ticketOwner;
    private String movieTitle;
    private int movieTechnology;
    private int discountType;
    private String movieDbRef;
    private String seatCollumn;
    private int screeningRoomNumber;
    private String seatRow;
    private String ticketPrice;
    private String movieStartDate;
    private String movieStartTime;


    public Ticket(String ticketOwner, int discountType, String movieDbRef, String seatCollumn, int screeningRoomNumber, String seatRow, String ticketPrice) {
        this.ticketOwner = ticketOwner;
        this.discountType = discountType;
        this.movieDbRef = movieDbRef;
        this.seatCollumn = seatCollumn;
        this.screeningRoomNumber = screeningRoomNumber;
        this.seatRow = seatRow;
        this.ticketPrice = ticketPrice;
    }

    public Ticket(String movieDbRef, String movieTitle, String movieStartDate, String movieStartTime,int discountType, int movieTechnology, String seatCollumn, String seatRow, int screeningRoomNumber, String ticketPrice) {
        this.discountType = discountType;
        this.movieTechnology = movieTechnology;
        this.movieTitle = movieTitle;
        this.movieStartDate = movieStartDate;
        this.movieStartTime = movieStartTime;
        this.movieDbRef = movieDbRef;
        this.seatCollumn = seatCollumn;
        this.screeningRoomNumber = screeningRoomNumber;
        this.seatRow = seatRow;
        this.ticketPrice = ticketPrice;
    }
}




