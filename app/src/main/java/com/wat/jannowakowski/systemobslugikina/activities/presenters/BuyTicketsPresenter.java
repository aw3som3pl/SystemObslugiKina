package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Movie;
import com.wat.jannowakowski.systemobslugikina.activities.models.ScreeningRoom;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnAllMoviesDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnAllScreeningsRoomsDataReload;

import java.util.ArrayList;

/**
 * Created by Jan Nowakowski on 24.01.2019.
 */

public class BuyTicketsPresenter {

    private View view;

    private Activity buyTicketsActivityRef = null;

    private DatabaseReference mDatabase;


    public BuyTicketsPresenter(){


    }

    public void setBuyTicketsActivityRef(Activity buyTicketsActivityRef) {
        this.buyTicketsActivityRef = buyTicketsActivityRef;
    }

    public interface View{
        void showToastMsg(String msg);
    }
}

