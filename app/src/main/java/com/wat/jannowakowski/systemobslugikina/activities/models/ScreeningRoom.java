package com.wat.jannowakowski.systemobslugikina.activities.models;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class ScreeningRoom {


    private String screeningRoomDbRef;
    private int maxSeatCount;
    private int projectionTechnology;
    private int referenceNumber;

    public String getScreeningRoomDbRef() {
        return screeningRoomDbRef;
    }

    public void setScreeningRoomDbRef(String screeningRoomDbRef) {
        this.screeningRoomDbRef = screeningRoomDbRef;
    }

    public int getMaxSeatCount() {
        return maxSeatCount;
    }

    public void setMaxSeatCount(int maxSeatCount) {
        this.maxSeatCount = maxSeatCount;
    }

    public int getProjectionTechnology() {
        return projectionTechnology;
    }

    public void setProjectionTechnology(int projectionTechnology) {
        this.projectionTechnology = projectionTechnology;
    }

    public int getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(int referenceNumber) {
        this.referenceNumber = referenceNumber;
    }



    public ScreeningRoom(int maxSeatCount, int projectionTechnology, int referenceNumber, String screeningRoomDbRef) {
        this.maxSeatCount = maxSeatCount;
        this.projectionTechnology = projectionTechnology;
        this.referenceNumber = referenceNumber;
        this.screeningRoomDbRef = screeningRoomDbRef;
    }

    public ScreeningRoom(int maxSeatCount, int projectionTechnology, int referenceNumber) {
        this.maxSeatCount = maxSeatCount;
        this.projectionTechnology = projectionTechnology;
        this.referenceNumber = referenceNumber;
    }

}
