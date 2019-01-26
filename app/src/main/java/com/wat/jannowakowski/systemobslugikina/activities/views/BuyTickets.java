package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;

public class BuyTickets extends AppCompatActivity {

    private Screening thisScreening;

    private ImageView movieThumbnail;
    private ImageView premiereIcon;

    private TextView movieTitle;
    private TextView movieTechnology;
    private TextView movieAgeRating;
    private TextView movieScreeningDate;
    private TextView movieScreeningTime;
    private TextView movieDuration;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_tickets);

        Intent i = getIntent();
        thisScreening = (Screening) i.getSerializableExtra("Screening");


    }
}
