package com.wat.jannowakowski.systemobslugikina.activities.models;

import java.util.ArrayList;

/**
 * Created by Jan Nowakowski on 24.11.2018.
 */

public class Repertoir {



    private int dayOfYear;
    private ArrayList<Screening> screeningsList;

    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public ArrayList<Screening> getScreeningsList() {
        return screeningsList;
    }

    public void setScreeningsList(ArrayList<Screening> screeningsList) {
        this.screeningsList = screeningsList;
    }
    public Repertoir(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public Repertoir(int dayOfYear, ArrayList<Screening> screeningsList) {
        this.dayOfYear = dayOfYear;
        this.screeningsList = screeningsList;
    }





}
