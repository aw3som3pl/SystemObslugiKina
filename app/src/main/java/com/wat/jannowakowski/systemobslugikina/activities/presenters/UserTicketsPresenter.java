package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.view.View;

import com.google.firebase.database.DatabaseReference;



public class UserTicketsPresenter {

    private View view;

    private Activity userTicketsActivityRef = null;

    private DatabaseReference mDatabase;

    public UserTicketsPresenter(View v){
        this.view = v;
    }

    public interface View{

    }
}
