package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Movie;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.models.ScreeningRoom;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.AddMovieToRepertoirePresenter;
import com.wat.jannowakowski.systemobslugikina.adapters.MoviesSpinnerAdapter;
import com.wat.jannowakowski.systemobslugikina.adapters.ScreeningRoomsSpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddMovieToRepertoire extends AppCompatActivity implements AddMovieToRepertoirePresenter.View{

    AddMovieToRepertoirePresenter presenter;

    private Activity thisActivity;
    private Button saveBtn;
    private Button cancelBtn;
    private TextView screeningDate;
    private TextView screeningTime;
    private Switch premiereButton;
    private EditText baseTicketPrice;
    private Spinner moviesSpinner;
    private Spinner screeningRoomsSpinner;

    private LinearLayout sendingLayout;

    private RelativeLayout loadingOverlayLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie_to_repertoire);

        loadingOverlayLayout = findViewById(R.id.loading_in_progress);
        sendingLayout = findViewById(R.id.sending_layout);

        presenter = new AddMovieToRepertoirePresenter(this);

        thisActivity = this;
        presenter.setAddMovieToRepertoireRef(thisActivity);

        saveBtn = findViewById(R.id.save_button);
        cancelBtn = findViewById(R.id.close_button);

        screeningTime = findViewById(R.id.time_field);
        screeningDate = findViewById(R.id.date_field);
        premiereButton = findViewById(R.id.is_premiere);
        baseTicketPrice = findViewById(R.id.ticket_price);
        moviesSpinner = findViewById(R.id.movies_spinner);
        screeningRoomsSpinner = findViewById(R.id.screening_rooms_spinner);

        screeningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        screeningTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        moviesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.selectedMovieIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        screeningRoomsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.selectedScreeningRoomIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                gatherInputData();
            }
        });

        cancelBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                navigateToStaffMenu();
            }
        });
    }

    private void OnInit(){

    }
    @Override
    public void initializeSpinners(ArrayList<ScreeningRoom> screeningRooms, ArrayList<Movie> movies){
        MoviesSpinnerAdapter moviesSpinnerAdapter = new MoviesSpinnerAdapter(thisActivity,movies);
        ScreeningRoomsSpinnerAdapter screeningRoomsSpinnerAdapter = new ScreeningRoomsSpinnerAdapter(thisActivity,screeningRooms);

        moviesSpinner.setAdapter(moviesSpinnerAdapter);
        screeningRoomsSpinner.setAdapter(screeningRoomsSpinnerAdapter);
        hideLoadingIndicator();
    }

    public void showDatePicker(){

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String strDate = format.format(calendar.getTime());

                        screeningDate.setText(strDate);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void showTimePicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                       screeningTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void gatherInputData(){
        if(screeningTime.getText().length()>0 && screeningDate.getText().length()>0 && baseTicketPrice.getText().length()>0) {
            showSendingIndicator();
            presenter.completeScreeningData(new Screening(Double.parseDouble(baseTicketPrice.getText().toString()),
                    screeningDate.getText().toString(),
                    screeningTime.getText().toString(),
                    EnumHandler.encodePremiereFlagState(premiereButton.isChecked())));
        }
        else {
            hideSendingIndicator();
            showToastMsg("Brak wymaganych danych w formularzu");
        }
    }

    @Override
    public void showLoadingIndicator() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingOverlayLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingOverlayLayout.setVisibility(View.GONE);
    }

    @Override
    public void showSendingIndicator() {
        saveBtn.setEnabled(false);
        sendingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSendingIndicator() {
        saveBtn.setEnabled(true);
        sendingLayout.setVisibility(View.GONE);
    }

    @Override
    public void navigateToStaffMenu(){
        onBackPressed();
    }

    @Override
    public void showToastMsg(String msg){
        Toast.makeText(AddMovieToRepertoire.this, msg, Toast.LENGTH_LONG).show();
    }


}
