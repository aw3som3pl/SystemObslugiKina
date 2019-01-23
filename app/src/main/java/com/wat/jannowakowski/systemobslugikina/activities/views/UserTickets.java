package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.UserTicketsPresenter;

public class UserTickets extends AppCompatActivity implements UserTicketsPresenter.View {

    private Activity thisActivity;
    private UserTicketsPresenter presenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tickets);


        thisActivity = this;

        presenter = new UserTicketsPresenter(this);
    }
}
