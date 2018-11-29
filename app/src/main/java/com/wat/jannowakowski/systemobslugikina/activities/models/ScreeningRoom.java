package com.wat.jannowakowski.systemobslugikina.activities.models;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class ScreeningRoom {

    private int maxSeatCount;
    private int projectionTechnology;
    private int referenceNumber;
    private int status;


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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public ScreeningRoom(int maxSeatCount, int projectionTechnology, int referenceNumber, int status) {
        this.maxSeatCount = maxSeatCount;
        this.projectionTechnology = projectionTechnology;
        this.referenceNumber = referenceNumber;
        this.status = status;
    }

}
