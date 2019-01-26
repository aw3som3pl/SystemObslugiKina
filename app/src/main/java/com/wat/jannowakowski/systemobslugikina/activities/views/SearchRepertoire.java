package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.SearchRepertoirePresenter;
import com.wat.jannowakowski.systemobslugikina.adapters.RepertoirListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchRepertoire extends AppCompatActivity implements SearchRepertoirePresenter.View {


    private Activity thisActivity;

    private SearchRepertoirePresenter presenter;
    private CoordinatorLayout mainLayout;
    private RelativeLayout loadingLayout;
    private LayoutInflater inflater;


    private TextView dateField;
    private Button backBtn;
    private ImageButton refreshBtn;

    private RepertoirListAdapter repertoireAdapter;

    private RecyclerView currentScreeningsList;
    private RecyclerView.LayoutManager mLayoutManager;

    private RepertoirListAdapter.RecyclerViewClickListener mRepertoirListListener = null;

    private Calendar currentSearchDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_repertoire);

        thisActivity = this;

        mainLayout = findViewById(R.id.main_layout);
        loadingLayout = findViewById(R.id.loading_in_progress);

        currentScreeningsList = findViewById(R.id.screenings_list);
        currentScreeningsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(thisActivity);
        currentScreeningsList.setLayoutManager(mLayoutManager);

        dateField = findViewById(R.id.date_field);
        refreshBtn = findViewById(R.id.refresh);
        backBtn = findViewById(R.id.close_button);

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        setTitle("Szukaj w repertuarze");

        initializeDate();

        presenter = new SearchRepertoirePresenter(this);

        presenter.setCustomerMenuActivityRef(thisActivity);

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.reloadCurrentRepertoire(EnumHandler.encodeDayOfYearFromDate(currentSearchDate));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCustomerMenu();
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        presenter.reloadCurrentRepertoire(EnumHandler.encodeDayOfYearFromDate(currentSearchDate));
    }

    @Override
    public void onPause(){
        super.onPause();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    private void initializeDate(){
        currentSearchDate = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = format.format(currentSearchDate.getTime());
        dateField.setText(strDate);
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

                        currentSearchDate = calendar;
                        dateField.setText(strDate);
                        presenter.reloadCurrentRepertoire(EnumHandler.encodeDayOfYearFromDate(currentSearchDate));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void navigateToBuyTickets(Screening screening){
        Intent intent = new Intent(SearchRepertoire.this, BuyTickets.class);
        intent.putExtra("Screening",screening);
        startActivity(intent);
    }

    @Override
    public void navigateToCustomerMenu() {
        onBackPressed();
    }

    @Override
    public void showLoadingIndicator() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void setScreeningsRecyclerViewAdapter(ArrayList<Screening> screeningsList){

        mRepertoirListListener = new RepertoirListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                presenter.showMovieDetailsPopup(position,mainLayout,inflater);
            }
        };

        repertoireAdapter = new RepertoirListAdapter(screeningsList,mRepertoirListListener);
        currentScreeningsList.setAdapter(repertoireAdapter);

        hideLoadingIndicator();

    }
}
