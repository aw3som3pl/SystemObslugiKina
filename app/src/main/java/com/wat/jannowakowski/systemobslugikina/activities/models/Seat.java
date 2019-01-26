package com.wat.jannowakowski.systemobslugikina.activities.models;

import android.widget.CheckBox;

/**
 * Created by Jan Nowakowski on 24.01.2019.
 */

public class Seat {

    public Seat(CheckBox iconRef, int seatColumn, char seatRow) {
        this.iconRef = iconRef;
        this.seatColumn = seatColumn;
        this.seatRow = seatRow;
    }

    public Seat(int seatColumn, char seatRow) {
        this.seatColumn = seatColumn;
        this.seatRow = seatRow;
    }

    CheckBox iconRef;
    int seatColumn;
    char seatRow;

    public CheckBox getIconRef() {
        return iconRef;
    }

    public void setIconRef(CheckBox iconRef) {
        this.iconRef = iconRef;
    }

    public int getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(int seatColumn) {
        this.seatColumn = seatColumn;
    }

    public char getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(char seatRow) {
        this.seatRow = seatRow;
    }

}
