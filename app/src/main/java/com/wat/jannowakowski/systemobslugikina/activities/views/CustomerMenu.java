package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.CustomerMenuPresenter;
import com.wat.jannowakowski.systemobslugikina.adapters.RepertoirListAdapter;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;

import java.util.ArrayList;
import java.util.Calendar;

public class CustomerMenu extends AppCompatActivity implements CustomerMenuPresenter.View {

    private Activity thisActivity;

    private CustomerMenuPresenter presenter;
    private CoordinatorLayout mainLayout;
    private LayoutInflater inflater;

    private RelativeLayout loadingOverlayLayout;
    private Button showUserTicketsBtn, showRepertoirBtn;
    private LinearLayout refreshBtn;
    private TextView screeningsDataMissing;
    private RecyclerView currentScreeningsList;
    private RecyclerView.Adapter repertoireAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RepertoirListAdapter.RecyclerViewClickListener mRepertoirListListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_menu);

        mainLayout = findViewById(R.id.main_layout);
        loadingOverlayLayout = findViewById(R.id.loading_in_progress);
        refreshBtn = findViewById(R.id.refresh);
        showUserTicketsBtn = findViewById(R.id.active_tickets);
        showRepertoirBtn = findViewById(R.id.browse_movies);
        currentScreeningsList = findViewById(R.id.current_screenings);
        screeningsDataMissing = findViewById(R.id.no_data_notifier);
        currentScreeningsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(thisActivity);
        currentScreeningsList.setLayoutManager(mLayoutManager);

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        thisActivity = this;

        setTitle(CurrentAppSession.getINSTANCE().getUserFullName());

        presenter = new CustomerMenuPresenter(this);

        presenter.setCustomerMenuActivityRef(thisActivity);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.reloadCurrentRepertoire();
            }
        });

        showRepertoirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSearch();
            }
        });

        showUserTicketsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        presenter.reloadCurrentRepertoire();
    }

    @Override
    public void onPause(){
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                break;
            case R.id.contact:
                break;
            case R.id.logout:
                presenter.commenceUserLogout();
                break;
        }
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(CustomerMenu.this, Login.class));
        finish();
    }

    @Override
    public void navigateToSearch(){
        startActivity(new Intent(CustomerMenu.this, SearchRepertoire.class));
    }

    @Override
    public void navigateToBuyTickets(Screening screening){
        Intent intent = new Intent(CustomerMenu.this, BuyTickets.class);
        intent.putExtra("Screening",screening);
        startActivity(intent);
    }

    @Override
    public void showScreeningsDataMissing(){
        screeningsDataMissing.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideScreeningsDataMissing(){
        screeningsDataMissing.setVisibility(View.GONE);
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
